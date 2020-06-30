package com.example.redelogin;

import java.io.Serializable;
import java.util.Date;

public class Entrada implements Serializable {
    public String nome, quantidade, valor, tipo;
    public Date data;

    public Entrada(){
    }

    public Entrada(Date dataEntrada, String nomeEntrada, String qtdEntrada, String valEntrada, String tipoEntrada) {
        data = dataEntrada;
        nome = nomeEntrada.trim();
        quantidade = qtdEntrada.trim();
        valor = valEntrada.trim();
        tipo = tipoEntrada.trim();
    }

    public Date getData() { return data; }
    public String getNome() { return nome; }
    public String getQuantidade() { return quantidade; }
    public String getValor() { return valor; }
    public String getTipo() { return tipo; }
}
