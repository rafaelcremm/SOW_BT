package com.sonitron.sow_interface_bt.util;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

public class SystemUiUtils {

    public static void enableImmersiveMode(Activity activity) {
        if (activity != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Para Android 11 ou superior
                WindowInsetsController insetsController = activity.getWindow().getInsetsController();
                if (insetsController != null) {
                    // Esconde as barras do sistema
                    insetsController.hide(WindowInsets.Type.systemBars());
                    // Configura o comportamento para que as barras do sistema só apareçam com um gesto
                    insetsController.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    );
                }
                Log.d("Log", "SystemUiUtils - Para Android 11 ou superior");
            } else {
                // Para versões anteriores
                activity.getWindow().getDecorView().setSystemUiVisibility(
                          View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                );
                Log.d("Log", "SystemUiUtils - Para versões anteriores");
            }
        }
    }
}
