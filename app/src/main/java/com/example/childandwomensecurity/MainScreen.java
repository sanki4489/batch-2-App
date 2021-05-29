package com.example.childandwomensecurity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);
    }

    public void quick(View view) {
        Intent help = new Intent(this, MusicActivity.class);
        startActivity(help);
    }

    public void location(View view) {
        Intent music = new Intent(this, LoginActivity.class);
        startActivity(music);
    }
}
