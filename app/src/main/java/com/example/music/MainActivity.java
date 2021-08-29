package com.example.music;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.music.data.DB_Handler;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] items;
    public static ArrayList<File> mySongs;
    public static CustomAdapter ca;
    public static ArrayList<String> favSongList = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewSong);

        ImageView button = findViewById(R.id.favouriteBtn);

        button.setOnClickListener(v -> {
            if (favSongList.isEmpty()){
                Toast t = Toast.makeText(getApplicationContext(),
                        "Nothing added to favourite !!!",
                        Toast.LENGTH_LONG);
                t.show();
            }
            else
            {
                favSongBtn();
            }
        });

        runtimePermission();
    }

    public void runtimePermission(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                displaySongs();
                DB_Handler db_handler = new DB_Handler(getApplicationContext());
                favSongList = (ArrayList<String>) db_handler.getAll_fav_song();
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();

    }


    public ArrayList<File> findSong(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findSong(singleFile));
                } else {
                    if (singleFile.getName().endsWith(".mp3")) {
                        arrayList.add(singleFile);
                    }
                }
            }
        }
        return arrayList;
    }

    void displaySongs(){
        mySongs = findSong(Environment.getExternalStorageDirectory());

        items = new String[mySongs.size()];
        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().replace(".mp3", "");

        }

        ca = new CustomAdapter();
        listView.setAdapter(ca);

    }

    class CustomAdapter extends BaseAdapter
    {
        @Override
        public int getCount() {return items.length;}

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View myView = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textSong = myView.findViewById(R.id.txtsongname);
            textSong.setSelected(true);
            textSong.setText(items[i]);
            textSong.setOnClickListener(v -> playSong(i));

            ImageView imageView = myView.findViewById(R.id.imgsong);
            imageView.setSelected(true);
            imageView.setOnClickListener(v -> playSong(i));


            ToggleButton toggleButton = myView.findViewById(R.id.favourite);
            if (favSongList.contains(Integer.toString(mySongs.get(i).hashCode()))){
                toggleButton.setChecked(true);
            }
            toggleButton.setOnClickListener(v -> {
                if (toggleButton.isChecked()){
                    DB_Handler db = new DB_Handler(MainActivity.this);
                    db.addFavSong(mySongs.get(i).hashCode());
                    favSongList.add(Integer.toString(mySongs.get(i).hashCode()));
                }
                else {
                    DB_Handler db = new DB_Handler(MainActivity.this);
                    if (db.removeSong(mySongs.get(i).hashCode())){
                        favSongList.remove(Integer.toString(mySongs.get(i).hashCode()));
                    }
                }
            });
            return myView;

        }
    }

    private void playSong(int i){
        String songName = (String) listView.getItemAtPosition(i);
        startActivity(new Intent(getApplicationContext(), mp3player.class)
                .putExtra("songs", mySongs)
                .putExtra("songname", songName)
                .putExtra("pos", i));
    }

    private void favSongBtn(){
        Intent intent = new Intent(this, fav_song.class);
        startActivity(intent.putExtra("mySong",mySongs));
    }

}