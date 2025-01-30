package com.sonitron.sow_interface_bt.ui.viewmodel;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

public class NormalizedFormatter extends ValueFormatter {

    private float minValue;
    private float maxValue;

    public NormalizedFormatter(float minValue, float maxValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // Calcula a porcentagem na faixa desejada
        float normalizedValue  = ((value - minValue) / (maxValue - minValue)) * 100;
        return String.format("%.1f", normalizedValue ); // Formata com 1 casa decimal
    }
}
