package com.app.sppd.Telas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Adapters.HistoricoViagemAdapter;
import com.app.sppd.Estacao.Estacao;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.SppdTools;
import com.app.sppd.Viagem.Viagem;
import com.android.vitor.testevolley.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoricoViagem extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemSelectedListener {

    Passageiro passageiro;
    ImageView imagemUsuario;
    SwipeRefreshLayout mySwipe;
    final ArrayList<Viagem> historico = new ArrayList<Viagem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico_viagem);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_historico_viagem);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        passageiro = (Passageiro) intent.getSerializableExtra("passageiro");

        View header=navigationView.getHeaderView(0);
        TextView name = (TextView)header.findViewById(R.id.nomeUsuarioMenu);
        name.setText(passageiro.getNome());

        imagemUsuario = (ImageView) header.findViewById(R.id.imagemUsuario);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        imagemUsuario = (ImageView) header.findViewById(R.id.imagemUsuario);
        try {
            URL url = new URL(passageiro.getUrlPicture());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imagemUsuario.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        buscarHistorico();

        Spinner spinner = (Spinner) findViewById(R.id.spnPeriodo);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_historico_viagem);
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

        if (id == R.id.nav_gerenciar_cartoes) {
            Intent intent = new Intent(this, InfoCartao.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("passageiro", passageiro);
            startActivity(intent);
        } else if (id == R.id.nav_gerenciar_perfil) {
            Intent intent = new Intent(this, GerenciarConta.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("passageiro", passageiro);
            startActivity(intent);
        } else if (id == R.id.nav_simulador) {
            Intent intent = new Intent(this, Simulador.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("passageiro", passageiro);
            startActivity(intent);
        }else if (id == R.id.nav_viagens){

        }else if (id == R.id.nav_voce_sabia){
            Intent intent = new Intent(this, VoceSabia.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if(id == R.id.nav_moedas){
            Intent intent = new Intent(this, Moedas.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if(id == R.id.nav_watsson){
            Intent intent = new Intent(this, Watson.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_historico_viagem);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void buscarHistorico(){

            RealizaRequisicao.getInstance().getArray(HistoricoViagem.this, url(), new VolleyCallbackArray() {

                @Override
                public void onSuccess(JSONArray result) {

                    Log.d("RETORNO", "onSuccess: " + result);
                    if(result == null) {
                        Toast.makeText(HistoricoViagem.this, "Não há histórico", Toast.LENGTH_SHORT).show();
                    }else {
                        try {
                            JSONArray viagens = result;
                            for (int i = 0; i < viagens.length(); i++) {
                                JSONObject jo = viagens.getJSONObject(i);

                                JSONObject origem = jo.getJSONObject("origem");

                                Estacao eOrigem = new Estacao();
                                eOrigem.setIdEstacao(Integer.parseInt(origem.getString("codEstacao")));
                                eOrigem.setLinha(Integer.parseInt(origem.getString("linha")));
                                eOrigem.setNomeEstacao(origem.getString("nome"));

                                JSONObject destino = jo.getJSONObject("destino");
                                Estacao eDestino = new Estacao();
                                eDestino.setIdEstacao(Integer.parseInt(destino.getString("codEstacao")));
                                eDestino.setLinha(Integer.parseInt(destino.getString("linha")));
                                eDestino.setNomeEstacao(destino.getString("nome"));

                                historico.add(new Viagem(
                                        Integer.parseInt(jo.getString("passageiro")),
                                        Integer.parseInt(jo.getString("cartao")),
                                        jo.getString("dataEntrada"),
                                        jo.getString("dataSaida"),
                                        eOrigem,
                                        eDestino,
                                        Double.parseDouble(jo.getString("valor"))
                                ));
                            }
                            montarLayout(historico);

                        } catch (JSONException ex) {
                            try {
                                JSONObject viagem = result.getJSONObject(0);

                                JSONObject origem = viagem.getJSONObject("origem");
                                Estacao eOrigem = new Estacao();
                                eOrigem.setIdEstacao(Integer.parseInt(origem.getString("codEstacao")));
                                eOrigem.setLinha(Integer.parseInt(origem.getString("linha")));
                                eOrigem.setNomeEstacao(origem.getString("nome"));

                                JSONObject destino = viagem.getJSONObject("destino");
                                Estacao eDestino = new Estacao();
                                eDestino.setIdEstacao(Integer.parseInt(destino.getString("codEstacao")));
                                eDestino.setLinha(Integer.parseInt(destino.getString("linha")));
                                eDestino.setNomeEstacao(destino.getString("nome"));

                                historico.add(new Viagem(
                                        Integer.parseInt(viagem.getString("passageiro")),
                                        Integer.parseInt(viagem.getString("cartao")),
                                        viagem.getString("dataEntrada"),
                                        viagem.getString("dataSaida"),
                                        eOrigem,
                                        eDestino,
                                        Double.parseDouble(viagem.getString("valor"))
                                ));

                                montarLayout(historico);

                            } catch (JSONException ex2) {
                                ex2.printStackTrace();
                            }
                        }
                    }

                }
            });

    }

    private void montarLayout(ArrayList<Viagem> viagens){

        for(Viagem v : viagens){
            Log.d("HISTORICO", "buscarHistorico: " + v.toString());
        }

        final HistoricoViagemAdapter hAdapter = new HistoricoViagemAdapter(this,viagens);
        final ListView listView = (ListView) super.findViewById(R.id.listHistoricoViagem);
        listView.setAdapter(hAdapter);
       // mySwipe.setRefreshing(false);
    }

    private String url(){
        return SppdTools.getInstance().getEndPoint()+"/viagens/getHistoricoViagem/"+passageiro.getCodPassageiro();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<Viagem> viagens = new ArrayList<Viagem>();

        if(position == 1) {
            for (Viagem viagem : historico) {
                Date data = criaData(viagem.getDataEntrada());
                if(data.getTime() >= (new Date().getTime()-604800000)){
                    viagens.add(viagem);
                }
            }
            montarLayout(viagens);
        }else if(position == 2){
            for (Viagem viagem : historico) {
                if(criaData(viagem.getDataEntrada()).getMonth() == (new Date().getMonth())){
                    viagens.add(viagem);
                }
            }
            montarLayout(viagens);
        }else{
            montarLayout(historico);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private Date criaData(String str){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return dt.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }
}
