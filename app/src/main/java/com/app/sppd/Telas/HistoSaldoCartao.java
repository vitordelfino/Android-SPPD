package com.app.sppd.Telas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.app.sppd.Adapters.HistoricoCartaoAdapter;
import com.app.sppd.Cartao.Cartao;
import com.app.sppd.Cartao.HistoricoSaldoCartaoBean;
import com.app.sppd.Interfaces.VolleyCallbackArray;
import com.app.sppd.Interfaces.VolleyCallbackObject;
import com.app.sppd.Passageiro.Passageiro;
import com.app.sppd.Tools.RealizaRequisicao;
import com.app.sppd.Tools.Retorno;
import com.app.sppd.Tools.SppdTools;
import com.app.sppd.Tools.StatusRetorno;
import com.app.sppd.mask.MascaraMonetaria;
import com.android.vitor.testevolley.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class HistoSaldoCartao extends AppCompatActivity {

    private Cartao cartao;
    private Passageiro passageiro;
    private HistoricoSaldoCartaoBean historicoCartaoBean;
    private Button carregar;
    private Button ativarDesativar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo_saldo_cartao);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        passageiro = (Passageiro) intent.getSerializableExtra("passageiro");
        cartao = (Cartao) intent.getSerializableExtra("cartao");

        carregar = (Button) findViewById(R.id.btnAtivar);
        ativarDesativar = (Button) findViewById(R.id.btnAtivar);

        listaCartao(cartao);
    }


    public void ativarDesativar(View view){
        final Retorno retorno = new Retorno();
        RealizaRequisicao.getInstance().post(HistoSaldoCartao.this, urlAtivarDesativar(), new VolleyCallbackObject() {
            @Override
            public void onSuccess(JSONObject result) {

                try{
                    retorno.setstatusMessage(result.getString("status"));
                    if(result.getBoolean("retorno"))
                        if(cartao.getAtivo() == 0) {
                            cartao.setAtivo(1);
                        }else {
                            cartao.setAtivo(0);
                        }
                    Toast.makeText(HistoSaldoCartao.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();

                    listaCartao(cartao);

                }catch(Exception e){

                }
            }
        });
    }

    public void carregar(View view){
        alertCarregaCartao(cartao);
    }


    private void alertCarregaCartao(final Cartao c){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HistoSaldoCartao.this);
        alertDialog.setTitle("RECARGA ("+c.getCodCartao()+")");
        alertDialog.setMessage("Entre com valor de recarga");

        final EditText valor = new EditText(HistoSaldoCartao.this);
        valor.setInputType(InputType.TYPE_CLASS_NUMBER);
        valor.addTextChangedListener(new MascaraMonetaria(valor));
        valor.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        valor.setHint("0,00");
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        valor.setLayoutParams(lp);

        alertDialog.setView(valor);

        alertDialog.setPositiveButton("YES",
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

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private void alertConfirmaRecarga(final Cartao c, final double valorRecarga){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HistoSaldoCartao.this);
        alertDialog.setTitle("CONFIRMAÇÃO DE RECARGA");
        alertDialog.setMessage("Deseja realizar a recarga de R$ " + valorRecarga + " ");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jo = new JSONObject();
                        try {
                            jo.put("valor",valorRecarga);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        RealizaRequisicao.getInstance().postJson(HistoSaldoCartao.this, urlRecargaCartao(c), jo, new VolleyCallbackObject() {
                            @Override
                            public void onSuccess(JSONObject result) {
                                try{
                                    Retorno retorno = new Retorno();
                                    retorno.setstatusMessage(result.getString("status"));
                                    if(result.getBoolean("retorno")){
                                        retorno.setRetorno(StatusRetorno.YES);
                                    }
                                    if(retorno.isSucess())
                                        c.setSaldo(c.getSaldo()+valorRecarga);

                                    Toast.makeText(HistoSaldoCartao.this, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();

                                    listaCartao(c);
                                }catch(Exception e){

                                }

                            }
                        });
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

    private String urlRecargaCartao(Cartao c){
        return SppdTools.getInstance().getEndPoint() + "/cartao/efetuarRecarga/"+c.getCodCartao()+"/"+c.getCodPassageiro();
    }

    private String urlAtivarDesativar(){
        if(cartao.getAtivo() == 0)
            return SppdTools.getInstance().getEndPoint() + "/cartao/ativarCartao/"+cartao.getCodCartao()+"/"+passageiro.getCodPassageiro();
        return SppdTools.getInstance().getEndPoint() + "/cartao/desativarCartao/"+cartao.getCodCartao()+"/"+passageiro.getCodPassageiro();
    }

    public void listaCartao(Cartao c){

        if(cartao.getAtivo() == 1){
            ativarDesativar.setText("Desativar");
        }else{
            ativarDesativar.setText("Ativar");
        }

        final ArrayList<HistoricoSaldoCartaoBean> historico = new ArrayList<HistoricoSaldoCartaoBean>();


            RealizaRequisicao.getInstance().getArray(HistoSaldoCartao.this, montaUrl(), new VolleyCallbackArray() {

                @Override
                public void onSuccess(JSONArray result) {
                    try{

                        for(int i = 0; i < result.length(); i++){
                            JSONObject jo = result.getJSONObject(i);
                            historico.add(new HistoricoSaldoCartaoBean(
                                    Integer.parseInt(jo.getString("codCartao")),
                                    jo.getString("dataTransacao"),
                                    Double.parseDouble(jo.getString("saldoAnterior")),
                                    Double.parseDouble(jo.getString("saldoAtual")),
                                    jo.getString("descricao"),
                                    Double.parseDouble(jo.getString("valor"))
                                    ));

                        }

                    }catch(JSONException e){
                        try{
                            JSONObject jo = result.getJSONObject(0);
                            historico.add(new HistoricoSaldoCartaoBean(
                                    Integer.parseInt(jo.getString("codCartao")),
                                    jo.getString("dataTransacao"),
                                    Double.parseDouble(jo.getString("saldoAnterior")),
                                    Double.parseDouble(jo.getString("saldoAtual")),
                                    jo.getString("descricao"),
                                    Double.parseDouble(jo.getString("valor"))
                            ));
                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                    }
                    montarLayout(historico);
                }
            });


    }

    public String montaUrl(){
        return SppdTools.getInstance().getEndPoint()+"/cartao/getHistoricoCartao/"+cartao.getCodCartao();
    }

    public void montarLayout(final ArrayList<HistoricoSaldoCartaoBean> historico){
        final HistoricoCartaoAdapter hAdapter = new HistoricoCartaoAdapter(this, historico, passageiro);

        final ListView listView = (ListView) super.findViewById(R.id.lstHistorico);
        listView.setAdapter(hAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                historicoCartaoBean = (HistoricoSaldoCartaoBean) listView.getAdapter().getItem(i);


            }
        });
    }

}
