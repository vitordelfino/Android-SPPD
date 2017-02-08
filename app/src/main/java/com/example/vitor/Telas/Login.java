package com.example.vitor.Telas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vitor.Interfaces.MontarUrl;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Tools.Retorno;
import com.example.vitor.Tools.RealizaRequisicao;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.testevolley.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements MontarUrl {
    private ProgressDialog dialog;
    EditText usuario;
    EditText senha;

    Retorno retorno = new Retorno();
    Passageiro passageiro = new Passageiro();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = (EditText) findViewById(R.id.txtUsuario);
        senha = (EditText) findViewById(R.id.txtSenha);
    }

    public void logar(View view){
        dialog = ProgressDialog.show(Login.this,"Processando","Confirmando dados....", false, true);
        dialog.setCancelable(false);
        new Thread() {
            public void run() {
                try {


                    RealizaRequisicao.getInstance().get(Login.this, url(), new VolleyCallback() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                result = result.getJSONObject("loginBean");
                                retorno.setRetorno(Boolean.parseBoolean(result.getString("retorno")));
                                retorno.setStatusRetorno(result.getString("statusRetorno"));

                                result = result.getJSONObject("passageiro");
                                passageiro.setNome(result.getString("nome"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread.sleep(1500);

                }catch (Exception e) {
                }
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (retorno.isRetorno()) {
                            Toast.makeText(Login.this, retorno.getStatusRetorno() + "\n" + passageiro.getNome(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this, Simulador.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(Login.this, retorno.getStatusRetorno(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });
            }
        }.start();

    }

    public void registrar(View view){

        Intent intent = new Intent(Login.this, CadastroPassageiro.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    @Override
    public String url() {
        return SppdTools.getInstance().getEndPoint()+"/logar/"+usuario.getText()+"/"+senha.getText();
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}
