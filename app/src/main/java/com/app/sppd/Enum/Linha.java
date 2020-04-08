package com.app.sppd.Enum;

/**
 * Created by vitor on 02/04/2017.
 */

public enum Linha {
    AZUL("LINHA AZUL"),
    VERDE("LINHA VERDE"),
    VERMELHA("LINHA VERMELHA"),
    AMARELA("LINHA AMARELA"),
    LILAS("LINHA LILAS"),
    LARANJA("LINHA LARANJA"),
    RUBI("LINHA RUBI"),
    DIAMANTE("LINHA DIAMANTE"),
    ESMERALDA("LINHA ESMERALDA"),
    TURQUESA("LINHA TURQUESA"),
    CORAL("LINHA CORAL"),
    SAFIRA("LINHA SAFIRA"),
    JADE("LINHA JADE"),
    PRATA("LINHA PRATA"),
    OURO("LINHA OURO"),
    BRONZE("LINHA BRONZE"),
    CELESTE("LINHA CELESTE"),
    ROSA("LINHA ROSA");

    private String linha;
    Linha(String l){
        linha = l;
    }

    public String getLinha(){return linha;}
}
