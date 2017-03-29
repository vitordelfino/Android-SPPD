package com.example.vitor.Adapters;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vitor.Cartao.HistoricoSaldoCartaoBean;
import com.example.vitor.Passageiro.Passageiro;
import com.example.vitor.testevolley.R;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by vitor on 23/03/2017.
 */



public class HistoricoCartaoAdapter extends ArrayAdapter<HistoricoSaldoCartaoBean> {
    private Activity contexto;
    private Passageiro p;

    public HistoricoCartaoAdapter(Activity contexto, List<HistoricoSaldoCartaoBean> historico, Passageiro p){
        super(contexto, R.layout.custom_layout_recarga, historico);
        this.contexto = contexto;
        this.p = p;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Activity activity = (Activity) super.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_layout_historico_cartao, null);

        LinearLayout historicoLayout = (LinearLayout) row.findViewById(R.id.historicoLayout);
        TextView descricao = (TextView) row.findViewById(R.id.txtDescricao);
        TextView valor = (TextView) row.findViewById(R.id.txtValor);
        TextView saldoAnterior = (TextView) row.findViewById(R.id.txtSaldoAnterior);
        TextView saldoAtual = (TextView) row.findViewById(R.id.txtSaldoAtual);
        TextView data = (TextView) row.findViewById(R.id.txtDataTransacao);


        HistoricoSaldoCartaoBean h = (HistoricoSaldoCartaoBean) super.getItem(position);
        String aux = "↑";
        String aux2 = "+";
        if(h.getDescricao().equalsIgnoreCase("viagem")){
            valor.setTextColor(Color.parseColor("#FFE14747"));
            saldoAtual.setTextColor(Color.parseColor("#FFE14747"));
            aux = "↓";
            aux2 = "-";
        }
        NumberFormat baseFormart = NumberFormat.getCurrencyInstance();

        descricao.setText(h.getDescricao());
        valor.setText(aux2 +" R$ " + baseFormart.format(h.getValor()));
        saldoAnterior.setText("R$ " +baseFormart.format(h.getSaldoAnterior()));
        saldoAtual.setText(aux +" R$ " +baseFormart.format(h.getSaldoAtual()));
        data.setText(h.getDataTransacao());
        return row;
    }
}
