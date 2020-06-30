package com.example.redelogin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class FluxoEntrada {
    private AlertDialog alertDialog;

    private Spinner listName;
    private TextView dateField, timeField;
    private EditText valField;
    private ImageButton dateButton, timeButton;
    private View addView;
    private Calendar date;
    private SimpleDateFormat datef, hformate;


    private FirebaseFirestore db;
    private FirebaseUser fUser;

    void Gain(Context context){
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        builder.setCancelable( true );
        addView = LayoutInflater.from(context).inflate(R.layout.fluxo_register, null);
        addView.setBackgroundColor( Color.parseColor("#8BC34A") );
        date = Calendar.getInstance();
        datef = new SimpleDateFormat( "dd-MM-yyyy ", new Locale( "pt","BR") );
        hformate = new SimpleDateFormat( "HH:mm ", new Locale( "pt","BR") );

        dateField = addView.findViewById( R.id.fluxoDate );
        timeField = addView.findViewById( R.id.fluxoTime );
        dateField.setText(datef.format( date.getTime() ));
        timeField.setText(hformate.format( date.getTime() ));

        valField = addView.findViewById( R.id.fluxoValor );
        timeButton = addView.findViewById( R.id.fluxoTimeButton );
        dateButton = addView.findViewById( R.id.fluxoDateButton );

        timeButton.setOnClickListener( View -> changeHour() );
        timeField.setOnClickListener( View -> changeHour() );

        dateButton.setOnClickListener( View -> changeDate() );
        dateField.setOnClickListener( View -> changeDate() );

        listName = addView.findViewById( R.id.fluxoName );

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( context, R.array.gainchoice, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listName.setAdapter(adapter);


        Button addPay = addView.findViewById( R.id.fluxoAdd );

        addPay.setOnClickListener( (View v) -> {
            String name = listName.getSelectedItem().toString();
            String valor = valField.getText().toString();
            String tipo;
            if (name.equals("Investimento")) { tipo = "Investimento"; }else { tipo = "Receita Financeira";}
            if (validateInputs( name,valor )) SaveFluxo( date.getTime(), name, valor, tipo, context);

        });

        builder.setView( addView );
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void changeHour(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minuts = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog( addView.getContext(), R.style.Theme_AppCompat_Dialog,
                (timePicker, hoursOfDay, minute) -> {
                    date.set(Calendar.HOUR_OF_DAY, hoursOfDay);
                    date.set(Calendar.MINUTE, minute);
                    date.setTimeZone( TimeZone.getDefault() );
                    String entrada_Time = hformate.format(date.getTime());
                    timeField.setText(entrada_Time);
                }, hours, minuts, true );
        timePickerDialog.show();
    }

    private void changeDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog( addView.getContext(), R.style.Theme_AppCompat_DayNight_Dialog,
                (view, startYear, starthMonth, startDay) -> {
                    date.set(Calendar.YEAR, startYear);
                    date.set(Calendar.MONTH, starthMonth);
                    date.set(Calendar.DAY_OF_MONTH, startDay);
                    String entrada_Date = datef.format(date.getTime());
                    dateField.setText(entrada_Date);
                }, year, month, day );
        datePickerDialog.show();
    }

    private boolean validateInputs(String name, String valor) {
        if(name.isEmpty()){
            listName.requestFocus();
            return false;
        }

        if(valor.isEmpty() || valor.equals( "0" )){
            valField.setError( "Valor é inválida!" );
            valField.requestFocus();
            return false;
        }

        return true;
    }

    private void SaveFluxo(Date data, String nome, String valor, String tipo, Context context) {

        CollectionReference dbSaida = db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Entrada" ); //user auth

        Entrada entrada = new Entrada(
                data,
                nome,
                "1",
                valor,
                tipo
        );

        dbSaida.add(entrada)
                .addOnSuccessListener( documentReference -> Toast.makeText( context, "Registro salvo!", Toast.LENGTH_LONG).show() )
                .addOnFailureListener( e -> Toast.makeText( context, "Não foi possível adicionar o registro!", Toast.LENGTH_LONG).show() );
        alertDialog.dismiss();
    }
}
