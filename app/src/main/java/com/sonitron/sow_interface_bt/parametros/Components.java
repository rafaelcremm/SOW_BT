package com.sonitron.sow_interface_bt.parametros;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.sonitron.sow_interface_bt.R;
import com.sonitron.sow_interface_bt.Terminal;
import com.sonitron.sow_interface_bt.data.DataManager;
import com.sonitron.sow_interface_bt.util.ButtonAction;

public class Components {

    public static Components instance;
    public View view;

    public final DataManager dataManager;

    private final Context context;
    private final Terminal terminal;
    private final Handler handler = new Handler();
    private static final long INITIAL_DELAY = 500; // Atraso inicial em milissegundos
    private static final long INTERVAL = 100;     // Intervalo entre alterações em milissegundos

    private static final long STEP_THRESHOLD_1 = 2500;  // ms
    private static final long STEP_THRESHOLD_2 = 7500; // ms

    public SwitchCompat switchInterno;
    public SwitchCompat switchCanal1;
    public SwitchCompat switchCanal2;

    private Components(View view, Terminal terminal) {
        this.terminal = terminal;
        this.view = view;
        this.context = view.getContext();

        dataManager = DataManager.getInstance(view, terminal);

        cfgBtts();
        cfgSwitch();
    }

    public static Components getInstance(View view, Terminal terminal){
        if (instance == null || instance.context != view.getContext()) {
            instance = new Components(view, terminal);
        }
        return instance;
    }

    public static Components getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Components ainda não foi inicializado.");
        }
        return instance;
    }

    //<editor-fold desc="IHM">
    // Metodo para configurar os listeners nos botões
    public void cfgBtts() {
        if (view == null) return;

        // Encontrar os botões no layout
        Button bttLast = view.findViewById(R.id.btt_last);
        Button bttNext = view.findViewById(R.id.btt_next);
        Button bttBack = view.findViewById(R.id.btt_back);
        Button bttOk = view.findViewById(R.id.btt_ok);

        // Definir os listeners para cada botão
        setButtonClickListener(bttLast, "[>B0]");
        setButtonClickListener(bttNext, "[>B1]");
        setButtonClickListener(bttBack, "[>B2]");
        setButtonClickListener(bttOk, "[>B3]");
    }

    // Metodo para registrar listeners nos botões
    private void setButtonClickListener(Button button, String command) {
        if (button != null) {
            button.setOnClickListener(v -> {
                if (terminal != null) {
                    terminal.send(command);
                } else {
                    Toast.makeText(context, "Terminal not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    //</editor-fold>

    //<editor-fold desc="Switchs">

    public void cfgSwitch() {

        switchInterno = view.findViewById(R.id.switch_interno);
        switchCanal1 = view.findViewById(R.id.switch_canal_1);
        switchCanal2 = view.findViewById(R.id.switch_canal_2);
        switchInterno.setOnClickListener(v -> {
            switchInterno.setChecked(true);
            switchCanal1.setChecked(false);
            switchCanal2.setChecked(false);
            Toast.makeText(context, "Controle Interno: ON", Toast.LENGTH_SHORT).show();
        });

        switchCanal1.setOnClickListener(v -> {
            switchInterno.setChecked(false);
            switchCanal1.setChecked(true);
            switchCanal2.setChecked(false);
            Toast.makeText(context, "Controle via canal 1: ON", Toast.LENGTH_SHORT).show();
        });

        switchCanal2.setOnClickListener(v -> {
            switchInterno.setChecked(false);
            switchCanal1.setChecked(false);
            switchCanal2.setChecked(true);
            Toast.makeText(context, "Controle via canal 2: ON", Toast.LENGTH_SHORT).show();
        });

        // Garantir que sempre um switch esteja ativado inicialmente
        if (!switchInterno.isChecked() && !switchCanal1.isChecked() && !switchCanal2.isChecked()) {
            switchInterno.setChecked(true);
            switchCanal1.setChecked(false);
            switchCanal2.setChecked(false);
        }
    }

    //</editor-fold>

    @SuppressLint("ClickableViewAccessibility")
    public void setButtonPressHoldFunctionality(Button button, double stepInicial, ButtonAction action, Runnable onReleaseCallback) {
        final long[] startTime = new long[1]; // Para armazenar o tempo de início da pressão
        final boolean[] isRunnableStarted = {false}; // Para rastrear se o Runnable foi iniciado
        final Runnable[] runnable = new Runnable[1];

        if (button != null) {
            button.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startTime[0] = System.currentTimeMillis(); // Armazena o tempo de início
                        isRunnableStarted[0] = false; // Reset do estado do Runnable
                        runnable[0] = new Runnable() {
                            @Override
                            public void run() {
                                isRunnableStarted[0] = true; // Runnable começou
                                long elapsedTime = System.currentTimeMillis() - startTime[0];
                                double step = stepInicial;

                                // Escalar o step com base no tempo pressionado
                                if (elapsedTime > STEP_THRESHOLD_2) {
                                    step = stepInicial * 100; // Escalado após 2 segundos
                                } else if (elapsedTime > STEP_THRESHOLD_1) {
                                    step = stepInicial * 10; // Escalado após 1 segundo
                                }

                                if (action != null) action.accept(button, step); // Executa o callback
                                handler.postDelayed(this, INTERVAL); // Repetir a cada intervalo
                            }
                        };
                        handler.postDelayed(runnable[0], INITIAL_DELAY); // Inicia após o atraso inicial
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        handler.removeCallbacks(runnable[0]); // Cancela o Runnable
                        if (!isRunnableStarted[0] && action != null) { // Executa o callback para cliques curtos
                            action.accept(button, stepInicial); // Aplica apenas o step inicial
                        }
                        if (onReleaseCallback != null) onReleaseCallback.run(); // Executa o callback ao soltar
                        v.performClick(); // Garante o comportamento padrão de clique
                        return true;

                    default:
                        return false;
                }
            });
        }
    }


}

