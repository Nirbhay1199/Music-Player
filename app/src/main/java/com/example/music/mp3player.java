package com.example.music;


import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
//import android.widget.Filter;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class mp3player extends AppCompatActivity {
    Button btnplay, btnnext, btnprevious;
    TextView txtplayer, txtseekbarstart, txtseekbarend;
    SeekBar seekBar;

    String sname;

    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateseekbar;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Music Player");

        btnprevious = findViewById(R.id.btnprevious);
        btnplay = findViewById(R.id.btnplay);
        btnnext = findViewById(R.id.btnnext);
        txtplayer = findViewById(R.id.txtplayer);
        txtseekbarstart = findViewById(R.id.txtseekbarstart);
        txtseekbarend = findViewById(R.id.txtseekbarend);
        seekBar = findViewById(R.id.seekbar);

        try {
            if (mediaPlayer != null || mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer.reset();
            }
        }catch (Exception ignored){

        }

        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        String songName = i.getStringExtra("song_Name");
        position = bundle.getInt("pos", 0);
        txtplayer.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sname = mySongs.get(position).getName();
        txtplayer.setText(sname);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

        updateseekbar = new Thread()
        {
            @Override
            public void run(){

                int currentposition = 0;
                    while (currentposition < mediaPlayer.getDuration())
                    {
                        try {
                            currentposition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentposition);
                            sleep(500);
                        }
                        catch (InterruptedException | IllegalStateException e)
                        {
                            e.printStackTrace();
                        }
                    }
            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateseekbar.start();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        String endTime =  createTime(mediaPlayer.getDuration());
        txtseekbarend.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer.isPlaying()){
                    String currentTime = createTime(mediaPlayer.getCurrentPosition());
                    txtseekbarstart.setText(currentTime);
                    handler.postDelayed(this, delay);
                }else {
                    handler.removeCallbacks(this);
                }
            }
        },delay);


        try {
            btnplay.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    btnplay.setBackgroundResource(R.drawable.play_foreground);
                    mediaPlayer.pause();
                } else {
                    btnplay.setBackgroundResource(R.drawable.pause_foreground);
                    mediaPlayer.start();
                }
            });
        }catch (Exception ignored){

        }
        //on complete -_-

        mediaPlayer.setOnCompletionListener(mp -> btnnext.performClick());

        btnnext.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position+1)%mySongs.size());
            Uri u = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            sname = mySongs.get(position).getName();
            txtplayer.setText(sname);
            txtseekbarstart.setText(createTime(mediaPlayer.getCurrentPosition()));
            txtseekbarend.setText(createTime(mediaPlayer.getDuration()));
            mediaPlayer.start();
            btnplay.setBackgroundResource(R.drawable.pause_foreground);
        });

        btnprevious.setOnClickListener(view -> {
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position-1)<0)?(mySongs.size()-1):(position-1);
            Uri u = Uri.parse(mySongs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
            sname = mySongs.get(position).getName();
            txtseekbarstart.setText(createTime(mediaPlayer.getCurrentPosition()));
            txtseekbarend.setText(createTime(mediaPlayer.getDuration()));
            txtplayer.setText(sname);
            mediaPlayer.start();
            btnplay.setBackgroundResource(R.drawable.pause_foreground);
        });
    }


    public String createTime(int duration)
    {
        String time = "";
        int min = duration/1000/60;
        int sec = duration/1000%60;

        time+=min+":";
        if(sec<10)
        {
            time+="0";
        }
        time+=sec;

        return time;
    }
}
