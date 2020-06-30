package com.example.redelogin;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FinaFluxo extends AppCompatActivity {

    GraphView graphView;
    LineGraphSeries entrada, saida;

    TextView fluxoTotal;

    private SaidaAdapter adapterS;
    private EntradaAdapter adapterE;
    private List<Saida> saidaList;
    private List<Entrada> entradaList;
    private ProgressBar progressBar;

    FirebaseUser fUser;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fina_fluxo );

        progressBar = findViewById( R.id.progBar );
        fluxoTotal = findViewById( R.id.fluxoTotal );
        Button saidaButton = findViewById( R.id.fluxoSaida );
        Button entradaButton = findViewById( R.id.fluxoEntrada );

        Typeface custom_font = Typeface.createFromAsset( getAssets(), "fonts/Trocchi.ttf" );
        fluxoTotal.setTypeface( custom_font );

        fluxoTotal.setText( String.format( Locale.ENGLISH, "Saldo Líquido: R$%.2f", FinaActivity.Tab1.lucroLiquido ) );

        graphView = findViewById( R.id.graph );
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        graphView.getViewport().setScalableY( true );
        graphView.getViewport().setXAxisBoundsManual( true );
        graphView.getViewport().setMinX( 1 );
        graphView.getViewport().setMaxX( 12 );
        graphView.setTitle( "Fluxo Anual (R$/mês)" );
        graphView.getLegendRenderer().setVisible( true );
        graphView.getLegendRenderer().setAlign( LegendRenderer.LegendAlign.TOP );
        graphView.getLegendRenderer().setWidth( width/5 );
        graphView.getLegendRenderer().setMargin( 10 );
        graphView.getLegendRenderer().setBackgroundColor( Color.parseColor( "#e4e4e4" ) );

        RecyclerView recyclerView = findViewById( R.id.fluxoRecyLista );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        saidaList = new ArrayList<>();
        entradaList = new ArrayList<>();
        adapterE = new EntradaAdapter( this, entradaList );
        adapterS = new SaidaAdapter( this, saidaList );
        recyclerView.setAdapter( adapterE );

        saidaButton.setOnClickListener( View -> recyclerView.setAdapter( adapterS ) );
        entradaButton.setOnClickListener( View -> recyclerView.setAdapter( adapterE ) );


        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        listarSaida();
        listarEntrada();
    }

    private void listarSaida() {
        if(fUser != null) {

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Saida" ).orderBy( "data", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {

                        double[] totalMonthSaida = new double[12];

                        progressBar.setVisibility( View.GONE );

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Saida p = d.toObject( Saida.class );
                                saidaList.add( p );
                                SimpleDateFormat fYear = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
                                SimpleDateFormat fMonth = new SimpleDateFormat( "MM", Locale.ENGLISH );

                                String currentYear = fYear.format( Calendar.getInstance().getTime() );
                                long dataFirebase = Long.parseLong( d.get( "data" ).toString().split( "," )[0].split( "=" )[1] ) * 1000;
                                String fluxoYear = fYear.format( dataFirebase );
                                int fluxoMonth = Integer.parseInt( fMonth.format( dataFirebase ) );

                                if (currentYear.equals( fluxoYear )) {
                                    for (int i = 0; i < 12; i++) {
                                        if (fluxoMonth == i + 1) {
                                            totalMonthSaida[i] += Double.parseDouble( d.get( "valor" ).toString() );
                                        }
                                    }
                                }
                            }
                            DataPoint[] dataSaida = new DataPoint[12];

                            for (int i = 0; i < 12; i++) {
                                dataSaida[i] = new DataPoint( i + 1, totalMonthSaida[i] );
                            }

                            saida = new LineGraphSeries<>( dataSaida );

                            saida.setColor( Color.parseColor( "#FC4040" ) );
                            saida.setDrawDataPoints( true );
                            saida.setDataPointsRadius( 10 );
                            saida.setThickness( 5 );
                            saida.setTitle( "Saída" );

                            graphView.addSeries( saida );
                            adapterS.notifyDataSetChanged();

                        }
                    } )

                    .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );
        }
    }

    private void listarEntrada(){
        if(fUser != null) {

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Entrada" ).orderBy( "data", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {
                        double[] totalMonthEntrada = new double[12];

                        progressBar.setVisibility( View.GONE );

                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Entrada p = d.toObject( Entrada.class );
                                entradaList.add( p );
                                SimpleDateFormat fYear = new SimpleDateFormat( "yyyy", Locale.ENGLISH );
                                SimpleDateFormat fMonth = new SimpleDateFormat( "MM", Locale.ENGLISH );

                                String currentYear = fYear.format( Calendar.getInstance().getTime() );
                                long dataFirebase = Long.parseLong( d.get( "data" ).toString().split( "," )[0].split( "=" )[1] ) * 1000;
                                String fluxoYear = fYear.format( dataFirebase );
                                int fluxoMonth = Integer.parseInt( fMonth.format( dataFirebase ) );

                                if (currentYear.equals( fluxoYear )) {
                                    for (int i = 0; i < 12; i++) {
                                        if (fluxoMonth == i + 1) {
                                            totalMonthEntrada[i] += Double.parseDouble( d.get( "valor" ).toString() );
                                        }
                                    }
                                }
                            }

                            DataPoint[] dataEntrada = new DataPoint[12];

                            for (int i = 0; i < 12; i++) {
                                dataEntrada[i] = new DataPoint( i + 1, totalMonthEntrada[i] );
                            }

                            entrada = new LineGraphSeries<>( dataEntrada );

                            entrada.setColor( Color.parseColor( "#8BC34A" ) );
                            entrada.setDrawDataPoints( true );
                            entrada.setDataPointsRadius( 10 );
                            entrada.setThickness( 5 );
                            entrada.setTitle( "Entrada" );

                            graphView.addSeries( entrada );
                            adapterE.notifyDataSetChanged();
                        }
                    } )

                    .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}
