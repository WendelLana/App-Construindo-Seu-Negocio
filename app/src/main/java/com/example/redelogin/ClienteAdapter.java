package com.example.redelogin;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {

    private Context mCtx;
    private List<Cliente> clienteList;
    private FirebaseFirestore db;
    private FirebaseUser fUser;

    ClienteAdapter(Context mCtx, List<Cliente> clienteList){
        this.mCtx = mCtx;
        this.clienteList = clienteList;
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ClienteAdapter.ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new ClienteAdapter.ClienteViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_client, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteAdapter.ClienteViewHolder holder, int position){
        Cliente cliente = clienteList.get(position);

        holder.textViewNome.setText( cliente.getNome() ); // campos
        holder.textViewTel.setText( cliente.getTelefone() );

        holder.infoCliente.setOnClickListener( (View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( mCtx );
            builder.setCancelable( true );
            View showView = LayoutInflater.from(mCtx).inflate(R.layout.gere_newclient, null); //xml com todas as info do cliente
            builder.setView(showView);
            AlertDialog alertDialog = builder.create();

            TextView CPFCli;
            EditText nomeCli, telCli, adendoCli;
            CPFCli = showView.findViewById( R.id.CPFCli );
            nomeCli = showView.findViewById( R.id.edNomeCli );
            telCli = showView.findViewById( R.id.edTelCli );
            adendoCli = showView.findViewById( R.id.edDescCli );
            CPFCli.setText( String.format( Locale.ENGLISH, "CPF: %d", cliente.getCpf()) );
            nomeCli.setText( cliente.getNome() );
            telCli.setText( cliente.getTelefone() );
            adendoCli.setText( cliente.getAdendo() );

            EditText edCpfCli = showView.findViewById( R.id.edCpfCli );
            TextView txtCpfCli = showView.findViewById( R.id.txtCpfCli );
            edCpfCli.setVisibility( View.GONE );
            txtCpfCli.setVisibility( View.GONE );

            Button saveCliente = showView.findViewById( R.id.saveCli );
            Button deleteCliente = showView.findViewById( R.id.deleteCli );
            deleteCliente.setVisibility( View.VISIBLE );

            CollectionReference listClient = db.collection("Empresas").document(fUser.getUid()).collection( "Clientes" );
            Query Client = listClient.whereEqualTo("cpf", cliente.getCpf() );

            saveCliente.setOnClickListener( (View janela) -> Client.get().addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        listClient.document(document.getId()).update("nome", nomeCli.getText().toString().trim());
                        listClient.document(document.getId()).update("telefone", telCli.getText().toString());
                        listClient.document(document.getId()).update("adendo", adendoCli.getText().toString().trim());
                    }
                }
                alertDialog.dismiss();
                notifyDataSetChanged();
            }) );

            deleteCliente.setOnClickListener( (View view) -> Client.get().addOnCompleteListener( task -> {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx)
                    .setTitle("Deseja realmente excluir?")
                    .setMessage("Ao deletar não será mais possível recuperar o cadastro do cliente!")
                    .setNegativeButton( "Cancelar", (dialog, whichButton) -> dialog.dismiss() )
                    .setPositiveButton("Deletar", (dialog, whichButton) -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                listClient.document(document.getId()).delete();
                            }
                            clienteList.remove(position);
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
        return clienteList.size();
    }

    class ClienteViewHolder extends RecyclerView.ViewHolder{

        Button infoCliente;
        TextView textViewNome, textViewTel; //declaração dos campos

        ClienteViewHolder(View itemView) {
            super(itemView);
            infoCliente = itemView.findViewById( R.id.cliInfo );
            textViewNome = itemView.findViewById(R.id.cliNome ); // buscas
            textViewTel = itemView.findViewById(R.id.cliTel);
        }
    }
}
