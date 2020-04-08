package com.app.sppd.Telas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.app.sppd.Interfaces.VolleyCallbackObject;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.SppdTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public class Moedas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Passageiro passageiro;
    ImageView imagemUsuario;

    TextView qtdmoedasTextView;
    TextView moedasFaltantesTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moedas);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        qtdmoedasTextView = (TextView) findViewById(R.id.qtdMoedasTextView);
        moedasFaltantesTextView = (TextView) findViewById(R.id.moedasfaltantesTextView);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_moedas);
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

        try {
            atualizarMoedas();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.attMoedas);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    atualizarMoedas();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void atualizarMoedas() throws InterruptedException {


        RealizaRequisicao.getInstance().get(Moedas.this,
                SppdTools.getInstance().getEndPoint() + "/passageiro/getPassageiro/" + passageiro.getCodPassageiro(),
                new VolleyCallbackObject() {
                    @Override
                    public void onSuccess(JSONObject result) throws JSONException, IOException {
                        moedasFaltantesTextView.setText(getString(R.string.moedas_faltantes));
                        qtdmoedasTextView.setText(String.valueOf(result.getInt("moeda")));
                        int faltantes = 25 - result.getInt("moeda");
                        String moedasFaltantes = moedasFaltantesTextView.getText().toString();
                        moedasFaltantes = moedasFaltantes.replaceAll("X", String.valueOf(faltantes));
                        moedasFaltantesTextView.setText(moedasFaltantes);
                        passageiro.setMoeda(result.getInt("moeda"));
                    }
                });


    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_moedas);
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
        }else if(id == R.id.nav_watsson){
            Intent intent = new Intent(this, Watson.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_moedas);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
