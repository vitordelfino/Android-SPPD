package com.app.sppd.Adapters;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vitor.testevolley.R;
import com.app.sppd.Cartao.Cartao;
import com.app.sppd.Passageiro.Passageiro;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        Locale.setDefault(new Locale("pt", "BR"));

        Date hoje = new Date();
        DateFormat d = new SimpleDateFormat("dd/MM/yyyy");

        NumberFormat baseFormart = NumberFormat.getCurrencyInstance();

        numCartao.setText(c.getCodCartao());
        categoria.setText(c.getDescrCategoria() );
        saldo.setText(baseFormart.format(c.getSaldo()));
        data.setText(d.format(hoje));

        return row;

    }







}
