package com.example.music;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class start extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(start.this,MainActivity.class));
            finish();

        },200);
    }
}
