package com.example.redelogin;

public class Fornecedor {

    private int cnpj;
    private String nome, telefone;

    public Fornecedor(){

    }

    public Fornecedor(int fcnpj, String nome, String tel){
        this.cnpj = fcnpj;
        this.nome = nome;
        this.telefone = tel;
    }

    public int getCnpj(){
        return cnpj;
    }
    public String getNome(){
        return nome;
    }
    public String getTelefone(){
        return telefone;
    }
}
