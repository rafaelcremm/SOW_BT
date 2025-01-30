package com.sonitron.sow_interface_bt.parametros;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.annotations.Expose;
import com.sonitron.sow_interface_bt.R;
import com.sonitron.sow_interface_bt.Terminal;
import com.sonitron.sow_interface_bt.util.KeyboardUtils;

import java.util.Locale;

public class TimeValues {

    static TimeValues instance;

    public double Max = 10.00;
    public double Min = 0.02;
    double Sum = 0.01;

    @Expose public double Retorno = 1.20;
    @Expose public double Descida = 1.20;
    @Expose public double Solda = 0.50;
    @Expose public double Espera = 1.00;

    public double updRetorno = 1.20;
    public double updDescida = 1.20;
    public double updSolda = 0.50;
    public double updEspera = 1.00;

    public EditText eTxtRetorno;
    public EditText eTxtDescida;
    public EditText eTxtSolda;
    public EditText eTxtEspera;

    private final Context context;
    private final View view;
    private final Terminal terminal;
    private final Components components;

    private TimeValues(View view, Terminal terminal) {
        this.view = view;
        this.context = view.getContext();
        this.terminal = terminal;

        components = Components.getInstance(view, terminal);

        eTxtRetorno = view.findViewById(R.id.txtTempoRetorno);
        eTxtDescida = view.findViewById(R.id.txtTempoDescida);
        eTxtSolda = view.findViewById(R.id.txtTempoSolda);
        eTxtEspera = view.findViewById(R.id.txtTempoEspera);

        cfgSumBttTime();
        cfgTimeEditText();

        // Tem que carregar os valores do arquivo JSON aqui

        getValues();
        Retorno = updRetorno;
        Descida = updDescida;
        Solda = updSolda;
        Espera = updEspera;

    }

    public static TimeValues getInstance(View view, Terminal terminal) {
        if (instance == null || instance.context != view.getContext()) {
            instance = new TimeValues(view, terminal);
        }
        return instance;
    }

    public static TimeValues getInstance() {
        if (instance == null) {
            throw new IllegalStateException("TimeValues ainda não foi inicializado.");
        }
        return instance;
    }

    //<editor-fold desc="Time">


    public void changeSumTimeValueBtt(Button button, double step) {
        int buttonId = button.getId();
        //carrega Time.updValues
        getValues();

        if (buttonId == R.id.btSumTempoRetorno) {
            updRetorno += step;
        }
        else if (buttonId == R.id.btMinusTempoRetorno) {
            updRetorno -= step;
        }

        else if (buttonId == R.id.btSumTempoDescida) {
            updDescida += step;
        }
        else if (buttonId == R.id.btMinusTempoDescida) {
            updDescida -= step;
        }

        else if (buttonId == R.id.btSumTempoSolda) {
            updSolda += step;
        }
        else if (buttonId == R.id.btMinusTempoSolda) {
            updSolda -= step;
        }

        else if (buttonId == R.id.btSumTempoEspera) {
            updEspera += step;
        }
        else if (buttonId == R.id.btMinusTempoEspera) {
            updEspera -= step;
        }

        updateValue();

        Log.d("Log", "changeSumTimeValueBtt");
    }

    private void sumTimesButtonsRun(Button button, double step) {
        changeSumTimeValueBtt(button, step); // Ajusta os valores internos
        updateStringTime(); // Atualiza os valores visuais e aplica limites
    }

    private void sendTimeDataToTerminal() {
        sendTimeToTerminal(); // Envia os valores ao terminal
    }

    public void cfgSumBttTime() {
        if (view == null) return;

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumTempoRetorno),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusTempoRetorno),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumTempoDescida),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusTempoDescida),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumTempoSolda),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusTempoSolda),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumTempoEspera),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusTempoEspera),
                0.01, this::sumTimesButtonsRun, this::sendTimeDataToTerminal);
    }

    public void cfgTimeEditText() {
        registerTimeEditText(eTxtRetorno);
        registerTimeEditText(eTxtDescida);
        registerTimeEditText(eTxtSolda);
        registerTimeEditText(eTxtEspera);
    }

    private void registerTimeEditText(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getValues();         //Carrega em upd valores iniciais
            } else {
                updateValue();          //Atualiza com os valores com upd atual.
                updateStringTime();      //Atualiza os valores visuais e aplica limites
                sendTimeToTerminal();    //Envia os valores ao terminal
            }
        });

        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getValues();  //Carrega em upd depois da alteracao...
                KeyboardUtils.hideKeyboard((Activity) context);
                return true;
            } else {
                return false;
            }
        });
    }

    //</editor-fold>

    public void getValues() {
        try {
            updRetorno = Double.parseDouble(eTxtRetorno.getText().toString());
            updDescida = Double.parseDouble(eTxtDescida.getText().toString());
            updSolda = Double.parseDouble(eTxtSolda.getText().toString());
            updEspera = Double.parseDouble(eTxtEspera.getText().toString());
            Log.d("Log", "TimeValues: getValues - " + updRetorno + "," + updDescida + "," + updSolda + "," + updEspera);


        } catch (NumberFormatException e) {
            Log.w("Log", "TimeValues: getValues - Erro ao atualizar valores de tempo: " + e.getMessage());
        }
    }

    private double limitValue(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public void updateValue() {
        Retorno = limitValue(updRetorno, Min, Max);
        Descida = limitValue(updDescida, Min, Max);
        Solda = limitValue(updSolda, Min, Max);
        Espera = limitValue(updEspera, Min, Max);
    }

    public void updateValues(double retorno, double descida, double solda, double espera) {
        Retorno = limitValue(retorno, Min, Max);
        Descida = limitValue(descida, Min, Max);
        Solda = limitValue(solda, Min, Max);
        Espera = limitValue(espera, Min, Max);
    }

    private void updateEditTextValue(EditText eTxt, double value) {
        eTxt.setText(String.format(Locale.US, "%.2f", value));
    }

    public void updateStringTime() {
        updateEditTextValue(eTxtRetorno, Retorno);
        updateEditTextValue(eTxtDescida, Descida);
        updateEditTextValue(eTxtSolda, Solda);
        updateEditTextValue(eTxtEspera, Espera);
    }

    public void sendTimeToTerminal() {
        terminal.send("[>T1," + updRetorno + "]");
        terminal.send("[>T2," + updDescida + "]");
        terminal.send("[>T3," + updSolda + "]");
        terminal.send("[>T4," + updEspera + "]");

        // Salva apenas TimeValues
        components.dataManager.salvarTimeValues(this);

        Log.d("Log", "TimeValues salvo após enviar ao terminal.");
    }

    public void updateValues(TimeValues time) {
        updateValues(time.Retorno, time.Descida, time.Solda, time.Espera);
    }
}
