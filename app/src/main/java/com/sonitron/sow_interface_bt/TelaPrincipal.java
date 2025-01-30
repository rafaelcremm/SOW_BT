package com.sonitron.sow_interface_bt;

import com.sonitron.sow_interface_bt.config.ConfigDialog;
import com.sonitron.sow_interface_bt.operacao.Operacao;
import com.sonitron.sow_interface_bt.parametros.Parametros;

import android.content.res.Configuration;

import android.util.Log;
import android.widget.LinearLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;

public class TelaPrincipal extends Fragment {

    private LineChart lineChart;
    private LinearLayout linearLayout;
    private Terminal terminal;

    public TelaPrincipal() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tela_principal, container, false);

        linearLayout = view.findViewById(R.id.tela_principal);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // A tela está na orientação paisagem (horizontal)
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            // A tela está na orientação retrato (vertical)
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        }

        //Inicializar Parametross
        terminal = (Terminal) getParentFragmentManager().findFragmentByTag("terminal");


        Log.d("TelaPrincipal", "Inicializando Parametros...");
        Parametros.getInstance(view, terminal);

        Log.d("TelaPrincipal", "Operacao Parametros...");
        Operacao.getInstance(view, terminal);

        Log.d("TelaPrincipal", "ConfigDialog Parametros...");
        ConfigDialog.getInstance(view);


        Log.d("TelaPrincipal", "Configurando gráfico...");

        // Encontrar o gráfico no layout
        lineChart = view.findViewById(R.id.lineChart);
        lineChart.setFocusableInTouchMode(true);
        lineChart.setFocusable(true);
        // Criar uma instância do LineChartManager (agora configura o gráfico internamente)
        LineChartManager chartManager = new LineChartManager(lineChart);



        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            linearLayout.setOrientation(LinearLayout.VERTICAL);
        }
    }
}

