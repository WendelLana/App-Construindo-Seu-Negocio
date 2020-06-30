package com.example.redelogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MarkActivity extends AppCompatActivity {
    Button btSwot, btLogo, bt4ps, btPesquisa, btEstrategia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mark );

        bt4ps = findViewById( R.id.mark4ps );
        btPesquisa = findViewById( R.id.markPesqMerc );
        btEstrategia = findViewById( R.id.markEstrat );
        btSwot = findViewById( R.id.markSWOT );

        btLogo = findViewById( R.id.markLogo );
        btLogo.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), LogoActivity.class );
            v.getContext().startActivity( intent );
        } );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        btSwot.setTypeface(custom_font);
        btLogo.setTypeface(custom_font);
        bt4ps.setTypeface(custom_font);
        btPesquisa.setTypeface(custom_font);
        btEstrategia.setTypeface(custom_font);

        btSwot.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), MarkSWOT.class );
            v.getContext().startActivity( intent );
        } );
        bt4ps.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), Mark4PS.class );
            v.getContext().startActivity( intent );
        } );
        btEstrategia.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), MarkEstrategia.class );
            v.getContext().startActivity( intent );
        } );
        btPesquisa.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), MarkPesquisa.class );
            v.getContext().startActivity( intent );
        } );
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
