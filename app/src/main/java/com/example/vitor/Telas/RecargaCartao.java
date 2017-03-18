package com.example.vitor.Telas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitor.Adapters.CartaoAdapter;
import com.example.vitor.Cartao.Cartao;
import com.example.vitor.Interfaces.MontarUrl;
import com.example.vitor.Interfaces.VolleyCallbackObject;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Tools.RealizaRequisicao;
import com.example.vitor.Tools.Retorno;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.mask.MascaraMonetaria;
import com.example.vitor.testevolley.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import static android.view.View.TEXT_ALIGNMENT_CENTER;
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class RecargaCartao extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, MontarUrl{

    EditText txtValor;
    TextView aux;
    Passageiro passageiro;
    int opt = 0;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recarga_cartao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_recarga_cartao);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //aux = (TextView) findViewById(R.id.textView3);

        Intent intent = getIntent();
        passageiro = (Passageiro) intent.getSerializableExtra("passageiro");
        listaCartoes(passageiro);

    }


    public void listaCartoes(Passageiro passageiro){
        dialog = ProgressDialog.show(RecargaCartao.this,"Processando","Carregando Cartões", false, true);
        dialog.setCancelable(false);
        final ArrayList<Cartao> cartao = new ArrayList<Cartao>();

        new Thread(){
            public void run(){

                try{
                    RealizaRequisicao.getInstance().get(RecargaCartao.this, url(), new VolleyCallbackObject() {
                        @Override
                        public void onSuccess(JSONObject result) {

                            try {
                                JSONArray jo = result.getJSONArray("cartao");
                                for(int i = 0; i < jo.length(); i++){
                                    JSONObject cartaoJson = jo.getJSONObject(i);
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
                                    result = result.getJSONObject("cartao");
                                    cartao.add(new Cartao(result.getString("codCartao"),
                                            Integer.parseInt(result.getString("categoria")),
                                            Integer.parseInt(result.getString("codPassageiro")),
                                            Integer.parseInt(result.getString("ativo")),
                                            Double.parseDouble(result.getString("saldo")),
                                            Double.parseDouble(result.getString("ultimoMovimento"))));
                                }catch(JSONException ex){
                                    ex.printStackTrace();
                                }
                            }
                            montaLayout(cartao);
                        }
                    }
                    );

                }catch(Exception e ){
                }

            }
        }.start();
        dialog.dismiss();
    }


    public void montaLayout(final ArrayList<Cartao> cartao){
        final CartaoAdapter cartaoAdapter = new CartaoAdapter(this, cartao, passageiro);

        final ListView listView = (ListView) super.findViewById(R.id.lstCartoes);
        listView.setAdapter(cartaoAdapter);
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
                //alertCarregaCartao(c);
                /*LinearLayout opcoes = (LinearLayout) findViewById(R.id.opcoesCard);
                if(opt == 0) {
                    opt++;
                    final Cartao c = (Cartao) listView.getAdapter().getItem(i);

                    opcoes.setVisibility(View.VISIBLE);
                    TextView ativar = (TextView) findViewById(R.id.optAtivar);
                    TextView carregar = (TextView) findViewById(R.id.optCarregar);

                    ativar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });

                    carregar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertCarregaCartao(c);
                        }
                    });
                }else{
                    opt--;
                    opcoes.setVisibility(View.INVISIBLE);
                }*/



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

        } else if (id == R.id.nav_gerenciar_conta) {
            Intent intent = new Intent(this, GerenciarConta.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_recarga_cartao);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void alertCarregaCartao(final Cartao c){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecargaCartao.this);
        alertDialog.setTitle("RECARGA ("+c.getCodCartao()+")");
        alertDialog.setMessage("Entre com valor de recarga");

        final EditText valor = new EditText(RecargaCartao.this);
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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecargaCartao.this);
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
                        RealizaRequisicao.getInstance().postJson(RecargaCartao.this, urlRecargaCartao(c), jo, new VolleyCallbackObject() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try{
                                    Retorno retorno = new Retorno();
                                    retorno.setstatusMessage(result.getString("status"));
                                    Toast.makeText(RecargaCartao.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();
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
