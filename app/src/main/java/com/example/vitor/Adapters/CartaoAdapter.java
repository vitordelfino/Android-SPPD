package com.example.vitor.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vitor.Cartao.Cartao;
import com.example.vitor.testevolley.R;

import java.util.List;

/**
 * Created by vitor on 28/02/17.
 */

public class CartaoAdapter extends ArrayAdapter<Cartao> {

    public CartaoAdapter(Activity contexto, List<Cartao> cartoes){
        super(contexto, R.layout.custom_layout_recarga, cartoes);
    }

    public View getView(int position, View convertView, ViewGroup parent){


        Activity activity = (Activity) super.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_layout_recarga, null);
        TextView categoria = (TextView) row.findViewById(R.id.txtCategoriaCartao);
        TextView numCartao = (TextView) row.findViewById(R.id.txtNumCartao);
        TextView saldo = (TextView) row.findViewById(R.id.txtSaldoCartao);

        Cartao c = (Cartao) super.getItem(position);
        numCartao.setText("Cartão: " + c.getCodCartao());
        categoria.setText(c.getDescrCategoria() );
        saldo.setText("R$ " + c.getSaldo());

        return row;

    }
}
