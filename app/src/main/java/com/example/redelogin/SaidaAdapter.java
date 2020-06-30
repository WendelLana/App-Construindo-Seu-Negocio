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

public class SaidaAdapter extends RecyclerView.Adapter<SaidaAdapter.SaidaViewHolder> {

    private Context mCtx;
    private List<Saida> saidaList;

    public SaidaAdapter(Context mCtx, List<Saida> saidaList){
        this.mCtx = mCtx;
        this.saidaList = saidaList;
    }

    @NonNull
    @Override
    public SaidaAdapter.SaidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new SaidaAdapter.SaidaViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_fluxo, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SaidaAdapter.SaidaViewHolder holder, int position){
        Saida saida = saidaList.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy", new Locale( "pt","BR") );
        SimpleDateFormat hf = new SimpleDateFormat( "HH:mm ", new Locale( "pt","BR") );
        holder.textViewNome.setText( saida.getNome() ); // campos
        holder.textViewValor.setText( String.format( Locale.ENGLISH, "R$ %.2f", Double.parseDouble( saida.getValor()) ));
        holder.textViewData.setText( dateFormat.format(saida.getData()) );
        holder.textViewHora.setText( hf.format( saida.getData() ) );
        holder.textViewTipo.setText( saida.getTipo() );
    }

    @Override
    public int getItemCount() {
        return saidaList.size();
    }

    class SaidaViewHolder extends RecyclerView.ViewHolder{

        TextView textViewNome, textViewValor, textViewQuant, textViewData, textViewHora, textViewTipo; //declaração dos campos

        SaidaViewHolder(View itemView) {
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
