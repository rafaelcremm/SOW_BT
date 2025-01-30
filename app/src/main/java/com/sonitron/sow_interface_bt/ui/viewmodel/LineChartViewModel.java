package com.sonitron.sow_interface_bt.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.mikephil.charting.data.Entry;
import java.util.ArrayList;
import java.util.List;

public class LineChartViewModel extends ViewModel {

    private final MutableLiveData<List<Entry>> potenciaData = new MutableLiveData<>();
    private final MutableLiveData<List<Entry>> forcaData = new MutableLiveData<>();
    private final MutableLiveData<List<Entry>> posicaoData = new MutableLiveData<>();

    public LineChartViewModel() {
        loadChartData();
    }

    private void loadChartData() {
        List<Entry> potencia = new ArrayList<>();
        List<Entry> forca = new ArrayList<>();
        List<Entry> posicao = new ArrayList<>();

        for (int i = 0; i <= 100; i++) {
            potencia.add(new Entry(i, (float) Math.sin(i * 0.1) * 10));
            forca.add(new Entry(i, (float) Math.cos(i * 0.1) * 15));
            posicao.add(new Entry(i, (float) Math.tan(i * 0.1) * 5));
        }

        potenciaData.setValue(potencia);
        forcaData.setValue(forca);
        posicaoData.setValue(posicao);
    }

    public LiveData<List<Entry>> getPotenciaData() {
        return potenciaData;
    }

    public LiveData<List<Entry>> getForcaData() {
        return forcaData;
    }

    public LiveData<List<Entry>> getPosicaoData() {
        return posicaoData;
    }

    public static List<Entry> createSampleData() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            entries.add(new Entry(i, (float) Math.sin(i * 0.1) * 10)); // Valores de exemplo
        }
        return entries;
    }
}

