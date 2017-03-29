package com.example.vitor.Cartao;

import java.text.DecimalFormat;

/**
 * Created by vitor on 22/03/2017.
 */

public class HistoricoSaldoCartaoBean {

    private int codCartao;
    private String dataTransacao;
    private double saldoAnterior;
    private double saldoAtual;
    private double valor;


    private String descricao;
    public HistoricoSaldoCartaoBean(int codCartao, String dataTransacao, double saldoAnterior, double saldoAtual, String descricao, double valor){
        this.setCodCartao(codCartao);
        this.setDataTransacao(dataTransacao);
        this.setSaldoAnterior(saldoAnterior);
        this.setSaldoAtual(saldoAtual);
        this.setDescricao(descricao);
        this.setValor(valor);
    }

    public HistoricoSaldoCartaoBean(){

    }

    public int getCodCartao() {
        return codCartao;
    }
    public void setCodCartao(int codCartao) {
        this.codCartao = codCartao;
    }
    public String getDataTransacao() {
        return dataTransacao;
    }
    public void setDataTransacao(String dataTransacao) {
        this.dataTransacao = dataTransacao;
    }
    public double getSaldoAnterior() {
        return saldoAnterior;
    }
    public void setSaldoAnterior(double saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }
    public double getSaldoAtual() {
        return saldoAtual;
    }
    public void setSaldoAtual(double saldoAtual) {
        this.saldoAtual = saldoAtual;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        DecimalFormat fmt = new DecimalFormat("#.00");
        fmt.setMinimumFractionDigits(2);
        this.valor = Double.parseDouble(fmt.format(valor));
    }
    @Override
    public String toString() {
        return "HistoricoSaldoCartaoBean [codCartao=" + codCartao + ", dataTransacao=" + dataTransacao
                + ", saldoAnterior=" + saldoAnterior + ", saldoAtual=" + saldoAtual + "]";
    }
}