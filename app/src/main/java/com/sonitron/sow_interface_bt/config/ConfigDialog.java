package com.sonitron.sow_interface_bt.config;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sonitron.sow_interface_bt.R;

public class ConfigDialog {
    private static ConfigDialog instance;
    private Context context;
    private View view;
    private AlertDialog dialog;

    private ConfigDialog(View view) {
        this.context = view.getContext();
        this.view = view;

        Button bttConfig = view.findViewById(R.id.btt_config);
        bttConfig.setOnClickListener(v -> showConfigDialog());

    }

    public static ConfigDialog getInstance(View view) {
        if (instance == null) {
            instance = new ConfigDialog(view);
        }
        return instance;
    }

    public void showConfigDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.config_list, null);
        builder.setView(view);

        TextView freqCentral = view.findViewById(R.id.freqCentral);
        TextView freqStart = view.findViewById(R.id.freqStart);
        TextView defasagem = view.findViewById(R.id.defasagem);
        TextView potenciaSaida = view.findViewById(R.id.potenciaSaida);
        TextView vbus = view.findViewById(R.id.vbus);
        TextView loginUser = view.findViewById(R.id.loginUser);
        TextView entradasInfo = view.findViewById(R.id.entradasInfo);
        TextView saidasInfo = view.findViewById(R.id.saidasInfo);

        Button btnSair = view.findViewById(R.id.btnSair);
        Button btnEditar = view.findViewById(R.id.btnEditar);

        // Preenchendo os valores fictícios
        freqCentral.setText("Central: 20.00kHz");
        freqStart.setText("Start: 20.10kHz");
        defasagem.setText("Defasagem: 90");
        potenciaSaida.setText("Saída: 2800W");
        vbus.setText("Vbus: 300V");
        loginUser.setText("Login: User");

        // Definir as informações das entradas e saídas
        StringBuilder entradas = new StringBuilder();
        String[] entradaNomes = {"Emergencia", "Inicio", "Reset", "Opcao 4", "Opcao 5", "Opcao 6", "Opcao 7", "Opcao 8", "Opcao 9", "Opcao 10", "Opcao 11", "Opcao 12"};
        for (int i = 0; i < 12; i++) {
            entradas.append("In ").append(i + 1).append(": ").append(entradaNomes[i]).append("\n");
        }
        entradasInfo.setText(entradas.toString());

        StringBuilder saidas = new StringBuilder();
        String[] saidasNomes = {"Status Run", "Opcao 2", "Opcao 3", "Opcao 4", "Opcao 5", "Opcao 6", "Opcao 7", "Opcao 8", "Opcao 9", "Opcao 10", "Opcao 11", "Opcao 12"};
        for (int i = 0; i < 12; i++) {
            saidas.append("In ").append(i + 1).append(": ").append(saidasNomes[i]).append("\n");
        }
        saidasInfo.setText(saidas.toString());

        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();

        btnSair.setOnClickListener(v -> dialog.dismiss());
        btnEditar.setOnClickListener(v -> openLoginScreen());
    }

    private void openLoginScreen() {
        dialog.dismiss();
        new LoginDialog(context).showLoginDialog();
    }
}