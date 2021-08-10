package com.example.music;


import android.content.Intent;
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
    Button btnPlay, btnNext, btnPrevious;
    TextView txtplayer, txtSeekBarStart, txtseekbarend;
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

        btnPrevious = findViewById(R.id.btnprevious);
        btnPlay = findViewById(R.id.btnplay);
        btnNext = findViewById(R.id.btnnext);
        txtplayer = findViewById(R.id.txtplayer);
        txtSeekBarStart = findViewById(R.id.txtseekbarstart);
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

        playSong();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateTime();

        try {
            btnPlay.setOnClickListener(v -> {
                if (mediaPlayer.isPlaying()) {
                    btnPlay.setBackgroundResource(R.drawable.play_foreground);
                    mediaPlayer.pause();
                } else {
                    btnPlay.setBackgroundResource(R.drawable.pause_foreground);
                    mediaPlayer.start();
                }
            });
        }catch (Exception ignored){

        }
        //on complete

        mediaPlayer.setOnCompletionListener(mp -> btnNext.performClick());

        btnNext.setOnClickListener(view -> {
            playSong();
            position = ((position+1)%mySongs.size());
            btnPlay.setBackgroundResource(R.drawable.pause_foreground);
        });

        btnPrevious.setOnClickListener(view -> {
            playSong();
            position = ((position-1)<0)?(mySongs.size()-1):(position-1);
            btnPlay.setBackgroundResource(R.drawable.pause_foreground);
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

    private void playSong(){
        try {
            if(mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
        }catch (Exception ignored)
        {

        }
        Uri u = Uri.parse(mySongs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), u);
        sname = mySongs.get(position).getName();
        txtplayer.setText(sname);
        mediaPlayer.start();
        try {
            if (updateseekbar.isAlive())
                updateseekbar.interrupt();
        }
        catch (Exception ignored){

        }
        manageSeekbar();
        seekBar.setMax(mediaPlayer.getDuration());
    }

    private void manageSeekbar(){
        updateseekbar = new Thread()
        {
            @Override
            public void run(){

                int currentposition = 0;
                try {
                    while (currentposition < mediaPlayer.getDuration()) {
                        currentposition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentposition);
                        sleep(500);
                    }
                }catch (Exception e){

                }
            }
        };
        updateseekbar.start();
    }

    private void updateTime(){
        String endTime =  createTime(mediaPlayer.getDuration());
        txtseekbarend.setText(endTime);

        final Handler handler = new Handler();
        final int delay = 1000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer.isPlaying()) {
                        String currentTime = createTime(mediaPlayer.getCurrentPosition());
                        txtSeekBarStart.setText(currentTime);
                        handler.postDelayed(this, delay);
                    } else {
                        handler.removeCallbacks(this);
                    }
                }catch (IllegalStateException e){
                    e.printStackTrace();
                }
            }
        },delay);
    }
}
