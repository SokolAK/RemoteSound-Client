package pl.sokolak.remotesoundclient;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ClientConnector clientConnector = new ClientConnector(this,
                "192.168.254.105",
                "8080");
        clientConnector.init();
    }
}