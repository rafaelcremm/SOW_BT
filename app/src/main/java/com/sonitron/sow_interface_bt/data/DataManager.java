package com.sonitron.sow_interface_bt.data;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sonitron.sow_interface_bt.R;
import com.sonitron.sow_interface_bt.Terminal;
import com.sonitron.sow_interface_bt.parametros.PositionValues;
import com.sonitron.sow_interface_bt.parametros.SoldaValues;
import com.sonitron.sow_interface_bt.parametros.TimeValues;

import org.json.JSONObject;
import org.json.JSONException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class DataManager {

    public static final String RECIPES_FOLDER = "parametros_recipes"; // Diretório para salvar receitas

    private static final String DEFAULT_PARAM_FOLDER = "parametros_default";
    private static final String DEFAULT_PARAM_FILE = "Default";

    private static DataManager instance;

    private final View view;
    private final Context context;
    private final Terminal terminal;

    private TextView str_receitaAtual;
    private String receitaAtual;
    private String receitaAtual_Json;

    // Construtor recebe apenas o contexto para manipulação de arquivos
    private DataManager(View view, Terminal terminal) {
        this.view = view;
        this.context = view.getContext();
        this.terminal = terminal;

        str_receitaAtual = view.findViewById(R.id.str_receitaAtual);
        receitaAtual = DEFAULT_PARAM_FILE;
    }

    public static DataManager getInstance(View view, Terminal terminal) {
        if (instance == null || instance.context != view.getContext()) {
            instance = new DataManager(view, terminal);
        }
        return instance;
    }

    public  DataManager getInstance(){
        if (instance == null) {
            throw new IllegalStateException("Components ainda não foi inicializado.");
        }
        return instance;
    }

    public String getReceitaAtual() {
        return receitaAtual;
    }

    public void setReceitaAtual(String receitaAtual) {
        this.receitaAtual = receitaAtual.replace(".json", "");
        str_receitaAtual.setText(this.receitaAtual);
        receitaAtual_Json = this.receitaAtual + ".json";
    }



    public void salvarDefaultParam(PositionValues pos, TimeValues time, SoldaValues solda) {
        salvarPositionValues(pos);
        salvarTimeValues(time);
        salvarSoldaValues(solda);
    }

    public void salvarPositionValues(PositionValues pos) {
        try {
            File directory = new File(context.getFilesDir(), DEFAULT_PARAM_FOLDER);
            if (!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directory, DEFAULT_PARAM_FILE + ".json");
            JSONObject json;

            // Lê o arquivo existente ou cria um novo JSON se o arquivo não existir
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                json = new JSONObject(builder.toString());
            } else {
                json = new JSONObject();
            }

            // Atualiza apenas os valores de PositionValues
            JSONObject positionJson = new JSONObject();
            positionJson.put("Retorno", pos.Retorno);
            positionJson.put("Inicial", pos.Inicial);
            positionJson.put("Final", pos.Final);
            positionJson.put("Prof", pos.Prof);
            json.put("PositionValues", positionJson);

            // Salva o arquivo atualizado
            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.close();

            Log.i("DataManager", "PositionValues atualizado em Default.json.");
        } catch (Exception e) {
            Log.e("DataManager", "Erro ao atualizar PositionValues em Default.json: " + e.getMessage());
        }
    }

    public void salvarTimeValues(TimeValues time) {
        try {
            File directory = new File(context.getFilesDir(), DEFAULT_PARAM_FOLDER);
            if (!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directory, DEFAULT_PARAM_FILE + ".json");
            JSONObject json;

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                json = new JSONObject(builder.toString());
            } else {
                json = new JSONObject();
            }

            JSONObject timeJson = new JSONObject();
            timeJson.put("Retorno", time.Retorno);
            timeJson.put("Descida", time.Descida);
            timeJson.put("Solda", time.Solda);
            timeJson.put("Espera", time.Espera);
            json.put("TimeValues", timeJson);

            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.close();

            Log.i("DataManager", "TimeValues atualizado em Default.json.");
        } catch (Exception e) {
            Log.e("DataManager", "Erro ao atualizar TimeValues em Default.json: " + e.getMessage());
        }
    }

    public void salvarSoldaValues(SoldaValues solda) {
        try {
            File directory = new File(context.getFilesDir(), DEFAULT_PARAM_FOLDER);
            if (!directory.exists()) {
                directory.mkdir();
            }

            File file = new File(directory, DEFAULT_PARAM_FILE + ".json");
            JSONObject json;

            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                reader.close();
                json = new JSONObject(builder.toString());
            } else {
                json = new JSONObject();
            }

            JSONObject soldaJson = new JSONObject();
            soldaJson.put("Force", solda.Force);
            soldaJson.put("Energia", solda.Energia);
            soldaJson.put("Amplitude", solda.Amplitude);
            soldaJson.put("Rampa", solda.Rampa);
            json.put("SoldaValues", soldaJson);

            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.close();

            Log.i("DataManager", "SoldaValues atualizado em Default.json.");
        } catch (Exception e) {
            Log.e("DataManager", "Erro ao atualizar SoldaValues em Default.json: " + e.getMessage());
        }
    }


    // Metodo para salvar uma receita com nome personalizado
    public boolean salvarReceita(String nomeArquivo, String diretorio, PositionValues pos, TimeValues time, SoldaValues solda) {
        try {
            // Verifica se o diretório existe, caso contrário, cria
            File directory = new File(context.getFilesDir(), diretorio);
            if (!directory.exists()) {
                directory.mkdir();
            }

            setReceitaAtual(nomeArquivo);

            File file = new File(directory, receitaAtual_Json);

            JSONObject json = new JSONObject();

            // Adiciona o deviceName e deviceAddress
            JSONObject fileValues = new JSONObject();
            fileValues.put("deviceName", terminal != null ? terminal.deviceName : "Desconhecido");
            fileValues.put("deviceAddress", terminal != null ? terminal.deviceAddress : "Desconhecido");
            fileValues.put("Date", new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()));
            json.put("FileValues", fileValues);

            // Adiciona os valores de PositionValues
            JSONObject positionJson = new JSONObject();
            positionJson.put("Retorno", pos.Retorno);
            positionJson.put("Inicial", pos.Inicial);
            positionJson.put("Final", pos.Final);
            positionJson.put("Prof", pos.Prof);
            json.put("PositionValues", positionJson);

            // Adiciona os valores de TimeValues
            JSONObject timeJson = new JSONObject();
            timeJson.put("Retorno", time.Retorno);
            timeJson.put("Descida", time.Descida);
            timeJson.put("Solda", time.Solda);
            timeJson.put("Espera", time.Espera);
            json.put("TimeValues", timeJson);

            // Adiciona os valores de SoldaValues
            JSONObject soldaJson = new JSONObject();
            soldaJson.put("Force", solda.Force);
            soldaJson.put("Energia", solda.Energia);
            soldaJson.put("Amplitude", solda.Amplitude);
            soldaJson.put("Rampa", solda.Rampa);
            json.put("SoldaValues", soldaJson);

            // Salva o arquivo
            FileWriter writer = new FileWriter(file);
            writer.write(json.toString());
            writer.close();

            Log.i("Log", "Receita salva com sucesso: " + receitaAtual);
            Log.i("Log", "JSON: " + json);
            return true;

        } catch (JSONException | IOException e) {
            Log.e("Log", "Erro ao salvar receita: " + e.getMessage());
            return false;
        }
    }

    public void carregarDefaultParam(PositionValues pos, TimeValues time, SoldaValues solda) {
        carregarReceita(DEFAULT_PARAM_FILE, DEFAULT_PARAM_FOLDER, pos, time, solda);
        pos.updateStringPos();
        time.updateStringTime();
        solda.updateStringSolda();
    }

    // Metodo para carregar uma receita e atualizar os objetos fornecidos
    public boolean carregarReceita(String nomeArquivo, String diretorio, PositionValues pos, TimeValues time, SoldaValues solda) {

        Log.i("Log", "DataManager: Carregando receita: " + nomeArquivo);
        setReceitaAtual(nomeArquivo);

        try {
            File directory = new File(context.getFilesDir(), diretorio);
            File file = new File(directory, receitaAtual_Json);
            if (!file.exists()) {
                Log.e("Log", "DataManager: Receita " + receitaAtual_Json + " ,não encontrada no diretório: " + directory);
                Log.e("Log", "DataManager: " + file);
                setReceitaAtual(DEFAULT_PARAM_FILE);
                return false;
            }

            // Lê o arquivo
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();

            // Parse do JSON
            JSONObject json = new JSONObject(builder.toString());
            // Atualiza os valores de PositionValues
            JSONObject positionJson = json.getJSONObject("PositionValues");
            pos.Retorno = positionJson.getDouble("Retorno");
            pos.Inicial = positionJson.getDouble("Inicial");
            pos.Final = positionJson.getDouble("Final");
            pos.Prof = positionJson.getDouble("Prof");

            // Atualiza os valores de TimeValues
            JSONObject timeJson = json.getJSONObject("TimeValues");
            time.Retorno = timeJson.getDouble("Retorno");
            time.Descida = timeJson.getDouble("Descida");
            time.Solda = timeJson.getDouble("Solda");
            time.Espera = timeJson.getDouble("Espera");

            // Atualiza os valores de SoldaValues
            JSONObject soldaJson = json.getJSONObject("SoldaValues");
            solda.Force = soldaJson.getDouble("Force");
            solda.Energia = soldaJson.getDouble("Energia");
            solda.Amplitude = soldaJson.getDouble("Amplitude");
            solda.Rampa = soldaJson.getDouble("Rampa");

            Log.i("Log", "DataManager: Receita carregada com sucesso: " + receitaAtual_Json);
            Log.i("Log", "JSON: " + json);
            return true;

        } catch (JSONException | IOException e) {
            Log.e("Log", "DataManager: Erro ao carregar receita: " + e.getMessage());
            return false;
        }
    }

    // Metodo para listar todas as receitas disponíveis
    public List<String> listarReceitas() {
        List<String> receitas = new ArrayList<>();
        File directory = new File(context.getFilesDir(), RECIPES_FOLDER);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    receitas.add(file.getName());
                }
            }
        }
        return receitas;
    }

    public List<String> listarReceitasOrdenadasPorData() {
        List<FileValues> fileValuesList = new ArrayList<>();
        File directory = new File(context.getFilesDir(), RECIPES_FOLDER);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        // Lê o conteúdo do arquivo para obter o campo "Date"
                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        reader.close();

                        JSONObject json = new JSONObject(builder.toString());
                        String date = json.getJSONObject("FileValues").getString("Date");

                        // Adiciona o nome do arquivo e a data à lista
                        fileValuesList.add(new FileValues(file.getName(), date));
                    } catch (Exception e) {
                        Log.e("DataManager", "Erro ao processar arquivo: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }

        // Ordena a lista pelo campo "Date"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Collections.sort(fileValuesList, (f1, f2) -> f2.getDate().compareTo(f1.getDate()));

        }

        // Converte os nomes dos arquivos para uma lista
        List<String> receitasOrdenadas = new ArrayList<>();
        for (FileValues fileValues : fileValuesList) {
            receitasOrdenadas.add(fileValues.getFileName());
        }

        // Remover duplicados antes de retornar
        return removerDuplicados(receitasOrdenadas);
    }

    public List<String> removerDuplicados(List<String> receitas) {
        // Usar um Set para remover duplicados
        Set<String> setUnico = new LinkedHashSet<>(receitas);

        // Retornar uma lista sem duplicados
        return new ArrayList<>(setUnico);
    }

    public boolean apagarReceita(String nomeArquivo) {
        File directory = new File(context.getFilesDir(), RECIPES_FOLDER);
        File file = new File(directory, nomeArquivo);
        if (file.exists()) {
            if (file.delete()) {
                Log.i("DataManager", "Receita apagada: " + nomeArquivo);
                return true;
            } else {
                Log.e("DataManager", "Erro ao apagar receita: " + nomeArquivo);
                return false;
            }
        } else {
            Log.e("DataManager", "Receita não encontrada para apagar: " + nomeArquivo);
            return false;
        }
    }

    public void apagarTodasReceitas() {
        File directory = new File(context.getFilesDir(), RECIPES_FOLDER);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.delete()) {
                        Log.i("Log", "DataManager: Arquivo deletado: " + file.getName());
                    } else {
                        Log.e("Log", "DataManager: Erro ao deletar arquivo: " + file.getName());
                    }
                }
            } else {
                Log.i("Log", "DataManager: Nenhum arquivo encontrado para deletar.");
            }
        } else {
            Log.e("DataManager", "Diretório de receitas não encontrado.");
        }
    }

}
