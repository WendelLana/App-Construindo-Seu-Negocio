package com.example.redelogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ClienteActivity extends AppCompatActivity {
    private ClienteAdapter adapter;
    private List<Cliente> clienteList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.gere_listclient );

        progressBar = findViewById( R.id.progBar );

        Button newClie = findViewById( R.id.btNovoCli );
        newClie.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), ClienteCreate.class );
            v.getContext().startActivity( intent );
            this.finish();
        } );

        RecyclerView recyclerView = findViewById( R.id.recyLista );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        clienteList = new ArrayList<>();
        adapter = new ClienteAdapter( this, clienteList );

        recyclerView.setAdapter( adapter );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


        if(fUser != null) {

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Clientes" )
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {

                        progressBar.setVisibility( View.GONE );

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Cliente p = d.toObject( Cliente.class );
                                clienteList.add( p );
                            }

                            adapter.notifyDataSetChanged();

                        }
                    } )
                    .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );
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
