package com.example.redelogin;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HumaFolPag extends AppCompatActivity {
    TextView txtEmpregad, txtEndereco, txtCnpj, txtFunc, txtData, refAdcPeric, provAdcPeric, refAdcInsalub, provAdcInsalub, salBase,
            provSalFam, refHorExtr, provHorExtr, refHorExtrFer, provHorExtrFer, refHorExtrNot, provHorExtrNot, refAdcNoturno,
            provAdcNoturno, refValeTransp, descValeTransp, refInss, descInss, refIrpf, descIrpf, baseInss, baseFgts, baseIrpf,
            refValeRefeic, descValeRefei, refValeAliment, descValeAliment, DSR, valorLiq, totalProv, totalDesc;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.huma_folpag );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

         txtEmpregad = findViewById( R.id.folpag_emp );
         txtEndereco = findViewById( R.id.folpag_end );
         txtCnpj = findViewById( R.id.folpag_cnpj );
         txtFunc = findViewById( R.id.folpag_func );
         txtData = findViewById( R.id.folpag_data );
         refAdcPeric = findViewById( R.id.folpag_refAdcPeric );
         provAdcPeric = findViewById( R.id.folpag_provAdcPeric );
         refAdcInsalub = findViewById( R.id.folpag_refAdcInsalub );
         provAdcInsalub = findViewById( R.id.folpag_provAdcInsalub );
         refHorExtr = findViewById( R.id.folpag_refextras );
         provHorExtr = findViewById( R.id.folpag_provextras );
         refHorExtrFer = findViewById( R.id.folpag_refextrasnot );
         provHorExtrFer = findViewById( R.id.folpag_provextrasnot );
         refHorExtrNot = findViewById( R.id.folpag_refextrasfer );
         provHorExtrNot = findViewById( R.id.folpag_provextrasfer );
         refAdcNoturno = findViewById( R.id.folpag_refnoturno );
         provAdcNoturno = findViewById( R.id.folpag_provnoturno );
         provSalFam = findViewById( R.id.folpag_provfam );
         refValeTransp = findViewById( R.id.folpag_reftransporte );
         descValeTransp = findViewById( R.id.folpag_desctransporte );
         refValeRefeic = findViewById( R.id.folpag_refrefeic );
         descValeRefei = findViewById( R.id.folpag_descrefeic );
         refValeAliment = findViewById( R.id.folpag_refalimenta );
         descValeAliment = findViewById( R.id.folpag_descalimenta );
         refInss = findViewById( R.id.folpag_refinss );
         descInss = findViewById( R.id.folpag_descinss );
         refIrpf = findViewById( R.id.folpag_refirpf );
         descIrpf = findViewById( R.id.folpag_descirpf );
         baseInss = findViewById( R.id.folpag_baseINSS );
         baseFgts = findViewById( R.id.folpag_baseFGTS );
         baseIrpf = findViewById( R.id.folpag_baseIRPF );
         DSR = findViewById( R.id.folpag_DSR );
         salBase = findViewById( R.id.folpag_salBase );
         valorLiq = findViewById( R.id.folpag_valLiq );
         totalProv = findViewById( R.id.folpag_ttProv );
         totalDesc = findViewById( R.id.folpag_ttDesc );

        SimpleDateFormat fdata =  new SimpleDateFormat( "MMMM/yyyy", new Locale( "pt","BR") );
        String data = fdata.format( Calendar.getInstance().getTime() );
        txtData.setText( data );
        createFolha();
    }

    public void createFolha(){
        String empregador = HumaFolha.empregador;
        String endereco = HumaFolha.endereco;
        String cnpj = HumaFolha.cnpj;
        String func = HumaFolha.nomeFunc;
        int dsr = Integer.parseInt(HumaFolha.dsr);
        int diasUteis = Integer.parseInt(HumaFolha.diasUteis);
        int cargaHora = Integer.parseInt(HumaFolha.cargaHora);
        int horaExtra = Integer.parseInt(HumaFolha.horaExtra);
        int horaExtraNot = Integer.parseInt(HumaFolha.horaExtraNot);
        int horaFeria = Integer.parseInt(HumaFolha.horaFeria);
        double salFunc = HumaFolha.salFunc;
        int adcInsalub = HumaFolha.adcInsalub;
        int adcNoturno = HumaFolha.adcNot;
        int adcPeric = HumaFolha.adcPeric;
        int valTrans = HumaFolha.valTrans;
        int valeRef = HumaFolha.valeRef;
        int valeAlim = HumaFolha.valeAlim;
        int salFam = HumaFolha.salFam;

        double DSRmensal = (salFunc * dsr) / diasUteis;
        //Total do Salário = (cargaHora x salHora) / diasUteis;
        double taxaInsalub = (double) adcInsalub/100;
        double insalubidade =  0.0;
        if (taxaInsalub != 0) insalubidade = salFunc + (998 * taxaInsalub);

        String taxa = "0";
        double periculosidade = 0.0;
        if (adcPeric == 1) {
            taxa = "30%";
            periculosidade = 0.3 * salFunc;
        }

        double salHora = salFunc / cargaHora;

        String refHoraExtra = "0";
        double valHoraExtra = 0.0;
        if (horaExtra != 0) {
            valHoraExtra = salHora + (salHora * 0.5 * horaExtra);
            refHoraExtra = "50%";
        }

        String refHoraExtraFer = "0";
        double valHoraExtraFer = 0.0;
        if (horaFeria != 0) {
            valHoraExtraFer = salHora + (salHora * horaFeria);
            refHoraExtraFer = "100%";
        }

        String refHoraExtraNot = "0";
        double valHoraExtraNot = 0.0;
        if (horaExtraNot != 0) {
            valHoraExtraNot = salHora + (salHora * 0.2 * horaExtraNot);
            valHoraExtraNot = 1.5 * valHoraExtraNot;
            refHoraExtraNot = "20%";
        }

        String refAdcNot = "0";
        double valAdcNot = 0.0;
        if (adcNoturno == 1) {
            valAdcNot = salHora + (salHora * 0.2);
            refAdcNot = "20%";

        }

        double salarioFam = 0.0;
        if (salFam == 1) {
            if (salFunc <= 907.77) salarioFam = 46.54;
            if (salFunc >= 907.78 && salFunc <= 1364.43) salarioFam = 32.80;
        }

        String referTrans = "0";
        double valeTrans = 0.0;
        if (valTrans == 1){
            referTrans = "6%";
            valeTrans = salFunc * 0.06;
        }

        String referRefei = "0";
        double valeRefei = 0.0;
        if (valeRef == 1){
            referRefei = "20%";
            valeRefei = salFunc * 0.2;
        }

        String referAlimen = "0";
        double valeAlimen = 0.0;
        if (valeAlim == 1){
            referAlimen = "20%";
            valeAlimen = salFunc * 0.2;
        }

        String refINSS = "8%";
        double INSS = 0.08 * salFunc;
        double FGTS = 0.08 * salFunc;
        double IRPF = 0.0;
        String refIRPF = "0";
        if (salFunc >= 1903.99 && salFunc <= 2826.65) {
            IRPF = salFunc * 0.075 - 142.80;
            refIRPF = "7.5%";
        } else if (salFunc >= 2826.66 && salFunc <= 3751.05) {
            IRPF = salFunc * 0.15 - 354.80;
            refIRPF = "15%";
        } else if (salFunc >= 3751.06 && salFunc <= 4664.68) {
            IRPF = salFunc * 0.225 - 636.13;
            refIRPF = "22.5%";
        } else if (salFunc > 4664.68){
            IRPF = salFunc * 0.275 - 869.36;
            refIRPF = "27.5%";
        }
        if (IRPF < 0) IRPF = 0.0;

        double totalProventos = salFunc + DSRmensal + insalubidade + periculosidade + valAdcNot + valHoraExtra + valHoraExtraFer +
                valHoraExtraNot + salarioFam;
        double totalDescontos = valeAlimen + valeTrans + valeRefei + INSS + IRPF + FGTS;
        double valorLiquido = totalProventos - totalDescontos ;

        txtEmpregad.setText( String.format( new Locale("pt", "BR"), "EMPREGADOR: %s", empregador ));
        txtEndereco.setText( String.format( new Locale("pt", "BR"), "ENDEREÇO: %s", endereco ));
        txtCnpj.setText( String.format( new Locale("pt", "BR"), "CNPJ: %s", cnpj ));
        txtFunc.setText( String.format( new Locale("pt", "BR"), "FUNCIONÁRIO: %s", func ));
        refAdcPeric.setText( taxa );
        provAdcPeric.setText( String.format( new Locale("pt", "BR"), "R$%.2f", periculosidade ));
        refAdcInsalub.setText( String.format( new Locale("pt", "BR"), "%d", adcInsalub ).concat( "%" ));
        provAdcInsalub.setText( String.format( new Locale("pt", "BR"), "R$%.2f", insalubidade ));
        refHorExtr.setText( refHoraExtra );
        provHorExtr.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valHoraExtra ));
        refHorExtrFer.setText( refHoraExtraFer );
        provHorExtrFer.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valHoraExtraFer ));
        refHorExtrNot.setText( refHoraExtraNot );
        provHorExtrNot.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valHoraExtraNot ));
        refAdcNoturno.setText( refAdcNot );
        provAdcNoturno.setText( String.format( new Locale("pt", "BR"), "R$%.2f/h", valAdcNot ));
        provSalFam.setText( String.format( new Locale("pt", "BR"), "R$%.2f", salarioFam ));
        refValeTransp.setText( referTrans );
        descValeTransp.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valeTrans ));
        refValeRefeic.setText( referRefei );
        descValeRefei.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valeRefei ));
        refValeAliment.setText( referAlimen );
        descValeAliment.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valeAlimen ));
        refInss.setText( refINSS );
        descInss.setText( String.format( new Locale("pt", "BR"), "R$%.2f", INSS ));
        refIrpf.setText( refIRPF );
        descIrpf.setText( String.format( new Locale("pt", "BR"), "R$%.2f", IRPF ));
        baseInss.setText( String.format( new Locale("pt", "BR"), "R$%.2f", INSS ));
        baseIrpf.setText( String.format( new Locale("pt", "BR"), "R$%.2f", IRPF ));
        baseFgts.setText( String.format( new Locale("pt", "BR"), "R$%.2f", FGTS ));
        DSR.setText( String.format( new Locale("pt", "BR"), "R$%.2f", DSRmensal ));
        salBase.setText( String.format( new Locale("pt", "BR"), "R$%.2f", salFunc ));
        valorLiq.setText( String.format( new Locale("pt", "BR"), "R$%.2f", valorLiquido ));
        totalProv.setText( String.format( new Locale("pt", "BR"), "R$%.2f", totalProventos ));
        totalDesc.setText( String.format( new Locale("pt", "BR"), "R$%.2f", totalDescontos ));
    }
}
