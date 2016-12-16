package com.example.vitor.testevolley;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
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
                    realizarLogin(usuario.getText().toString(), senha.getText().toString(),
                            new VolleyCallback() {
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

    }

    public void realizarLogin(String usuario, String senha, final VolleyCallback callback){
        String url = "http://192.168.15.7:8080/WebServiceSPPD/sppd/logar"+"/"+usuario+"/"+senha;
        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jo = response.getJSONObject("loginBean");
                            callback.onSuccess(response);

                        } catch (JSONException e1) {
                            Log.i("PARSE", "onResponse: " + e1);
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
            }
        });
        request.add(requisicao);

    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}
