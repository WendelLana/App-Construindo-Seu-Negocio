package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MarkEstrategia extends AppCompatActivity {
    TextView title, text, text2, text3, text4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mark_estrategia );

        title = findViewById( R.id.strategy_title );
        text = findViewById( R.id.strategy_text );
        text2 = findViewById( R.id.strategy_text2 );
        text3 = findViewById( R.id.strategy_text3 );
        text4 = findViewById( R.id.strategy_text4 );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        title.setTypeface(custom_font);
        text.setTypeface(custom_font);
        text2.setTypeface(custom_font);
        text3.setTypeface(custom_font);
        text4.setTypeface(custom_font);
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
