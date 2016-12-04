package com.example.vitor.testevolley;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.List;

public class Simulador extends AppCompatActivity {
    boolean  responseOk;
    private ProgressDialog dialog;
    private List<Estacao> estacoesObj; //-- lista para validar a estacao selecionada
    private List<String> nomeEstacoes; //-- lista para mostrar as estacoes
    private Spinner entradaEstacoes;
    private Spinner saidaEstacoes;
    private int entradaSelecionada;
    private int saidaSelecionada;

    List<Estacao> estacao = new ArrayList<Estacao>() ;
    final String url = "http://192.168.15.7:8080/WebServiceSPPD/sppd/getListaEstacao";
    //192.168.0.120 wifi lab 202
    //192.168.15.7 wifi casa

    //Tive que montar 2 lista pois não da pra mandar um objeto pro spinner
    //Então um mostra as estacoes e com base no retorno do spinner eu valido qual o id da mesma
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        carregaEstacoes();

    }

    public void clicar(View src){
        // preenche a posicao das estacoes de entrada e saida
        entradaSelecionada = entradaEstacoes.getSelectedItemPosition();
        saidaSelecionada = saidaEstacoes.getSelectedItemPosition();

        //mostra a telinha de processando
        dialog = ProgressDialog.show(Simulador.this,"Processando","Buscando melhor caminho....", false, true);
        dialog.setCancelable(false);

        new Thread() {
            public void run() {
                try {
                    responseOk = testar();
                }catch (Exception e) {
                }
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Simulador.this, "Informacao \r\nEntrada = " + estacoesObj.get(entradaSelecionada).getIdEstacao() +
                                                        "\nSaida = " + estacoesObj.get(saidaSelecionada).getIdEstacao(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }



    public boolean testar(){
        //somente pra ter um processo um pouco demorado para mostrar a tela de processado
        //aqui seria a requisicao do WS, se a requisicao for mto rápida, da pra fazer algo semelhante
        for(int i = 0; i < 99999999; i++){
            for(int j = 0; j < 5; j ++){

            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_simulador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void carregaEstacoes(){

        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("TESTE", "onResponse: " + response);
                            JSONArray arr = response.getJSONArray("estacao");
                            for(int i = 0; i < arr.length(); i++){
                                JSONObject jo = arr.getJSONObject(i);
                                int cod = Integer.parseInt(jo.getString("codEstacao"));
                                int linha = Integer.parseInt(jo.getString("linha"));
                                String nome = jo.getString("nome");
                                estacao.add(new Estacao(cod,linha,nome));
                            }
                        } catch (JSONException e1) {
                            Log.i("PARSE", "onResponse: " + e1);
                            e1.printStackTrace();
                        }

                        estacoesObj = estacao;
                        nomeEstacoes = Estacao.allString(estacao);

                        //preech array e spinners
                        ArrayAdapter userAdapter = new ArrayAdapter<String>(Simulador.this,android.R.layout.simple_spinner_item, nomeEstacoes);
                        entradaEstacoes = (Spinner) findViewById(R.id.user);
                        saidaEstacoes = (Spinner) findViewById(R.id.user2);
                        entradaEstacoes.setAdapter(userAdapter);
                        saidaEstacoes.setAdapter(userAdapter);



                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
            }
        });
        request.add(requisicao);
    }
}
