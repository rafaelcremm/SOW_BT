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

public class PositionValues {

    private static PositionValues instance;

    public double Max = 240.00;
    public double Min = 10.00;
    public double MaxProf = 50.00;
    public double MinProf = 0.00;

    @Expose
    public double Retorno;
    @Expose
    public double Inicial;
    @Expose
    public double Final;
    @Expose
    public double Prof;

    public double updRetorno;
    public double updInicial;
    public double updFinal;
    public double updProf ;

    public EditText eTxtRetorno;
    public EditText eTxtInicial;
    public EditText eTxtFinal;
    public EditText eTxtProf;

    private final Context context;
    private final View view;
    private final Terminal terminal;
    private final Components components;

    private PositionValues(View view, Terminal terminal) {
        this.view = view;
        this.context = view.getContext();
        this.terminal = terminal;

        components = Components.getInstance(view, terminal);

        eTxtRetorno = view.findViewById(R.id.txtPosicaoRetorno);
        eTxtInicial = view.findViewById(R.id.txtPosicaoInicial);
        eTxtFinal = view.findViewById(R.id.txtPosicaoFinal);
        eTxtProf = view.findViewById(R.id.txtProfundidade);

        cfgSumBttPos();
        cfgPositionEditText();

        // Tem que carregar os valores do arquivo JSON aqui

        getValues();
        Retorno = updRetorno;
        Inicial = updInicial;
        Final = updFinal;
        Prof = updProf;

    }

    public static PositionValues getInstance(View view, Terminal terminal) {
        if (instance == null || instance.context != view.getContext()) {
            instance = new PositionValues(view, terminal);
        }
        return instance;
    }

    public static PositionValues getInstance() {
        if (instance == null) {
            throw new IllegalStateException("PositionValues ainda não foi inicializado.");
        }
        return instance;
    }

    //<editor-fold desc="Position">

    public void changeSumPosValueBtt(Button button, double step){
        int buttonId = button.getId();
        getValues();

        if (buttonId == R.id.btSumPosicaoRetorno) {
            updRetorno += step;
            updateValue();
        }
        else if (buttonId == R.id.btMinusPosicaoRetorno) {
            updRetorno -= step;
            updateValue();
        }

        else if (buttonId == R.id.btSumPosicaoInicial) {
            updInicial += step;
            updateValue();
        }
        else if (buttonId == R.id.btMinusPosicaoInicial) {
            updInicial -= step;
            updateValue();
        }

        else if (buttonId == R.id.btSumPosicaoFinal) {
            updFinal += step;
            updateValue();
        }
        else if (buttonId == R.id.btMinusPosicaoFinal) {
            updFinal -= step;
            updateValue();
        }

        else if (buttonId == R.id.btSumProfundidade) {
            updProf += step;
            updateValue();
        }
        else if (buttonId == R.id.btMinusProfundidade) {
            updProf -= step;
            updateValue();
        }
        else {
            Log.w("sumValueBtt", "Button not recognized");
            return;
        }

        updateStringPos();
    }

    private void sumPositionsButtonsRun(Button button, double step) {
        changeSumPosValueBtt(button, step); // Ajusta os valores internos
        updateStringPos(); // Atualiza os valores visuais e aplica limites
    }

    private void sendPositionDataToTerminal() {
        sendPositionsToTerminal(); // Envia os valores ao terminal
    }

    private void cfgSumBttPos() {
        if (view == null) return;

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumPosicaoRetorno),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusPosicaoRetorno),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumPosicaoInicial),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusPosicaoInicial),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumPosicaoFinal),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusPosicaoFinal),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumProfundidade),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusProfundidade),
                0.01, this::sumPositionsButtonsRun, this::sendPositionDataToTerminal);
    }

    private void cfgPositionEditText() {

        //<editor-fold desc="Retorno">
        eTxtRetorno.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getValues(); //Carrega em upd antes da alteracao...
                Log.d("Log", "FocusChange ganha foco: " + updRetorno);
            } else {
                updateValue();   //Atualiza com os valores atuais do upd...
                updateStringPos();
                sendPositionsToTerminal();

                Log.d("Log", "FocusChange perde foco: " + Retorno);
            }
        });

        eTxtRetorno.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getValues();  //Carrega em upd depois da alteracao...
                Log.d("Log", "EditorAction Done: " + Retorno);
                KeyboardUtils.hideKeyboard((Activity) context);

                return true;
            } else {
                Log.d("Log", "EditorAction " + actionId + ": " + eTxtRetorno.getText().toString());
                return false;
            }
        });
        //</editor-fold>

        //<editor-fold desc="Inicial">
        eTxtInicial.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getValues();
                Log.d("Log", "FocusChange ganha foco: " + updInicial);
            } else {
                updateValue();
                updateStringPos();
                sendPositionsToTerminal();

                Log.d("Log", "FocusChange perde foco: " + Inicial);
            }
        });

        eTxtInicial.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getValues();
                KeyboardUtils.hideKeyboard((Activity) context);
                Log.d("Log", "EditorAction Done: " + updInicial);
                return true;
            } else {
                Log.d("Log", "EditorAction " + actionId + ": " + eTxtInicial.getText().toString());
                return false;
            }
        });
        //</editor-fold>

        //<editor-fold desc="Final">
        eTxtFinal.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getValues();
                Log.d("Log", "FocusChange ganha foco: " + updFinal);
            } else {
                updateValue();
                updateStringPos();
                sendPositionsToTerminal();

                Log.d("Log", "FocusChange perde foco: " + Final);
            }
        });

        eTxtFinal.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getValues();
                KeyboardUtils.hideKeyboard((Activity) context);
                Log.d("Log", "EditorAction Done: " + updFinal);
                return true;
            } else {
                Log.d("Log", "EditorAction " + actionId + ": " + eTxtFinal.getText().toString());
                return false;
            }
        });
        //</editor-fold>

        //<editor-fold desc="Profundidade">
        eTxtProf.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getValues();
                Log.d("Log", "FocusChange ganha foco: " + updProf);
            } else {
                updateValue();
                updateStringPos();
                sendPositionsToTerminal();

                Log.d("Log", "FocusChange perde foco: " + Prof);
            }
        });

        eTxtProf.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                getValues();
                KeyboardUtils.hideKeyboard((Activity) context);
                Log.d("Log", "EditorAction Done: " + updProf);
                return true;
            } else {
                Log.d("Log", "EditorAction " + actionId + ": " + eTxtProf.getText().toString());
                return false;
            }
        });
        //</editor-fold>

    }

    //</editor-fold>

    public void getValues() {
        try {
            updRetorno = Double.parseDouble(eTxtRetorno.getText().toString());
            updInicial = Double.parseDouble(eTxtInicial.getText().toString());
            updFinal = Double.parseDouble(eTxtFinal.getText().toString());
            updProf = Double.parseDouble(eTxtProf.getText().toString());
            Log.d("Log", "PositionValues: getValues - " + updRetorno + "," + updInicial + "," + updFinal + "," + updProf);

        } catch (NumberFormatException e) {
            Log.w("Log", "PositionValues: getValues - Erro ao atualizar valores de posição: " + e.getMessage());
        }
    }

    public void updateValues(double retorno, double inicial, double finalValue, double prof) {
        int flag;
        do{
            flag = 0;
            flag += handleRetorno(retorno);
            flag += handleInicial(inicial);
            flag += handleFinal(finalValue);
            flag += handleProfundidade(prof);

        }while (flag > 0);

    }

    public void updateValue() {
        int flag;
        do{
            flag = 0;
            flag += handleRetorno(updRetorno);
            flag += handleInicial(updInicial);
            flag += handleFinal(updFinal);
            flag += handleProfundidade(updProf);

        }while (flag > 0);
    }

    private int handleRetorno(double value) {
        if(value == Retorno)
            return 0;

        Retorno = limitValue(value, Min, Inicial);
        updRetorno = Retorno;
        Log.d("Log", "handle Retorno: " + value + "->" + Retorno);
        return 1;
    }

    private int handleInicial(double value) {
        if(value == Inicial)
            return 0;

        Inicial = limitValue(value, Retorno, Max - Prof);
        updInicial = Inicial;
        updFinal = Inicial + Prof;
        Log.d("Log", "handle Inicial: " + value + "->" + Inicial);
        return 1;
    }

    private int handleFinal(double value) {
        if(value == Final)
            return 0;

        Final = limitValue(value, Inicial, Max);
        updFinal = Final;
        updProf = Final - Inicial;
        Log.d("Log", "handle Final: " + value + "->" + Final);
        return 1;
    }

    private int handleProfundidade(double value) {
        if(value == Prof)
            return 0;

        Prof = limitValue(value, MinProf, MaxProf);
        updProf = Prof;
        updFinal = Inicial + Prof;
        Log.d("Log", "handle Prof: " + value + "->" + Prof);
        return 1;
    }

    private double limitValue(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    private void updateEditTextValue(EditText eTxt, double value) {
        eTxt.setText(String.format(Locale.US, "%.2f", value));
    }

    public void updateStringPos() {
        updateEditTextValue(eTxtRetorno, Retorno);
        updateEditTextValue(eTxtInicial, Inicial);
        updateEditTextValue(eTxtFinal, Final);
        updateEditTextValue(eTxtProf, Prof);
    }

    public void sendPositionsToTerminal() {
        terminal.send("[>R " + eTxtRetorno.getText().toString() + "]");
        terminal.send("[>I " + eTxtInicial.getText().toString() + "]");
        terminal.send("[>F " + eTxtFinal.getText().toString() + "]");
        terminal.send("[>P " + eTxtProf.getText().toString() + "]");

        // Salva apenas PositionValues
        components.dataManager.salvarPositionValues(this);

        Log.d("Log", "PositionValues salvo após enviar ao terminal.");

    }


    public void updateValues(PositionValues pos) {
        updateValues(pos.Retorno, pos.Inicial, pos.Final, pos.Prof);
    }
}
