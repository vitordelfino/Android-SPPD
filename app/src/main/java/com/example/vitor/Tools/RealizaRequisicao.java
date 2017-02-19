package com.example.vitor.Tools;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.vitor.Telas.Login;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vitor on 07/02/17.
 *
 * Classe responsável por realizar a requisicao ao WebService e retornar um Json
 */

public class RealizaRequisicao {

    private static RealizaRequisicao realizaRequisicao = null;

    private RealizaRequisicao(){

    }

    public static RealizaRequisicao getInstance(){
        if(realizaRequisicao == null){
            realizaRequisicao = new RealizaRequisicao();
        }
        return realizaRequisicao;
    }

    public void get(Context contexto, String url, final Login.VolleyCallback callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
            }
        });
        request.add(requisicao);
    }

    public void post(Context contexto, String url, final Login.VolleyCallback callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        request.add(requisicao);
    }
}