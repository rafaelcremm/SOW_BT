package com.sonitron.sow_interface_bt.parametros;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sonitron.sow_interface_bt.R;
import com.sonitron.sow_interface_bt.Terminal;
import com.sonitron.sow_interface_bt.data.DataManager;

import java.io.File;
import java.util.List;

public class Parametros {

    private static Parametros instance;
    private final View view;
    private final Context context;
    private Terminal terminal;

    public PositionValues Pos;
    public TimeValues Time;
    public SoldaValues Solda;

    public Components components;
    public DataManager dataManager;

    public Parametros(View view, Terminal terminal) {
        this.view = view;
        this.terminal = terminal;
        this.context = view.getContext();

        components = Components.getInstance(view, terminal);

        Pos = PositionValues.getInstance(view,terminal);
        Time = TimeValues.getInstance(view, terminal);
        Solda = SoldaValues.getInstance(view, terminal);

        dataManager = DataManager.getInstance(view,terminal);

        cfg_bttloadparametros();
        cfg_bttSaveParametros();

        dataManager.carregarDefaultParam(Pos, Time, Solda); // Sempre carrega o Default

        List<String> receitasOrdenadas = dataManager.listarReceitasOrdenadasPorData();
        for (String receita : receitasOrdenadas) {
            Log.i("Log", "Arquivo: " + receita);
        }
    }

    // Metodo público para obter a única instância de Parametros
    public static Parametros getInstance(View view, Terminal terminal) {
        if (instance == null || instance.context != view.getContext()) {
            instance = new Parametros(view, terminal);
        }
        return instance;
    }

    // Sobrecarga para obter a instância sem precisar passar o View novamente
    public static Parametros getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Parametros ainda não foi inicializado.");
        }
        return instance;
    }

    public void cfg_bttloadparametros() {
        Button bttLoadParam = view.findViewById(R.id.btt_loadParam);
        bttLoadParam.setOnClickListener(v -> {
            // Obter a lista de receitas ordenadas por data
            List<String> receitasOrdenadas = dataManager.listarReceitasOrdenadasPorData();

            if (receitasOrdenadas.isEmpty()) {
                Toast.makeText(context, "Nenhuma receita disponível", Toast.LENGTH_SHORT).show();
                return;
            }

            // Exibir as receitas em um AlertDialog com opção de apagar
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Selecione ou Apague Receitas");

            String[] receitasArray = receitasOrdenadas.toArray(new String[0]);

            // Configurar itens clicáveis
            builder.setItems(receitasArray, (dialog, which) -> {
                String receitaSelecionada = receitasArray[which];
                if (!dataManager.carregarReceita(receitaSelecionada, DataManager.RECIPES_FOLDER, Pos, Time, Solda)) {
                    Toast.makeText(context, "Erro ao carregar a receita: " + receitaSelecionada, Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(context, "Receita carregada: " + receitaSelecionada, Toast.LENGTH_SHORT).show();
                Pos.updateStringPos();
                Time.updateStringTime();
                Solda.updateStringSolda();
                dataManager.setReceitaAtual(receitaSelecionada);
            });

            // Configurar botão para apagar receitas
            builder.setNegativeButton("Apagar Receita(s)", (dialog, which) -> {
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(context);
                deleteDialog.setTitle("Apagar Receitas");

                boolean[] selecionados = new boolean[receitasArray.length];
                deleteDialog.setMultiChoiceItems(receitasArray, selecionados, (d, i, isChecked) -> {
                    selecionados[i] = isChecked;
                });

                deleteDialog.setPositiveButton("Apagar", (d, w) -> {
                    for (int i = 0; i < receitasArray.length; i++) {
                        if (selecionados[i]) {
                            String receita = receitasArray[i];
                            if (dataManager.apagarReceita(receita)) {
                                Toast.makeText(context, "Receita apagada: " + receita, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Erro ao apagar: " + receita, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                deleteDialog.setNegativeButton("Cancelar", (d, w) -> d.dismiss());
                deleteDialog.create().show();
            });

            builder.setNeutralButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

    public void cfg_bttSaveParametros() {
        Button bttSaveParam = view.findViewById(R.id.btt_saveParam);
        bttSaveParam.setOnClickListener(v -> {

            // Abrir um AlertDialog para o usuário digitar o nome do arquivo
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Salvar Parâmetros");

            // Adicionar um campo de entrada para o nome do arquivo
            final EditText input = new EditText(context);
            input.setHint("Digite o nome do arquivo");
            input.setText(dataManager.getReceitaAtual()); // Pré-carregar com o nome da receita atual
            builder.setView(input);

            builder.setPositiveButton("Salvar", (dialog, which) -> {
                String nomeArquivo = input.getText().toString().trim();

                if (nomeArquivo.isEmpty()) {
                    Toast.makeText(context, "Nome do arquivo não pode estar vazio.", Toast.LENGTH_SHORT).show();
                    cfg_bttSaveParametros(); // Reabrir a tela de salvar
                    return;
                }

                File directory = new File(context.getFilesDir(), DataManager.RECIPES_FOLDER);
                File file = new File(directory, nomeArquivo + ".json");

                // Verifica se o arquivo já existe
                if (file.exists()) {
                    // Perguntar se deseja sobrescrever
                    AlertDialog.Builder overwriteDialog = new AlertDialog.Builder(context);
                    overwriteDialog.setTitle("Sobrescrever Arquivo");
                    overwriteDialog.setMessage("Já existe um arquivo com esse nome. Deseja sobrescrevê-lo?");
                    overwriteDialog.setPositiveButton("Sim", (overwriteDialogInterface, overwriteWhich) -> {
                        // Sobrescrever o arquivo
                        if(dataManager.salvarReceita(nomeArquivo, DataManager.RECIPES_FOLDER, Pos, Time, Solda)){
                            Toast.makeText(context, "Parâmetros sobrescritos como: " + nomeArquivo, Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Erro ao sobrescrever os parâmetros.", Toast.LENGTH_SHORT).show();
                        }

                    });
                    overwriteDialog.setNegativeButton("Não", (overwriteDialogInterface, overwriteWhich) -> {
                        cfg_bttSaveParametros(); // Reabrir a tela de salvar
                    });
                    overwriteDialog.create().show();
                } else {
                    // Salvar o arquivo
                    boolean salvo = dataManager.salvarReceita(nomeArquivo, DataManager.RECIPES_FOLDER, Pos, Time, Solda);

                    if (salvo) {
                        Toast.makeText(context, "Parâmetros salvos como: " + nomeArquivo, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(context, "Erro ao salvar os parâmetros.", Toast.LENGTH_SHORT).show();
                        cfg_bttSaveParametros(); // Reabrir a tela de salvar em caso de erro
                    }
                }
            });

            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });
    }

}

