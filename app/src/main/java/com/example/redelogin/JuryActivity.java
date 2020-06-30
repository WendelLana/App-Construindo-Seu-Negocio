package com.example.redelogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class JuryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_jury );

        Button btEmpresa = findViewById( R.id.juryMicroempr );
        Button btRegistro = findViewById( R.id.juryRegis );
        Button btDireitos = findViewById( R.id.juryDireit );
        Button btTributos = findViewById( R.id.juryTributo );
        btEmpresa.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), JuryMEI.class );
            v.getContext().startActivity( intent );
        } );

        btRegistro.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), JuryRegistro.class );
            v.getContext().startActivity( intent );
        } );

        btDireitos.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), JuryDireitos.class );
            v.getContext().startActivity( intent );
        } );

        btTributos.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), JuryTributos.class );
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
