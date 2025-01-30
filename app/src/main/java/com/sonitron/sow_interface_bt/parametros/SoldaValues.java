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

public class SoldaValues {

    @Expose
    public double Force = 100;
    public double MaxForce = 150;
    public double MinForce = 0;
    double SumForce = 1;

    @Expose
    public double Energia = 100;
    public double MaxEnergia = 2800;  //???
    public double MinEnergia = 0;     //???
    double SumEnergia = 10;     //???

    @Expose
    public double Amplitude = 90;
    public double MaxAmplitude = 100;
    public double MinAmplitude = 50;
    double SumAmplitude = 1;

    @Expose
    public double Rampa = 100;
    public double MaxRampa = 1000;
    public double MinRampa = 20;
    double SumRampa = 1;

    public EditText eTxtAmplitude;
    public EditText eTxtRampa;
    public EditText eTxtEnergia;
    public EditText eTxtForce;

    static SoldaValues instance;
    private final Context context;
    private final View view;
    private final Terminal terminal;
    private final Components components;

    private SoldaValues(View view, Terminal terminal) {
        this.view = view;
        this.context = view.getContext();
        this.terminal = terminal;
        components = Components.getInstance(view, terminal);

        eTxtAmplitude = view.findViewById(R.id.txtAmplitudeMaxima);
        eTxtRampa = view.findViewById(R.id.txtTempoAmpMax);
        eTxtEnergia = view.findViewById(R.id.txtLimiteEnergia);
        eTxtForce = view.findViewById(R.id.txtLimiteForca);

        cfgSumBttSolda();
        cfgSoldaEditText();

        // Tem que carregar os valores do arquivo JSON aqui
        getValues();

    }

    public static SoldaValues getInstance(View view, Terminal terminal) {
        if (instance == null || instance.context != view.getContext()) {
            instance = new SoldaValues(view, terminal);
        }
        return instance;
    }

    public static SoldaValues getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SoldaValues ainda não foi inicializado.");
        }
        return instance;
    }


    public void changeSumSoldaValueBtt(Button button, double step) {
        int buttonId = button.getId();

        // Verifica qual botão foi pressionado e realiza a ação correspondente
        if (buttonId == R.id.btSumLimiteForca) {
            Force += step;
            Force = updateValue(Force, MinForce, MaxForce);
        } else if (buttonId == R.id.btMinusLimiteForca) {
            Force -= step;
            Force = updateValue(Force, MinForce, MaxForce);
        } else if (buttonId == R.id.btSumLimiteEnergia) {
            Energia += step;
            Energia = updateValue(Energia, MinEnergia, MaxEnergia);
        } else if (buttonId == R.id.btMinusLimiteEnergia) {
            Energia -= step;
            Energia = updateValue(Energia, MinEnergia, MaxEnergia);
        } else if (buttonId == R.id.btSumAmplitudeMaxima) {
            Amplitude += step;
            Amplitude = updateValue(Amplitude, MinAmplitude, MaxAmplitude);
        } else if (buttonId == R.id.btMinusAmplitudeMaxima) {
            Amplitude -= step;
            Amplitude = updateValue(Amplitude, MinAmplitude, MaxAmplitude);
        } else if (buttonId == R.id.btSumTempoAmpMax) {
            Rampa += step;
            Rampa = updateValue(Rampa, MinRampa, MaxRampa);
        } else if (buttonId == R.id.btMinusTempoAmpMax) {
            Rampa -= step;
            Rampa = updateValue(Rampa, MinRampa, MaxRampa);
        } else {
            Log.w("ChangeSumSolda", "Botão não reconhecido: " + buttonId);

        }
    }

    public void updateStringSolda() {
        updateEditTextValue(eTxtAmplitude, Amplitude);
        updateEditTextValue(eTxtRampa, Rampa);
        updateEditTextValue(eTxtEnergia, Energia);
        updateEditTextValue(eTxtForce, Force);
        Log.d("Log", "SoldaValues: updateStringSolda - " + Force + "," + Energia + "," + Amplitude + "," + Rampa);
    }

    private void sumWeldButtonsRun(Button button, double step) {
            changeSumSoldaValueBtt(button, step); // Ajusta os valores internos
            updateStringSolda(); // Atualiza os valores visuais e aplica limites
    }

    public void cfgSumBttSolda() {
        if (view == null) return;

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumLimiteForca),
                1, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusLimiteForca),
                1, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumLimiteEnergia),
                10.0, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusLimiteEnergia),
                10.0, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumAmplitudeMaxima),
                1, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusAmplitudeMaxima),
                1, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btSumTempoAmpMax),
                1, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);

        components.setButtonPressHoldFunctionality(view.findViewById(R.id.btMinusTempoAmpMax),
                1, this::sumWeldButtonsRun, this::sendSoldaDataToTerminal);
    }

    public void cfgSoldaEditText(){
        registerTimeEditText(eTxtAmplitude);
        registerTimeEditText(eTxtRampa);
        registerTimeEditText(eTxtEnergia);
        registerTimeEditText(eTxtForce);
    }

    private void registerTimeEditText(EditText editText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                getValues();         //Carrega em upd valores iniciais
            } else {
                updateValue();          //Atualiza com os valores com upd atual.
                updateStringSolda();      //Atualiza os valores visuais e aplica limites
                sendSoldaDataToTerminal();    //Envia os valores ao terminal
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

    public void getValues() {
        try {
            Force = Double.parseDouble(eTxtForce.getText().toString());
            Energia = Double.parseDouble(eTxtEnergia.getText().toString());
            Amplitude = Double.parseDouble(eTxtAmplitude.getText().toString());
            Rampa = Double.parseDouble(eTxtRampa.getText().toString());
            Log.d("Log", "SoldaValues: getValues - " + Force + "," + Energia + "," + Amplitude + "," + Rampa);
        } catch (NumberFormatException e) {
            Log.w("Log", "SoldaValues: getValues - Erro ao atualizar valores de Solda: " + e.getMessage());
        }
    }

    private double updateValue(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public void updateValue() {
        Force = updateValue(Force, MinForce, MaxForce);
        Energia = updateValue(Energia, MinEnergia, MaxEnergia);
        Amplitude = updateValue(Amplitude, MinAmplitude, MaxAmplitude);
        Rampa = updateValue(Rampa, MinRampa, MaxRampa);
    }

    public void updateValues(double force, double energia, double amplitude, double rampa) {
        Force = updateValue(force, MinForce, MaxForce);
        Energia = updateValue(energia, MinEnergia, MaxEnergia);
        Amplitude = updateValue(amplitude, MinAmplitude, MaxAmplitude);
        Rampa = updateValue(rampa, MinRampa, MaxRampa);
    }

    void updateEditTextValue(EditText eTxt, double value) {
        eTxt.setText(String.format(Locale.US, "%.0f", value));
    }

    public void sendSoldaDataToTerminal() {
        terminal.send("[>A " + eTxtAmplitude.getText().toString() + "]");
        terminal.send("[>R " + eTxtRampa.getText().toString() + "]");
        terminal.send("[>E " + eTxtEnergia.getText().toString() + "]");
        terminal.send("[>F " + eTxtForce.getText().toString() + "]");

        // Salva apenas SoldaValues
        components.dataManager.salvarSoldaValues(this);

        Log.d("Log", "SoldaValues salvo após enviar ao terminal.");
    }

    public void updateValues(SoldaValues solda) {
        updateValues(solda.Force, solda.Energia, solda.Amplitude, solda.Rampa);
    }
}
