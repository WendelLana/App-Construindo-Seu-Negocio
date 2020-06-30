package com.example.redelogin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class EntradaAdapter extends RecyclerView.Adapter<EntradaAdapter.EntradaViewHolder>{

    private Context mCtx;
    private List<Entrada> entradaList;

    public EntradaAdapter(Context mCtx, List<Entrada> entradaList){
        this.mCtx = mCtx;
        this.entradaList = entradaList;
    }

    @NonNull
    @Override
    public EntradaAdapter.EntradaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new EntradaAdapter.EntradaViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_fluxo, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EntradaAdapter.EntradaViewHolder holder, int position){
        Entrada entrada = entradaList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy", new Locale( "pt","BR") );
        SimpleDateFormat hf = new SimpleDateFormat( "HH:mm ", new Locale( "pt","BR") );
        holder.textViewNome.setText( entrada.getNome() ); // campos
        holder.textViewValor.setText( String.format( Locale.ENGLISH, "R$ %.2f", Double.parseDouble( entrada.getValor()) ));
        if (!entrada.getQuantidade().equals("1")) holder.textViewQuant.setText( "qtd: ".concat(entrada.getQuantidade()) );
        holder.textViewData.setText( dateFormat.format(entrada.getData()) );
        holder.textViewHora.setText( hf.format( entrada.getData() ) );
        holder.textViewTipo.setText( entrada.getTipo() );

    }

    @Override
    public int getItemCount() {
        return entradaList.size();
    }

    class EntradaViewHolder extends RecyclerView.ViewHolder{

        TextView textViewNome, textViewValor, textViewQuant, textViewData, textViewHora, textViewTipo; //declaração dos campos

        EntradaViewHolder(View itemView) {
            super(itemView);
            textViewNome = itemView.findViewById(R.id.fluxoNome ); // buscas
            textViewValor = itemView.findViewById(R.id.fluxoValor);
            textViewQuant = itemView.findViewById(R.id.fluxoQuant);
            textViewData = itemView.findViewById(R.id.fluxoData);
            textViewHora = itemView.findViewById(R.id.fluxoHora);
            textViewTipo = itemView.findViewById(R.id.fluxoTipo);
        }
    }
}
