package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MarkPesquisa extends AppCompatActivity {
    TextView titlePesquisa, text1, text2, text3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mark_pesquisa );

        titlePesquisa = findViewById( R.id.pesquisa_title );
        text1 = findViewById( R.id.pesquisa_text );
        text2 = findViewById( R.id.pesquisa_primaria );
        text3 = findViewById( R.id.pesquisa_secundaria );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        titlePesquisa.setTypeface(custom_font);
        text1.setTypeface(custom_font);
        text2.setTypeface(custom_font);
        text3.setTypeface(custom_font);
    }

    public void dialPesPri(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Pesquisa Primária?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É a coleta e análise de informações ainda não disponíveis feita em primeira mão pela sua empresa ou por alguém " +
                        "contratado diretamente para isso, sobre seu mercado e seus clientes. Serve para fornecer ideias sobre as " +
                        "necessidades e percepções dos clientes. ")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialPesMerc(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Pesquisa de Mercado?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É uma ferramenta para coletar informações sobre o mercado que atuam ou pretendem atuar e avaliar a " +
                        "viabilidade de um novo produto ou serviço, por meio de pesquisas direcionadas com potenciais consumidores. Ela " +
                        "serve para o empreendedor testar uma ideia ou empresas já em atuação avaliarem os rumos do seu negócio e tomar " +
                        "decisões mais seguras e acertadas.")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialPesSec(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Pesquisa Secundária?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É a síntese de uma pesquisa já feita por terceiros e está a sua disposição (Revistas, livros, dados de " +
                        "importação e exportação, dados de produção, estatísticas, censos governamentais, etc.) Ela fornece uma visão " +
                        "geral de um mercado e indica as tendências de consumo e é útil para analisar seus concorrentes.")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialPesQuant(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Pesquisa Quantitativa?");
        // set dialog message
        alertDialogBuilder
                .setMessage("Serve para medir um problema e entender a dimensão dele. Essa pesquisa fornece informações numéricas " +
                        "sobre o comportamento do consumidor.")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialPesQuali(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Pesquisa Qualitativa?");
        // set dialog message
        alertDialogBuilder
                .setMessage("Serve para entender o comportamento do consumidor, não simplesmente medir.")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
