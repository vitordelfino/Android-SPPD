package com.example.vitor.testevolley;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private TextView t;
    private EditText e;
    private Map<String, String> params;
    final String url = "http://192.168.15.9:8080/TesteJson/estacao/getEstacoes";
    List<Estacao> estacao = new ArrayList<Estacao>() ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        t = (TextView) findViewById(R.id.textView);
        e = (EditText) findViewById(R.id.edText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void clicar(View view){

        final RequestQueue request = Volley.newRequestQueue(this);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray arr = response.getJSONArray("estacaoBean");
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
                        t.setText(response.toString());
                        Log.i("MOSTRAR dentro do TRY", "clicar: " + estacao.toString());

                    }
                }, new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error ) {
                        Log.i("ERROR", "onErrorResponse: " + error );
                            t.setText(error.toString());
                    }
                });

        new Thread(){
            public void run(){
                request.add(requisicao);
            }
        }.start();

        Log.i("MOSTRAR", "clicar: " + estacao.toString());
    }
}
