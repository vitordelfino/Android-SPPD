package com.app.sppd.Tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.vitor.testevolley.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Interfaces.VolleyCallbackObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by vitor on 07/02/17.
 *
 * Classe responsável por realizar a requisicao ao WebService e retornar um Json
 */

public class RealizaRequisicao {

    private final static String api_rest = "https://ws-sppd.herokuapp.com/rest/sppd";

    private ProgressDialog dialog;

    private static RealizaRequisicao realizaRequisicao = null;
    private RealizaRequisicao(){

    }

    public static RealizaRequisicao getInstance(){
        if(realizaRequisicao == null){
            realizaRequisicao = new RealizaRequisicao();
        }
        return realizaRequisicao;
    }

    public void get(Context contexto, String url, final VolleyCallbackObject callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
                dialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requisicao.setRetryPolicy(policy);

        request.add(requisicao);
        dialog = new ProgressDialog(contexto);
        dialog.setMessage(contexto.getString(R.string.carregando));
        dialog.show();
    }



    public void getArray(Context contexto, String url, final VolleyCallbackArray callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonArrayRequest requisicao = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
                dialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requisicao.setRetryPolicy(policy);
        request.add(requisicao);
        dialog = new ProgressDialog(contexto);
        dialog.setMessage(contexto.getString(R.string.carregando));
        dialog.show();
    }

    public void postJsonArray(Context contexto, String url,final JSONObject jsonBody, final VolleyCallbackArray callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonArrayRequest requisicao = new JsonArrayRequest(Request.Method.POST, url,jsonBody,
                new Response.Listener<JSONArray>(){

                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
                dialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requisicao.setRetryPolicy(policy);
        request.add(requisicao);
        dialog = new ProgressDialog(contexto);
        dialog.setMessage(contexto.getString(R.string.carregando));
        dialog.show();

    }

    public void postJson(Context contexto, String url,final JSONObject jsonBody, final VolleyCallbackObject callback){

        Log.d(TAG, "postJson: URL -> " + url );
        Log.d(TAG, "postJson: Body -> " + jsonBody);

        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.POST, url,jsonBody,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "onResponse: " + response);
                        try {
                            dialog.dismiss();
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
                dialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requisicao.setRetryPolicy(policy);
        request.add(requisicao);
        dialog = new ProgressDialog(contexto);
        dialog.setMessage(contexto.getString(R.string.carregando));
        dialog.show();
    }

    public void  post(Context contexto, String url, final VolleyCallbackObject callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.POST, url,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        dialog.dismiss();
                        try {
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
                dialog.dismiss();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requisicao.setRetryPolicy(policy);
        request.add(requisicao);
        dialog = new ProgressDialog(contexto);
        dialog.setMessage(contexto.getString(R.string.carregando));
        dialog.show();
    }



    public void getJson(Context contexto, String url, final JSONObject joBody,final VolleyCallbackObject callback){
        final RequestQueue request = Volley.newRequestQueue(contexto);

        final JsonObjectRequest requisicao = new JsonObjectRequest(Request.Method.GET, url,joBody,
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            dialog.dismiss();
                            callback.onSuccess(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error ) {
                Log.i("ERROR", "onErrorResponse: " + error );
                dialog.dismiss();
            }
        })/*{
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        }*/;

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        requisicao.setRetryPolicy(policy);
        request.add(requisicao);
        dialog = new ProgressDialog(contexto);
        dialog.setMessage(contexto.getString(R.string.carregando));
        dialog.show();
    }


}
