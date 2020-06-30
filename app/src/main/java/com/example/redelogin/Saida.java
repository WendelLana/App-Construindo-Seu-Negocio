package com.example.redelogin;

import java.io.Serializable;
import java.util.Date;

public class Saida implements Serializable {
    public String nome, quantidade, valor, tipo;
    public Date data;

    public Saida(){
    }

    public Saida(Date dataSaida, String nomeSaida, String qtdSaida, String valSaida, String tipoSaida) {
        data = dataSaida;
        nome = nomeSaida.trim();
        quantidade = qtdSaida.trim();
        valor = valSaida.trim();
        tipo = tipoSaida.trim();
    }

    public Date getData() { return data; }
    public String getNome() { return nome; }
    public String getQuantidade() { return quantidade; }
    public String getValor() { return valor; }
    public String getTipo() { return tipo; }

    public void setData(Date date) { data = date; }
    public void setNome(String name) { nome = name; }
    public void setQuantidade(String quant) { quantidade = quant; }
    public void setValor(String val) { valor = val; }
    public void setTipo(String type) { tipo = type; }
}
