package com.example.redelogin;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context mCtx;
    private List<Events> eventList;
    private FirebaseFirestore db;
    private FirebaseUser fUser;

    EventAdapter(Context mCtx, List<Events> events){
        this.mCtx = mCtx;
        this.eventList = events;
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        return new EventAdapter.EventViewHolder(
                LayoutInflater.from(mCtx).inflate(R.layout.layout_event, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapter.EventViewHolder holder, int position){
        Events event = eventList.get(position);
        holder.textViewDate.setText( event.getData() ); // campos
        holder.textViewName.setText( event.getNome() );
        holder.textViewTime.setText( event.getTempo() );
        holder.buttonDelete.setOnClickListener( (View v) -> {

            CollectionReference listEvents = db.collection("Empresas").document(fUser.getUid()).collection( "Eventos" );
            Query deleteEvent = listEvents.whereEqualTo("data", event.getData()  ).whereEqualTo( "tempo", event.getTempo() );

            deleteEvent.get().addOnCompleteListener( task -> {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx)
                    .setTitle("Deseja realmente excluir?")
                    .setMessage("Ao deletar não será mais possível recuperar o evento!")
                    .setNegativeButton( "Cancelar", (dialog, whichButton) -> dialog.dismiss() )
                    .setPositiveButton("Deletar", (dialog, whichButton) -> {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                listEvents.document(document.getId()).delete();
                            }
                            eventList.remove(position);
                        }
                        dialog.dismiss();
                        notifyDataSetChanged();
                    } );
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } );
        } );
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder{

        TextView textViewDate, textViewName, textViewTime; //declaração dos campos
        Button buttonDelete;

        EventViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.rowEventDate); // buscas
            textViewName = itemView.findViewById(R.id.rowEventName);
            textViewTime = itemView.findViewById(R.id.rowEventTime);
            buttonDelete = itemView.findViewById(R.id.rowEventDelete);
        }
    }
}
