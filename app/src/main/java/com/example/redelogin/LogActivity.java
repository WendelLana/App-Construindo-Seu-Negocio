package com.example.redelogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class LogActivity extends AppCompatActivity {

    Button logGestao, btProd, btEstoque, estoSeg, logRever, infoImportant, btVenda, btCompra;
    TextView lbVendas, lbCompras, registre, txtVenda, txtComprar;
    FirebaseFirestore db;
    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        btEstoque = findViewById( R.id.log_estoque );
        btProd = findViewById( R.id.log_regprod );
        logGestao = findViewById( R.id.log_cadsuprimento );
        estoSeg = findViewById( R.id.log_gerirestoque );
        logRever = findViewById( R.id.log_logreversa );
        infoImportant = findViewById( R.id.log_informa );
        lbVendas = findViewById( R.id.lbVendas );
        lbCompras = findViewById( R.id.lbCompras );
        btVenda = findViewById( R.id.logVenda );
        btCompra = findViewById( R.id.logCompra );
        registre = findViewById( R.id.logRegistre );
        txtVenda = findViewById( R.id.logTxtVenda );
        txtComprar = findViewById( R.id.logTxtCompra );

        btVenda.setOnClickListener( View -> {
            ProductSell sell = new ProductSell();
            sell.Sell(this);
        } );
        btCompra.setOnClickListener( View -> {
            ProductBuy buy = new ProductBuy();
            buy.Buy(this);
        } );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        btVenda.setTypeface( custom_font );
        btCompra.setTypeface( custom_font );
        btEstoque.setTypeface(custom_font);
        btProd.setTypeface(custom_font);
        logGestao.setTypeface(custom_font);
        estoSeg.setTypeface(custom_font);
        logRever.setTypeface(custom_font);
        infoImportant.setTypeface(custom_font);
        lbVendas.setTypeface(custom_font);
        lbCompras.setTypeface(custom_font);
        registre.setTypeface(custom_font);
        txtVenda.setTypeface(custom_font);
        txtComprar.setTypeface(custom_font);

        btProd.setOnClickListener( ( View v ) -> {
            Intent intent = new Intent( getBaseContext(), ProductActivity.class );
            v.getContext().startActivity( intent );
        });
        logGestao.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), LogGestao.class );
            v.getContext().startActivity( intent );
        } );
        estoSeg.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), LogGerir.class );
            v.getContext().startActivity( intent );
        } );
        logRever.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), LogReversa.class );
            v.getContext().startActivity( intent );
        } );
        btEstoque.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), FinaFluxo.class );
            v.getContext().startActivity( intent );
        } );
        infoImportant.setOnClickListener( (View v) -> {
            Intent intent = new Intent( getBaseContext(), LogInfo.class );
            v.getContext().startActivity( intent );
        } );
        setComprar();
        setVendasAlta();
    }

    private void setComprar(){
        db.collection( "Empresas" ).document( fUser.getUid() ).collection("Produtos").orderBy("quantidade").limit( 1 )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                            txtComprar.setText( d.get( "nome" ).toString() );

                        }
                    }
                } );
    }
    private HashMap<String,Integer> quantProd;
    private void setVendasAlta(){
        db.collection( "Empresas" ).document( fUser.getUid() ).collection("Entrada")
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        int maxQuantValue = 0;
                        String maxQuant = "Nenhum";
                        for (DocumentSnapshot d : list) {
                            String fluxoProd = "";
                            if (d.get( "tipo" ).equals("Vendas")) fluxoProd = d.get( "nome" ).toString();
                            int fluxoQuant = Integer.parseInt(d.get( "quantidade" ).toString());

                            int oldQuantValue = 0;
                            try {
                                oldQuantValue = quantProd.get(fluxoProd);
                            } catch (Throwable ignored) {
                            }
                            int newQuantValue = oldQuantValue + fluxoQuant;
                            if (maxQuantValue < newQuantValue) {
                                maxQuantValue = newQuantValue;
                                maxQuant = fluxoProd;
                            } else if (maxQuantValue == newQuantValue){
                                maxQuant = maxQuant.concat( " " + fluxoProd );
                            }
                        }
                        txtVenda.setText(maxQuant);
                    }
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
