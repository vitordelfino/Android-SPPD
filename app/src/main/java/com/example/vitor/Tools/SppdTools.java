package com.example.vitor.Tools;

/**
 * Created by vitor on 06/02/17.
 */

//ip fatec 192.168.190.162
//ip casa http://192.168.15.3
//ip hotspot celular 192.168.43.72
public class SppdTools {
    private static SppdTools sppdTools = null;
    private String dominio = "http://192.168.15.10:8080";

    private SppdTools(){

    }

    public static SppdTools getInstance(){
        if(sppdTools == null){
            sppdTools = new SppdTools();
        }
        return sppdTools;
    }

    private String getDominio(){
        return dominio;
    }

    public String getEndPoint(){
        return getDominio()+"/WebServiceSPPD/sppd";
    }
}
