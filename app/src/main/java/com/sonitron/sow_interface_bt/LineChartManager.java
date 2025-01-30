package com.sonitron.sow_interface_bt;

import android.graphics.Color;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class LineChartManager {

    private LineChart lineChart;

    public LineChartManager(LineChart lineChart) {
        this.lineChart = lineChart;
        configureChart();
//        configureChartModes();
    }

    private void configureChart() {
        // Gerar dados de solda para as 3 variáveis
        List<Entry> potenciaRealData = generatePotenciaData();
        List<Entry> forcaRealData = generateForcaData();
        List<Entry> posicaoRealData = generateProfundidadeData();

        // Normalizar os dados
        List<Entry> potenciaData = normalizeData(potenciaRealData, 0, 2800);
        List<Entry> forcaData = normalizeData(forcaRealData, 0, 100);
        List<Entry> posicaoData = normalizeData(posicaoRealData, 0, 2);

        // Configurar os eixos Y
        YAxis leftAxis = lineChart.getAxisLeft();
        lineChart.getAxisRight().setEnabled(false);

        // Configurar eixo esquerdo
        leftAxis.setAxisMinimum(-5f);
        leftAxis.setAxisMaximum(105f);
        leftAxis.setDrawGridLines(true);

        // Configurar conjuntos de dados
        LineDataSet potenciaDataSet = new LineDataSet(potenciaData, "Potência (W)");
        potenciaDataSet.setColor(Color.RED);
        potenciaDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);     // Ativa a interpolação cúbica
        potenciaDataSet.setCubicIntensity(.01f);                    // Ajusta a suavidade da curva
        potenciaDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        potenciaDataSet.setDrawCircles(false);
        potenciaDataSet.setDrawValues(false);

        LineDataSet forcaDataSet = new LineDataSet(forcaData, "Força (kgf)");
        forcaDataSet.setColor(Color.GREEN);
        forcaDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);     // Ativa a interpolação cúbica
        forcaDataSet.setCubicIntensity(.01f);                    // Ajusta a suavidade da curva
        forcaDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        forcaDataSet.setDrawCircles(false);
        forcaDataSet.setDrawValues(false);

        LineDataSet posicaoDataSet = new LineDataSet(posicaoData, "Posição (mm)");
        posicaoDataSet.setColor(Color.BLUE);
        posicaoDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);     // Ativa a interpolação cúbica
        posicaoDataSet.setCubicIntensity(.01f);                    // Ajusta a suavidade da curva
        posicaoDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        posicaoDataSet.setDrawCircles(false);
        posicaoDataSet.setDrawValues(false);

        // Configurar os dados no gráfico
        LineData lineData = new LineData();

        lineData.addDataSet(potenciaDataSet);
        lineData.addDataSet(forcaDataSet);
        lineData.addDataSet(posicaoDataSet);
//
//        //mudar para LINEAR MODE:
//        LineDataSet potenciaDataSetLin = new LineDataSet(potenciaData, "Potência (W)-lin");
//        potenciaDataSetLin.setMode(LineDataSet.Mode.LINEAR);
//        potenciaDataSetLin.setLabel("W -lin");
//        potenciaDataSetLin.setDrawCircles(false);
//        potenciaDataSetLin.setDrawValues(false);
//
//        LineDataSet forcaDataSetLin = new LineDataSet(forcaData, "Força (kgf)-lin");
//        forcaDataSetLin.setMode(LineDataSet.Mode.LINEAR);
//        forcaDataSetLin.setLabel("kgf -lin");
//        forcaDataSetLin.setDrawCircles(false);
//        forcaDataSetLin.setDrawValues(false);
//
//        LineDataSet posicaoDataSetLin = new LineDataSet(posicaoData, "Posição (mm)-lin");
//        posicaoDataSetLin.setMode(LineDataSet.Mode.LINEAR);
//        posicaoDataSetLin.setLabel("mm -lin");
//        posicaoDataSetLin.setDrawCircles(false);
//        posicaoDataSetLin.setDrawValues(false);
//
//        lineData.addDataSet(potenciaDataSetLin);
//        lineData.addDataSet(forcaDataSetLin);
//        lineData.addDataSet(posicaoDataSetLin);

        lineChart.setData(lineData);

        // Adicionar animação ao gráfico
        lineChart.animateX(500);

        // Atualizar o gráfico
        lineChart.invalidate();
    }

    private List<Entry> normalizeData(List<Entry> originalData, float minValue, float maxValue) {
        List<Entry> normalizedData = new ArrayList<>();
        for (Entry entry : originalData) {
            float normalizedValue = 100.0f * (entry.getY() - minValue) / (maxValue - minValue);
            normalizedData.add(new Entry(entry.getX(), normalizedValue));
//            Log.d("log", "normalizedData (" +entry.getX() + "," + normalizedValue + ")");
        }
        return normalizedData;
    }

    private void configureChartModes() {
        List<Entry> potenciaData = generatePotenciaData();

        // Configurar os eixos Y
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(getMaxValue(potenciaData) * 1.1f); // Ajuste o valor máximo conforme necessário
        leftAxis.setDrawGridLines(true);

        lineChart.getAxisRight().setEnabled(false); // Desabilita o eixo direito
        lineChart.getDescription().setEnabled(false); // Desabilita a descrição do gráfico

        // Criar LineDataSets com diferentes modos de interpolação
        LineDataSet linearDataSet = createDataSet(potenciaData, "Linear", Color.RED, LineDataSet.Mode.LINEAR);
        LineDataSet cubicBezierDataSet = createDataSet(potenciaData, "Cubic Bezier", Color.BLUE, LineDataSet.Mode.CUBIC_BEZIER);
        LineDataSet horizontalBezierDataSet = createDataSet(potenciaData, "Horizontal Bezier", Color.GREEN, LineDataSet.Mode.HORIZONTAL_BEZIER);
        LineDataSet steppedDataSet = createDataSet(potenciaData, "Stepped", Color.MAGENTA, LineDataSet.Mode.STEPPED);

        // Configurar os dados no gráfico
        LineData lineData = new LineData(linearDataSet, cubicBezierDataSet, horizontalBezierDataSet, steppedDataSet);
        lineChart.setData(lineData);

        // Adicionar legenda
        lineChart.getLegend().setEnabled(true);

        // Adicionar animação ao gráfico
        lineChart.animateX(2000);

        // Atualizar o gráfico
        lineChart.invalidate();
    }

    private LineDataSet createDataSet(List<Entry> data, String label, int color, LineDataSet.Mode mode) {
        LineDataSet dataSet = new LineDataSet(data, label);
        dataSet.setColor(color);
        dataSet.setLineWidth(2f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(mode);
        return dataSet;
    }


    // Função para obter o valor máximo de uma série de dados
    private float getMaxValue(List<Entry> data) {
        float max = Float.MIN_VALUE;
        for (Entry entry : data) {
            if (entry.getY() > max) {
                max = entry.getY();
            }
        }
        return max;
    }

    public void updateDataSet(List<Entry> entries, String label, int color) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(color);
        dataSet.setValueTextColor(color);

        if (lineChart.getData() == null) {
            lineChart.setData(new LineData(dataSet));
        } else {
            lineChart.getData().addDataSet(dataSet);
            lineChart.getData().notifyDataChanged();
        }
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private List<Entry> generatePotenciaData() {
        List<Entry> data = new ArrayList<>();
        data.add(new Entry(0.00f, 150)); // Início
        data.add(new Entry(0.10f, 150)); // Intermediário 1
        data.add(new Entry(0.20f, 150)); // Encosta na peça
        data.add(new Entry(0.25f, 350)); // Intermediário 2
        data.add(new Entry(0.30f, 550)); // Performa a solda
        data.add(new Entry(0.40f, 890)); // Intermediário 3
        data.add(new Entry(0.50f, 1235)); // Liquefação
        data.add(new Entry(0.75f, 1142)); // Intermediário 4
        data.add(new Entry(1.00f, 1050)); // Se aproxima da prof. destino
        data.add(new Entry(1.50f, 950)); // Consolida a solda

        data.add(new Entry(1.60f, 550)); // Tempo de espera
        data.add(new Entry(1.70f, 200)); // Tempo de espera
        data.add(new Entry(1.80f, 0)); // Tempo de espera
        data.add(new Entry(2.00f, 0)); // Tempo de espera

        return data;
    }

    private List<Entry> generateForcaData() {
        List<Entry> data = new ArrayList<>();
        data.add(new Entry(0.00f, 5));   // Início
        data.add(new Entry(0.10f, 10));  // Intermediário 1
        data.add(new Entry(0.20f, 15));  // Encosta na peça
        data.add(new Entry(0.25f, 35));  // Intermediário 2
        data.add(new Entry(0.30f, 55));  // Performa a solda
        data.add(new Entry(0.40f, 60));  // Intermediário 3
        data.add(new Entry(0.50f, 65));  // Liquefação
        data.add(new Entry(0.75f, 52));  // Intermediário 4
        data.add(new Entry(1.00f, 40));  // Se aproxima da prof. destino
        data.add(new Entry(1.50f, 55));  // Consolida a solda

        data.add(new Entry(2.00f, 55));  // Tempo de espera

        return data;
    }

    private List<Entry> generateProfundidadeData() {
        List<Entry> data = new ArrayList<>();
        data.add(new Entry(0.00f, 0.00f)); // Início
        data.add(new Entry(0.10f, 0.25f)); // Intermediário 1
        data.add(new Entry(0.20f, 0.50f)); // Encosta na peça
        data.add(new Entry(0.25f, 0.52f)); // Intermediário 2
        data.add(new Entry(0.30f, 0.55f)); // Performa a solda
        data.add(new Entry(0.40f, 0.67f)); // Intermediário 3
        data.add(new Entry(0.50f, 0.80f)); // Liquefação
        data.add(new Entry(0.75f, 1.02f)); // Intermediário 4
        data.add(new Entry(1.00f, 1.25f)); // Se aproxima da prof. destino
        data.add(new Entry(1.50f, 2.00f)); // Consolida a solda
        //Tempo de espera:
        data.add(new Entry(2.00f, 2.00f));

        return data;
    }


}

