package com.example.vitor.Estacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vitor on 05/11/2016.
 */
public class Estacao {
    private String nomeEstacao;
    private int linha;
    private int idEstacao;

    public Estacao(){

    }
    public Estacao(int idEstacao, int linha, String nomeEstacao) {
        this.nomeEstacao = nomeEstacao;
        this.linha = linha;
        this.idEstacao = idEstacao;
    }

    public String getNomeEstacao() {
        return nomeEstacao;
    }

    public int getIdEstacao() {
        return idEstacao;
    }

    public void setNomeEstacao(String nomeEstacao) {
        this.nomeEstacao = nomeEstacao;
    }

    public void setIdEstacao(int idEstacao) {
        this.idEstacao = idEstacao;
    }

    // método retorna um Lista de <Estacao> para validar a estacao selecionada no spinner
    public static List<Estacao> all(){
        List<Estacao> list = new ArrayList<Estacao>();
        return list;
    }


    public static Estacao returnEstacao(String nome, int id){
        Estacao e = new Estacao();
        e.setNomeEstacao(nome);
        e.setIdEstacao(id);
        return e;
    }

    //metodo retorna uma lista com o nome das estacoes para mostrar no spinner
    public static List<String> allString(List<Estacao> e){
        List<String> retorno = new ArrayList<String>();
        for(Estacao a : e){
            retorno.add(a.getNomeEstacao());
        }
        return retorno;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    @Override
    public String toString() {
        return "Estacao{" +
                "nomeEstacao='" + nomeEstacao + '\'' +
                ", linha=" + linha +
                ", idEstacao=" + idEstacao +
                '}';
    }
}
