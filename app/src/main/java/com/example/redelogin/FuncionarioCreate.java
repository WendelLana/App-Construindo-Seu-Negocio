package com.example.redelogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FuncionarioCreate extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextNome;
    private EditText editTextRG;
    private EditText editTextDataNasc;
    private EditText editTextCargo;
    private EditText editTextDataContrata;
    private EditText editTextSalario;
    private EditText editTextDesc;

    private FirebaseFirestore db;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.huma_newfuncio );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        editTextNome = findViewById( R.id.edNomeFunc );
        editTextRG = findViewById( R.id.edRGFunc );
        editTextDataNasc = findViewById( R.id.edNascFunc );
        editTextCargo = findViewById( R.id.edCargoFunc );
        editTextDataContrata = findViewById( R.id.edContratoFunc );
        editTextSalario = findViewById( R.id.edSalarFunc );
        editTextDesc = findViewById( R.id.edDescFunc );

        findViewById( R.id.saveFunc ).setOnClickListener( this );
    }

    private boolean validateInputs(String nome, String rg, String dataNasc, String cargo, String dataContrata, String salario, String desc ) {
        if(nome.isEmpty()){
            editTextNome.setError( "Nome é obrigatório!" );
            editTextNome.requestFocus();
            return false;
        }

        if(rg.isEmpty()){
            editTextRG.setError( "RG é obrigatório!" );
            editTextRG.requestFocus();
            return false;
        }

        if(dataNasc.isEmpty()){
            editTextDataNasc.setError( "Data de nascimento é obrigatório!" );
            editTextDataNasc.requestFocus();
            return false;
        }
        if(cargo.isEmpty()){
            editTextCargo.setError( "Cargo é obrigatório!" );
            editTextCargo.requestFocus();
            return false;
        }
        if(dataContrata.isEmpty()){
            editTextDataContrata.setError( "Data de contratação é obrigatório!" );
            editTextDataContrata.requestFocus();
            return false;
        }
        if(salario.isEmpty()){
            editTextSalario.setError( "Salário é obrigatório!" );
            editTextSalario.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String nome = editTextNome.getText().toString().trim();
        String frg = editTextRG.getText().toString().trim();
        String dataNasc = editTextDataNasc.getText().toString().trim();
        String cargo = editTextCargo.getText().toString().trim();
        String dataContrata = editTextDataContrata.getText().toString().trim();
        String fsalario = editTextSalario.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();

        int rg = (int) Double.parseDouble( frg );
        double salario = Double.parseDouble(fsalario);
        if(validateInputs( nome, frg, dataNasc, cargo, dataContrata, fsalario, desc )) {
            CollectionReference dbFuncionarios = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Funcionarios" ); //user auth

            Funcionario func = new Funcionario(
                    nome,
                    rg,
                    dataNasc,
                    cargo,
                    dataContrata,
                    salario,
                    desc
            );

            dbFuncionarios.add(func)
                    .addOnSuccessListener( documentReference -> Toast.makeText( this, "Funcionário adicionado!", Toast.LENGTH_LONG).show() )
                    .addOnFailureListener( e -> Toast.makeText( this, "Não foi possível adicionar funcionário!", Toast.LENGTH_LONG).show() );
            editTextNome.setText("");
            editTextRG.setText("");
            editTextDataNasc.setText("");
            editTextCargo.setText("");
            editTextDataContrata.setText("");
            editTextSalario.setText("");
            editTextDesc.setText("");
            Intent intent = new Intent( getBaseContext(), FuncionarioActivity.class );
            v.getContext().startActivity( intent );
            this.finish();
        }
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
