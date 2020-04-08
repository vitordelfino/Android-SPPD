package com.app.sppd.Telas;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.IdRes;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vitor.testevolley.R;
import com.app.sppd.Adapters.SimuladorAdapter;
import com.app.sppd.Estacao.Estacao;
import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.SppdTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Simulador extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    boolean  responseOk;
    private List<Estacao> estacoesObj; //-- lista para validar a estacao selecionada
    private List<String> nomeEstacoes; //-- lista para mostrar as estacoes
    private Spinner entradaEstacoes;
    private Spinner saidaEstacoes;
    private int entradaSelecionada;
    private int saidaSelecionada;
    private TextView qtdEstacoes;
    private TextView vlrSimulacao;
    private TextView btnGogleMaps;
    public static final String EXTRA_ESTACAO_ENTRADA = "entrada";
    public static final String EXTRA_ESTACAO_SAIDA = "saida";
    private double preco = 0;

    private RadioGroup rdGroup;
    private RadioButton inteira;
    private RadioButton meia;

    private int mic = 0;
    List<Estacao> estacao = new ArrayList<Estacao>() ;
    final String url = SppdTools.getInstance().getEndPoint()+"/getListaEstacao";

    TextView nomePassageiro;
    Passageiro p;

    private ImageView imagemUsuario;

    private SpeechRecognizer speechRecognizer;
    private RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }
        @Override
        public void onBeginningOfSpeech() {

        }
        @Override
        public void onRmsChanged(float rmsdB) {

        }
        @Override
        public void onBufferReceived(byte[] buffer) {

        }
        @Override
        public void onEndOfSpeech() {

        }
        @Override
        public void onError(int error) {

        }
        @Override
        public void onResults(Bundle results) {
            List<String> textoCapturado =
                    results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            //coloca o resultado obtido como texto central do gráfico.
            Toast.makeText(Simulador.this,textoCapturado.get(0), Toast.LENGTH_SHORT).show();
            String aux = textoCapturado.get(0).toUpperCase().replaceAll("ESTAÇÃO ", "");

            if(mic == 1){
                if(nomeEstacoes.contains(textoCapturado.get(0).toUpperCase())){
                    for(int i = 0; i < nomeEstacoes.size(); i++){
                        if(nomeEstacoes.get(i).equals(textoCapturado.get(0).toUpperCase()))
                            entradaEstacoes.setSelection(i);
                    }
                }

            }else{
                if(nomeEstacoes.contains(textoCapturado.get(0).toUpperCase())){
                    for(int i = 0; i < nomeEstacoes.size(); i++){
                        if(nomeEstacoes.get(i).equals(textoCapturado.get(0).toUpperCase()))
                            saidaEstacoes.setSelection(i);
                    }
                }
            }



        }@Override
        public void onPartialResults(Bundle partialResults) {

        }
        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

    //Tive que montar 2 lista pois não da pra mandar um objeto pro spinner
    //Então um mostra as estacoes e com base no retorno do spinner eu valido qual o id da mesma
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulador);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(listener);

        rdGroup = (RadioGroup) findViewById(R.id.radioGroup);
        inteira = (RadioButton) findViewById(R.id.rbInteira);
        meia = (RadioButton) findViewById(R.id.rbMeia);
        btnGogleMaps = (TextView) findViewById(R.id.txtBtnMaps);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_simulador);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        p = (Passageiro) intent.getSerializableExtra("passageiro");

        View header=navigationView.getHeaderView(0);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        imagemUsuario = (ImageView) header.findViewById(R.id.imagemUsuario);
        TextView name = (TextView)header.findViewById(R.id.nomeUsuarioMenu);



        try {
            name.setText(p.getNome());
            URL url = new URL(p.getUrlPicture());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imagemUsuario.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        qtdEstacoes = (TextView) findViewById(R.id.txtQtdEstacoes);
        vlrSimulacao = (TextView) findViewById(R.id.txtVlrSimulacao);

        carregaEstacoes();


        rdGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                if(preco > 0){
                    if(checkedId == inteira.getId()){
                        preco = preco * 2;
                        atribuirValor();
                    }else{
                        preco = preco / 2;
                        atribuirValor();
                    }
                }

            }
        });


    }

    /* metodo que faz a requisição ao web service e retorna as estações percorridas
    com origem e destino selecionado */
    public void clicar(View src){
        // preenche a posicao das estacoes de entrada e saida
        entradaSelecionada = entradaEstacoes.getSelectedItemPosition();
        saidaSelecionada = saidaEstacoes.getSelectedItemPosition();

        //mostra a telinha de processando
        JSONObject joOrigemDestino = new JSONObject();
        try{
            joOrigemDestino.put("origem",String.valueOf(estacoesObj.get(entradaSelecionada).getIdEstacao()));
            joOrigemDestino.put("destino",String.valueOf(estacoesObj.get(saidaSelecionada).getIdEstacao()));
            Log.d("SIMULADOR", "clicar: " + joOrigemDestino.toString());
        }catch(JSONException e){
            e.printStackTrace();
        }

        final ArrayList<Estacao> estacoes = new ArrayList<Estacao>();
        RealizaRequisicao.getInstance().postJsonArray(Simulador.this, urlSimulador(),joOrigemDestino,new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray result) {
                try{
                    JSONArray array = result;
                    for(int i = 0; i < array.length(); i++){
                        JSONObject jo = array.getJSONObject(i);
                        try{
                            estacoes.add(new Estacao(Integer.parseInt(jo.getString("codEstacao")),
                                Integer.parseInt(jo.getString("linha")),
                                jo.getString("nome")));
                                btnGogleMaps.setVisibility(View.VISIBLE);
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }

                    }
                }catch (JSONException e){
                    try{
                        JSONObject jo = result.getJSONObject(0);
                        estacoes.add(new Estacao(Integer.parseInt(jo.getString("codEstacao")),
                                Integer.parseInt(jo.getString("linha")),
                                jo.getString("nome")));
                    }catch(JSONException ex2){
                        ex2.printStackTrace();
                    }

                }
                montarListaEstacoes(estacoes);
            }
        });
    }

    private String urlSimulador(){
        return SppdTools.getInstance().getEndPoint()+"/dijkstra/encontrarMenorCaminho/";
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_simulador);
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
            intent.putExtra("passageiro", p);
            startActivity(intent);
        } else if (id == R.id.nav_gerenciar_perfil) {
            Intent intent = new Intent(this, GerenciarConta.class);
            intent.putExtra("passageiro", p);
            startActivity(intent);
        } else if (id == R.id.nav_simulador) {

        }else if (id == R.id.nav_viagens){
            Intent intent = new Intent(this, HistoricoViagem.class);
            intent.putExtra("passageiro", p);
            startActivity(intent);
        }else if (id == R.id.nav_voce_sabia){
            Intent intent = new Intent(this, VoceSabia.class);
            intent.putExtra("passageiro", p);
            startActivity(intent);
        }else if (id == R.id.nav_moedas){
            Intent intent = new Intent(this, Moedas.class);
            intent.putExtra("passageiro", p);
            startActivity(intent);
        }else if(id == R.id.nav_watsson){
            Intent intent = new Intent(this, Watson.class);
            intent.putExtra("passageiro", p);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_simulador);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }else{
            super.onResume();
        }
    }

    public void carregaEstacoes() {

        RealizaRequisicao.getInstance().getArray(this, url, new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray arr) {

                try {
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject jo = arr.getJSONObject(i);
                        int cod = Integer.parseInt(jo.getString("codEstacao"));
                        int linha = Integer.parseInt(jo.getString("linha"));
                        String nome = jo.getString("nome");
                        estacao.add(new Estacao(cod, linha, nome));
                    }

                    estacoesObj = estacao;
                    nomeEstacoes = Estacao.allString(estacao);

                    //preech array e spinners
                    ArrayAdapter userAdapter = new ArrayAdapter<String>(Simulador.this, android.R.layout.simple_spinner_item, nomeEstacoes);
                    entradaEstacoes = (Spinner) findViewById(R.id.user);
                    saidaEstacoes = (Spinner) findViewById(R.id.user2);
                    entradaEstacoes.setAdapter(userAdapter);
                    saidaEstacoes.setAdapter(userAdapter);


                } catch (JSONException e1) {
                    Log.i("PARSE", "onResponse: " + e1);
                    e1.printStackTrace();
                }


            }
        });

    }

    private void montarListaEstacoes(ArrayList<Estacao> estacoes){
        final SimuladorAdapter sAdapter = new SimuladorAdapter(this,estacoes);
        final ListView listView = (ListView) super.findViewById(R.id.lstEstacoes);
        listView.setAdapter(sAdapter);

        qtdEstacoes.setText("Percorreu " + String.valueOf(estacoes.size()) + " estações por: ");

        switch (rdGroup.getCheckedRadioButtonId()){
            case R.id.rbInteira:
                preco = estacoes.size() * 0.30;
                break;
            case R.id.rbMeia:
                preco = estacoes.size() * 0.15;
                break;
        }

        atribuirValor();

    }

    private void atribuirValor(){

        NumberFormat baseFormart = NumberFormat.getCurrencyInstance();
        vlrSimulacao.setText(baseFormart.format(preco) );

    }

    public void speachOrigem(View view){
        mic = 1;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt-BR");
        //iremos pegar somente um resultado, aquele cuja probabilidade for maior
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        //inicia a escuta com base na intenção criada
        speechRecognizer.startListening(intent);

    }

    public void speachDestino(View view){
        mic = 2;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "pt-BR");
        //iremos pegar somente um resultado, aquele cuja probabilidade for maior
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1);
        //inicia a escuta com base na intenção criada
        speechRecognizer.startListening(intent);

    }

    public void onClickMap(View view){
        Intent i = new Intent(this, MapActivity.class);
        String teste = entradaEstacoes.getSelectedItem().toString();
        i.putExtra(EXTRA_ESTACAO_ENTRADA, entradaEstacoes.getSelectedItem().toString());
        i.putExtra(EXTRA_ESTACAO_SAIDA, saidaEstacoes.getSelectedItem().toString());
        //i.putExtra(((Estacao)entradaEstacoes.getSelectedItem()).getNomeEstacao(),"entrada");

        startActivity(i);
    }

}
