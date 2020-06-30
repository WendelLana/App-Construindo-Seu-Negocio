package com.example.redelogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProdutoViewHolder> {

    private Context mCtx;
    private List<Product> productList;
    private FirebaseFirestore db;
    private FirebaseUser fUser;

    ProductAdapter(Context mCtx, List<Product> productList){
        this.mCtx = mCtx;
        this.productList = productList;
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ProductAdapter.ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ProductAdapter.ProdutoViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_prod, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProdutoViewHolder holder, int position){
        Product product = productList.get(position);
        String quant = "Qt: ";
        holder.textViewNome.setText( product.getNome() ); // campos
        holder.textViewQuant.setText( quant.concat(product.getQuantidade() ));
        holder.textViewValor.setText( "R$".concat( product.getValor() ));

        holder.infoProd.setOnClickListener( (View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( mCtx );
            builder.setCancelable( true );
            View showView = LayoutInflater.from(mCtx).inflate(R.layout.log_newprod, null); //xml com todas as info do cliente
            builder.setView(showView);
            AlertDialog alertDialog = builder.create();

            TextView IDProd;
            EditText nomeProd, quantProd, valProd;
            IDProd = showView.findViewById( R.id.IDProd );
            nomeProd = showView.findViewById( R.id.edNomeProd );
            quantProd = showView.findViewById( R.id.edQuantProd );
            valProd = showView.findViewById( R.id.edValorProd );
            IDProd.setText( String.format( Locale.ENGLISH, "ID: %d", product.getId()) );
            nomeProd.setText( product.getNome() );
            quantProd.setText( product.getQuantidade() );
            valProd.setText( product.getValor() );

            EditText edIdProd = showView.findViewById( R.id.edIdProd );
            TextView txtIdProd = showView.findViewById( R.id.txtIdProd );
            edIdProd.setVisibility( View.GONE );
            txtIdProd.setVisibility( View.GONE );

            Button saveProduto = showView.findViewById( R.id.saveProd );
            Button deleteProduto = showView.findViewById( R.id.deleteProd );
            deleteProduto.setVisibility( View.VISIBLE );

            CollectionReference listProd = db.collection("Empresas").document(fUser.getUid()).collection( "Produtos" );
            Query Produto = listProd.whereEqualTo("id", product.getId() );

            saveProduto.setOnClickListener( (View janela) -> Produto.get().addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        listProd.document(document.getId()).update("nome", nomeProd.getText().toString().trim());
                        listProd.document(document.getId()).update("quantidade", quantProd.getText().toString());
                        listProd.document(document.getId()).update("valor", valProd.getText().toString());
                    }
                }
                alertDialog.dismiss();
                notifyDataSetChanged();
            }) );

            deleteProduto.setOnClickListener( (View view) -> Produto.get().addOnCompleteListener( task -> {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx)
                        .setTitle("Deseja realmente excluir?")
                        .setMessage("Ao deletar não será mais possível recuperar o cadastro do produto!")
                        .setNegativeButton( "Cancelar", (dialog, whichButton) -> dialog.dismiss() )
                        .setPositiveButton("Deletar", (dialog, whichButton) -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    listProd.document(document.getId()).delete();
                                }
                                productList.remove(position);
                            }
                            dialog.dismiss();
                            alertDialog.dismiss();
                            notifyDataSetChanged();
                        } );
                AlertDialog deleteDialog = alertDialogBuilder.create();
                deleteDialog.show();
            } ) );
            alertDialog.show();
        } );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProdutoViewHolder extends RecyclerView.ViewHolder{

        Button infoProd;
        TextView textViewNome, textViewQuant, textViewValor; //declaração dos campos

        ProdutoViewHolder(View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.txtNome ); // buscas
            textViewQuant = itemView.findViewById(R.id.txtQuant );
            textViewValor = itemView.findViewById(R.id.txtValor);
            infoProd = itemView.findViewById( R.id.prodInfo );
        }
    }
}
