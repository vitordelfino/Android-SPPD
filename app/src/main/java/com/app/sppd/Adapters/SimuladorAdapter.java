package com.app.sppd.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.sppd.Estacao.Estacao;
import com.app.sppd.Tools.SppdTools;
import com.android.vitor.testevolley.R;

import java.util.List;

/**
 * Created by vitor on 02/04/2017.
 */

public class SimuladorAdapter extends ArrayAdapter<Estacao> {
    private Activity contexto;

    public SimuladorAdapter(Activity contexto, List<Estacao> estacoes){
        super(contexto, R.layout.custom_layout_simulador, estacoes);
        this.contexto = contexto;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Activity activity = (Activity) super.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_layout_simulador, null);

        LinearLayout cartaoLayout = (LinearLayout) row.findViewById(R.id.cst_simulador_layout);
        TextView linha = (TextView) row.findViewById(R.id.txt_sml_linha);
        TextView estacao = (TextView) row.findViewById(R.id.txt_sml_estacao);

        final Estacao e = (Estacao) super.getItem(position);

        linha = (TextView) SppdTools.getInstance().pintarText(linha, e.getLinha());
        estacao = (TextView) SppdTools.getInstance().pintarText(estacao, e.getLinha());
        estacao.setText(e.getNomeEstacao());


        return row;

    }
}
