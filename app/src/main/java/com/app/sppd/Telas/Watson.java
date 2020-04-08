package com.app.sppd.Telas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vitor.testevolley.R;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.SppdTools;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.ibm.watson.developer_cloud.discovery.v1.Discovery;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryRequest;
import com.ibm.watson.developer_cloud.discovery.v1.model.query.QueryResponse;

import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class Watson extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Passageiro passageiro;
    ImageView imagemUsuario;
    private PieChart chart;

    List<String> palavras = Arrays.asList("Pollution", "Trains", "Cars");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watson);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Random random = new Random();
        String palavra = palavras.get(random.nextInt(palavras.size()));
        inicializaChart(palavra, 0f, 0f, 0f);
        new ExecutaQueryWatson().execute(palavra);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.updatewatsonFloat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random = new Random();
                String palavra = palavras.get(random.nextInt(palavras.size()));
                inicializaChart(palavra, 0f, 0f, 0f);

                new ExecutaQueryWatson().execute(palavra);
            }
        });



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_watson);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        passageiro = (Passageiro) intent.getSerializableExtra("passageiro");

        View header = navigationView.getHeaderView(0);
        TextView name = (TextView) header.findViewById(R.id.nomeUsuarioMenu);
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
    }

    private void inicializaChart (String centralText, float... v){
        //busca o gráfico pelo id
        chart = (PieChart) findViewById(R.id.chart);
        //constrói um ArrayList com dados para o gráfico, incialmente não há valores
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(v[0], "Positive"));
        entries.add(new PieEntry(v[1], "Negative"));
        entries.add(new PieEntry(v[2], "Neutral"));
        //desliga a legenda, opcional
        chart.getLegend().setEnabled(false);
        //tamanho do texto no centro
        chart.setCenterTextSize(40f);
        //o ArrayList é encapsulado por um PieDataSet
        PieDataSet set = new PieDataSet(entries, "");

        //Cores para cada possível valor, na ordem em que eles são definidos
        set.setColors(Color.BLUE, Color.RED, Color.GRAY);
        // passando os dados para o gráfico
        PieData data = new PieData(set);
        chart.setData(data);
        //configurações finais

        if (centralText != null)
            chart.setCenterText(centralText);

        chart.setCenterTextTypeface(Typeface.SANS_SERIF);
        //redesenha o gráfico
        chart.invalidate();
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_watson);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            SppdTools.getInstance().onBackPressed(this);
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
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_gerenciar_perfil) {
            Intent intent = new Intent(this, GerenciarConta.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_simulador) {
            Intent intent = new Intent(this, Simulador.class);
            intent.putExtra("passageiro", passageiro);
            startActivity(intent);

        } else if (id == R.id.nav_viagens) {
            Intent intent = new Intent(this, HistoricoViagem.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_voce_sabia) {
            Intent intent = new Intent(this, VoceSabia.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if(id == R.id.nav_moedas){
            Intent intent = new Intent(this, Moedas.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_watson);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class ExecutaQueryWatson extends AsyncTask<String, Void, float[]> {
        @Override
        protected float[] doInBackground(String... query) {
            try{
                String userName = "5d1c8ada-7aa6-4e0a-b1bf-f8d4ac236d92";
                String password = "mq3tiaNkMyME";
                String collectionID = "8ec7ea5c-8763-4431-ac02-3baf3c25f9a8";
                String environmentID = "e7ec46b5-84b8-49f4-af1a-32a7bf408f31";
                Discovery discovery = new Discovery("2016-12-01");
                discovery.setEndPoint("https://gateway.watsonplatform.net/discovery/api");
                discovery.setUsernameAndPassword(userName, password);
                QueryRequest.Builder queryBuilder = new QueryRequest.Builder(environmentID,
                        collectionID);
                queryBuilder.query("concepts.text:" + query[0]).count(50);
                float results[] = new float [3];
                QueryResponse queryResponse =
                        discovery.query(queryBuilder.build()).execute();
                for ( final Map<String, Object> item : queryResponse.getResults()){
                    if (item.get("docSentiment") != null){
                        JSONObject j = new JSONObject(item.get("docSentiment").toString());
                        if (j != null && j.get("type") != null &&
                                j.get("type").toString().equalsIgnoreCase("positive")){
                            results[0]++;
                        }
                        else if (j != null && j.get("type") != null &&
                                j.get("type").toString().equalsIgnoreCase("negative")){
                            results[1]++;
                        }
                        else{
                            results[2]++;
                        }
                    }
                }
                return results;
            }
            catch (final Exception e){
                //se der exceção o gráfico ficará com zero em todas as partes
                return new float[3];
            }
        }
        @Override
        protected void onPostExecute(float[] results) {
            //redesenha o gráfico, null pq o texto já foi configurado quando a fala foi capturada

            inicializaChart(null,results);
        }
    }

}
