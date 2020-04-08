package com.app.sppd.Cartao;

import android.util.Log;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by vitor on 28/02/17.
 */

public class Cartao implements Serializable {

    private String codCartao;
    private int categoria;
    private int codPassageiro;
    private int ativo;
    private double saldo;
    private double ultimoMovimento;

    private boolean clicado;

    public Cartao(){

    }

    public Cartao(String numCartao, CategoriaCartao categoria, double saldo){
        this.codCartao = numCartao;
        this.categoria = categoria.getCategoria();
        this.saldo = saldo;
        this.clicado = false;
    }

    public Cartao(String codCartao, CategoriaCartao categoria, int codPassageiro, int ativo, double saldo,
                  double ultimoMovimento) {
        super();
        this.codCartao = codCartao;
        this.categoria = categoria.getCategoria();
        this.codPassageiro = codPassageiro;
        this.ativo = ativo;
        this.setSaldo(saldo);
        this.ultimoMovimento = ultimoMovimento;
        this.clicado = false;
    }

    public Cartao(String codCartao, int categoria, int codPassageiro, int ativo, double saldo,
                  double ultimoMovimento) {
        super();
        this.codCartao = codCartao;
        this.setCategoria(categoria);
        this.codPassageiro = codPassageiro;
        this.ativo = ativo;
        this.setSaldo(saldo);
        this.ultimoMovimento = ultimoMovimento;
        this.clicado = false;
    }

    public Cartao(String codCartao, int codPassageiro){
        this.codCartao = codCartao;
        this.codPassageiro = codPassageiro;
        this.clicado = false;
    }

    public Cartao(String codCartao, int codPassageiro, CategoriaCartao categoria){
        this.codCartao = codCartao;
        this.codPassageiro = codPassageiro;
        this.categoria = categoria.getCategoria();
        this.clicado = false;
    }

    public String getCodCartao() {
        return codCartao;
    }

    public void setCodCartao(String codCartao) {
        this.codCartao = codCartao;
    }

    public int getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaCartao categoria) {
        this.categoria = categoria.getCategoria();
    }

    public int getCodPassageiro() {
        return codPassageiro;
    }

    public void setCodPassageiro(int codPassageiro) {
        this.codPassageiro = codPassageiro;
    }

    public int getAtivo() {
        return ativo;
    }

    public void setAtivo(int ativo) {
        this.ativo = ativo;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {

        Log.d("TAG", "setSaldo: " + saldo);

        Locale.setDefault(Locale.ENGLISH);
        DecimalFormat fmt = new DecimalFormat("0.00");
        Log.d("TAG", "setSaldo: " + fmt.format(saldo));
        this.saldo = Double.parseDouble(fmt.format(saldo));
        Locale.setDefault(new Locale("pt", "BR"));
    }

    public double getUltimoMovimento() {
        return ultimoMovimento;
    }

    public void setUltimoMovimento(double ultimoMovimento) {
        this.ultimoMovimento = ultimoMovimento;
    }
    public String getDescrCategoria(){

        String retorno = "";
        switch (this.getCategoria()){
            case 1 :
                retorno = "Normal";
                break;
            case 2 :
                retorno = "Estudante";
                break;
            case 3 :
                retorno = "Idoso";
                break;
            case 4 :
                retorno = "Passe-Livre";
                break;
        }

        return retorno;
    }

    public void setCategoria(int i){
        CategoriaCartao categoria = null;
        switch (i){
            case 1:
                categoria = CategoriaCartao.NORMAL;
                break;
            case 2:
                categoria = CategoriaCartao.ESTUDANTE;
                break;
            case 3:
                categoria = CategoriaCartao.IDOSO;
                break;
            case 4:
                categoria = CategoriaCartao.PASSE_LIVRE;
                break;
        }
        this.setCategoria(categoria);
    }

    @Override
    public String toString() {
        return "Cartao [codCartao=" + codCartao + ", categoria=" + categoria + ", codPassageiro=" + codPassageiro
                + ", ativo=" + ativo + ", saldo=" + saldo + ", ultimoMovimento=" + ultimoMovimento + "]";
    }

    public boolean isClicado() {
        return clicado;
    }

    public void setClicado(boolean clicado) {
        this.clicado = clicado;
    }

}
