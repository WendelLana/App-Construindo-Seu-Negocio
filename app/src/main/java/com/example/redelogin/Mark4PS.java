package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Mark4PS extends AppCompatActivity {
    TextView title, text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mark_4ps );

        title = findViewById( R.id.os4ps_title );
        text = findViewById( R.id.os4ps_text );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        title.setTypeface(custom_font);
        text.setTypeface(custom_font);
    }

    public void dial4Ps(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que são os 4P's?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É um conjunto de ferramentas de marketing fundamentais, que tem por objetivo ajudar a empresa alcançar seu público-alvo e garantir seu sucesso no mercado ao qual está inserida.")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
