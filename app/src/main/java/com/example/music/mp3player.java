package com.example.music;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.media.Image;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
//import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class mp3player extends AppCompatActivity {
    Button btnPlay, btnNext, btnPrevious;
    TextView txtPlayer, txtSeekBarStart, txtSeekBarEnd;
    SeekBar seekBar;

    String sName;

    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekbar;

//    @RequiresApi(api = Build.VERSION_CODES.P)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

//        Objects.requireNonNull(getSupportActionBar()).setTitle("Music Player");

        btnPrevious = findViewById(R.id.btnprevious);
        btnPlay = findViewById(R.id.btnplay);
        btnNext = findViewById(R.id.btnnext);
        txtPlayer = findViewById(R.id.txtplayer);
        txtSeekBarStart = findViewById(R.id.txtseekbarstart);
        txtSeekBarEnd = findViewById(R.id.txtseekbarend);
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
        txtPlayer.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName = mySongs.get(position).getName();
        txtPlayer.setText(sName);

        playSong(position);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtSeekBarStart.setText(createTime(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

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
            nextBtn();
            btnPlay.setBackgroundResource(R.drawable.pause_foreground);
        });

        btnPrevious.setOnClickListener(view -> {
            previousBtn();
            playSong(position);
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

    private void playSong(int position){
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
        sName = mySongs.get(position).getName();
        txtPlayer.setText(sName);
        mediaPlayer.start();
        try {
            if (updateSeekbar.isAlive())
                updateSeekbar.interrupt();
        }
        catch (Exception ignored){

        }
        manageSeekbar();

        txtSeekBarEnd.setText(createTime(mediaPlayer.getDuration()));
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_700), PorterDuff.Mode.SRC_IN);

        mediaPlayer.setOnCompletionListener(mp -> nextBtn());
    }



    private void manageSeekbar(){
        updateSeekbar = new Thread()
        {
            @Override
            public void run(){
                int currentPosition = 0;
                try {
                    while (currentPosition < mediaPlayer.getDuration()) {
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(1000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeekbar.start();
    }

    private void nextBtn(){
        position = ((position+1)%mySongs.size());
        playSong(position);
    }

    private void previousBtn(){
        position = ((position-1)<0)?(mySongs.size()-1):(position-1);

    }

}
