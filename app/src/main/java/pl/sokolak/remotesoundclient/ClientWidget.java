package pl.sokolak.remotesoundclient;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RemoteViews;

import androidx.core.content.ContextCompat;

import static pl.sokolak.remotesoundclient.MainActivity.ACTION_WIDGET_BUTTON;


public class ClientWidget extends AppWidgetProvider {

    public static String ACTION_CONNECTION_STATUS = "ConnectionStatus";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.client_widget);

        Intent active = new Intent();
        active.putExtra("button", 1);
        active.setAction(ACTION_WIDGET_BUTTON);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 1, active, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.buttonConnectW, actionPendingIntent);

        active = new Intent();
        active.putExtra("button", 2);
        active.setAction(ACTION_WIDGET_BUTTON);
        actionPendingIntent = PendingIntent.getBroadcast(context, 2, active, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button1W, actionPendingIntent);

        active = new Intent();
        active.putExtra("button", 3);
        active.setAction(ACTION_WIDGET_BUTTON);
        actionPendingIntent = PendingIntent.getBroadcast(context, 3, active, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button2W, actionPendingIntent);

        active = new Intent();
        active.putExtra("button", 4);
        active.setAction(ACTION_WIDGET_BUTTON);
        actionPendingIntent = PendingIntent.getBroadcast(context, 4, active, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.button3W, actionPendingIntent);

        active = new Intent();
        active.putExtra("button", 5);
        active.setAction(ACTION_WIDGET_BUTTON);
        actionPendingIntent = PendingIntent.getBroadcast(context, 5, active, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.buttonStopW, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
//        if (intent.getAction().equals(ACTION_CONNECTION_STATUS)) {
//            boolean isConnected = intent.getBooleanExtra("isConnected", false);
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.client_widget);
//            if (isConnected) {
//                remoteViews.setTextViewText(R.id.buttonConnectW, "C");
////                LinearLayout root = new LinearLayout(context);
////                View view = LinearLayout.inflate(context, R.layout.client_widget, root);
////                Button imageButton = view.findViewById(R.id.buttonConnectW);
////                imageButton.setText("CPA");
////                //imageButton.setBackgroundTintList(ContextCompat.getColorStateList(context.getApplicationContext(), R.color.colorBackDark));
//            } else {
//                remoteViews.setTextViewText(R.id.buttonConnectW, "D");
//            }
//
//            AppWidgetManager manager = AppWidgetManager.getInstance(context);
//            manager.updateAppWidget(new ComponentName(context, ClientWidget.class), remoteViews);
//        }
//    }


}