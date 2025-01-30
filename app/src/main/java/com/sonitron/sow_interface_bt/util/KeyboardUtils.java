package com.sonitron.sow_interface_bt.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    public static boolean hideKeyboardReturned(Activity activity) {
        View viewCurrentFocus = activity.getCurrentFocus();
        if (viewCurrentFocus == null || !viewCurrentFocus.isAttachedToWindow()) {
            Log.w("KeyboardUtils", "hideKeyboardReturned - Teclado já fechado ou View não anexada.");
            return false;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            Log.w("Log", "hideKeyboardReturned - InputMethodManager nula.");
            return false;
        }
        // Use the InputMethodManager directly to hide the keyboard
        Log.d("KeyboardUtils", "hideKeyboardReturned: hideKeyboardReturned");
        viewCurrentFocus.clearFocus();
        return imm.hideSoftInputFromWindow(viewCurrentFocus.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            Log.w("KeyboardUtils", "hideKeyboard - Activity nula.");
            return;
        }
        View viewCurrentFocus = activity.getCurrentFocus();
        if (viewCurrentFocus == null || !viewCurrentFocus.isAttachedToWindow()) {
            Log.w("KeyboardUtils", "hideKeyboard - Nenhuma View focada ou já desconectada.");
            return;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            Log.w("KeyboardUtils", "hideKeyboard - InputMethodManager nula.");
            return;
        }
        // Use the InputMethodManager directly to hide the keyboard
        Log.d("KeyboardUtils", "hideKeyboard - currentFocus.clearFocus");
        viewCurrentFocus.clearFocus();
        imm.hideSoftInputFromWindow(viewCurrentFocus.getWindowToken(), 0);
    }
}
