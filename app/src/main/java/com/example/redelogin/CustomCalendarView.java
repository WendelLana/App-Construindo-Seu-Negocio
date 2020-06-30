package com.example.redelogin;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import javax.annotation.Nullable;

public class CustomCalendarView extends LinearLayout {

    ImageButton NextButton, PreviousButton;
    TextView CurrentDate, gereComunicado, EventTime;
    GridView gridView;
    RecyclerView recyclerView;
    View addView, showView;
    private static final int MAX_CALENDAR_DAYS = 42;
    Calendar calendar = Calendar.getInstance( Locale.ENGLISH );
    Context context;
    SimpleDateFormat formatMonthYear = new SimpleDateFormat( "MMMM yyyy", new Locale( "pt","BR") );
    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd", new Locale( "pt","BR") );
    SimpleDateFormat monthFormat = new SimpleDateFormat( "MM", new Locale( "pt","BR") );
    SimpleDateFormat yearFormat = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();

    EditText EventName;
    FirebaseFirestore db;
    FirebaseUser fUser;

    ProgressBar progressEvents;

    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        InitializeLayout();
        SetUpCalendar();

        PreviousButton.setOnClickListener( View -> {
            calendar.add( Calendar.MONTH, -1 );
            SetUpCalendar();
        });

        NextButton.setOnClickListener( View -> {
            calendar.add( Calendar.MONTH, 1 );
            SetUpCalendar();
        });

        gridView.setOnItemClickListener( (parent, view, position, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder( context );
            builder.setCancelable( true );
            addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gere_newevent, null);
            EventName = addView.findViewById( R.id.calenNameEvent );
            EventTime = addView.findViewById( R.id.calenEventTime );
            ImageButton SetTime = addView.findViewById( R.id.calenAddEventTime );
            Button AddEvent = addView.findViewById( R.id.calenAddEvent );
            Button ListEvent = addView.findViewById( R.id.calenListEvent );

            TextView currentDate = addView.findViewById( R.id.calenDateEvent );

            SetTime.setOnClickListener( View -> {
                changeHour();
            });

            EventTime.setOnClickListener( View -> {
                changeHour();
            });

            String date = String.valueOf( dateFormat.format( dates.get(position) ));
            String month =  String.valueOf( monthFormat.format( dates.get(position) ));
            String year =  String.valueOf( yearFormat.format( dates.get( position ) ));

            SimpleDateFormat datePortuguese = new SimpleDateFormat( "dd/MM/yyyy", new Locale ("pt", "br") );
            currentDate.setText( datePortuguese.format(dates.get( position )) );

            AddEvent.setOnClickListener( View -> {

                if(validateInputs(EventName.getText().toString())){
                        SaveEvent( EventName.getText().toString().trim(), EventTime.getText().toString(), date, month, year );
                } else{
                        Toast.makeText( context, "Nome do evento é obrigatório!", Toast.LENGTH_LONG).show();
                }
            });

            ListEvent.setOnClickListener( View -> {
                alertDialog.dismiss();
                listEvents( parent, position );
            } );

            builder.setView( addView );
            alertDialog = builder.create();
            alertDialog.show();
            alertDialog.setOnCancelListener( dialogInterface -> SetUpCalendar() );
        });

        gridView.setOnItemLongClickListener( (parent, view, position, id) -> {
            listEvents( parent, position );
            return true;
        } );
    }

    private void changeHour(){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minuts = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog( addView.getContext(), R.style.Theme_AppCompat_Dialog,
                (timePicker, hoursOfDay, minute) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, hoursOfDay);
                    c.set(Calendar.MINUTE, minute);
                    c.setTimeZone( TimeZone.getDefault() );
                    SimpleDateFormat hformate = new SimpleDateFormat( "HH:mm ", new Locale( "pt","BR") );
                    String event_Time = hformate.format(c.getTime());
                    EventTime.setText(event_Time);
                }, hours, minuts, true );
        timePickerDialog.show();
    }

    private void listEvents(AdapterView parent, int position) {
        String date = dateFormat.format( dates.get(position) );

        AlertDialog.Builder builder = new AlertDialog.Builder( context );
        builder.setCancelable( true );
        showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.gere_listevent, null);
        progressEvents = showView.findViewById( R.id.progBar );

        recyclerView = showView.findViewById( R.id.eventsRecyView );
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( showView.getContext() );
        recyclerView.setLayoutManager( layoutManager );
        recyclerView.setHasFixedSize( true );
        CollectEventByDate(date);

        builder.setView(showView);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnCancelListener( dialogInterface -> SetUpCalendar() );
        alertDialog.setOnDismissListener( dialogInterface -> SetUpCalendar() );
    }

    private void CollectEventByDate(String date){
        List<Events> arrayList = new ArrayList<>();
        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Eventos" ).whereEqualTo( "data", date )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                            Events p = d.toObject( Events.class );
                            arrayList.add( p );
                        }
                    }
                    progressEvents.setVisibility( View.GONE );
                    EventAdapter eventRecyclerAdapter = new EventAdapter( showView.getContext(), arrayList);
                    recyclerView.setAdapter( eventRecyclerAdapter );
                    eventRecyclerAdapter.notifyDataSetChanged();
                } );
    }

    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean validateInputs(String event) {
        if(event.isEmpty()){
            EventName.setError( "Nome do evento é obrigatório!" );
            EventName.requestFocus();
            return false;
        }
        return true;
    }

    private void SaveEvent(String nome, String tempo, String dia, String mes, String ano) {

        CollectionReference dbEvents = db.collection( "Empresas" ).document( Objects.requireNonNull( fUser ).getUid() ).collection( "Eventos" ); //user auth

        Events event = new Events(
                nome,
                tempo,
                dia,
                mes,
                ano
        );

        dbEvents.add(event)
                .addOnSuccessListener( documentReference -> {
                    Toast.makeText( this.getContext(), "Evento adicionado!", Toast.LENGTH_LONG).show();
                    SetUpCalendar();
                } )
                .addOnFailureListener( e -> Toast.makeText( this.getContext(), "Não foi possível adicionar o evento!", Toast.LENGTH_LONG).show() );
        alertDialog.dismiss();
    }

    private void InitializeLayout(){
        LayoutInflater inflater;
        inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View view = inflater.inflate( R.layout.layout_calendar, this );
        NextButton = view.findViewById( R.id.calendarNextBtn );
        PreviousButton = view.findViewById( R.id.calendarPreviousBtn );
        CurrentDate = view.findViewById( R.id.calendarDate );
        gridView = view.findViewById( R.id.calendarGrid );
        gereComunicado = view.findViewById( R.id.gereMessage );
    }

    private void SetUpCalendar(){
        String curenteDate = formatMonthYear.format(calendar.getTime());
        CurrentDate.setText(curenteDate);
        dates.clear();
        Calendar monthCalendar = (Calendar) calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int FirstDayofMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        monthCalendar.add(Calendar.DAY_OF_MONTH, -FirstDayofMonth);

        while (dates.size() < MAX_CALENDAR_DAYS) {
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        CollectEventsPerMonth(monthFormat.format(calendar.getTime()), yearFormat.format( calendar.getTime() ));
        countDayEvents();
    }

    private void countDayEvents(){
        List<Events> arrayList = new ArrayList<>();
        Calendar calend = Calendar.getInstance( Locale.ENGLISH );
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd", new Locale( "pt","BR") );
        String date = dateFormat.format( calend.getTime() );
        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Eventos" ).whereEqualTo( "data", date )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                            Events p = d.toObject( Events.class );
                            arrayList.add( p );
                        }
                        gereComunicado.setText( String.format( "Você tem %d compromisso(s) hoje.", arrayList.size() ));
                    }
                } );
    }

    private void CollectEventsPerMonth(String month, String year){
        eventsList.clear();
        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Eventos" ).whereEqualTo( "mes", month ).whereEqualTo( "ano", year )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                            Events p = d.toObject( Events.class );
                            eventsList.add( p );
                        }
                    }
                    myGridAdapter = new MyGridAdapter( context, dates, calendar, eventsList );
                    gridView.setAdapter( myGridAdapter );
                } );
    }
}
