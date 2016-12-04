package com.example.vitor.testevolley;

import android.os.Bundle;
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

    EditText usuario;
    EditText senha;
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
        Toast.makeText(this, "REALIZANDO LOGIN+\nusuario: " +
                usuario.getText().toString()+"\nsenha: "+
                senha.getText().toString(), Toast.LENGTH_SHORT).show();
        realizarLogin(usuario.getText().toString(), senha.getText().toString());

        String url = "http://192.168.15.7:8080/WebServiceSPPD/sppd/logar"+"/"+usuario+"/"+senha;
        Log.d("TESTE", "realizarLogin: " + url);
        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("TESTE", "realizarLogin: TRY");
                            JSONArray arr = response.getJSONArray("loginBean");
                            JSONObject jo = arr.getJSONObject(0);
                            boolean retorno = Boolean.parseBoolean(jo.getString("retorno"));
                            String status = (jo.getString("statusRetorno"));
                            Log.i("TESTE", "realizarLogin: " + jo);
                            Toast.makeText(Login.this, "retorno: " + retorno+" status: " + status, Toast.LENGTH_SHORT).show();


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

    public void registrar(View view){

    }

    public void realizarLogin(String usuario, String senha){


    }

}
