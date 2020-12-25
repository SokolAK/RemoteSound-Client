package pl.sokolak.remotesoundclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

@SuppressLint("SetTextI18n")
public class ClientConnector {
    private final Activity activity;
    private Thread threadConnection = null;
    private EditText etIP, etPort;
    private TextView tvConnection;
    private String SERVER_IP;
    private int SERVER_PORT;
    private PrintWriter output;
    private BufferedReader input;
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    private String ip;
    private String port;
    private TextView tvMessages;
    private Button btnConnect;
    private Button button1;

    public ClientConnector(Activity activity, String ip, String port) {
        this.activity = activity;
        this.ip = ip;
        this.port = port;
    }

    public void init() {
        etIP = activity.findViewById(R.id.etIP);
        etIP.setText(ip);
        etPort = activity.findViewById(R.id.etPort);
        etPort.setText(port);
        tvConnection = activity.findViewById(R.id.tvConnection);
        btnConnect = activity.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> {
            //tvConnection.setText("");
            SERVER_IP = etIP.getText().toString().trim();
            SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
            threadConnection = new Thread(new ThreadConnection());
            threadConnection.start();
        });
        tvMessages = activity.findViewById(R.id.tvMessages);

        button1 = activity.findViewById(R.id.button1);
        button1.setOnClickListener(b -> clickButton(1));
        activity.findViewById(R.id.button2).setOnClickListener(b -> clickButton(2));
        activity.findViewById(R.id.button3).setOnClickListener(b -> clickButton(3));
        activity.findViewById(R.id.buttonStop).setOnClickListener(b -> clickButton(104));
        activity.findViewById(R.id.buttonForward).setOnTouchListener((v, e) -> v.onTouchEvent(MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 0, 0, 0)));
    }

    private void clickButton(int c) {
        new Thread(new ThreadWrite(c)).start();
    }

    enum ConnectionStatus {
        CONNECTED,
        DISCONNECTED
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
                    new Thread(new ThreadRead()).start();
                    connectionStatus = ConnectionStatus.CONNECTED;
                    activity.runOnUiThread(() -> btnConnect.setEnabled(false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                socket = null;
                connectionStatus = ConnectionStatus.DISCONNECTED;
            }
        }
    }

    class ThreadWrite implements Runnable {
        private int message;

        ThreadWrite(int c) {
            this.message = c;
        }

        @Override
        public void run() {
            if (output != null) {
                output.write(message);
                output.flush();
            }
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    tvMessages.append("client: " + message + "\n");
//                    etMessage.setText("");
//                }
//            });
        }
    }

    class ThreadRead implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    int message = input.read();
                    if (message != -1) {
                        activity.runOnUiThread(() -> tvMessages.setText("Server: " + message));
                        switch (message) {
                            case 10:
                                button1.setText("1");
                                break;
                            case 11:
                                button1.setText("||");
                                break;
                            case 12:
                                button1.setText(">>");
                                break;

                            case 500:
                                String stat500 = activity.getString(R.string.status) + ": " + activity.getString(R.string.disconnected);
                                tvConnection.setText(stat500);
                                break;
                            case 501:
                                String stat501 = activity.getString(R.string.status) + ": " + activity.getString(R.string.connected);
                                tvConnection.setText(stat501);
                                break;
                        }
                    } else {
                        threadConnection = new Thread(new ThreadConnection());
                        threadConnection.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
