package com.example.redelogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;

public class HumaFolha extends AppCompatActivity {

    public static String nomeFunc, empregador, endereco, cnpj, dsr, diasUteis, cargaHora, horaExtra, horaExtraNot, horaFeria;
    public static int salFam = 0, adcNot = 0, valTrans = 0, adcPeric = 0, valeRef = 0, valeAlim = 0, adcInsalub;
    public static double salFunc = 0.0;

    private HashMap<Integer,Double> spinnerMap;
    private Spinner listFunc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.huma_folha );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        listFunc = findViewById( R.id.folhaFuncList );

        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Funcionarios" )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        String[] spinnerArray = new String[queryDocumentSnapshots.size()];
                        spinnerMap = new HashMap<>();

                        int i = 0;

                        for (DocumentSnapshot d : list) {
                            Funcionario p = d.toObject( Funcionario.class );
                            if ( p!= null) {
                                spinnerMap.put( i, p.getSalario() );
                                spinnerArray[i] = p.getNome();
                            }
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        listFunc.setAdapter(adapter);
                    }
                } )
                .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );

        TextView txtEmpreg = findViewById( R.id.folhaNomeEmpre );
        TextView txtEnder = findViewById( R.id.folhaEnde );
        TextView txtCnpj = findViewById( R.id.folhaCnpj );
        TextView txtDsr = findViewById( R.id.folhaDsr );
        TextView txtDias = findViewById( R.id.folhaDiasUteis );
        TextView txtCargaHora = findViewById( R.id.folhaCargHora );
        TextView txtHoraExtra = findViewById( R.id.folhaHoraExt );
        TextView txtHoraExtNot = findViewById( R.id.folhaHoraExtNot );
        TextView txtHoraFeria = findViewById( R.id.folhaHoraFeri );

        CheckBox checkSalFam = findViewById( R.id.folhaCheckSalFam );
        CheckBox checkAdcNot = findViewById( R.id.folhaCheckAdcNot );
        CheckBox checkValTrans = findViewById( R.id.folhaCheckValTran );
        CheckBox checkValRefe = findViewById( R.id.folhaCheckValRef );
        CheckBox checkValAlim = findViewById( R.id.folhaCheckValAlim );
        CheckBox checkAdcPeric = findViewById( R.id.folhaCheckAdcPeri );
        RadioGroup radioGroup = findViewById(R.id.folhaAdcInsalub );
        Button concluir = findViewById( R.id.folhaConcluir );

        db.collection( "Empresas" ).document( fUser.getUid() ).collection( "Questionário" )
                .get()
                .addOnSuccessListener( queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {

                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : list) {
                                Quest p = d.toObject( Quest.class );
                                try {
                                    String cnpj = d.get("cnpj").toString();
                                    txtCnpj.setText( cnpj );
                                }catch (Exception ignored){}

                                try {
                                    String nome = d.get("nome").toString();
                                    Log.d("nome:", nome);
                                    txtEmpreg.setText( nome );
                                }catch (Exception ignored){}
                                try{
                                    String endereco = d.get("endereco").toString();
                                    txtEnder.setText( endereco );
                                }catch (Exception ignored) {
                            }
                        }
                    }
                } )
                .addOnFailureListener( e -> Toast.makeText( this, e.getMessage(), Toast.LENGTH_LONG ).show() );


        radioGroup.setOnCheckedChangeListener( (group, checkedId) -> {
            if(checkedId == R.id.folhaAdcInsalub10) {
                adcInsalub = 10;
            } else if(checkedId == R.id.folhaAdcInsalub20) {
                adcInsalub = 20;
            } else if(checkedId == R.id.folhaAdcInsalub40) {
                adcInsalub = 40;
            } else {
                adcInsalub = 0;
            }
        } );


        concluir.setOnClickListener( View ->{
            nomeFunc = listFunc.getSelectedItem().toString();
            salFunc = spinnerMap.get( listFunc.getSelectedItemPosition() );
            empregador = txtEmpreg.getText().toString().trim();
            endereco = txtEnder.getText().toString().trim();
            cnpj = txtCnpj.getText().toString().trim();
            dsr = (txtDsr.getText().toString().trim().length() == 0) ? "0" : txtDsr.getText().toString().trim();
            diasUteis = txtDias.getText().toString().trim();
            cargaHora = txtCargaHora.getText().toString().trim();
            horaExtra = (txtHoraExtra.getText().toString().trim().length() == 0) ? "0" : txtHoraExtra.getText().toString().trim();
            horaExtraNot = (txtHoraExtNot.getText().toString().trim().length() == 0) ? "0" : txtHoraExtNot.getText().toString().trim();
            horaFeria = (txtHoraFeria.getText().toString().trim().length() == 0) ? "0" : txtHoraFeria.getText().toString().trim();

            if (checkSalFam.isChecked()) salFam = 1;
            if (checkAdcNot.isChecked()) adcNot = 1;
            if (checkValTrans.isChecked()) valTrans = 1;
            if (checkValRefe.isChecked()) valeRef = 1;
            if (checkValAlim.isChecked()) valeAlim = 1;
            if (checkAdcPeric.isChecked()) adcPeric = 1;

            if (empregador.isEmpty() || cnpj.isEmpty() || cargaHora.isEmpty() || diasUteis.isEmpty() ) {
                Toast.makeText( this, "Por favor complete os campos da empresa, dias úteis e a carga diária!", Toast.LENGTH_LONG ).show();
            }else {
                Intent intent = new Intent( getBaseContext(), HumaFolPag.class );
                View.getContext().startActivity( intent );
            }
        } );
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
