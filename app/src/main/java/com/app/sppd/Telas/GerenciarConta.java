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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vitor.testevolley.R;
import com.app.sppd.Interfaces.VolleyCallbackObject;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.LoginAssistant;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.SppdTools;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class GerenciarConta extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Gerenciar Conta";
    Passageiro passageiro;
    ImageView imagemUsuario;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    EditText txtNome;
    EditText txtSenha;
    EditText txtNovaSenha;

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_conta);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_gerenciar_cartao);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        Intent intent = getIntent();
        passageiro = (Passageiro) intent.getSerializableExtra("passageiro");
        TextView name = (TextView) header.findViewById(R.id.nomeUsuarioMenu);
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




        txtSenha = (EditText) findViewById(R.id.txtNovaSenha_config);
        txtNovaSenha = (EditText) findViewById(R.id.txtConfirmacaoSenha_config);

        if(passageiro.getCpf().equals("VAZIO")){
            txtSenha.setEnabled(false);
            txtNovaSenha.setEnabled(false);
            findViewById(R.id.btnAlterarSenha).setEnabled(false);
            findViewById(R.id.btnAtualizarCadastro).setEnabled(false);
        }


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        /* Login com Facebook */
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile","user_birthday")    );
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                try {
                                    JSONObject jo = new JSONObject();
                                    jo.put("facebookId", object.getString("id"));
                                    jo.put("urlPicture",Profile.getCurrentProfile().getProfilePictureUri(100,100).toString());
                                    jo.put("codPassageiro",passageiro.getCodPassageiro());

                                    RealizaRequisicao.getInstance().postJson(GerenciarConta.this,
                                            SppdTools.getInstance().getEndPoint()+"/passageiro/vincularFacebook/",jo, new VolleyCallbackObject() {
                                                @Override
                                                public void onSuccess(JSONObject result) throws JSONException, IOException {

                                                    if(Boolean.parseBoolean(result.getString("retorno"))){
                                                        Toast.makeText(GerenciarConta.this, result.getString("status"), Toast.LENGTH_SHORT).show();
                                                        URL url = new URL(Profile.getCurrentProfile().getProfilePictureUri(100,100).toString());
                                                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                                        imagemUsuario.setImageBitmap(bmp);
                                                    }else{
                                                        Toast.makeText(GerenciarConta.this,result.getString("status"), Toast.LENGTH_SHORT).show();
                                                        LoginManager.getInstance().logOut();
                                                    }

                                                }
                                            });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(GerenciarConta.this,"Erro ao vincular conta", Toast.LENGTH_SHORT).show();
                                    LoginManager.getInstance().logOut();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,birthday,picture");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Cancelado");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "onError: " + exception);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void alterarSenha(View view) {

        if(txtSenha.getText().equals(txtNovaSenha.getText().toString())){
            Toast.makeText(GerenciarConta.this,"As senhas não conferem", Toast.LENGTH_SHORT).show();
        }else{

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(GerenciarConta.this);
            alertDialog.setTitle("SENHA ATUAL !!!");
            alertDialog.setMessage("Digite a senha atual");

            final EditText senhaAtual = new EditText(GerenciarConta.this);
            senhaAtual.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            senhaAtual.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            senhaAtual.setHint("Senha Atual");

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            senhaAtual.setLayoutParams(lp);
            alertDialog.setView(senhaAtual);

            alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        RealizaRequisicao.getInstance().post(GerenciarConta.this,
                                urlTrocaSenha(passageiro.getCpf(), senhaAtual.getText().toString(),txtNovaSenha.getText().toString()),
                                new VolleyCallbackObject() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        try{

                                            Log.d("", "onSuccess: " + result.toString());
                                            if(Boolean.parseBoolean(result.getString("retorno"))){
                                                Toast.makeText(GerenciarConta.this, "Senha Alterada", Toast.LENGTH_SHORT ).show();
                                                LoginAssistant.gravarUsuarioNoBanco(GerenciarConta.this, passageiro.getCpf(),txtSenha.getText().toString(),passageiro.getNome());
                                            }else{
                                                Toast.makeText(GerenciarConta.this, result.getString("status"), Toast.LENGTH_SHORT ).show();
                                            }

                                           }catch(JSONException e){
                                            Toast.makeText(GerenciarConta.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    }
                                }
                        );
                    }
                });

            alertDialog.setNegativeButton("NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();

        }
    }

    public String urlTrocaSenha(String cpf, String senhaAtual, String novaSenha) {
        return SppdTools.getInstance().getEndPoint() + "/login/alterarSenha/" + cpf.replace(".", "").replace("-", "") +
                "/" + senhaAtual + "/" + novaSenha;
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_gerenciar_cartao);
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
            Intent intent = new Intent(this, InfoCartao.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        } else if (id == R.id.nav_gerenciar_perfil) {

        } else if (id == R.id.nav_simulador) {
            Intent intent = new Intent(this, Simulador.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        } else if (id == R.id.nav_viagens) {
            Intent intent = new Intent(this, HistoricoViagem.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);

        }else if (id == R.id.nav_voce_sabia){
            Intent intent = new Intent(this, VoceSabia.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }/*else if (id == R.id.nav_recarga_cartao){
            Intent intent = new Intent(this, RecargaCartao.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }*/else if(id == R.id.nav_moedas){
            Intent intent = new Intent(this, Moedas.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }else if(id == R.id.nav_watsson){
            Intent intent = new Intent(this, Watson.class);
            intent.putExtra("passageiro", passageiro);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_gerenciar_cartao);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
