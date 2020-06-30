package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MarkSWOT extends AppCompatActivity {
    TextView title, text, text2, text3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.mark_swot );

        title = findViewById( R.id.swot_title );
        text = findViewById( R.id.swot_text );
        text2 = findViewById( R.id.swot_text2 );
        text3 = findViewById( R.id.swot_text3 );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        title.setTypeface(custom_font);
        text.setTypeface(custom_font);
    }

    public void dialSWOT(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Análise SWOT?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É uma ferramenta simples e útil para ajudar a empresa a definir ações, objetivos, metas e iniciativas," +
                        " com um direcionamento muito maior e mais preciso para planejar as tomadas de decisões em sua empresa, analisando sua" +
                        " concorrência e identificando com mais facilidade os pontos positivos e negativos que influenciam o seu negócio. ")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialAmbInt(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Ambiente Interno?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É tudo que está dentro da empresa, faz parte dela e a empresa consegue controlar e dominar. (pessoal, " +
                        "maquinário, políticas de vendas, tecnologias empregadas, softwares e sistemas de gestão, frotas de veículos, rede " +
                        "de filiais, carteiras de clientes, a cultura organizacional, capacidade de investimento etc.) ")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialAmbExt(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Ambiente Externo?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É o que a empresa não controla diretamente. (clima, taxa de juros, mudanças de legislação, câmbio, " +
                        "desastres naturais, políticas ambientais, guerras, embargos econômicos, crises econômicas, eleições etc.) ")
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
