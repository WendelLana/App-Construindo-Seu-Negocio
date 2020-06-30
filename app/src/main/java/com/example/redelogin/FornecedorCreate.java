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

public class FornecedorCreate extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextNome, editTextCNPJ, editTextTel;

    private FirebaseFirestore db;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.gere_newfornecedor );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        editTextCNPJ = findViewById( R.id.edCNPJForn );
        editTextNome = findViewById( R.id.edNomeForn );
        editTextTel = findViewById( R.id.edTelForn );

        findViewById( R.id.saveForn ).setOnClickListener( this );
    }

    private boolean validateInputs(String cnpj, String nome) {
        if(cnpj.length() < 14){
            editTextCNPJ.setError( "CNPJ é obrigatório!" );
            editTextCNPJ.requestFocus();
            return false;
        }
        if(nome.isEmpty()){
            editTextNome.setError( "Nome é obrigatório!" );
            editTextNome.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String fcnpj = editTextCNPJ.getText().toString().trim();
        String nome = editTextNome.getText().toString().trim();
        String tel = editTextTel.getText().toString().trim();

        int cnpj = (int) Double.parseDouble( fcnpj );

        if(validateInputs( fcnpj, nome )) {
            CollectionReference dbFornecedores = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Fornecedores" ); //user auth

            Fornecedor forn = new Fornecedor(
                    cnpj,
                    nome,
                    tel
            );

            dbFornecedores.add(forn)
                    .addOnSuccessListener( documentReference -> Toast.makeText( this, "Fornecedor adicionado!", Toast.LENGTH_LONG).show() )
                    .addOnFailureListener( e -> Toast.makeText( this, "Não foi possível adicionar o fornecedor!", Toast.LENGTH_LONG).show() );
            editTextCNPJ.setText( "" );
            editTextNome.setText( "" );
            editTextTel.setText( "" );
            Intent intent = new Intent( getBaseContext(), FornecedorActivity.class );
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
