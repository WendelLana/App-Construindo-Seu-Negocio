package com.example.redelogin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FinaActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    GraphView graphView;
    LineGraphSeries entrada, saida;

    TextView registre;
    Button btEstoque, btCaixa, btIndice, btEntrada, btSaida;

    FirebaseFirestore db;
    FirebaseUser fUser;

    Tab1 frag1; Tab2 frag2; Tab3 frag3;
    double recBruta = 0.0, deducoes = 0.0, varSaida = 0.0, custoFixo = 0.0, despesaFixa = 0.0, taxas = 0.0, despesaFina = 0.0,
            receitaFina = 0.0;


    @SuppressLint({"ClickableViewAccessibility", "UseSparseArrays"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fina);

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        registre = findViewById( R.id.finaRegister );
        btEntrada = findViewById( R.id.finaCompra );
        btSaida = findViewById( R.id.finaVenda );
        btEstoque = findViewById( R.id.finaEstoque );
        btCaixa = findViewById( R.id.finaCaixa );
        btIndice = findViewById( R.id.finaIndice );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        registre.setTypeface(custom_font);
        btEntrada.setTypeface(custom_font);
        btSaida.setTypeface(custom_font);
        btEstoque.setTypeface(custom_font);
        btCaixa.setTypeface(custom_font);
        btIndice.setTypeface(custom_font);


        btEstoque.setOnClickListener( View -> {
            Intent intent = new Intent( getBaseContext(), FinaFluxo.class );
            View.getContext().startActivity( intent );
        } );

        btCaixa.setOnClickListener( View -> {
            Intent intent = new Intent( getBaseContext(), FinaCustoDespesa.class );
            View.getContext().startActivity( intent );
        } );

        btIndice.setOnClickListener( View -> {
            Intent intent = new Intent( getBaseContext(), FinaIndice.class );
            View.getContext().startActivity( intent );
        } );

        btSaida.setOnClickListener( View -> {
            FluxoSaida pay = new FluxoSaida();
            pay.Pay(this);
        } );

        btEntrada.setOnClickListener( View -> {
            FluxoEntrada buy = new FluxoEntrada();
            buy.Gain(this);
        } );

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        graphView = findViewById(R.id.graph);
        graphView.setTitle("Fluxo Anual (R$/mês)");
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(1);
        graphView.getViewport().setMaxX(12);

        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign( LegendRenderer.LegendAlign.TOP );
        graphView.getLegendRenderer().setWidth( width/5 );
        graphView.getLegendRenderer().setMargin( 10 );
        graphView.getLegendRenderer().setBackgroundColor( Color.parseColor( "#e4e4e4" ) );

        graphView.setOnClickListener( View -> {
            Intent intent = new Intent( getBaseContext(), FinaFluxo.class );
            View.getContext().startActivity( intent );
        } );

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter( getSupportFragmentManager() );

        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter( mSectionsPagerAdapter );

        TabLayout tabLayout = findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        mViewPager.setOnTouchListener( (v, event) -> {
            mViewPager.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        } );
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }

    public void graphBuild(){
        graphView.removeAllSeries();
        deducoes = 0.0; recBruta = 0.0; varSaida = 0.0;  custoFixo = 0.0; despesaFixa = 0.0; despesaFina = 0.0; taxas = 0.0; receitaFina = 0.0;
        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Saida" )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    double[] totalMonthSaida= new double[12];

                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                            Saida p = d.toObject( Saida.class );
                            if (p.getNome().equals("Comissão de Venda")) {
                                deducoes += Double.parseDouble( p.getValor() );
                            } else if (p.getTipo().contains("Variável")) varSaida += Double.parseDouble( p.getValor() );
                            if (p.getTipo().equals( "Custo Fixo" )) custoFixo += Double.parseDouble( p.getValor() );
                            if (p.getTipo().equals( "Despesa Fixa" )) despesaFixa += Double.parseDouble( p.getValor() );
                            if (p.getTipo().equals( "Despesa Financeira" )) despesaFina += Double.parseDouble( p.getValor() );
                            if (p.getTipo().equals( "Imposto" )) taxas += Double.parseDouble( p.getValor() );
                            if (p.getTipo().equals( "Investimento" )) receitaFina += Double.parseDouble( p.getValor() );

                            SimpleDateFormat fMonth = new SimpleDateFormat( "MM", Locale.ENGLISH );
                            SimpleDateFormat fYear = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

                            String currentYear = fYear.format(Calendar.getInstance().getTime());
                            String fluxoYear = fYear.format( p.getData() );
                            int fluxoMonth = Integer.parseInt( fMonth.format(p.getData()) );

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
                        saida.setTitle( "Saida" );
                        graphView.addSeries( saida );
                        frag1.calculos(recBruta, deducoes, varSaida, custoFixo, despesaFixa, despesaFina, taxas, receitaFina);
                    }
                });
        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Entrada" )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    double[] totalMonthEntrada = new double[12];
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                            Entrada p = d.toObject( Entrada.class );

                            SimpleDateFormat fMonth = new SimpleDateFormat( "MM", Locale.ENGLISH );
                            SimpleDateFormat fYear = new SimpleDateFormat( "yyyy", Locale.ENGLISH );

                            String currentYear = fYear.format( Calendar.getInstance().getTime() );
                            String fluxoYear = fYear.format( p.getData() );
                            int fluxoMonth = Integer.parseInt( fMonth.format( p.getData() ) );

                            recBruta += Double.parseDouble( p.getValor() );
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
                        frag1.calculos(recBruta, deducoes, varSaida, custoFixo, despesaFixa, despesaFina, taxas, receitaFina);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        graphBuild();
    }

    public void dialPontoEquil(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Ponto de Equilíbrio?");
        // set dialog message
        alertDialogBuilder
                .setMessage("Indica o quanto (%) você deve vender sobre seu faturamento para quitar seus custos e despesas variáveis!")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public static class Tab1 extends Fragment {
        static double taxaDAS = 0.0, receitaBruta = 0.0, lucroOperacional = 0.0, lucroLiquido = 0.0, IMC = 0.0, pontoEqui = 0.0,
                CDVariaveis = 0.0, custoFixo = 0.0, despesaFixa = 0.0, margemContrib = 0.0, receitaLiquida = 0.0;
        FirebaseFirestore db;
        FirebaseUser fUser;
        TextView valorLiquido, margemContribuicao, pontoEquilibrio;
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState){
            View newView = inflater.inflate(R.layout.tab1_fina, container, false);
            valorLiquido = newView.findViewById( R.id.edValorLiquido );
            margemContribuicao = newView.findViewById( R.id.edIndiceIMC );
            pontoEquilibrio = newView.findViewById( R.id.edEquilibrioIMC );


            valorLiquido.setText( String.format( Locale.ENGLISH, "R$%.2f", receitaLiquida ) );
            margemContribuicao.setText( String.format( Locale.ENGLISH, "R$%.2f (%.2f", margemContrib, IMC ).concat( "%)" ) );
            pontoEquilibrio.setText( String.format( Locale.ENGLISH, "%.2f", pontoEqui ).concat( "%" ) );
            return newView;
        }

        void calculos(double receita, double deducao, double variaveis, double cFixo, double dFixo, double despesaFina,
                      double taxas, double receitaFina) {
            db = FirebaseFirestore.getInstance();
            fUser = FirebaseAuth.getInstance().getCurrentUser();
            receitaBruta = receita;
            CDVariaveis = variaveis;
            custoFixo = cFixo;
            despesaFixa = dFixo;

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Questionário" )
            .get()
            .addOnSuccessListener( queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot d : list) {
                        Quest p = d.toObject( Quest.class );
                        if (p.getQtFuncionarios() < 2){
                            if (p.getsegmentoEmpresa().equals("Comércio") || p.getsegmentoEmpresa().equals("Indústria")) {
                                taxaDAS = 50.90;
                            } else if(p.getsegmentoEmpresa().equals( "Comércio e Serviço" )) {
                                taxaDAS = 55.90;
                            } else {
                                taxaDAS = 54.90;
                            }
                        } else {
                            switch (p.getsegmentoEmpresa()) {
                                case "Comércio":
                                    taxaDAS = 0.073 * receitaBruta;
                                    break;
                                case "Indústria":
                                    taxaDAS = 0.078 * receitaBruta;
                                    break;
                                case "Serviço de vigilância, limpeza, obra ou advocácia":
                                    taxaDAS = 0.09 * receitaBruta;
                                    break;
                                default:
                                    taxaDAS = 0.112 * receitaBruta;
                                    break;
                            }
                        }
                     }
                    taxaDAS += deducao;
                    receitaLiquida = receitaBruta - taxaDAS;
                    margemContrib = receitaLiquida - CDVariaveis;
                    IMC = (margemContrib / receitaBruta) * 100;
                    if (custoFixo != 0) pontoEqui = custoFixo/IMC;
                    lucroOperacional = margemContrib - despesaFixa;
                    lucroLiquido = (lucroOperacional + receitaFina) - (despesaFina + taxas);
                    valorLiquido.setText( String.format( Locale.ENGLISH, "R$%.2f", receitaLiquida ) );
                    margemContribuicao.setText( String.format( Locale.ENGLISH, "R$%.2f (%.2f", margemContrib, IMC ).concat( "%)" ) );
                    pontoEquilibrio.setText( String.format( Locale.ENGLISH, "%.2f", pontoEqui ).concat( "%" ) );
                    Tab2.lucroOperacional.setText( String.format( Locale.ENGLISH, "R$%.2f", lucroOperacional ) );
                    Tab2.lucro.setText( String.format( Locale.ENGLISH, "R$%.2f", lucroLiquido ) );

                 }
             });
        }
    }

    public static class Tab2 extends Fragment {
        @SuppressLint("StaticFieldLeak")
        static TextView lucroOperacional, lucro;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState){
            View newView = inflater.inflate(R.layout.tab2_fina, container, false);
            lucroOperacional = newView.findViewById( R.id.edLucroOperaDRE );
            lucro = newView.findViewById( R.id.edLucroDRE );

            return newView;
        }
    }

    public static class Tab3 extends Fragment {
        FirebaseFirestore db;
        FirebaseUser fUser;

        static double lucratividade = 0.0, margemOpera = 0.0, margemLiq = 0.0, PRI = 0.0;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState){
            View newView = inflater.inflate(R.layout.tab3_fina, container, false);
            db = FirebaseFirestore.getInstance();
            fUser = FirebaseAuth.getInstance().getCurrentUser();
            TextView txtIMC = newView.findViewById( R.id.txtIMCAnaFina );
            TextView txtPonto = newView.findViewById( R.id.txtPontoAnaFina );
            TextView txtReceita = newView.findViewById( R.id.txtReceitaAnaFina );
            TextView txtCD = newView.findViewById( R.id.txtCDAnaFina );
            TextView txtDRE = newView.findViewById( R.id.txtDREAnaFina );
            TextView txtLucratividade = newView.findViewById( R.id.txtLucraAnaFina );
            TextView txtMargemOpera = newView.findViewById( R.id.txtMargOperAnaFina );
            TextView txtMargemLiq = newView.findViewById( R.id.txtMargLiqAnaFina );
            TextView txtPRI = newView.findViewById( R.id.txtPRIAnaFina );

            double receita = Tab1.receitaBruta;
            double IMC = Tab1.IMC;
            double pontoEquilibrio = Tab1.pontoEqui;
            double lucroLiq = Tab1.lucroLiquido;
            double lucroOpe = Tab1.lucroOperacional;
            double CustoDespesa = Tab1.CDVariaveis + Tab1.custoFixo + Tab1.despesaFixa;

            txtCD.setTextColor( Color.rgb( 0, 255,0  ) );
            txtDRE.setTextColor( Color.rgb( 0, 255,0  ) );
            if(receita*0.54 <= CustoDespesa) txtCD.setTextColor( Color.rgb( 255, 0,0  ) );
            if(lucroLiq <= 0) txtDRE.setTextColor( Color.rgb( 255, 0,0  ) );

            txtReceita.setText( String.format( Locale.ENGLISH, "R$%.2f", receita ) );
            txtCD.setText( String.format( Locale.ENGLISH, "R$%.2f", CustoDespesa ) );
            txtIMC.setText( String.format( Locale.ENGLISH, "%.2f", IMC ).concat( "%" ) );
            txtPonto.setText( String.format( Locale.ENGLISH, "%.2f", pontoEquilibrio ).concat( "%" ) );
            txtDRE.setText( String.format( Locale.ENGLISH, "R$%.2f", lucroLiq ) );

            lucratividade = receita / lucroLiq;
            margemLiq = (lucroLiq / receita) * 100;
            txtLucratividade.setText( String.format( Locale.ENGLISH, "%.2f", lucratividade ).concat( "%" ) );
            txtMargemLiq.setText( String.format( Locale.ENGLISH, "%.2f", margemLiq ).concat( "%" ) );

            db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Entrada" )
                    .get()
                    .addOnSuccessListener( queryDocumentSnapshots -> {
                        double investimento = 0;
                        int vendas = 0;
                        margemOpera = 0;
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot d : list) {
                                Entrada p = d.toObject( Entrada.class );
                                if (p.getTipo().equals( "Investimento" )) {
                                    investimento += Double.parseDouble( p.getValor() );
                                }
                                if (p.getTipo().equals( "Vendas" )) {
                                    vendas += Integer.parseInt( p.getQuantidade() );
                                }
                            }
                            if (vendas != 0) margemOpera = lucroOpe / vendas;
                            PRI = investimento / lucroLiq;
                        }
                        txtPRI.setText( String.format( Locale.ENGLISH, "%.0f anos", PRI ) );
                        txtMargemOpera.setText( String.format( Locale.ENGLISH, "%.2f", margemOpera ).concat( "%" ) );
                    } );

            return newView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    frag1 = new Tab1();
                    return frag1;
                case 1:
                    frag2 = new Tab2();
                    return frag2;
                case 2:
                    frag3 = new Tab3();
                    return frag3;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


}
