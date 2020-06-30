package com.example.redelogin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import agency.tango.materialintroscreen.MaterialIntroActivity;


public class TourActivity extends MaterialIntroActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );



    }

    @Override
    public void onFinish() {
        super.onFinish();
        Toast.makeText(this, "Try this library in your project! :)", Toast.LENGTH_SHORT).show();
    }
}
