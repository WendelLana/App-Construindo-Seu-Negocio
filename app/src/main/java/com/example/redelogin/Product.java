package com.example.redelogin;

public class Product {
    private int id;
    private String nome, valor, quantidade;

    public Product(){

    }

    Product(int id, String nome, String valor, String quant){
        this.id = id;
        this.nome = nome.trim();
        this.valor = valor.trim();
        this.quantidade = quant.trim();
    }
    public int getId(){
        return id;
    }
    public String getNome(){
        return nome;
    }
    public String getQuantidade(){ return quantidade; }
    public String getValor(){
        return valor;
    }
}
