package com.sonitron.sow_interface_bt.util;

import android.widget.Button;

@FunctionalInterface
public interface ButtonAction {
    void accept(Button button, double step);
}
