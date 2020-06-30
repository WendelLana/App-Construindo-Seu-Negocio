package com.example.redelogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HumaActivity extends AppCompatActivity {
    Button btGestao, btFunc, btFolha, btContrataFunc, btTecOrganiza, btBemEmpresarial, btLayEmpresarial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_huma );

        btFolha = findViewById( R.id.humaFolha );
        btGestao = findViewById( R.id.gestaoPessoa );
        btContrataFunc = findViewById( R.id.contrataFunc );
        btTecOrganiza = findViewById( R.id.tecOrganiza );
        btBemEmpresarial = findViewById( R.id.bemEmpresarial );
        btLayEmpresarial = findViewById( R.id.layEmpresarial );
        btFunc = findViewById( R.id.humaFunc );
        btFunc.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), FuncionarioActivity.class );
            v.getContext().startActivity( intent );
        } );
        btFolha.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaFolha.class );
            v.getContext().startActivity( intent );
        });
        btGestao.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaGestao.class );
            v.getContext().startActivity( intent );
        } );
        btContrataFunc.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaContrat.class );
            v.getContext().startActivity( intent );
        });
        btTecOrganiza.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaTecn.class );
            v.getContext().startActivity( intent );
        });
        btBemEmpresarial.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaBemEstar.class );
            v.getContext().startActivity( intent );
        });
        btLayEmpresarial.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), HumaLayEmpresa.class );
            v.getContext().startActivity( intent );
        });

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        btFunc.setTypeface(custom_font);
        btGestao.setTypeface(custom_font);
        btFolha.setTypeface(custom_font);
        btContrataFunc.setTypeface(custom_font);
        btTecOrganiza.setTypeface(custom_font);
        btBemEmpresarial.setTypeface(custom_font);
        btLayEmpresarial.setTypeface(custom_font);

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
