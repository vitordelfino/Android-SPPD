package com.example.vitor.Telas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vitor.Estacao.Estacao;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Tools.LoginAssistant;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.testevolley.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Simulador extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    boolean  responseOk;
    private ProgressDialog dialog;
    private List<Estacao> estacoesObj; //-- lista para validar a estacao selecionada
    private List<String> nomeEstacoes; //-- lista para mostrar as estacoes
    private Spinner entradaEstacoes;
    private Spinner saidaEstacoes;
    private int entradaSelecionada;
    private int saidaSelecionada;

    List<Estacao> estacao = new ArrayList<Estacao>() ;
    final String url = SppdTools.getInstance().getEndPoint()+"/getListaEstacao";

    TextView nomePassageiro;
    Passageiro p;
    //Tive que montar 2 lista pois não da pra mandar um objeto pro spinner
    //Então um mostra as estacoes e com base no retorno do spinner eu valido qual o id da mesma
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_simulador);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        p = (Passageiro) intent.getSerializableExtra("passageiro");

        carregaEstacoes();

    }

    public void atualizaMenu(){
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_simulador);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lateral, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            LoginAssistant.deslogarUsuario(this);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_recarregar) {
            Intent intent = new Intent(this, RecargaCartao.class);
            intent.putExtra("passageiro", p);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_gerenciar_conta) {
            Intent intent = new Intent(this, GerenciarConta.class);
            intent.putExtra("passageiro", p);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_simulador);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void carregaEstacoes(){

        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
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
