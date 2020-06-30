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

public class ProductCreate extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextId;
    private EditText editTextNome;
    private EditText editTextValor;
    private EditText editTextQuant;

    private FirebaseFirestore db;
    private FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.log_newprod );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        editTextId = findViewById( R.id.edIdProd );
        editTextNome = findViewById( R.id.edNomeProd );
        editTextValor = findViewById( R.id.edValorProd );
        editTextQuant = findViewById( R.id.edQuantProd );

        findViewById( R.id.saveProd ).setOnClickListener( this );
    }

    private boolean validateInputs(String id, String nome, String valor, String quant) {
        if(id.isEmpty()){
            editTextId.setError( "ID é obrigatório!" );
            editTextId.requestFocus();
            return false;
        }
        if(nome.isEmpty()){
            editTextNome.setError( "Nome é obrigatório!" );
            editTextNome.requestFocus();
            return false;
        }
        if(valor.isEmpty()){
            editTextValor.setError( "Valor é obrigatório!" );
            editTextValor.requestFocus();
            return false;
        }
        if(quant.isEmpty()){
            editTextQuant.setError( "Quantidade é obrigatório!" );
            editTextQuant.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        String nome = editTextNome.getText().toString().trim();
        String valor = editTextValor.getText().toString().trim();
        String quant = editTextQuant.getText().toString().trim();
        String pid = editTextId.getText().toString().trim();

        int id = (int) Double.parseDouble( pid );

        if(validateInputs( pid, nome, valor, quant )) {
            CollectionReference dbProducts = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Produtos" ); //user auth

            Product prod = new Product(
                    id,
                    nome,
                    valor,
                    quant
            );

            dbProducts.add(prod)
                    .addOnSuccessListener( documentReference -> Toast.makeText( this, "Produto adicionado!", Toast.LENGTH_LONG).show() )
                    .addOnFailureListener( e -> Toast.makeText( this, "Não foi possível adicionar o produto!", Toast.LENGTH_LONG).show() );
            editTextNome.setText( "" );
            editTextValor.setText( "" );
            editTextQuant.setText( "" );
            editTextId.setText( "" );
            Intent intent = new Intent( getBaseContext(), ProductActivity.class );
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
