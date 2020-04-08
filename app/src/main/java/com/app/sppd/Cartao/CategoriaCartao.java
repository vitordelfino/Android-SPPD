package com.app.sppd.Cartao;

/**
 * Created by vitor on 28/02/17.
 */

public enum CategoriaCartao {
    NORMAL(1),
    ESTUDANTE(2),
    IDOSO(3),
    PASSE_LIVRE(4);

    private int status;

    CategoriaCartao(int c){
        status = c;
    }

    public int getCategoria(){
        return status;
    }


}
