package com.example.music;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.music.data.DB_Handler;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class fav_song extends AppCompatActivity {
    private ArrayList<File> arrayList;
    ListView listView;
    String[] items;
    public static int index = -1;
    public static CustomAdapter customAdapter;
    public static ArrayList<String> songsFav;

    @SuppressLint("SetTextI18n")
    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.activity_fav_song);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Favourites");
        listView = findViewById(R.id.favSongList);

        Intent intent = getIntent();
        Bundle bundle1 = intent.getExtras();
        ArrayList<File> mySong = (ArrayList) bundle1.getParcelableArrayList("mySong");

        songsFav = MainActivity.favSongList;


        if(!songsFav.isEmpty() && !mySong.isEmpty()){
            arrayList = new ArrayList<>();
            for (int i = 0; i<songsFav.size(); i++){
                for(int j = 0; j<mySong.size(); j++){
                    if (Integer.toString(mySong.get(j).hashCode()).equals(songsFav.get(i))){
                        arrayList.add(mySong.get(j));
                        break;
                    }
                }
            }
        }

        items = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            items[i] = arrayList.get(i).getName().replace(".mp3", "");

        }
        customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    class CustomAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
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
            if(index == i){
                textSong.setSelected(true);
            }

            toggleButton.setChecked(true);
            toggleButton.setOnClickListener(v ->{
                if (!toggleButton.isChecked()){
                    DB_Handler db = new DB_Handler(fav_song.this);
                    if (db.removeSong(arrayList.get(i).hashCode())){
                        MainActivity.favSongList.remove(i);
                        arrayList.remove(i);
                        refreshList();
                    }
                }
                this.notifyDataSetChanged();
                MainActivity.ca.notifyDataSetChanged();
                if (arrayList.isEmpty()){
                    TextView textView = findViewById(R.id.empty);
                    textView.setText("Nothing added to Favourite !");
                }
            });
            return myView;
        }
    }
    private void playSong(int i){
        String songName = (String) listView.getItemAtPosition(i);
        startActivity(new Intent(getApplicationContext(), mp3player.class)
                .putExtra("songs", arrayList)
                .putExtra("songname", songName)
                .putExtra("pos", i));
    }

    private void refreshList(){
        items = new String[arrayList.size()];
        for (int i = 0; i < arrayList.size(); i++) {
            items[i] = arrayList.get(i).getName().replace(".mp3", "");
        }
    }

}
