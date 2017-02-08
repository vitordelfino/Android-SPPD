package com.example.vitor.Tools;

/**
 * Created by vitor on 15/12/16.
 */

public class Retorno {

    private boolean retorno;
    private String statusRetorno;

    public synchronized String getStatusRetorno() {
        return statusRetorno;

    }

    public synchronized void setStatusRetorno(String statusRetorno) {
         this.statusRetorno = statusRetorno;

    }

    public synchronized boolean isRetorno() {
        return retorno;

    }

    public synchronized void setRetorno(boolean retorno) {
        this.retorno = retorno;

    }




}
