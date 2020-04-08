package com.app.sppd.Telas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.app.sppd.Interfaces.MontarUrl;
import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Interfaces.VolleyCallbackObject;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.Retorno;
import com.app.sppd.Tools.SppdTools;
import com.app.sppd.mask.Mask;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.RealizaRequisicao;
import com.android.vitor.testevolley.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class CadastroPassageiro extends AppCompatActivity implements MontarUrl {


    private Spinner txtDeficiente;
    private EditText txtnome;
    private EditText txtcpf;
    private EditText txtrg;
    private EditText txtlogradouro;
    private EditText txtnumero;
    private EditText txtcomplemento;
    private EditText txtcep;
    private EditText txtbairro;
    private EditText txtmunicipio;
    private EditText txtnascimento;

    private String nome;
    private String cpf;
    private String rg;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private String bairro;
    private String municipio;
    private String nascimento;
    private boolean isDeficiente = false;

    private static Passageiro p;

    Retorno retorno = new Retorno();

    List<String> listDeficiente = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_passageiro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listDeficiente.add("Deficiente ?");
        listDeficiente.add("Não");
        listDeficiente.add("Sim");

        ArrayAdapter userAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listDeficiente);
        txtDeficiente = (Spinner) findViewById(R.id.txtDeficiente);
        txtDeficiente.setAdapter(userAdapter);

        txtnome = (EditText) findViewById(R.id.txtNome);
        txtcpf = (EditText) findViewById(R.id.txtCPF);
        txtcpf.addTextChangedListener(Mask.insert(Mask.CPF_MASK, txtcpf));
        txtrg = (EditText) findViewById(R.id.txtRG);
        txtrg.addTextChangedListener(Mask.insert(Mask.RG_MASK, txtrg));
        txtlogradouro = (EditText) findViewById(R.id.txtLogradouro);
        txtnumero = (EditText) findViewById(R.id.txtNumero);
        txtcomplemento = (EditText) findViewById(R.id.txtComplemento);
        txtcep = (EditText) findViewById(R.id.txtCEP);
        txtcep.addTextChangedListener(Mask.insert(Mask.CEP_MASK, txtcep));

        txtbairro = (EditText) findViewById(R.id.txtBairro);
        txtmunicipio = (EditText) findViewById(R.id.txtMunicipio);
        txtnascimento = (EditText) findViewById(R.id.txtData);
        txtnascimento.addTextChangedListener(Mask.insert(Mask.DATA_MASK, txtnascimento));

        txtcep.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    RealizaRequisicao.getInstance().get(CadastroPassageiro.this, buscaCep(txtcep.getText().toString()), new VolleyCallbackObject() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try{

                                Log.d("", "onSuccess: " + result.toString());
                                txtmunicipio.setText(result.getString("localidade"));
                                txtlogradouro.setText(result.getString("logradouro"));
                                txtbairro.setText(result.getString("bairro"));
                            }catch(Exception e){
                            }

                        }
                    });

                }
            }
        });
    }

    public String buscaCep(String cep){
        Log.d("", "buscaCep: "+"https://viacep.com.br/ws/"+cep.replaceAll("-","")+"/json/");
        return "https://viacep.com.br/ws/"+cep.replaceAll("-","")+"/json/";
    }

    public void registrar(View view){
        nome = txtnome.getText().toString();
        cpf = txtcpf.getText().toString();
        rg = txtrg.getText().toString();
        logradouro = txtlogradouro.getText().toString();
        numero = txtnumero.getText().toString();
        complemento = txtcomplemento.getText().toString();
        cep = txtcep.getText().toString();
        bairro = txtbairro.getText().toString();
        municipio = txtmunicipio.getText().toString();
        nascimento = txtnascimento.getText().toString();
        if(txtDeficiente.getSelectedItemPosition() == 2){
            isDeficiente = true;
        }
        p = new Passageiro(0,nome,cpf,rg,logradouro,numero,complemento,cep,bairro,municipio,nascimento,isDeficiente);

        RealizaRequisicao.getInstance().postJsonArray(CadastroPassageiro.this, url(),p.montarJson(), new VolleyCallbackArray() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    JSONObject result = response.getJSONObject(0);
                    retorno.setstatusMessage(result.getString("status"));
                    if (result.getString("retorno").equalsIgnoreCase("true")){
                        atualizaLoginAutomatico(p.getCpf(),p.getCpf(),p.getNome());

                        AlertDialog.Builder configAlerta = new AlertDialog.Builder(CadastroPassageiro.this);

                        configAlerta.setTitle("ALERTA");
                        configAlerta.setMessage("Sua senha é o seu CPF, deseja alterar agora?");
                        configAlerta.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(CadastroPassageiro.this, Simulador.class);
                                intent.putExtra("passageiro", p);
                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(intent);
                                finish();
                            }
                        });

                        configAlerta.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                alertAlterarSenha(p);

                            }
                        });
                        AlertDialog alerta = configAlerta.create();
                        alerta.show();

                    }else{
                        Toast.makeText(CadastroPassageiro.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }






    private void atualizaLoginAutomatico(String longin, String senha, String nome){
        LoginAssistant.gravarUsuarioNoBanco(CadastroPassageiro.this,longin,senha,
                nome);
    }

    public void realizaCadastro(String url, final Login.VolleyCallback callback){

        final RequestQueue request = Volley.newRequestQueue(this);

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
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                return params;
            }
        };
        request.add(requisicao);

    }

    @Override
    public String url() {
        return SppdTools.getInstance().getEndPoint()+"/passageiro/cadastraPassageiro/"/*+p.geraParametros()*/;
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

    private void alertAlterarSenha(final Passageiro p){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CadastroPassageiro.this);
        alertDialog.setTitle("ALTERAÇÃO DE SENHA");
        alertDialog.setMessage("Entre com sua Senha");

        final EditText senha = new EditText(CadastroPassageiro.this);
        senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        senha.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        senha.setHint("senha");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        senha.setLayoutParams(lp);

        alertDialog.setView(senha);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertConfirmaSenha(p, senha.getText().toString());
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        avancarTela();
                    }
                });

        alertDialog.show();


    }


    private void alertConfirmaSenha(final Passageiro p, final String senha){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(CadastroPassageiro.this);
        alertDialog.setTitle("ALTERACAO DE SENHA");
        alertDialog.setMessage("Confirme sua Senha");

        final EditText confirmaSenha = new EditText(CadastroPassageiro.this);
        confirmaSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmaSenha.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        confirmaSenha.setHint("confirmação de senha");


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        confirmaSenha.setLayoutParams(lp);
        alertDialog.setView(confirmaSenha);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ProgressDialog dialogSenha = ProgressDialog.show(CadastroPassageiro.this,"Processando","Realizando Cadastro....", false, true);
                        dialogSenha.setCancelable(false);
                        if(!confirmaSenha.getText().toString().equals(senha)){
                            Toast.makeText(CadastroPassageiro.this, "Senhas digitadas se diferem, tente novamente", Toast.LENGTH_SHORT).show();
                            alertAlterarSenha(p);
                        }else {

                            RealizaRequisicao.getInstance().post(CadastroPassageiro.this, urlTrocaSenha(p, senha), new VolleyCallbackObject() {
                                @Override
                                public void onSuccess(JSONObject result) {
                                    Retorno retornoSenha = new Retorno();
                                    try{
                                        if(result.getString("retorno").equals("true")){
                                            Toast.makeText(CadastroPassageiro.this, "Senha Alterada", Toast.LENGTH_SHORT).show();
                                            atualizaLoginAutomatico(p.getCpf(),senha,p.getNome());
                                        }else{
                                            Toast.makeText(CadastroPassageiro.this, "Não foi possível alterar a senha !", Toast.LENGTH_SHORT).show();
                                        }
                                    }catch(JSONException e){
                                        Toast.makeText(CadastroPassageiro.this, "Não foi possível alterar a senha !", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }
                        avancarTela();
                        dialogSenha.dismiss();

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        avancarTela();

                    }
                });

        alertDialog.show();
    }

    private String urlTrocaSenha(Passageiro p, String novaSenha){
        String url = SppdTools.getInstance().getEndPoint() + "/login/alterarSenha/" + p.getCpf().replace(".","").replace("-","") + "/" + novaSenha;
        Log.d(null, "urlTrocaSenha: " + url);
        return url;
    }

    private void avancarTela(){
        Intent intent = new Intent(CadastroPassageiro.this, Simulador.class);
        intent.putExtra("passageiro", p);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

}
