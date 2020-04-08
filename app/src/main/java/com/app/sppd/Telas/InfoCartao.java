package com.app.sppd.Telas;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vitor.testevolley.R;
import com.app.sppd.Adapters.CartaoAdapter;
import com.app.sppd.Cartao.Cartao;
import com.app.sppd.Interfaces.MontarUrl;
import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Interfaces.VolleyCallbackObject;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.Retorno;
import com.app.sppd.Tools.SppdTools;
import com.app.sppd.mask.MascaraMonetaria;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class InfoCartao extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MontarUrl {

    EditText txtValor;
    TextView aux;
    Passageiro passageiro;
    SwipeRefreshLayout mySwipe;
    ImageView imagemUsuario;

    int opt = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_cartoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_recarga_cartao);
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

        listaCartoes(passageiro);


        /**
         * Atualiza a lista com swipe down
         */
        mySwipe = (SwipeRefreshLayout) findViewById(R.id.content_gerenciar_cartoes);
        mySwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(InfoCartao.this,"Atualizando...",Toast.LENGTH_SHORT).show();
                listaCartoes(passageiro);
            }
        });

    }


    public void listaCartoes(Passageiro passageiro){

        final ArrayList<Cartao> cartao = new ArrayList<Cartao>();

        RealizaRequisicao.getInstance().getArray(InfoCartao.this, url(), new VolleyCallbackArray() {

            @Override
            public void onSuccess(JSONArray result) {

                try {
                    //JSONArray jo = result.getJSONArray("cartao");
                    Log.d("TAG", "onSuccess: " + result);
                    for(int i = 0; i < result.length(); i++){
                        JSONObject cartaoJson = result.getJSONObject(i);
                        cartao.add(new Cartao(cartaoJson.getString("codCartao"),
                                Integer.parseInt(cartaoJson.getString("categoria")),
                                Integer.parseInt(cartaoJson.getString("codPassageiro")),
                                Integer.parseInt(cartaoJson.getString("ativo")),
                                Double.parseDouble(cartaoJson.getString("saldo")),
                                Double.parseDouble(cartaoJson.getString("ultimoMovimento"))
                        ));


                    }
                } catch (JSONException e) {
                    try{
                        JSONObject jo  = result.getJSONObject(0);
                        cartao.add(new Cartao(jo.getString("codCartao"),
                                Integer.parseInt(jo.getString("categoria")),
                                Integer.parseInt(jo.getString("codPassageiro")),
                                Integer.parseInt(jo.getString("ativo")),
                                Double.parseDouble(jo.getString("saldo")),
                                Double.parseDouble(jo.getString("ultimoMovimento"))));
                    }catch(JSONException ex){
                        ex.printStackTrace();
                    }
                }
                montaLayout(cartao);
            }
        }
        );

    }


    public void montaLayout(final ArrayList<Cartao> cartao){
        final CartaoAdapter cartaoAdapter = new CartaoAdapter(this, cartao, passageiro);

        final ListView listView = (ListView) super.findViewById(R.id.lstCartoes);
        listView.setAdapter(cartaoAdapter);
        mySwipe.setRefreshing(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Cartao c = (Cartao) listView.getAdapter().getItem(i);
                if(!c.isClicado()){
                    c.setClicado(true);
                }else{
                    c.setClicado(false);
                }
                cartaoAdapter.notifyDataSetChanged();

                Intent intent = new Intent(InfoCartao.this, HistoSaldoCartao.class);
                intent.putExtra("passageiro", passageiro);
                intent.putExtra("cartao", c);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    @Override
    public String url() {
        return SppdTools.getInstance().getEndPoint()+"/cartao/getCartoes/"+passageiro.getCodPassageiro();
    }


    public void efetuarRecarga(View view){
        aux.setText(txtValor.getText().toString().substring(1,txtValor.getText().toString().length()));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_recarga_cartao);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gerenciar_cartoes) {

        } else if (id == R.id.nav_gerenciar_perfil) {
            Intent intent = new Intent(this, GerenciarConta.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_simulador) {
            Intent intent = new Intent(this, Simulador.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }else if (id == R.id.nav_viagens){
            Intent intent = new Intent(this, HistoricoViagem.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
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
            Intent intent = new Intent(this, Moedas.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_recarga_cartao);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }






    private void alertCarregaCartao(final Cartao c){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InfoCartao.this);
        alertDialog.setTitle("RECARGA ("+c.getCodCartao()+")");
        alertDialog.setMessage("Entre com valor de recarga");

        final EditText valor = new EditText(InfoCartao.this);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER);
        valor.addTextChangedListener(new MascaraMonetaria(valor));
        valor.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        valor.setHint("0,00");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        valor.setLayoutParams(lp);

        alertDialog.setView(valor);

        alertDialog.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        NumberFormat baseFormart = NumberFormat.getCurrencyInstance();
                        try {
                            Double valorRecarga = baseFormart.parse(valor.getText().toString()).doubleValue();

                            alertConfirmaRecarga(c, valorRecarga);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                });

        alertDialog.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private void alertConfirmaRecarga(final Cartao c, final double valorRecarga){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(InfoCartao.this);
        alertDialog.setTitle("CONFIRMAÇÃO DE RECARGA");
        alertDialog.setMessage("Deseja realizar a recarga de R$ " + valorRecarga + " ");

        alertDialog.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("valor",valorRecarga);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RealizaRequisicao.getInstance().postJson(InfoCartao.this, urlRecargaCartao(c), jo, new VolleyCallbackObject() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try{
                                    Retorno retorno = new Retorno();
                                    retorno.setstatusMessage(result.getString("status"));
                                    Toast.makeText(InfoCartao.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();
                                    //listaCartoes(passageiro);
                                }catch(Exception e){

                                }

                            }
                        });
                    }
                });

        alertDialog.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private String urlRecargaCartao(Cartao c){
        return SppdTools.getInstance().getEndPoint() + "/cartao/efetuarRecarga/"+c.getCodCartao()+"/"+c.getCodPassageiro();
    }

}
