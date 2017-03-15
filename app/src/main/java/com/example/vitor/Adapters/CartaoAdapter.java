package com.example.vitor.Adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vitor.Cartao.Cartao;
import com.example.vitor.Interfaces.VolleyCallbackObject;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.Telas.RecargaCartao;
import com.example.vitor.Tools.RealizaRequisicao;
import com.example.vitor.Tools.Retorno;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.Tools.StatusRetorno;
import com.example.vitor.mask.MascaraMonetaria;
import com.example.vitor.testevolley.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Console;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by vitor on 28/02/17.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
public class CartaoAdapter extends ArrayAdapter<Cartao> {
    private Activity contexto;
    private Passageiro p;
    public CartaoAdapter(Activity contexto, List<Cartao> cartoes, Passageiro p){
        super(contexto, R.layout.custom_layout_recarga, cartoes);
        this.contexto = contexto;
        this.p = p;
    }

    public View getView(int position, View convertView, ViewGroup parent){


        Activity activity = (Activity) super.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_layout_recarga, null);

        LinearLayout cartaoLayout = (LinearLayout) row.findViewById(R.id.cartaoLayout);
        TextView categoria = (TextView) row.findViewById(R.id.txtCategoriaCartao);
        TextView numCartao = (TextView) row.findViewById(R.id.txtNumCartao);
        TextView saldo = (TextView) row.findViewById(R.id.txtSaldoCartao);
        TextView ativar = (TextView) row.findViewById(R.id.optAtivar);
        TextView carregar = (TextView) row.findViewById(R.id.optCarregar);

        final Cartao c = (Cartao) super.getItem(position);

        if(c.getAtivo() == 0){
            ativar.setText("Ativar");
        }else{
            ativar.setText("Desativar");
        }

        if(c.isClicado()){
            ativar.setVisibility(View.VISIBLE);
            carregar.setVisibility(View.VISIBLE);
        }else{
            ativar.setVisibility(View.INVISIBLE);
            carregar.setVisibility(View.INVISIBLE);
        }

        numCartao.setText("Cartão: " + c.getCodCartao());
        categoria.setText(c.getDescrCategoria() );
        saldo.setText("R$ " + c.getSaldo());


        ativar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ativarCartao(c,p);
            }
        });

        carregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCarregaCartao(c);
            }
        });
        return row;

    }
    private void alertCarregaCartao(final Cartao c){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(contexto);
        alertDialog.setTitle("RECARGA ("+c.getCodCartao()+")");
        alertDialog.setMessage("Entre com valor de recarga");

        final EditText valor = new EditText(contexto);
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

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(contexto);
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
                        RealizaRequisicao.getInstance().postJson(contexto, urlRecargaCartao(c), jo, new VolleyCallbackObject() {
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

                                    Toast.makeText(contexto, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();

                                    CartaoAdapter.this.notifyDataSetChanged();
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

    public void ativarCartao(final Cartao cartao, Passageiro passageiro){
        final Retorno retorno = new Retorno();
        RealizaRequisicao.getInstance().post(contexto, urlAtivaCartao(cartao, passageiro), new VolleyCallbackObject() {
            @Override
            public void onSuccess(JSONObject result) {

                try{
                    retorno.setstatusMessage(result.getString("status"));
                    if(result.getBoolean("retorno"))
                        cartao.setAtivo(1);

                    Toast.makeText(contexto, retorno.getstatusMessage(), Toast.LENGTH_SHORT).show();
                    CartaoAdapter.this.notifyDataSetChanged();

                }catch(Exception e){

                }
            }
        });

    }
    private String urlRecargaCartao(Cartao c){
        return SppdTools.getInstance().getEndPoint() + "/cartao/efetuarRecarga/"+c.getCodCartao()+"/"+c.getCodPassageiro();
    }

    private String urlAtivaCartao(Cartao c, Passageiro p){
        return SppdTools.getInstance().getEndPoint() + "/cartao/ativarCartao/"+c.getCodCartao()+"/"+p.getCodPassageiro();
    }
}
