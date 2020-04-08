package com.app.sppd.Telas;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.vitor.testevolley.R;
import com.app.sppd.Interfaces.MontarUrl;
import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Interfaces.VolleyCallbackObject;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.Retorno;
import com.app.sppd.Tools.SppdTools;
import com.app.sppd.Tools.StatusRetorno;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login extends AppCompatActivity implements MontarUrl {

    private static final String TAG = Application.class.getCanonicalName();

    private ProgressDialog dialog;
    EditText usuario;
    EditText senha;
    Retorno retorno = new Retorno();
    Passageiro passageiro = new Passageiro();

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {

        }
        catch (NoSuchAlgorithmException e) {

        }

        usuario = (EditText) findViewById(R.id.txtUsuario);
        senha = (EditText) findViewById(R.id.txtSenha);

        validarAcessoFacebook();
        validarAcesso();

        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","user_birthday"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {

                                JSONObject jo = new JSONObject();
                                try {

                                    Log.d(TAG, "onCompleted: " + object);

                                    jo.put("facebookId", object.getString("id"));
                                    jo.put("urlPicture", Profile.getCurrentProfile().getProfilePictureUri(100,100).toString());
                                    jo.put("nome", object.getString("name"));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                RealizaRequisicao.getInstance().postJson(Login.this,
                                        SppdTools.getInstance().getEndPoint() + "/passageiro/cadastrarComFacebook/",
                                        jo, new VolleyCallbackObject() {
                                            @Override
                                            public void onSuccess(JSONObject result) throws JSONException, IOException {
                                                if(result.getBoolean("retorno")){

                                                    String msg = result.getString("statusRetorno");

                                                    result = result.getJSONObject("passageiro");

                                                    passageiro.setCodPassageiro(result.getInt("codPassageiro"));
                                                    passageiro.setNome(result.getString("nome"));
                                                    passageiro.setCpf(result.getString("cpf"));
                                                    passageiro.setRg(result.getString("rg"));
                                                    passageiro.setLogradouro(result.getString("logradouro"));
                                                    passageiro.setNumero(result.getString("numero"));
                                                    passageiro.setComplemento(result.getString("complemento"));
                                                    passageiro.setCep(result.getString("cep"));
                                                    passageiro.setBairro(result.getString("bairro"));
                                                    passageiro.setMunicipio(result.getString("municipio"));
                                                    passageiro.setNascimento(result.getString("nascimento"));
                                                    passageiro.setDeficiente(result.getBoolean("deficiente"));
                                                    passageiro.setFacebookId(result.getString("facebookId"));
                                                    passageiro.setUrlPicture(result.getString("urlPicture"));

                                                    Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(Login.this, Simulador.class);
                                                    intent.putExtra("passageiro", passageiro);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                    startActivity(intent);

                                                    finish();

                                                }
                                            }
                                        });
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,birthday");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(Login.this, exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

    private void validarAcessoFacebook(){

        if(AccessToken.getCurrentAccessToken() != null){
            GraphRequest request = GraphRequest.newMeRequest(
                    AccessToken.getCurrentAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            try {
                                RealizaRequisicao.getInstance().get(Login.this,
                                        SppdTools.getInstance().getEndPoint() + "/passageiro/getPassageiroFacebookId/"+object.getString("id"), new VolleyCallbackObject() {
                                            @Override
                                            public void onSuccess(JSONObject result) throws JSONException, IOException {
                                                passageiro = new Passageiro(
                                                        result.getInt("codPassageiro"),
                                                        result.getString("nome"),
                                                        result.getString("cpf"),
                                                        result.getString("rg"),
                                                        result.getString("logradouro"),
                                                        result.getString("numero"),
                                                        result.getString("complemento"),
                                                        result.getString("cep"),
                                                        result.getString("bairro"),
                                                        result.getString("municipio"),
                                                        result.getString("nascimento"),
                                                        result.getBoolean("deficiente"),
                                                        result.getString("facebookId"),
                                                        result.getString("urlPicture"),
                                                        result.getInt("moeda"));



                                                Intent intent = new Intent(Login.this, Simulador.class);
                                                intent.putExtra("passageiro", passageiro);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                                startActivity(intent);

                                                finish();



                                            }
                                        });
                            } catch (JSONException e) {
                                Toast.makeText(Login.this, "Não foi possível retornar os dados", Toast.LENGTH_SHORT).show();
                                LoginManager.getInstance().logOut();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,link,birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }
    }



    public boolean validaCampos(){
        if(usuario.getText().toString().isEmpty() || senha.getText().toString().isEmpty()){
            Toast.makeText(this, "Os campos usuario e senha são obrigatórios", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    public void logar(View view){

        if(validaCampos()){

            RealizaRequisicao.getInstance().getArray(Login.this, url(), new VolleyCallbackArray() {
                @Override
                public void onSuccess(JSONArray jsonArray) {
                try {
                    Log.d("TAG", "onSuccess: " + jsonArray.toString());
                    JSONObject result = jsonArray.getJSONObject(0);
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
                        passageiro.setFacebookId(result.getString("facebookId"));
                        passageiro.setUrlPicture(result.getString("urlPicture"));
                        passageiro.setMoeda(result.getInt("moeda"));
                        Log.d("RetornoLogin", "onSuccess: " + retorno.getstatusMessage());
                        Log.d("RetornoLogin", "onSuccess: " + passageiro.toString());

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
        }
    }

    public void registrar(View view){

        Intent intent = new Intent(Login.this, CadastroPassageiro.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);

    }

    @Override
    public String url() {
        Log.d(TAG, "url: " + SppdTools.getInstance().getEndPoint()+"/login/logar/"+usuario.getText()+"/"+senha.getText());
        return SppdTools.getInstance().getEndPoint()+"/login/logar/"+usuario.getText()+"/"+senha.getText();
    }



    public interface VolleyCallback{
        void onSuccess(JSONObject result);
    }

}
