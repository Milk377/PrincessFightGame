package com.ndolpin.myapplication.dcar;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;


public class SpaceActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.spaceshipgame);

    }
}

