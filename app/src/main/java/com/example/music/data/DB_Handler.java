package com.example.music.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.example.music.Parameters.Parameters;
import com.example.music.fav_song;

import java.util.ArrayList;
import java.util.List;

public class DB_Handler extends SQLiteOpenHelper {

    public DB_Handler(Context context) {
        super(context, Parameters.DB_NAME, null, Parameters.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + Parameters.TABLE_NAME + "("
                + Parameters.KEY_ID + " INTEGER PRIMARY KEY," + Parameters.HASH_CODE + " TEXT" + ")";
        Log.d("DBfavourite", "Query : " + create);
        db.execSQL(create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addFavSong(int hashCode){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Parameters.HASH_CODE, Integer.toString(hashCode));

        db.insert(Parameters.TABLE_NAME, null, values);
        Log.d("DBfavourite", "Successfully inserted");
        db.close();


    }
    public boolean removeSong(int hashCode){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(Parameters.TABLE_NAME,Parameters.HASH_CODE+" = "+Integer.toString(hashCode),null)>0;
    }

    public List<String> getAll_fav_song(){
        List<String> fav_songList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "SELECT * FROM " + Parameters.TABLE_NAME;
        Cursor cursor = db.rawQuery(select, null);

        if (cursor.moveToFirst()){
            do {
                String music = cursor.getString(1);
                fav_songList.add(music);
            }while (cursor.moveToNext());
        }
        db.close();
        return fav_songList;
    }


}
