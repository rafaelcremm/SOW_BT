package com.sonitron.sow_interface_bt.config;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sonitron.sow_interface_bt.R;

public class LoginDialog {
    private Context context;
    private View view;
    private static final String FABRICA_PASSWORD = "7485";
    private static final String SONITRON_PASSWORD = "1425";
    private static final String USER_PASSWORD = "0000";

    private EditText editLogin;
    private EditText editPassword;
    private Button btnConfirm;
    private Button btnCancel;

    public LoginDialog(Context context) {
        this.context = context;
    }

    public void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.dialog_login, null);
        builder.setView(view);

        editLogin = view.findViewById(R.id.editLogin);
        editPassword = view.findViewById(R.id.editPassword);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);

        AlertDialog dialog = builder.create();
        dialog.show();

        btnConfirm.setOnClickListener(v -> {
            String enteredLogin = editLogin.getText().toString();
            String enteredPassword = editPassword.getText().toString();
            checkLogin(enteredLogin, enteredPassword, dialog);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
    }

    private void checkLogin(String login, String password, AlertDialog dialog) {
        if (password.equals(FABRICA_PASSWORD)) {
            Toast.makeText(context, "Acesso total concedido (FABRICA) para " + login, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else if (password.equals(SONITRON_PASSWORD)) {
            Toast.makeText(context, "Acesso com restrições concedido (SONITRON) para " + login, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else if (password.equals(USER_PASSWORD)) {
            Toast.makeText(context, "Acesso apenas a parâmetros (USER) para " + login, Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            Toast.makeText(context, "Senha incorreta para " + login, Toast.LENGTH_SHORT).show();
        }
    }
}