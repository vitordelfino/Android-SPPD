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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vitor.testevolley.R;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.SppdTools;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class VoceSabia extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Passageiro passageiro;
    ImageView imagemUsuario;
    SwipeRefreshLayout mySwipe;
    TextView vocesabiaTextview;
    List<String> curiosidades = Arrays.asList(
            "Cada litro de gasolina tem o peso de 800g. " +
                    "Quando entra em combustão o carbono do qual é composto o petróleo, " +
                    "matéria prima para a gasolina, se combinam com o ar da atmosfera " +
                    "produzindo simplesmente mais de 100 vezes esse peso que dá 8.5 kg " +
                    "em dióxido de carbono, ou seja, um dos maiores vilões causadores do " +
                    "efeito estufa que em última análise é responsável pelo aquecimento " +
                    "global e o buraco na camada de ozônio.",

            "Se uma família possui mais de um carro, a poluição gerada apenas por meio do " +
                    "transporte será superior ao total produzido por todas as outras atividades " +
                    "cotidianas juntas, incluindo-se:  consumo de energia, produção de todos " +
                    "os resíduos e demais atividades.",

            "Carros e centrais de produção de eletricidade são os principais " +
                    "“fabricantes” de dióxido de carbono, ou seja, se há cerca de " +
                    "500 milhões de veículos no mundo, são emitidos no ar 4 bilhões de " +
                    "toneladas do composto que vem a ser 1/5 de tudo o que é emitido pelo homem no planeta.",

            "Se apenas 1 em cada 10 pessoas utilizasse transporte coletivo nas grandes cidades, " +
                    "teríamos uma diminuição de cerca de 7.301 toneladas de monóxido de carbono, " +
                    "além de 120 toneladas de óxidos de azoto e também 45.913 toneladas de dióxido de carbono.",

            "Em engarrafamentos, se você acha que está bem protegido em seu carro fechado, " +
                    "pense de novo: estudos revelam que a qualidade do ar no interior do " +
                    "veículo é pior que no exterior; isso ocorre por conta do contato entre " +
                    "o sistema de ventilação de seu automóvel e o escapamento do veículo em frente, " +
                    "que é sugado pelo seu sistema de ventilação/ar condicionado em geral, localizado frontalmente no veículo.");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voce_sabia);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_voce_sabia);
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

        vocesabiaTextview = (TextView) findViewById(R.id.vocesabiaTextView);

        mySwipe = (SwipeRefreshLayout) findViewById(R.id.content_voce_sabia);
        mySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                buscarCuriosidade();
            }
        });

        buscarCuriosidade();

    }

    private void buscarCuriosidade() {
        Random radom = new Random();

        int i = radom.nextInt(curiosidades.size());

        while (vocesabiaTextview.getText().equals(curiosidades.get(i))) {
            i = radom.nextInt(curiosidades.size());
        }
        vocesabiaTextview.setText(curiosidades.get(i));
        mySwipe.setRefreshing(false);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_voce_sabia);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_voce_sabia);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
