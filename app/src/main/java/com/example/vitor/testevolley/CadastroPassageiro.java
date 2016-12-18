package com.example.vitor.testevolley;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CadastroPassageiro extends AppCompatActivity {

    private ProgressDialog dialog;

    private Spinner txtDeficiente;
    private TextView txtnome;
    private TextView txtcpf;
    private TextView txtrg;
    private TextView txtlogradouro;
    private TextView txtnumero;
    private TextView txtcomplemento;
    private TextView txtcep;
    private TextView txtbairro;
    private TextView txtmunicipio;
    private TextView txtnascimento;

    private String nome;
    private String cpf;
    private String rg;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String nascimento;
    private boolean isDeficiente = false;

    private Passageiro p;

    private String url = "";

    Retorno retorno = new Retorno();

    List<String> listDeficiente = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_passageiro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listDeficiente.add("Não");
        listDeficiente.add("Sim");

        ArrayAdapter userAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listDeficiente);
        txtDeficiente = (Spinner) findViewById(R.id.txtDeficiente);
        txtDeficiente.setAdapter(userAdapter);

        txtnome = (TextView) findViewById(R.id.txtNome);
        txtcpf = (TextView) findViewById(R.id.txtCPF);
        txtrg = (TextView) findViewById(R.id.txtRG);
        txtlogradouro = (TextView) findViewById(R.id.txtLogradouro);
        txtnumero = (TextView) findViewById(R.id.txtNumero);
        txtcomplemento = (TextView) findViewById(R.id.txtComplemento);
        txtcep = (TextView) findViewById(R.id.txtCEP);
        txtbairro = (TextView) findViewById(R.id.txtBairro);
        txtmunicipio = (TextView) findViewById(R.id.txtMunicipio);
        txtnascimento = (TextView) findViewById(R.id.txtData);



    }

    public void registrar(View view){

        url = "http://192.168.15.7:8080/WebServiceSPPD/sppd/cadastraPassageiro/";

        nome = txtnome.getText().toString();
        cpf = txtcpf.getText().toString();
        rg = txtrg.getText().toString();
        logradouro = txtlogradouro.getText().toString();
        numero = txtnumero.getText().toString();
        complemento = txtcomplemento.getText().toString();
        cep = txtcep.getText().toString();
        bairro = txtbairro.getText().toString();
        municipio = txtmunicipio.getText().toString();
        nascimento = txtnascimento.getText().toString();
        if(txtDeficiente.getSelectedItemPosition() == 1){
            isDeficiente = true;
        }
        p = new Passageiro(0,nome,cpf,rg,logradouro,numero,complemento,cep,bairro,municipio,nascimento,isDeficiente);

        url += p.geraURL();




        dialog = ProgressDialog.show(CadastroPassageiro.this,"Processando","Realizando Cadastro....", false, true);
        dialog.setCancelable(false);
        new Thread() {
            public void run() {
                try {
                    realizaCadastro(url,
                            new Login.VolleyCallback() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    try {
                                        retorno.setStatusRetorno(result.getString("status"));
                                        retorno.setRetorno(Boolean.parseBoolean(result.getString("retorno")));
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
                            AlertDialog.Builder configAlerta = new AlertDialog.Builder(CadastroPassageiro.this);

                                configAlerta.setTitle("ALERTA");
                                configAlerta.setMessage("Sua senha é o seu CPF, deseja altera-la agora?");
                                configAlerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        Intent intent = new Intent(CadastroPassageiro.this, Simulador.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                                configAlerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(CadastroPassageiro.this, "Vamos implementar esta funcao", Toast.LENGTH_LONG).show();
                                    }
                                });
                                AlertDialog alerta = configAlerta.create();
                                alerta.show();


                        } else {
                            Toast.makeText(CadastroPassageiro.this, retorno.getStatusRetorno(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });
            }
        }.start();



    }

    public void realizaCadastro(String url, final Login.VolleyCallback callback){

        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jo = response.getJSONObject("retorno");
                            callback.onSuccess(jo);

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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        request.add(requisicao);

    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}
