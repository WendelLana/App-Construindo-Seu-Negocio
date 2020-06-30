package com.example.redelogin;

public class Funcionario {

    private String nome, datanasc, cargo, datacontrata, desc;
    private int rg;
    private double salario;

    public Funcionario(){

    }

    Funcionario(String nome, int rg, String dataNasc, String cargo, String dataContrata, double salario, String desc){
        this.nome = nome;
        this.rg = rg;
        this.datanasc = dataNasc;
        this.cargo = cargo;
        this.datacontrata = dataContrata;
        this.salario = salario;
        this.desc = desc;
    }

    public String getNome(){
        return nome;
    }
    public int getRg(){ return rg; }
    public String getDatanasc(){ return datanasc; }
    public String getCargo(){
        return cargo;
    }
    public String getDatacontrata(){ return datacontrata; }
    public double getSalario(){ return salario; }
    public String getDesc(){ return desc; }
}
