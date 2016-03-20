package com.ryce.frugalist.view.create;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * TextWatcher for currency input
 * credit to http://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
 *
 * Created by Tony on 2016-03-15.
 */
public class MoneyTextWatcher implements TextWatcher {
    private EditText mEditText;

    public MoneyTextWatcher(EditText editText) {
        mEditText = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (mEditText == null) return;
        String s = editable.toString();
        if (s.isEmpty()) return;

        // remove this listener while we modify the input to avoid circular logic
        mEditText.removeTextChangedListener(this);

        // format the price string
        String cleanString = s.replaceAll("[$,.]", "");
        BigDecimal parsed = new BigDecimal(cleanString)
                                .setScale(2, BigDecimal.ROUND_FLOOR)
                                .divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        String formatted = NumberFormat.getCurrencyInstance(Locale.CANADA).format(parsed);
        editable.clear();
        mEditText.setText(formatted);
        mEditText.setSelection(formatted.length());

        // re-add this listener
        mEditText.addTextChangedListener(this);
    }
}
