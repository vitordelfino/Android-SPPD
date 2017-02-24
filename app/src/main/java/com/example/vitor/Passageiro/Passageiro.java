package com.example.vitor.Passageiro;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by vitor on 15/12/16.
 */

public class Passageiro implements Serializable {


    private int codPassageiro;
    private String nome;
    private String cpf;
    private String rg;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String nascimento;
    private boolean deficiente;

    public Passageiro(){

    }


    public Passageiro(int codPassageiro, String nome, String cpf, String rg, String logradouro, String numero,
                      String complemento, String cep, String bairro, String municipio, String nascimento, boolean deficiente) {
        super();
        this.codPassageiro = codPassageiro;
        this.nome = nome.toUpperCase();
        this.cpf = cpf.toUpperCase();
        this.rg = rg.toUpperCase();
        this.logradouro = logradouro.toUpperCase();
        this.numero = numero.toUpperCase();
        this.complemento = complemento.toUpperCase();
        this.cep = cep.toUpperCase();
        this.bairro = bairro.toUpperCase();
        this.municipio = municipio.toUpperCase();
        this.nascimento = nascimento;
        this.deficiente = deficiente;
    }


    public int getCodPassageiro() {
        return codPassageiro;
    }
    public void setCodPassageiro(int codPassageiro) {
        this.codPassageiro = codPassageiro;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getCpf() {
        return cpf;
    }
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
    public String getRg() {
        return rg;
    }
    public void setRg(String rg) {
        this.rg = rg;
    }
    public String getLogradouro() {
        return logradouro;
    }
    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public String getComplemento() {
        return complemento;
    }
    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }
    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }
    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    public String getMunicipio() {
        return municipio;
    }
    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
    public String getNascimento() {
        return nascimento;
    }
    public void setNascimento(String nascimento) {
        this.nascimento = nascimento;
    }
    public boolean isDeficiente() {
        return deficiente;
    }
    public void setDeficiente(boolean deficiente) {
        this.deficiente = deficiente;
    }


    public String geraParametros(){
        String url = "";

        url += this.getNome()+"/"+this.getCpf()+"/"+this.getRg()+"/"+this.getLogradouro()+"/"+
                this.getNumero()+"/"+this.getComplemento()+"/"+this.getCep()+"/"+this.getBairro()+"/"+
                this.getMunicipio()+"/"+this.getNascimento()+"/";

        if(this.isDeficiente()){
            url+= "true";
        }else{
            url+="false";
        }

        url = url.replaceAll(" ", "%20");
        url = url.replaceAll("//","/VAZIO/");
        return url;

    }

    public JSONObject montarJson(){
        JSONObject retorno = new JSONObject();
        try {
            retorno.put("codPassageiro", this.getCodPassageiro());
            retorno.put("nome", this.getNome());
            retorno.put("cpf", this.getCpf());
            retorno.put("rg", this.getRg());
            retorno.put("logradouro", this.getLogradouro());
            retorno.put("numero", this.getNumero());
            retorno.put("complemento", this.getComplemento());
            retorno.put("cep", this.getCep());
            retorno.put("bairro", this.getBairro());
            retorno.put("municipio", this.getMunicipio());
            retorno.put("nascimento", this.getNascimento());
            retorno.put("deficiente",this.isDeficiente());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(null, "montarJson: " + retorno);
        return retorno;
    }


}
