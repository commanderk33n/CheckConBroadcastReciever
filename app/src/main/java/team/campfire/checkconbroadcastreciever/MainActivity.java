package team.campfire.checkconbroadcastreciever;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private BroadcastReceiver MyReceiver = null;
    protected WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyReceiver = new MyReceiver();
        broadcastIntent();

        wv = findViewById(R.id.webkit);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("file:///android_asset/index.html");
    }

    public void broadcastIntent() {
        registerReceiver(MyReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        broadcastIntent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(MyReceiver);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = NetworkUtil.getConnectivityStatusString(context);
            if (status.isEmpty()) {
                status = "No Internet Connection";
            }
            Boolean inetConnected = false;
            try {
              inetConnected = NetworkUtil.isConnected();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            Toast.makeText(context, status, Toast.LENGTH_LONG).show();

            if(inetConnected) {
                wv.evaluateJavascript("internetIsConnected()",null);

            } else {
                wv.evaluateJavascript("internetIsNotConnected()",null);
            }

            switch (status) {
                case "Wifi enabled":
                    wv.evaluateJavascript("networkStatus('Wifi enabled');", null);
                    break;
                case "Mobile data enabled":
                    wv.evaluateJavascript("networkStatus('Mobile data enabled');", null);
                    break;
                case "No internet is available":
                    wv.evaluateJavascript("networkStatus('No internet is available');", null);
                    break;
            }


        }
    }
}