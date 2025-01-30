package com.sonitron.sow_interface_bt;

import android.Manifest;
import androidx.annotation.NonNull;
import android.annotation.SuppressLint;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import com.sonitron.sow_interface_bt.serial.BluetoothUtil;

import java.util.ArrayList;
import java.util.Collections;

public class DevicesFragment extends ListFragment {

    private BluetoothAdapter bluetoothAdapter; // Adaptador Bluetooth para gerenciar conexões
    private final ArrayList<BluetoothDevice> listItems = new ArrayList<>(); // Lista de dispositivos Bluetooth encontrados
    private ArrayAdapter<BluetoothDevice> listAdapter; // Adaptador para exibir dispositivos na UI
    private ActivityResultLauncher<String[]> requestBluetoothPermissionLauncherForRefresh; // Gerenciador de permissões
    private boolean permissionMissing; // Flag para verificar permissões

    private BroadcastReceiver discoveryReceiver;// BroadcastReceiver para lidar com eventos Bluetooth, como descoberta de dispositivos

    // BroadcastReceiver para lidar com eventos Bluetooth, como descoberta de dispositivos
//    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
//
//        @Override
//        @SuppressLint("MissingPermission")
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction(); // Obtem a ação do evento recebido
//
//            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                // Evento de dispositivo encontrado
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                if (device != null) {
//                    Log.d("DevicesFragment", "Dispositivo encontrado: " + device.getName() + " [" + device.getAddress() + "]");
//
//                    // Evita dispositivos duplicados na lista
//                    boolean isDuplicate = false;
//                    for (BluetoothDevice listedDevice : listItems) {
//                        if (listedDevice.getAddress().equals(device.getAddress())) {
//                            isDuplicate = true;
//                            break;
//                        }
//                    }
//                    if (!isDuplicate) {
//                        listItems.add(device); // Adiciona dispositivo à lista
//                        listAdapter.notifyDataSetChanged(); // Atualiza a UI
//                        Log.d("DevicesFragment", "Dispositivo adicionado: " + device.getName());
//                    }
//                } else {
//                    Log.w("DevicesFragment", "Dispositivo desconhecido encontrado durante a descoberta.");
//                }
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
//                // Evento quando a descoberta começa
//                Log.d("DevicesFragment", "Descoberta iniciada pelo adaptador Bluetooth.");
//                Toast.makeText(context, "Descoberta de dispositivos iniciada.", Toast.LENGTH_SHORT).show();
//            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
//                // Evento quando a descoberta termina
//                Log.d("DevicesFragment", "Descoberta finalizada.");
//                Toast.makeText(context, "Descoberta de dispositivos concluída.", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // Indica que o fragmento possui opções de menu

        // Verifica se o dispositivo suporta Bluetooth
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        // Configura o adaptador da lista para exibir dispositivos
        listAdapter = new ArrayAdapter<BluetoothDevice>(getActivity(), 0, listItems) {

            @Override
            @SuppressLint("MissingPermission")
            public View getView(int position, View view, ViewGroup parent) {
                BluetoothDevice device = listItems.get(position);
                if (view == null)
                    view = getActivity().getLayoutInflater().inflate(R.layout.device_list_item, parent, false);

                TextView nameItem = view.findViewById(R.id.nameItem); // Nome do dispositivo
                TextView adressItem = view.findViewById(R.id.adressItem); // Endereço MAC do dispositivo

                nameItem.setText(device.getName());
                adressItem.setText(device.getAddress());

                return view;
            }
        };

        // BroadcastReceiver para lidar com eventos Bluetooth, como descoberta de dispositivos
        discoveryReceiver = new BroadcastReceiver() {
            @Override
            @SuppressLint("MissingPermission")
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction(); // Obtem a ação do evento recebido

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Evento de dispositivo encontrado
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        Log.d("DevicesFragment", "Dispositivo encontrado: " + device.getName() + " [" + device.getAddress() + "]");

                        // Evita dispositivos duplicados na lista
                        boolean isDuplicate = false;
                        for (BluetoothDevice listedDevice : listItems) {
                            if (listedDevice.getAddress().equals(device.getAddress())) {
                                isDuplicate = true;
                                break;
                            }
                        }
                        if (!isDuplicate) {
                            listItems.add(device); // Adiciona dispositivo à lista
                            listAdapter.notifyDataSetChanged(); // Atualiza a UI
                            Log.d("DevicesFragment", "Dispositivo adicionado: " + device.getName());
                        }
                    } else {
                        Log.w("DevicesFragment", "Dispositivo desconhecido encontrado durante a descoberta.");
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    // Evento quando a descoberta começa
                    Log.d("DevicesFragment", "Descoberta iniciada pelo adaptador Bluetooth.");
                    Toast.makeText(context, "Descoberta de dispositivos iniciada.", Toast.LENGTH_SHORT).show();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    // Evento quando a descoberta termina
                    Log.d("DevicesFragment", "Descoberta finalizada.");
                    Toast.makeText(context, "Descoberta de dispositivos concluída.", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Configura ActivityResultLauncher para múltiplas permissões
        requestBluetoothPermissionLauncherForRefresh = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean allGranted = true;
                    for (Boolean granted : permissions.values()) {
                        if (!granted) {
                            allGranted = false;
                            break;
                        }
                    }
                    if (allGranted) {
                        Log.d("DevicesFragment", "Todas as permissões concedidas.");
                        refresh(); // Atualiza a lista de dispositivos
                    } else {
                        Log.e("DevicesFragment", "Permissões Bluetooth não concedidas.");
                        Toast.makeText(getActivity(), "Permissões Bluetooth necessárias.", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        Log.d("DevicesFragment", "onCreate finalizado.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar o layout do fragmento que contem a lista.
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(null);

        Log.d("DevicesFragment", "onActivityCreated iniciado.");
        View header = getActivity().getLayoutInflater().inflate(R.layout.device_list_header, null, false);

        // Configura botões no cabeçalho
        header.findViewById(R.id.btn_discover).setOnClickListener(v -> startDiscovery()); // Botão para iniciar descoberta
        header.findViewById(R.id.bt_settings).setOnClickListener(v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent); // Abre as configurações de Bluetooth
            Log.d("DevicesFragment", "Abrindo configurações de Bluetooth.");
        });
        header.findViewById(R.id.bt_refresh).setOnClickListener(v -> {
            Log.d("DevicesFragment", "Atualizando dispositivos.");
            refresh(); // Atualiza lista de dispositivos
        });

        // Adiciona o cabeçalho à lista
        getListView().addHeaderView(header, null, false);
        Log.d("DevicesFragment", "Cabeçalho adicionado ao ListView.");

        setListAdapter(listAdapter); // Define o adaptador da lista
        Log.d("DevicesFragment", "Adaptador configurado.");

        refresh(); // Garante que a lista seja inicializada
    }

    @SuppressLint("MissingPermission")
    void refresh() {
        Log.d("DevicesFragment", "Iniciando refresh...");
        listItems.clear(); // Limpa a lista atual de dispositivos

        if (bluetoothAdapter == null) {
            Log.d("DevicesFragment", "Bluetooth não suportado.");
            Toast.makeText(getActivity(), "Bluetooth não suportado.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Log.d("DevicesFragment", "Bluetooth está desativado.");
            Toast.makeText(getActivity(), "Bluetooth está desativado.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica permissões para dispositivos mais recentes (API >= S)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e("DevicesFragment", "Permissão BLUETOOTH_CONNECT ausente.");
                requestBluetoothPermissionLauncherForRefresh.launch(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT
                });
                return;
            }
        }

        // Adiciona dispositivos emparelhados à lista
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getType() != BluetoothDevice.DEVICE_TYPE_LE) { // Ignora dispositivos BLE
                listItems.add(device);
            }
        }
        Collections.sort(listItems, BluetoothUtil::compareTo); // Ordena os dispositivos

        listAdapter.notifyDataSetChanged(); // Atualiza a interface
        Log.d("DevicesFragment", "Dispositivos emparelhados carregados: " + listItems.size());
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        BluetoothDevice device = listItems.get(position - 1); // Obtém o dispositivo clicado

        // Passa os argumentos para iniciar conexão no Terminal
        Bundle args = new Bundle();
        args.putString("deviceAdress", device.getAddress());
        args.putString("deviceName", device.getName());
        Fragment fragment = new Terminal();
        fragment.setArguments(args);

        // Abre a tela Terminal em fragment_device_log
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_device_log, fragment, "terminal")
                .commit();
        // Depois da conexao, vai para TelaPrincipal.

    }

    @SuppressLint("MissingPermission")
    private void startDiscovery() {
        // Verifica permissões para iniciar descoberta (API >= S)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                Log.e("DevicesFragment", "Permissão BLUETOOTH_SCAN ausente. Solicitando permissão.");
                requestBluetoothPermissionLauncherForRefresh.launch(new String[]{
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT
                });
                return;
            }
        }

        // Cancela descoberta em andamento, se necessário
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
            Log.d("DevicesFragment", "Descoberta cancelada.");
        }

        if (bluetoothAdapter != null) {
            bluetoothAdapter.startDiscovery(); // Inicia descoberta de dispositivos
            Toast.makeText(getActivity(), "Iniciando descoberta de dispositivos...", Toast.LENGTH_SHORT).show();
            Log.d("DevicesFragment", "Descoberta iniciada.");

            // Registra BroadcastReceiver para eventos de descoberta
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            getActivity().registerReceiver(discoveryReceiver, filter);
            Log.d("DevicesFragment", "BroadcastReceiver registrado para descoberta de dispositivos.");
        } else {
            Log.e("DevicesFragment", "bluetoothAdapter é nulo.");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (discoveryReceiver != null) {
            try {
                getActivity().unregisterReceiver(discoveryReceiver); // Remove registro do BroadcastReceiver
                Log.d("DevicesFragment", "BroadcastReceiver desregistrado.");
            } catch (IllegalArgumentException e) {
                Log.e("DevicesFragment", "Receiver já desregistrado: " + e.getMessage());
            }
        }
    }
}
