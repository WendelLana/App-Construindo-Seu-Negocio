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

public class FornecedorAdapter extends RecyclerView.Adapter<FornecedorAdapter.FornecedorViewHolder>{
    private Context mCtx;
    private List<Fornecedor> fornecedorList;
    private FirebaseFirestore db;
    private FirebaseUser fUser;

    FornecedorAdapter(Context mCtx, List<Fornecedor> fornecedorList){
        this.mCtx = mCtx;
        this.fornecedorList = fornecedorList;
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public FornecedorAdapter.FornecedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new FornecedorAdapter.FornecedorViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_fornecedor, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FornecedorAdapter.FornecedorViewHolder holder, int position){
        Fornecedor fornecedor = fornecedorList.get(position);

        holder.textViewNome.setText( fornecedor.getNome() ); // campos
        holder.textViewTel.setText( fornecedor.getTelefone() );

        holder.infoForn.setOnClickListener( (View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( mCtx );
            builder.setCancelable( true );
            View showView = LayoutInflater.from(mCtx).inflate(R.layout.gere_newfornecedor, null); //xml com todas as info do cliente
            builder.setView(showView);
            AlertDialog alertDialog = builder.create();

            TextView CNPJForn;
            EditText nomeForn, telForn;
            CNPJForn = showView.findViewById( R.id.CNPJForn );
            nomeForn = showView.findViewById( R.id.edNomeForn );
            telForn = showView.findViewById( R.id.edTelForn );
            CNPJForn.setText( String.format( Locale.ENGLISH, "CPF: %d", fornecedor.getCnpj()) );
            nomeForn.setText( fornecedor.getNome() );
            telForn.setText( fornecedor.getTelefone() );

            EditText edCnpjForn = showView.findViewById( R.id.edCNPJForn );
            TextView txtCnpjForn = showView.findViewById( R.id.txtCNPJForn );
            edCnpjForn.setVisibility( View.GONE );
            txtCnpjForn.setVisibility( View.GONE );

            Button saveFornecedor = showView.findViewById( R.id.saveForn );
            Button deleteFornecedor = showView.findViewById( R.id.deleteForn );
            deleteFornecedor.setVisibility( View.VISIBLE );

            CollectionReference listForn = db.collection("Empresas").document(fUser.getUid()).collection( "Fornecedores" );
            Query Fornecedor = listForn.whereEqualTo("cnpj", fornecedor.getCnpj() );

            saveFornecedor.setOnClickListener( (View janela) -> Fornecedor.get().addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        listForn.document(document.getId()).update("nome", nomeForn.getText().toString().trim());
                        listForn.document(document.getId()).update("telefone", telForn.getText().toString());
                    }
                }
                alertDialog.dismiss();
                notifyDataSetChanged();
            }) );

            deleteFornecedor.setOnClickListener( (View view) -> Fornecedor.get().addOnCompleteListener( task -> {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx)
                        .setTitle("Deseja realmente excluir?")
                        .setMessage("Ao deletar não será mais possível recuperar o cadastro do fornecedor!")
                        .setNegativeButton( "Cancelar", (dialog, whichButton) -> dialog.dismiss() )
                        .setPositiveButton("Deletar", (dialog, whichButton) -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    listForn.document(document.getId()).delete();
                                }
                                fornecedorList.remove(position);
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
        return fornecedorList.size();
    }

    class FornecedorViewHolder extends RecyclerView.ViewHolder{

        Button infoForn;
        TextView textViewNome, textViewTel; //declaração dos campos

        FornecedorViewHolder(View itemView) {
            super(itemView);
            infoForn = itemView.findViewById( R.id.fornInfo );
            textViewNome = itemView.findViewById(R.id.fornNome ); // buscas
            textViewTel = itemView.findViewById(R.id.fornTel);
        }
    }
}
