package pl.sokolak.remotesoundclient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;

import lombok.Getter;

import static pl.sokolak.remotesoundclient.ClientWidget.ACTION_CONNECTION_STATUS;

@SuppressLint("SetTextI18n")
public class ClientConnector {
    private final Activity activity;
    private Thread threadConnection = null;
    private EditText etIP, etPort, etRepeat;
    private String SERVER_IP;
    private int SERVER_PORT;
    private PrintWriter output;
    private BufferedReader input;
    private ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    private String ip;
    private String port;
    private TextView tvConnection, tvMessages, tvTime, tvPlayerStatus, tvRepeat, tvVolume;
    @Getter
    private Button btnConnect, btn1, btn2, btn3, btn4, btn5, btn6;
    @Getter
    private ImageButton btnStop, btnBack, btnForw, btnVolUp, btnVolDown;
    private Socket socket;

    public ClientConnector(Activity activity, String ip, String port) {
        this.activity = activity;
        this.ip = ip;
        this.port = port;
    }

    public void init() {
        configureUI();
    }

    private void clickButton(String msg) {
        new Thread(new ThreadWrite(msg)).start();
        //etRepeat.clearFocus();
    }

    class ThreadConnection implements Runnable {
        @Override
        public void run() {
            if (connectionStatus == ConnectionStatus.DISCONNECTED) {
                try {
                    //connectionStatus = ConnectionStatus.CONNECTED;
                    socket = new Socket(SERVER_IP, SERVER_PORT);
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    new Thread(new ThreadRead()).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                socket = null;
                //connectionStatus = ConnectionStatus.DISCONNECTED;
            }
        }
    }

    class ThreadWrite implements Runnable {
        private String msg;

        ThreadWrite(String msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            if (output != null) {
                output.println(msg);
                output.flush();

                //String repeatVal = "R" + etRepeat.getText();
                //output.println(repeatVal);
                //output.flush();
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
                if (input != null) {
                    try {
                        String message = input.readLine();
                        if (message != null) {
                            @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("HH:mm:ss");
                            String time = df.format(Calendar.getInstance().getTime());
                            activity.runOnUiThread(() -> tvMessages.setText("Server [" + time + "]:\n" + message));
                            String[] items = message.trim().split("_");

                            if (Integer.parseInt(items[0]) == 1) {
                                changeConnectionStatus(true);
                            }
                            if (Integer.parseInt(items[0]) == 0) {
                                changeConnectionStatus(false);
                            }

                            activity.runOnUiThread(() -> tvPlayerStatus.setText(String.valueOf(items[1])));
                            activity.runOnUiThread(() -> tvTime.setText(String.valueOf(items[2])));
                            activity.runOnUiThread(() -> tvRepeat.setText(String.valueOf(items[3])));
                            if (!etRepeat.hasFocus()) {
                                activity.runOnUiThread(() -> etRepeat.setText(String.valueOf(items[4])));
                            }

                            activity.runOnUiThread(() -> tvVolume.setText(String.valueOf(items[5])));

                            activity.runOnUiThread(() -> btn1.setText(String.valueOf(items[6])));
                            activity.runOnUiThread(() -> btn2.setText(String.valueOf(items[7])));
                            activity.runOnUiThread(() -> btn3.setText(String.valueOf(items[8])));
                            activity.runOnUiThread(() -> btn4.setText(String.valueOf(items[9])));
                            activity.runOnUiThread(() -> btn5.setText(String.valueOf(items[10])));
                            activity.runOnUiThread(() -> btn6.setText(String.valueOf(items[11])));
                        } else {
                            changeConnectionStatus(false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void configureUI() {
        etIP = activity.findViewById(R.id.etIP);
        etIP.setText(ip);
        etPort = activity.findViewById(R.id.etPort);
        etPort.setText(port);

        tvConnection = activity.findViewById(R.id.tvConnection);
        btnConnect = activity.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(v -> {
            SERVER_IP = etIP.getText().toString().trim();
            SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
            threadConnection = new Thread(new ThreadConnection());
            threadConnection.start();
        });
        changeConnectionStatus(false);

        tvMessages = activity.findViewById(R.id.tvMessages);
        tvTime = activity.findViewById(R.id.time);
        tvPlayerStatus = activity.findViewById(R.id.playerStatus);
        tvRepeat = activity.findViewById(R.id.repeatLabel);
        etRepeat = activity.findViewById(R.id.repeatValue);
        etRepeat.clearFocus();
        tvVolume = activity.findViewById(R.id.volumeValue);

        btn1 = activity.findViewById(R.id.button1);
        btn2 = activity.findViewById(R.id.button2);
        btn3 = activity.findViewById(R.id.button3);
        btn4 = activity.findViewById(R.id.button4);
        btn5 = activity.findViewById(R.id.button5);
        btn6 = activity.findViewById(R.id.button6);
        btnStop = activity.findViewById(R.id.buttonStop);
        btnForw = activity.findViewById(R.id.buttonForward);
        btnBack = activity.findViewById(R.id.buttonBackward);
        btnVolUp = activity.findViewById(R.id.volumeUp);
        btnVolDown = activity.findViewById(R.id.volumeDown);

        btn1.setOnClickListener(b -> clickButton("1"));
        btn2.setOnClickListener(b -> clickButton("2"));
        btn3.setOnClickListener(b -> clickButton("3"));
        btn4.setOnClickListener(b -> clickButton("4"));
        btn5.setOnClickListener(b -> clickButton("5"));
        btn6.setOnClickListener(b -> clickButton("6"));
        btnStop.setOnClickListener(b -> clickButton("stop"));
        btnVolUp.setOnClickListener(b -> clickButton("vu"));
        btnVolDown.setOnClickListener(b -> clickButton("vd"));

        etRepeat.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                new Thread(new ThreadWrite("r" + etRepeat.getText())).start();
                etRepeat.clearFocus();
            }
            return false;
        });

        btnForw.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    new Thread(new ThreadWrite("fd")).start();
                    break;
                case MotionEvent.ACTION_UP:
                    new Thread(new ThreadWrite("fu")).start();
                    break;
            }
            return false;
        });

        btnBack.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    new Thread(new ThreadWrite("bd")).start();
                    break;
                case MotionEvent.ACTION_UP:
                    new Thread(new ThreadWrite("bu")).start();
                    break;
            }
            return false;
        });
    }

    private void changeConnectionStatus(boolean isConnected) {
        if (isConnected) {
            connectionStatus = ConnectionStatus.CONNECTED;
            tvConnection.setText(activity.getString(R.string.status) + ": " + activity.getString(R.string.connected));
            activity.runOnUiThread(() -> btnConnect.setText(R.string.reconnect));
        } else {
            connectionStatus = ConnectionStatus.DISCONNECTED;
            tvConnection.setText(activity.getString(R.string.status) + ": " + activity.getString(R.string.disconnected));
            activity.runOnUiThread(() -> btnConnect.setText(R.string.connect));
        }

        Intent intent = new Intent(activity, ClientWidget.class);
        intent.setAction(ACTION_CONNECTION_STATUS);
        int ids[] = AppWidgetManager.getInstance(activity.getApplication()).getAppWidgetIds(new ComponentName(activity.getApplication(), ClientWidget.class));
        intent.putExtra("isConnected", isConnected);
        activity.sendBroadcast(intent);
    }

    enum ConnectionStatus {
        CONNECTED,
        DISCONNECTED
    }
}
