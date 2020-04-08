package com.app.sppd.Tools;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.app.sppd.Enum.Linha;
import com.app.sppd.Interfaces.AtribuirCor;

/**
 * Created by vitor on 06/02/17.
 */

//ip fatec 192.168.190.162
//ip casa http://192.168.15.3
//ip hotspot celular 192.168.43.72
public class SppdTools implements AtribuirCor{
    private static SppdTools sppdTools = null;
    private String dominio = "https://ws-sppd.herokuapp.com/rest/sppd";

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
        return getDominio();
    }

    public void onBackPressed(Context contexto){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(contexto);
        alertDialog.setTitle("CONFIRMAÇÃO !!!");
        alertDialog.setMessage("Deseja fechar o app ? ");

        alertDialog.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
        alertDialog.show();
    }

    @Override
    public TextView pintarText(TextView txt, int codLinha) {

        TextView retorno = null;

        switch(codLinha){
            case 1:
                txt.setTextColor(Color.parseColor("#FF1E10EE"));
                txt.setText(Linha.AZUL.getLinha());
                retorno = txt;
                break;
            case 2:
                txt.setTextColor(Color.parseColor("#FF0C7917"));
                txt.setText(Linha.VERDE.getLinha());
                retorno = txt;
                break;
            case 3:
                txt.setTextColor(Color.parseColor("#FFE01515"));
                txt.setText(Linha.VERMELHA.getLinha());
                retorno = txt;
                break;
            case 4:
                txt.setTextColor(Color.parseColor("#FFE5DD40"));
                txt.setText(Linha.AMARELA.getLinha());
                retorno = txt;
                break;
            case 6:
                txt.setTextColor(Color.parseColor("#FFAD5BC8"));
                txt.setText(Linha.LARANJA.getLinha());
                retorno = txt;
                break;
            case 7:
                txt.setTextColor(Color.parseColor("#FFE87F16"));
                txt.setText(Linha.RUBI.getLinha());
                retorno = txt;
                break;
            case 8:
                txt.setTextColor(Color.parseColor("#FFBCBCBC"));
                txt.setText(Linha.DIAMANTE.getLinha());
                retorno = txt;
                break;
            case 9:
                txt.setTextColor(Color.parseColor("#FF9F3180"));
                txt.setText(Linha.ESMERALDA.getLinha());
                retorno = txt;
                break;
            case 10:
                txt.setTextColor(Color.parseColor("#FF52CB92"));
                txt.setText(Linha.TURQUESA.getLinha());
                retorno = txt;
                break;
            case 11:
                txt.setTextColor(Color.parseColor("#FF0C9896"));
                txt.setText(Linha.CORAL.getLinha());
                retorno = txt;
                break;
            case 12:
                txt.setTextColor(Color.parseColor("#FFF06246"));
                txt.setText(Linha.SAFIRA.getLinha());
                retorno = txt;
                break;
            case 13:
                txt.setTextColor(Color.parseColor("#FF28C870"));
                txt.setText(Linha.JADE.getLinha());
                retorno = txt;
                break;
            case 15:
                txt.setTextColor(Color.parseColor("#FFC0C0C0"));
                txt.setText(Linha.PRATA.getLinha());
                retorno = txt;
                break;
            case 17:
                txt.setTextColor(Color.parseColor("#FFFFD700"));
                txt.setText(Linha.OURO.getLinha());
                retorno = txt;
                break;
            case 18:
                txt.setTextColor(Color.parseColor("#FFCD7F32"));
                txt.setText(Linha.BRONZE.getLinha());
                retorno = txt;
                break;
            case 19:
                txt.setTextColor(Color.parseColor("#FF0EB6DF"));
                txt.setText(Linha.CELESTE.getLinha());
                retorno = txt;
                break;
            case 20:
                txt.setTextColor(Color.parseColor("#FFE76CE9"));
                txt.setText(Linha.ROSA.getLinha());
                retorno = txt;
                break;
            case 5:
                txt.setTextColor(Color.parseColor("#FFE76CE9"));
                txt.setText(Linha.LILAS.getLinha());
                retorno = txt;
                break;
        }

        return retorno;
    }
}
