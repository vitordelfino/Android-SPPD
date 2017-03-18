package com.example.vitor.Telas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.vitor.Interfaces.MontarUrl;
import com.example.vitor.Interfaces.VolleyCallbackObject;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Tools.LoginAssistant;
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

        validarAcesso();
    }

    public void validarAcesso(){
        final String nome;
        final String login;
        final String password;
        int key;


        /* ler tabela e verificar ultimo registro */
        SQLiteDatabase db = this.openOrCreateDatabase("SPPD",MODE_PRIVATE,null);
        db.execSQL("CREATE TABLE IF NOT EXISTS LOGIN(" +
                "ID INTEGER, " +
                "NOME VARCHAR(100)," +
                "LOGIN VARCHAR(100)," +
                "SENHA VARCHAR(100))");

        Cursor c = db.rawQuery("SELECT * FROM LOGIN", null);
        int collum1 = c.getColumnIndex("ID");
        int collum2 = c.getColumnIndex("NOME");
        int collum3 = c.getColumnIndex("LOGIN");
        int collum4 = c.getColumnIndex("SENHA");

        if(c.moveToNext()){
            c.moveToFirst();
            key = Integer.parseInt(c.getString(collum1));
            nome = c.getString(collum2);
            login = c.getString(collum3);
            password = c.getString(collum4);

            if(!login.equalsIgnoreCase("0")){
                /*
                    perguntar se deseja continuar logado;
                 */

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Login.this);
                alertDialog.setTitle("CONFIRMAÇÃO DE LOGIN");
                alertDialog.setMessage("Deseja continuar logado com: " + nome + " ?");

                alertDialog.setNegativeButton("Não",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.setPositiveButton("Sim",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                usuario.setText(login);
                                senha.setText(password);
                                Button b = (Button) findViewById(R.id.btnLogar);
                                logar(b);
                            }
                        });
                alertDialog.show();
            }
        }else{
            Log.d("", "validarAcesso: Inserindo usuario 000" );
            db.execSQL("INSERT INTO LOGIN(ID,NOME,LOGIN,SENHA)VALUES(1,'0','0','0')");
        }
    }

    /*public void gravarUsuarioNoBanco(String login, String password, String nome){
        SQLiteDatabase db = this.openOrCreateDatabase("SPPD",MODE_PRIVATE,null);
        db.execSQL("UPDATE LOGIN SET LOGIN = " + login +
                                    ", SENHA = " + password +
                                    ", NOME = '" + nome +
                   "' WHERE ID = 1 ");
    }*/

    public boolean validaCampos(){
        if(usuario.getText().toString().isEmpty() || senha.getText().toString().isEmpty()){
            Toast.makeText(this, "Os campos usuario e senha são obrigatórios", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    public synchronized void logar(View view){

        if(validaCampos()){
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



                                    if (retorno.isSucess()) {

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

                                        LoginAssistant.gravarUsuarioNoBanco(Login.this,usuario.getText().toString(),
                                                             senha.getText().toString(),
                                                             passageiro.getNome());

                                        Toast.makeText(Login.this, "Logado com: " + passageiro.getNome(), Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(Login.this, Simulador.class);
                                        intent.putExtra("passageiro", passageiro);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        startActivity(intent);

                                        finish();

                                    } else {
                                        Toast.makeText(Login.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                    }catch (Exception e) { }

                }
            }.start();
            dialog.dismiss();
        }


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
