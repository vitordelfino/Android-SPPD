package com.app.sppd.Adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.sppd.Viagem.Viagem;
import com.android.vitor.testevolley.R;

import java.text.NumberFormat;
import java.util.List;

/**
 * Created by vitor on 18/04/2017.
 */

public class HistoricoViagemAdapter  extends ArrayAdapter<Viagem> {
    private Activity contexto;

    public HistoricoViagemAdapter(Activity contexto, List<Viagem> historico){
        super(contexto, R.layout.custom_layout_historico_viagens, historico);
        this.contexto = contexto;
    }

    public View getView(int position, View convertView, ViewGroup parent){

        Activity activity = (Activity) super.getContext();
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.custom_layout_historico_viagens, null);

        LinearLayout cartaoLayout = (LinearLayout) row.findViewById(R.id.historicoViagensLayout);

        TextView origem = (TextView) row.findViewById(R.id.txtOrigemViavem);
        TextView destino = (TextView) row.findViewById(R.id.txtDestinoViagem);
        TextView valor = (TextView) row.findViewById(R.id.txtValorViagem);
        TextView data = (TextView) row.findViewById(R.id.txtDataViagem);

        final Viagem v = (Viagem) super.getItem(position);

        origem.setText(v.getOrigem().getNomeEstacao());
        destino.setText(v.getDestino().getNomeEstacao());
        data.setText(v.getDataEntrada());

        NumberFormat baseFormart = NumberFormat.getCurrencyInstance();
        valor.setText("R$ " +baseFormart.format(v.getValor()));
        return row;
    }
}
