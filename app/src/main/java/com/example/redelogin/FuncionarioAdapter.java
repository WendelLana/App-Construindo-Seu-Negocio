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

public class FuncionarioAdapter extends RecyclerView.Adapter<FuncionarioAdapter.FuncionarioViewHolder>{

    private Context mCtx;
    private List<Funcionario> funcionarioList;
    private FirebaseFirestore db;
    private FirebaseUser fUser;

    FuncionarioAdapter(Context mCtx, List<Funcionario> funcionarioList){
        this.mCtx = mCtx;
        this.funcionarioList = funcionarioList;
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public FuncionarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new FuncionarioViewHolder(
          LayoutInflater.from(mCtx).inflate(R.layout.layout_funcio, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull FuncionarioViewHolder holder, int position){
        Funcionario funcionario = funcionarioList.get(position);

        holder.textViewNome.setText( funcionario.getNome() );// campos
        holder.textViewCargo.setText( funcionario.getCargo() );
        holder.textViewSalario.setText( String.format( new Locale("pt", "BR"), "%.2f", funcionario.getSalario()));

        holder.infoFunc.setOnClickListener( (View v) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( mCtx );
            builder.setCancelable( true );
            View showView = LayoutInflater.from(mCtx).inflate(R.layout.huma_newfuncio, null); //xml com todas as info do cliente
            builder.setView(showView);
            AlertDialog alertDialog = builder.create();
            //campos
            TextView RGFunc;
            EditText nomeFunc, dataNascFunc, cargoFunc, dataContratoFunc, salarioFunc, descFunc;
            RGFunc = showView.findViewById( R.id.RGFunc );
            nomeFunc = showView.findViewById( R.id.edNomeFunc );
            dataNascFunc = showView.findViewById( R.id.edNascFunc );
            cargoFunc = showView.findViewById( R.id.edCargoFunc );
            dataContratoFunc = showView.findViewById( R.id.edContratoFunc );
            salarioFunc = showView.findViewById( R.id.edSalarFunc );
            descFunc = showView.findViewById( R.id.edDescFunc );
            //valores do funcionário
            RGFunc.setText( String.format( Locale.ENGLISH, "RG: %d", funcionario.getRg()) );
            nomeFunc.setText( funcionario.getNome() );
            dataNascFunc.setText( funcionario.getDatanasc() );
            cargoFunc.setText( funcionario.getCargo() );
            dataContratoFunc.setText( funcionario.getDatacontrata() );
            salarioFunc.setText( String.valueOf(funcionario.getSalario()) );
            descFunc.setText( funcionario.getDesc() );
            //campo único - chave primária
            EditText edRgFunc = showView.findViewById( R.id.edRGFunc );
            TextView txtRgFunc = showView.findViewById( R.id.txtRGFunc );
            edRgFunc.setVisibility( View.GONE );
            txtRgFunc.setVisibility( View.GONE );

            Button saveFuncionario = showView.findViewById( R.id.saveFunc );
            Button deleteFuncionario = showView.findViewById( R.id.deleteFunc );
            deleteFuncionario.setVisibility( View.VISIBLE );

            CollectionReference listFunc = db.collection("Empresas").document(fUser.getUid()).collection( "Funcionarios" );
            Query Func = listFunc.whereEqualTo("rg", funcionario.getRg() );

            saveFuncionario.setOnClickListener( (View janela) -> Func.get().addOnCompleteListener( task -> {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        listFunc.document(document.getId()).update("nome", nomeFunc.getText().toString().trim());
                        listFunc.document(document.getId()).update("datanasc", dataNascFunc.getText().toString());
                        listFunc.document(document.getId()).update("cargo", cargoFunc.getText().toString().trim());
                        listFunc.document(document.getId()).update("datacontrata", dataContratoFunc.getText().toString().trim());
                        listFunc.document(document.getId()).update("salario", Double.parseDouble(salarioFunc.getText().toString().trim()));
                        listFunc.document(document.getId()).update("desc", descFunc.getText().toString().trim());
                    }
                }
                alertDialog.dismiss();
                notifyDataSetChanged();
            }) );

            deleteFuncionario.setOnClickListener( (View view) -> Func.get().addOnCompleteListener( task -> {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx)
                        .setTitle("Deseja realmente excluir?")
                        .setMessage("Ao deletar não será mais possível recuperar o cadastro do funcionário!")
                        .setNegativeButton( "Cancelar", (dialog, whichButton) -> dialog.dismiss() )
                        .setPositiveButton("Deletar", (dialog, whichButton) -> {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    listFunc.document(document.getId()).delete();
                                }
                                funcionarioList.remove(position);
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
        return funcionarioList.size();
    }

    class FuncionarioViewHolder extends RecyclerView.ViewHolder{

        Button infoFunc;
        TextView textViewNome, textViewCargo, textViewSalario; //declaração dos campos

        FuncionarioViewHolder(View itemView) {
            super(itemView);
            infoFunc = itemView.findViewById( R.id.funcInfo );
            textViewNome = itemView.findViewById(R.id.txtNome ); // buscas
            textViewSalario = itemView.findViewById(R.id.txtSalario );
            textViewCargo = itemView.findViewById(R.id.txtCargo);
        }
    }
}
