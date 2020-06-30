package com.example.redelogin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class FinaIndice extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseUser fUser;
    TextView lbLucro, lbRentab, lbMargemOpe, lbMargemLiq, lbPRI, txtLucro, txtMargemOpera, txtMargemLiq, txtPRI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.fina_indices );

        db = FirebaseFirestore.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        lbLucro = findViewById( R.id.lbLucraIndic );
        lbRentab = findViewById( R.id.lbRentabIndic );
        lbMargemOpe = findViewById( R.id.lbMargemOpera );
        lbMargemLiq = findViewById( R.id.lbMargemLiq );
        lbPRI = findViewById( R.id.lbPRIIndic );
        txtLucro = findViewById( R.id.txtLucra );
        txtMargemOpera = findViewById( R.id.txtMagemOpera );
        txtMargemLiq = findViewById( R.id.txtMargemLiq );
        txtPRI = findViewById( R.id.txtPRI );

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/Trocchi.ttf");
        lbLucro.setTypeface(custom_font);
        lbRentab.setTypeface(custom_font);
        lbMargemOpe.setTypeface(custom_font);
        lbMargemLiq.setTypeface(custom_font);
        lbPRI.setTypeface(custom_font);
        txtLucro.setTypeface(custom_font);
        txtMargemOpera.setTypeface(custom_font);
        txtMargemLiq.setTypeface(custom_font);
        txtPRI.setTypeface(custom_font);

        calculos();
    }

    public void calculos(){
        double lucratividade = FinaActivity.Tab3.lucratividade;
        double margemOpera = FinaActivity.Tab3.margemOpera;
        double margemLiq = FinaActivity.Tab3.margemLiq;
        double PRI = FinaActivity.Tab3.PRI;
        txtLucro.setText( String.format( Locale.ENGLISH, "%.2f", lucratividade ).concat( "%" ) );
        txtMargemOpera.setText( String.format( Locale.ENGLISH, "%.2f", margemOpera ).concat( "%" ) );
        txtMargemLiq.setText( String.format( Locale.ENGLISH, "%.2f", margemLiq ).concat( "%" ) );
        txtPRI.setText( String.format( Locale.ENGLISH, "%.0f anos", PRI ) );

    }

    public void dialLucro(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Indíce de Lucratividade?");
        // set dialog message
        alertDialogBuilder
                .setMessage("Indica em % o quanto sua empresa está lucrando após o pagamento de TODAS as dívidas!")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialRentab(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Indíce de Rentabilidade?");
        // set dialog message
        alertDialogBuilder
                .setMessage("São importantes para registrar o sucesso ou insucesso de sua empresa, o investimento está sendo recompensado?\n\n" +
                        "Margem Operacional - Mede a eficiência do setor operacional: você está vendendo bem?\n\n" +
                        "Margem Líquida - Mede a capacidade de sua empresa em obter lucro: você está lucrando o máximo que pode?")
                .setCancelable(false)
                .setPositiveButton("Entendi!", null);
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void dialPRI(View view) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        // set title
        alertDialogBuilder.setTitle("O que é Prazo de Retorno do Investimento?");
        // set dialog message
        alertDialogBuilder
                .setMessage("É o tempo (em anos) em que você receberá o retorno do seu investimento!")
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
