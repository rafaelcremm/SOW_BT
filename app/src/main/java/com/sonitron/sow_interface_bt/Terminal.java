package com.sonitron.sow_interface_bt;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sonitron.sow_interface_bt.operacao.Operacao;
import com.sonitron.sow_interface_bt.serial.SerialListener;
import com.sonitron.sow_interface_bt.serial.SerialService;
import com.sonitron.sow_interface_bt.serial.SerialSocket;

import com.sonitron.sow_interface_bt.util.BaseFragment;
import com.sonitron.sow_interface_bt.util.TextUtil;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class Terminal extends Fragment implements ServiceConnection, SerialListener {

    public enum BtConnected { False, Pending, True }
    private Queue<String> filaComandos = new LinkedList<>();

    public String deviceAddress;
    public String deviceName;

    public SerialService service;

    private TextView receiveText;
    private TextView sendText;
//    private TextUtil.HexWatcher hexWatcher;

    public BtConnected connected = BtConnected.False;
    private boolean initialStart = true;
    private boolean pendingNewline = false;
    private String newline = TextUtil.newline_crlf;

    /*
     * Lifecycle
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
        setRetainInstance(true);
        if (getArguments() != null) {
            deviceAddress = getArguments().getString("deviceAdress");
            deviceName = getArguments().getString("deviceName");
            Log.d("Terminal", "Endereço: " + deviceAddress);
            Log.d("Terminal", "Nome: " + deviceName);

        } else {
            Log.e("Terminal", "Nenhum argumento foi passado!");
        }

    }

    @Override
    public void onDestroy() {
        if (connected != BtConnected.False)
            disconnect();
        getActivity().stopService(new Intent(getActivity(), SerialService.class));
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            getActivity().startService(new Intent(getActivity(), SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    public void onStop() {
        if(service != null && !getActivity().isChangingConfigurations())
            service.detach();
        super.onStop();
    }

    @SuppressWarnings("deprecation") // onAttach(context) was added with API 23. onAttach(activity) works for all API versions
    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        getActivity().bindService(new Intent(getActivity(), SerialService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        try { getActivity().unbindService(this); } catch(Exception ignored) {}
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(initialStart && service != null) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(initialStart && isResumed()) {
            initialStart = false;
            getActivity().runOnUiThread(this::connect);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }

    /*
     * UI
     */

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);

        receiveText = view.findViewById(R.id.receive_text);
        receiveText.setTextColor(getResources().getColor(R.color.colorRecieveText));
        receiveText.setMovementMethod(ScrollingMovementMethod.getInstance());
        receiveText.setText(""); // Certifique-se de limpar o texto inicial

        sendText = view.findViewById(R.id.send_text);

        View sendBtn = view.findViewById(R.id.send_btn);
        sendBtn.setOnClickListener(v -> send(sendText.getText().toString()));
        return view;
    }

    /*
     * Serial + UI
     */
    private void connect() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
            status("connecting...");
            connected = BtConnected.Pending;
            SerialSocket socket = new SerialSocket(getActivity().getApplicationContext(), device);
            service.connect(socket);
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    public void disconnect() {
        connected = BtConnected.False;
        service.disconnect();
    }

    public void send(String str) {
        if(connected != BtConnected.True) {
            Toast.makeText(getActivity(), "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;

            msg = str;
            data = (str + newline).getBytes();

            SpannableStringBuilder spn = new SpannableStringBuilder(msg + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn);
            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }
    /***********************************************************************************************/

//    public void receive(ArrayDeque<byte[]> datas) {
//        SpannableStringBuilder spn = new SpannableStringBuilder();
//        for (byte[] data : datas) {
//
//            String msg = new String(data);
//            if (newline.equals(TextUtil.newline_crlf) && !msg.isEmpty()) {
//                // don't show CR as ^M if directly before LF
//                msg = msg.replace(TextUtil.newline_crlf, TextUtil.newline_lf);
//                // special handling if CR and LF come in separate fragments
//                if (pendingNewline && msg.charAt(0) == '\n') {
//                    if(spn.length() >= 2) {
//                        spn.delete(spn.length() - 2, spn.length());
//                    } else {
//                        Editable edt = receiveText.getEditableText();
//                        if (edt != null && edt.length() >= 2)
//                            edt.delete(edt.length() - 2, edt.length());
//                    }
//                }
//                pendingNewline = msg.charAt(msg.length() - 1) == '\r';
//            }
//            spn.append(TextUtil.toCaretString(msg, !newline.isEmpty()));
//
//        }
//        receiveText.append(spn);
//    }

    /***********************************************************************************************/


    public void status(String str) {
        if (receiveText != null) {
            SpannableStringBuilder spn = new SpannableStringBuilder(str + '\n');
            spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorStatusText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            receiveText.append(spn); // Adiciona o texto

            // Rolagem automática para o final
            receiveText.post(() -> {
                int scrollAmount = receiveText.getLayout().getLineTop(receiveText.getLineCount()) - receiveText.getHeight();
                if (scrollAmount > 0) {
                    receiveText.scrollTo(0, scrollAmount);
                } else {
                    receiveText.scrollTo(0, 0);
                }
            });
        } else {
            Log.e("Terminal", "receiveText não inicializado.");
        }
    }

    /*
     * starting with Android 14, notifications are not shown in notification bar by default when App is in background
     */

    private void showNotificationSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", getActivity().getPackageName());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(Arrays.equals(permissions, new String[]{Manifest.permission.POST_NOTIFICATIONS}) &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !service.areNotificationsEnabled())
            showNotificationSettings();
    }

    /*
     * SerialListener
     */
    @Override
    public void onSerialConnect() {
        status("connected");
        connected = BtConnected.True;

//        //Ir para tela Principal!
//        Bundle args = new Bundle();
//        args.putString("device", "connected");
        Fragment fragment = new TelaPrincipal();
        fragment.setArguments(getArguments());
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment, fragment, "principal").addToBackStack(null).commit();
    }

    @Override
    public void onSerialConnectError(Exception e) {
        status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        ArrayDeque<byte[]> datas = new ArrayDeque<>();
        datas.add(data);
        receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
        receive(datas);


    }

    @Override
    public void onSerialIoError(Exception e) {
        status("connection lost: " + e.getMessage());
        disconnect();
    }


    /***********************************************************************************************/
    /***********************************************************************************************/
    /***********************************************************************************************/


    public void receive(ArrayDeque<byte[]> datas) {
        SpannableStringBuilder spn = new SpannableStringBuilder();
        for (byte[] data : datas) {
            String msg = new String(data).trim();
            Context context = getContext();

            if (msg.startsWith("{") && msg.endsWith("}")) {
                if (context != null) { // Verifica se o contexto está disponível
                    Toast toast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                    toast.setGravity(android.view.Gravity.TOP, 0, 50);
                    toast.show();
                }
                spn.append(msg).append("\n");
            } else if (msg.startsWith("[") && msg.endsWith("]")) {
                Operacao.getInstance().adicionarComandoNaFila(msg);
            }
        }
        receiveText.append(spn);
    }


    private OnDataReceivedListener dataReceivedListener;
    public interface OnDataReceivedListener {
        void onDataReceived(String data);
    }

    public void setOnDataReceivedListener(OnDataReceivedListener listener) {
        this.dataReceivedListener = listener;
    }


}
