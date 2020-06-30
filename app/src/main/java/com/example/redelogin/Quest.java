package com.example.redelogin;

public class Quest {

    private String Cnpj;
    private String nomeFantasia;
    private String endereco;
    private String emailEmpresario;
    private String segmentoEmpresa;
    private String formalizado;
    private int qtFuncionarios;

    public Quest(){}

    public Quest(String cnpj, String nome, String endereco, String email, String typeCompany, String isLegal, int qtFunc){
        this.Cnpj = cnpj;
        this.nomeFantasia = nome;
        this.endereco = endereco;
        this.emailEmpresario = email.trim();
        this.segmentoEmpresa = typeCompany.trim();
        this.formalizado = isLegal.trim();
        this.qtFuncionarios = qtFunc;
    }

    public String getCnpj(){
        return Cnpj;
    }

    public String getnomeFantasia(){
        return nomeFantasia;
    }

    public String getEndereco(){
        return endereco;
    }

    public String getemailEmpresario(){
        return emailEmpresario;
    }

    public String getsegmentoEmpresa(){
        return segmentoEmpresa;
    }

    public String getFormalizado(){
        return formalizado;
    }

    public int getQtFuncionarios(){ return qtFuncionarios; }

    public void setnomeFantasia(String nome){
        nomeFantasia = nome;
    }

    public void setCnpj(String cnpj){
        Cnpj = cnpj;
    }

    public void setEndereco(String end){
        endereco = end;
    }

    public void setemailEmpresario(String email){
        emailEmpresario = email;
    }

    public void setsegmentoEmpresa(String segm){ segmentoEmpresa = segm;  }

    public void setFormalizado(String form){
        formalizado = form;
    }

    public void setQtFuncionarios(int quant){
        qtFuncionarios = quant;
    }

}
