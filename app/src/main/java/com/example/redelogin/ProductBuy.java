package com.example.redelogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProductBuy {

    private AlertDialog alertDialog;

    private HashMap<Integer,String> spinnerMap;
    private Spinner listProduct;
    private EditText quantField, valField;


    private FirebaseFirestore db;
    private FirebaseUser fUser;

    void Buy(Context context){
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        builder.setCancelable( true );
        View addView = LayoutInflater.from(context).inflate(R.layout.product_buy, null);

        quantField = addView.findViewById( R.id.buyProdQuant );
        valField = addView.findViewById( R.id.buyProdValor );

        listProduct = addView.findViewById( R.id.buyProductList );
        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Produtos" )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        String[] spinnerArray = new String[queryDocumentSnapshots.size()];
                        spinnerMap = new HashMap<>();

                        int i = 0;

                        for (DocumentSnapshot d : list) {
                            spinnerMap.put(i, d.getId());
                            spinnerArray[i] = d.get("nome").toString();
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        listProduct.setAdapter(adapter);
                    }
                } )
                .addOnFailureListener( e -> Toast.makeText( context, e.getMessage(), Toast.LENGTH_LONG ).show() );


        Button addSell = addView.findViewById( R.id.buyAdd );

        addSell.setOnClickListener( (View v) -> {
            String quantProd = quantField.getText().toString().trim();
            String valProd = valField.getText().toString().trim();
            if (listProduct.getSelectedItem() != null) {
                String nameProd = listProduct.getSelectedItem().toString();
                String idProd = spinnerMap.get( listProduct.getSelectedItemPosition() );
                if (validateInputs( nameProd.trim(), quantProd, valProd )) {
                    Date data = Calendar.getInstance().getTime();

                    DocumentReference prod = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Produtos" ).document(idProd);

                    prod.get().addOnSuccessListener( (DocumentSnapshot d) -> {
                        final String quant = d.get( "quantidade" ).toString();
                        int total = Integer.parseInt( quant ) + Integer.parseInt( quantProd );
                            SaveFluxo( data, nameProd, quantProd, valProd, context);
                            prod.update( "quantidade", String.valueOf(total) );
                    } );
                } else {
                    Toast.makeText( context, "Todos os campos são obrigatórios!", Toast.LENGTH_LONG ).show();
                }
            } else {
                Toast.makeText( context, "Por favor, cadastre um produto primeiro!", Toast.LENGTH_LONG ).show();
            }
        });

        builder.setView( addView );
        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean validateInputs(String produto, String quant, String valor) {
        if(produto.isEmpty()){
            listProduct.requestFocus();
            return false;
        }

        if(quant.isEmpty() || quant.equals( "0" )){
            quantField.setError( "Quantidade é obrigatória!" );
            quantField.requestFocus();
            return false;
        }

        if(valor.isEmpty()){
            valField.setError( "Valor é obrigatório!" );
            valField.requestFocus();
            return false;
        }
        return true;
    }

    private void SaveFluxo(Date data, String produto, String qtd, String valor, Context context) {

        CollectionReference dbFluxo = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Saida" ); //user auth

        Saida saida = new Saida(
                data,
                produto,
                qtd,
                valor,
                "Custo Variável"
        );

        dbFluxo.add(saida)
                .addOnSuccessListener( documentReference -> Toast.makeText( context, "Compra adicionada!", Toast.LENGTH_LONG).show() )
                .addOnFailureListener( e -> Toast.makeText( context, "Não foi possível adicionar a compra!", Toast.LENGTH_LONG).show() );
        alertDialog.dismiss();
    }
}
