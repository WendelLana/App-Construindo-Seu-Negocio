package com.example.redelogin;

public class Cliente {

    private String nome, adendo, telefone;
    private int cpf;

    public Cliente(){

    }

    public Cliente(String nome, String tel, String desc, int ncpf){
        this.nome = nome.trim();
        this.telefone = tel.trim();
        this.adendo = desc.trim();
        this.cpf = ncpf;
    }

    public String getNome(){
        return nome;
    }
    public String getTelefone(){
        return telefone;
    }
    public String getAdendo(){ return adendo; }
    public int getCpf(){ return cpf; }
}
