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
import com.example.vitor.Tools.RealizaRequisicao;
import com.example.vitor.Tools.Retorno;
import com.example.vitor.Tools.SppdTools;
import com.example.vitor.Tools.StatusRetorno;
import com.example.vitor.mask.MascaraMonetaria;
import com.example.vitor.testevolley.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        TextView data = (TextView) row.findViewById(R.id.customCartaoData);
        final Cartao c = (Cartao) super.getItem(position);

        Date hoje = new Date();
        DateFormat d = new SimpleDateFormat("dd/MM/yyyy");


        numCartao.setText(c.getCodCartao());
        categoria.setText(c.getDescrCategoria() );
        saldo.setText("R$ " + c.getSaldo());
        data.setText(d.format(hoje));

        return row;

    }







}
