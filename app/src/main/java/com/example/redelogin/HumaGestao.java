package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HumaGestao extends AppCompatActivity {

    TextView title, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.huma_gestao );

        title = findViewById( R.id.humaGestao_title );
        text = findViewById( R.id.humaGestao_text );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        title.setTypeface(custom_font);
        text.setTypeface(custom_font);
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
