package com.example.vitor.Telas;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vitor.Interfaces.MontarUrl;
import com.example.vitor.Interfaces.VolleyCallbackObject;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Tools.RealizaRequisicao;
import com.example.vitor.Tools.Retorno;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.Tools.StatusRetorno;
import com.example.vitor.testevolley.R;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements MontarUrl {
    private ProgressDialog dialog;
    EditText usuario;
    EditText senha;
    Retorno retorno = new Retorno();
    Passageiro passageiro = new Passageiro();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usuario = (EditText) findViewById(R.id.txtUsuario);
        senha = (EditText) findViewById(R.id.txtSenha);
    }

    public synchronized void logar(View view){
        dialog = ProgressDialog.show(Login.this,"Processando","Confirmando dados....", false, true);
        dialog.setCancelable(false);
        new Thread() {
            public void run() {
                try {


                    RealizaRequisicao.getInstance().get(Login.this, url(), new VolleyCallbackObject() {
                        @Override
                        public void onSuccess(JSONObject result) {
                            try {
                                result = result.getJSONObject("loginBean");
                                if(result.getString("retorno").equalsIgnoreCase("true")){
                                    retorno.setRetorno(StatusRetorno.YES);
                                }else{
                                    retorno.setRetorno(StatusRetorno.NO);
                                }

                                retorno.setstatusMessage(result.getString("statusRetorno"));

                                result = result.getJSONObject("passageiro");
                                passageiro.setBairro(result.getString("bairro"));
                                passageiro.setCep(result.getString("cep"));
                                passageiro.setCodPassageiro(Integer.parseInt(result.getString("codPassageiro")));
                                passageiro.setComplemento(result.getString("complemento"));
                                passageiro.setCpf(result.getString("cpf"));
                                passageiro.setDeficiente(Boolean.parseBoolean(result.getString("deficiente")));
                                passageiro.setLogradouro(result.getString("logradouro"));
                                passageiro.setMunicipio(result.getString("cpf"));
                                passageiro.setNascimento(result.getString("nascimento"));
                                passageiro.setNumero(result.getString("numero"));
                                passageiro.setRg(result.getString("rg"));
                                passageiro.setNome(result.getString("nome"));
                                    Log.d("RetornoLogin", "onSuccess: " + retorno.getstatusMessage());

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    Thread.sleep(1500);

                }catch (Exception e) { }
                dialog.dismiss();

                runOnUiThread(new Runnable() {
                    public void run() {

                        if (retorno.isSucess()) {
                            Toast.makeText(Login.this, retorno.getstatusMessage() + "\n" + passageiro.getNome(), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(Login.this, Simulador.class);
                            intent.putExtra("passageiro", passageiro);
                            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            startActivity(intent);

                            finish();

                        } else {
                            Toast.makeText(Login.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }.start();

    }

    public void registrar(View view){

        Intent intent = new Intent(Login.this, CadastroPassageiro.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    @Override
    public String url() {
        return SppdTools.getInstance().getEndPoint()+"/login/logar/"+usuario.getText()+"/"+senha.getText();
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}
