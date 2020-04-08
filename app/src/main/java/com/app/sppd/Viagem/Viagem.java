package com.app.sppd.Viagem;

import com.app.sppd.Estacao.Estacao;

/**
 * Created by vitor on 18/04/2017.
 */

public class Viagem {

    private int id;
    private int passageiro;
    private int cartao;
    private String dataEntrada;
    private String dataSaida;
    private Estacao origem;
    private Estacao destino;
    private int entrandoSaindo;
    private double valor;

    public Viagem() {

    }

    public Viagem(int passageiro, int cartao, String dataEntrada, String dataSaida, Estacao origem, Estacao destino, double valor) {
        this.passageiro = passageiro;
        this.cartao = cartao;
        this.dataEntrada = dataEntrada;
        this.dataSaida = dataSaida;
        this.origem = origem;
        this.destino = destino;
        this.entrandoSaindo = entrandoSaindo;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Viagem{" +
                "passageiro=" + passageiro +
                ", cartao=" + cartao +
                ", dataEntrada='" + dataEntrada + '\'' +
                ", dataSaida='" + dataSaida + '\'' +
                ", origem=" + origem +
                ", destino=" + destino +
                ", entrandoSaindo=" + entrandoSaindo +
                ", valor=" + valor +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPassageiro() {
        return passageiro;
    }

    public void setPassageiro(int passageiro) {
        this.passageiro = passageiro;
    }

    public int getCartao() {
        return cartao;
    }

    public void setCartao(int cartao) {
        this.cartao = cartao;
    }

    public String getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(String dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(String dataSaida) {
        this.dataSaida = dataSaida;
    }

    public Estacao getOrigem() {
        return origem;
    }

    public void setOrigem(Estacao origem) {
        this.origem = origem;
    }

    public Estacao getDestino() {
        return destino;
    }

    public void setDestino(Estacao destino) {
        this.destino = destino;
    }

    public int getEntrandoSaindo() {
        return entrandoSaindo;
    }

    public void setEntrandoSaindo(int entrandoSaindo) {
        this.entrandoSaindo = entrandoSaindo;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
