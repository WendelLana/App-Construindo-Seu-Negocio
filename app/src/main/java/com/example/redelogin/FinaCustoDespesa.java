package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class FinaCustoDespesa extends AppCompatActivity {

    private SaidaAdapter adapterC, adapterD;
    private List<Saida> custoList;
    private List<Saida> despesaList;
    private ProgressBar progressCusto, progressDespesa;

    TextView custoText, despesaText;

    FirebaseUser fUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fina_custodespesa );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        listarCusto();
        listarDespesa();

        progressCusto = findViewById( R.id.custoProgBar );
        progressDespesa = findViewById( R.id.despesasProgBar );
        despesaText = findViewById( R.id.lbDespesasCD );
        custoText = findViewById( R.id.lbCustosCD );

        RecyclerView recyclerCusto = findViewById( R.id.custosRecyLista );
        RecyclerView recyclerDespesa = findViewById( R.id.despesasRecyLista );
        recyclerCusto.setHasFixedSize( true );
        recyclerCusto.setLayoutManager( new LinearLayoutManager( this ) );
        recyclerDespesa.setHasFixedSize( true );
        recyclerDespesa.setLayoutManager( new LinearLayoutManager( this ) );

        custoList = new ArrayList<>();
        despesaList = new ArrayList<>();
        adapterC = new SaidaAdapter( this, custoList );
        adapterD = new SaidaAdapter( this, despesaList );
        recyclerCusto.setAdapter( adapterC );
        recyclerDespesa.setAdapter( adapterD );

        Typeface custom_font = Typeface.createFromAsset( getAssets(), "fonts/Trocchi.ttf" );
        custoText.setTypeface( custom_font );
        despesaText.setTypeface( custom_font );
    }

    private void listarCusto() {
        if(fUser != null) {

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Saida" ).orderBy( "data", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {

                        progressCusto.setVisibility( View.GONE );

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Saida p = d.toObject( Saida.class );
                                if (p.getTipo().equals( "Custo Variável" )) custoList.add( p );
                            }
                            adapterC.notifyDataSetChanged();
                        }
                    } )

                    .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );
        }
    }
    private void listarDespesa() {
        if(fUser != null) {

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Saida" ).orderBy( "data", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {

                        progressDespesa.setVisibility( View.GONE );

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Saida p = d.toObject( Saida.class );
                                if (p.getTipo().equals( "Despesa Variável" )) despesaList.add( p );
                            }
                            adapterD.notifyDataSetChanged();
                        }
                    } )

                    .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );
        }
    }

    public void dialDespesas(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que são Despesas?");
        // set dialog message
        alertDialogBuilder
                .setMessage("São os gastos que não estão relacionados à produção do seu serviço ou produto! Podem ser divididos entre:\n\n" +
                        "Fixas/Variáveis: Seus valores variam ou não de acordo com o faturamento da empresa;")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
    public void dialCustos(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que são Custos?");
        // set dialog message
        alertDialogBuilder
                .setMessage("São os gastos relacionados à produção do seu produto ou serviço! Podem ser divididos entre:\n\n" +
                        "Fixos/Variáveis: seus valores variam ou não de acordo com produção;\n\n" +
                        "Indiretos/Diretos: seus valores afetam ou não diretamente no preço do produto;")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
