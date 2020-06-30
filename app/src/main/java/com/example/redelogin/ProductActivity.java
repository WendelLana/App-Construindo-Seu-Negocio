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

public class ProductActivity extends AppCompatActivity {
    private ProductAdapter adapter;
    private List<Product> productList;
    private ProgressBar progressBar;
    private Button newProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.log_listprod );

        progressBar = findViewById( R.id.progBar );

        newProd = findViewById( R.id.btNovoProd );
        newProd.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), ProductCreate.class );
            v.getContext().startActivity( intent );
            this.finish();
        } );

        RecyclerView recyclerView = findViewById( R.id.recyLista );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        productList = new ArrayList<>();
        adapter = new ProductAdapter( this, productList );

        recyclerView.setAdapter( adapter );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();


        if(fUser != null) {

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Produtos" )
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {

                        progressBar.setVisibility( View.GONE );

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Product p = d.toObject( Product.class );
                                productList.add( p );
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
