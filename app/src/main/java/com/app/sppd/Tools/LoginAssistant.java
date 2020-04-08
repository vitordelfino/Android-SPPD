package com.app.sppd.Tools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;

import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Telas.Simulador;
import com.facebook.login.LoginManager;

/**
 * Created by vitor on 18/03/2017.
 */

public class LoginAssistant {

    public static void gravarUsuarioNoBanco(final Context contexto, String login, String password, String nome){
        SQLiteDatabase db = contexto.openOrCreateDatabase("SPPD",contexto.MODE_PRIVATE,null);
        db.execSQL("UPDATE LOGIN SET LOGIN = " + login.replace(".","").replace("-","") +
                ", SENHA = " + password.replace(".","").replace("-","") +
                ", NOME = '" + nome +
                "' WHERE ID = 1 ");
    }

    public static void deslogarUsuario(final Context contexto){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(contexto);
        alertDialog.setTitle("CONFIRMAÇÃO DE LOGOUT");
        alertDialog.setMessage("Deseja deslogar-se ?");

        alertDialog.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        SQLiteDatabase db = contexto.openOrCreateDatabase("SPPD",contexto.MODE_PRIVATE,null);
                        db.execSQL("UPDATE LOGIN SET LOGIN = '0', SENHA = '0', NOME = '0' WHERE ID = 1 ");
                        LoginManager.getInstance().logOut();


                        Intent intent = new Intent(contexto, Simulador.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        contexto.startActivity(intent);


                    }
                });
        alertDialog.show();



    }
}
