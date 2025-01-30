package com.sonitron.sow_interface_bt;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import com.sonitron.sow_interface_bt.util.KeyboardUtils;
import com.sonitron.sow_interface_bt.util.SystemUiUtils;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // Força o modo tela ligada
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getSupportFragmentManager().addOnBackStackChangedListener(MainActivity.this);

        ImageView splashImage = findViewById(R.id.splash_image);
        RelativeLayout mainContent = findViewById(R.id.fragmentFull);
        mainContent.setFitsSystemWindows(true);

        SystemUiUtils.enableImmersiveMode(this);

        // Monitora o estado do teclado
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
            boolean isKeyboardVisible = heightDiff > dpToPx(200); // Verifica se o teclado está visível

            if (!isKeyboardVisible) {
                KeyboardUtils.hideKeyboard(this); // Executa suas verificações
            }
        });

        Button btnBack = findViewById(R.id.bt_voltar);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Exibir a splash screen por 2 segundos
        new Handler().postDelayed(() -> {
            splashImage.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment, new DevicesFragment(), "devices").commit();
            } else {
                onBackStackChanged();
            }
        }, 500); // 2000 milissegundos = 2 segundos
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportActionBar() != null) {
            // Enable or disable the "up" button based on the back stack count
            getSupportActionBar().setDisplayHomeAsUpEnabled(getSupportFragmentManager().getBackStackEntryCount() > 0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        // Fecha o teclado, se estiver aberto
        if (KeyboardUtils.hideKeyboardReturned(this)) {
            Log.i("Log", "MainActivity - Teclado fechado.");
            return;
        }

        // Verifica o estado do stack de fragmentos
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() <= 1) {
            Log.i("Log", "MainActivity - Mostrando diálogo de saída.");
            showExitDialog();
        } else {
            Log.i("Log", "MainActivity - Navegando no stack de fragmentos.");
            fragmentManager.popBackStack();
        }
    }

    // Diálogo de saída do aplicativo
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Sair do aplicativo")
                .setMessage("Tem certeza de que deseja sair?")
                .setPositiveButton("Sim", (dialog, which) -> finish())
                .setNegativeButton("Não", null)
                .show();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
