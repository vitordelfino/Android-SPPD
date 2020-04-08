package com.app.sppd.Tools;

/**
 * Created by vitor on 15/12/16.
 */

public class Retorno {

    private boolean status = false;
    private String statusMessage;

    public synchronized String getstatusMessage() {
        return statusMessage;

    }

    public synchronized void setstatusMessage(String statusRetorno) {
         this.statusMessage = statusRetorno;

    }

    public synchronized boolean isSucess() {
        return status;

    }

    public synchronized void setRetorno(StatusRetorno s) {
        this.status = s.getStatus();

    }




}
