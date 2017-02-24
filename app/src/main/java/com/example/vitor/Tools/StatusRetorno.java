package com.example.vitor.Tools;

/**
 * Created by vitor on 22/02/17.
 */

public enum StatusRetorno {
    YES(true),
    NO(false);

    private boolean status;
    StatusRetorno(boolean b) {
        status = b;
    }

    public boolean getStatus(){
        return status;
    }
}
