package pl.sokolak.remotesoundclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String ACTION_WIDGET_BUTTON = "ActionButton";
    private ClientConnector clientConnector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clientConnector = new ClientConnector(this,
                "192.168.0.114",
                "8080");
        clientConnector.init();
    }


    private IntentFilter filter = new IntentFilter(ACTION_WIDGET_BUTTON);
    private BroadcastReceiver broadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int buttonId = intent.getIntExtra("button", -1);
            switch (buttonId) {
                case 1:
                    clientConnector.getBtnConnect().callOnClick();
                    break;
                case 2:
                    clientConnector.getBtn1().callOnClick();
                    break;
                case 3:
                    clientConnector.getBtn2().callOnClick();
                    break;
                case 4:
                    clientConnector.getBtn3().callOnClick();
                    break;
                case 5:
                    clientConnector.getBtnStop().callOnClick();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcast, filter);
    }
}