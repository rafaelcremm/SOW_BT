package com.sonitron.sow_interface_bt.operacao;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.sonitron.sow_interface_bt.Terminal;
import com.sonitron.sow_interface_bt.parametros.Components;

import java.util.Queue;
import java.util.LinkedList;

public class Operacao {

    private static Operacao instance;
    private Terminal terminal;
    private final Context context;
    private final View view;
    private Queue<String> filaComandos;

    private Operacao(View view, Terminal terminal) {
        this.terminal = terminal;
        this.view = view;
        this.context = view.getContext();

        this.filaComandos = new LinkedList<>();
    }

    public static synchronized Operacao getInstance(View view, Terminal terminal) {
        if (instance == null) {
            instance = new Operacao(view, terminal);
        }
        return instance;
    }

    public static synchronized Operacao getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Operacao ainda não foi inicializado corretamente. Chame getInstance(view, terminal) primeiro.");
        }
        return instance;
    }

    public void iniciarSolda() {
        if (terminal != null) {
            terminal.send("(>C1)"); // Enviar comando para iniciar RUN no gerador
            Log.d("Operacao", "Comando RUN enviado.");
        } else {
            Log.e("Operacao", "Terminal não inicializado.");
        }
    }

    public void adicionarComandoNaFila(String comando) {
        filaComandos.add(comando);
        processarFilaComandos();
    }


    private void processarFilaComandos() {
        while (!filaComandos.isEmpty()) {
            String comando = filaComandos.poll();
            Log.d("Operacao", "Executando comando: " + comando);
            executarComando(comando);
        }
    }

    private void executarComando(String comando) {
        if (comando.equals("[>C1]")) {
            Log.d("Operacao", "Executando comando RUN");
            iniciarSolda();
        } else {
            Log.d("Operacao", "Comando desconhecido: " + comando);
        }
    }
}
