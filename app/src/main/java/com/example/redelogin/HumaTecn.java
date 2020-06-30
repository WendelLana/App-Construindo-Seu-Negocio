package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HumaTecn extends AppCompatActivity {

    TextView text, text2, text3, text4, text5, text6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.huma_tecn );

        text = findViewById( R.id.tecn_text1 );
        text2 = findViewById( R.id.tecn_text2 );
        text3 = findViewById( R.id.tecn_text3 );
        text4 = findViewById( R.id.tecn_text4 );
        text5 = findViewById( R.id.tecn_text5 );
        text6 = findViewById( R.id.tecn_text6 );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        text.setTypeface(custom_font);
        text2.setTypeface(custom_font);
        text3.setTypeface(custom_font);
        text4.setTypeface(custom_font);
        text5.setTypeface(custom_font);
        text6.setTypeface(custom_font);
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
