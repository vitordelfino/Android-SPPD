package com.app.sppd.mask;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;

/**
 * Created by vitor on 20/02/17.
 */

public class MascaraMonetaria implements TextWatcher{

    final EditText campo;

    public MascaraMonetaria(EditText campo) {
        super();
        this.campo = campo;
    }

    private boolean isUpdating = false;
    private NumberFormat nf = NumberFormat.getCurrencyInstance();



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int after) {
        if (isUpdating) {
            isUpdating = false;
            return;
        }
        isUpdating = true;
        String str = s.toString();
        boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) &&
                (str.indexOf(".") > -1 || str.indexOf(",") > -1));
        if (hasMask) {
            str = str.replaceAll("[R$]", "").replaceAll("[,]", "")
                    .replaceAll("[.]", "");
        }

        try {
            str = nf.format(Double.parseDouble(str) / 100);
            campo.setText(str);
            campo.setSelection(campo.getText().length());
        } catch (NumberFormatException e) {
            s = "";
        }
    }


    @Override
    public void afterTextChanged(Editable editable) {

    }
}
