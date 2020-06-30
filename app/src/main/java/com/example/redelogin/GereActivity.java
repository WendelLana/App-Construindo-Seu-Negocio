package com.example.redelogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class GereActivity extends AppCompatActivity {

    Button btClientes, btFornec;
    ImageButton btAgenda;
    CustomCalendarView customCalendarView;
    FirebaseFirestore db;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_gere );
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        customCalendarView = findViewById( R.id.activity_custom_calendar );

        btClientes = findViewById( R.id.gereClientes );
        btFornec = findViewById( R.id.gereFornecedores );
        btAgenda = findViewById( R.id.gereCalen );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        btClientes.setTypeface(custom_font);
        btFornec.setTypeface(custom_font);

        btClientes.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), ClienteActivity.class );
            v.getContext().startActivity( intent );
        } );

        btFornec.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), FornecedorActivity.class );
            v.getContext().startActivity( intent );
        } );
    }
}
