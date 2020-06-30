package com.example.redelogin;

public class Events {
    public String nome, tempo, data, mes, ano;

    public Events(){

    }

    public Events(String event, String time, String date, String month, String year) {
        nome = event.trim();
        tempo = time.trim();
        data = date.trim();
        mes = month.trim();
        ano = year.trim();
    }

    public String getNome() { return nome; }

    //public void setNome(String event) { nome = event; }

    public String getTempo() { return tempo; }

    //public void setTempo(String time) { tempo = time; }

    public String getData() { return data; }

    //public void setDia(String day) { dia = day; }

    //public String getMes() { return mes; }

    //public void setMes(String month) { mes = month; }

    //public String getAno() { return ano; }

    //public void setAno(String year) { ano = year; }
}
