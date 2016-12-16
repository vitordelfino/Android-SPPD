package com.example.vitor.testevolley;

/**
 * Created by vitor on 15/12/16.
 */

public class Passageiro {


    private String nome;

    public synchronized String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}
