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

public class ClienteCreate extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextCPF, editTextTel, editTextDesc, editTextNome;

    private FirebaseFirestore db;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.gere_newclient );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        editTextNome = findViewById( R.id.edNomeCli );
        editTextDesc = findViewById( R.id.edDescCli );
        editTextTel = findViewById( R.id.edTelCli );
        editTextCPF = findViewById( R.id.edCpfCli );

        findViewById( R.id.saveCli ).setOnClickListener( this );
    }

    private boolean validateInputs(String cpf, String nome) {
        if(cpf.isEmpty()){
            editTextCPF.setError( "CPF é obrigatório!" );
            editTextCPF.requestFocus();
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
        String nome = editTextNome.getText().toString().trim();
        String tel = editTextTel.getText().toString().trim();
        String desc = editTextDesc.getText().toString().trim();
        String ccpf = editTextCPF.getText().toString().trim();
        int cpf = (int) Double.parseDouble(ccpf);

        if(validateInputs( ccpf, nome )) {
            CollectionReference dbClientes = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Clientes" ); //user auth

            Cliente cli = new Cliente(
                    nome,
                    tel,
                    desc,
                    cpf
            );

            dbClientes.add(cli)
                    .addOnSuccessListener( documentReference -> {
                        Toast.makeText( this, "Cliente adicionado!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent( getBaseContext(), ClienteActivity.class );
                        v.getContext().startActivity( intent );
                        this.finish();
                    } )
                    .addOnFailureListener( e -> Toast.makeText( this, "Não foi possível adicionar o cliente!", Toast.LENGTH_LONG).show() );
            editTextNome.setText( "" );
            editTextTel.setText( "" );
            editTextDesc.setText( "" );
            editTextCPF.setText( "" );
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
