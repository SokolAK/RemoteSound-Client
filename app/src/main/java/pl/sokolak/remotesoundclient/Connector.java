package pl.sokolak.remotesoundclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static android.content.Context.WIFI_SERVICE;

public class Connector {
    private final Activity activity;
    private Thread threadConnection = null;
    private EditText etIP, etPort;
    private TextView tvConnection;
    private String SERVER_IP;
    private int SERVER_PORT;
    private PrintWriter output;
    private BufferedReader input;
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

    public Connector(Activity activity) {
        this.activity = activity;
    }

    public void init() {
        etIP = activity.findViewById(R.id.etIP);
        etPort = activity.findViewById(R.id.etPort);
        tvConnection = activity.findViewById(R.id.tvConnection);
        Button btnConnect = activity.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> {
            tvConnection.setText("");
            SERVER_IP = etIP.getText().toString().trim();
            SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
            threadConnection = new Thread(new ThreadConnection());
            threadConnection.start();
        });
    }

    class ThreadConnection implements Runnable {
        @Override
        public void run() {
            Socket socket;
            if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                try {
                    socket = new Socket(SERVER_IP, SERVER_PORT);
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    activity.runOnUiThread(() -> tvConnection.setText("Connected\n"));
                    //new Thread(new Thread2()).start();
                    connectionStatus = ConnectionStatus.CONNECTED;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                socket = null;
                activity.runOnUiThread(() -> tvConnection.setText("Disonnected\n"));
                connectionStatus = ConnectionStatus.DISCONNECTED;
            }
        }
    }

    enum ConnectionStatus {
        CONNECTED,
        DISCONNECTED
    }
}
