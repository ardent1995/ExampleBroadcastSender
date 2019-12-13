package com.example.examplebroadcastsender;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvMessage;
    private Button btnSendBroadcast, btnExplicitBroadcast,btnOrderedBroadcast,btnLocalBroadcast;
    private ExampleLocalBroadcastReceiver exampleLocalBroadcastReceiver = new ExampleLocalBroadcastReceiver();
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMessage = findViewById(R.id.tv_message);
        btnSendBroadcast = findViewById(R.id.btn_send_broadcast);
        btnExplicitBroadcast = findViewById(R.id.btn_send_explicit_broadcast);
        btnOrderedBroadcast = findViewById(R.id.btn_send_ordered_broadcast);
        btnLocalBroadcast = findViewById(R.id.btn_send_local_broadcast);

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        btnSendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
                intent.putExtra("com.example.broadcastreceiveroreoonwards.EXTRA_EXAMPLE","Broadcast Received");
                sendBroadcast(intent);
            }
        });
        btnExplicitBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1st approach
                /*Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.example.broadcastreceiveroreoonwards",
                        "com.example.broadcastreceiveroreoonwards.ExampleExplicitBroadcastReceiver");
                intent.setComponent(componentName);
                sendBroadcast(intent);*/
                //2nd approach
                /*Intent intent = new Intent();
                intent.setClassName("com.example.broadcastreceiveroreoonwards",
                        "com.example.broadcastreceiveroreoonwards.ExampleExplicitBroadcastReceiver");
                sendBroadcast(intent);*/
                //3rd approach
                //add that action inside IntentFilter of any BroadcastReceiver of that Package
                /*Intent intent = new Intent("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
                intent.setPackage("com.example.broadcastreceiveroreoonwards");
                sendBroadcast(intent);*/
                //4th approach
                //add that action inside IntentFilter of any BroadcastReceiver of that Package
                Intent intent = new Intent("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> infos = packageManager.queryBroadcastReceivers(intent,0);
                for(ResolveInfo info: infos){
                    ComponentName componentName = new ComponentName(info.activityInfo.packageName,
                            info.activityInfo.name);
                    intent.setComponent(componentName);
                    sendBroadcast(intent);
                }
            }
        });

        btnOrderedBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
                intent.setPackage("com.example.broadcastreceiveroreoonwards");

                Bundle extras = new Bundle();
                extras.putString("stringExtra","start");
                sendOrderedBroadcast(intent, Manifest.permission.WAKE_LOCK,broadcastReceiver,null,0,"start",extras);
//                sendBroadcast(intent,Manifest.permission.WAKE_LOCK);
            }
        });

        btnLocalBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
                localBroadcastManager.sendBroadcast(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
        registerReceiver(broadcastReceiver,intentFilter);

        IntentFilter intentFilter1 = new IntentFilter("com.example.broadcastreceiveroreoonwards.ACTION_EXAMPLE");
        localBroadcastManager.registerReceiver(exampleLocalBroadcastReceiver,intentFilter1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
        localBroadcastManager.unregisterReceiver(exampleLocalBroadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("com.example.broadcastreceiveroreoonwards.EXTRA_EXAMPLE")) {
                tvMessage.setText(intent.getStringExtra("com.example.broadcastreceiveroreoonwards.EXTRA_EXAMPLE"));
            }else{
                int resultCode = getResultCode();
                String resultData = getResultData();
                Bundle resultExtras = getResultExtras(true);
                String stringExtra = resultExtras.getString("stringExtra");
                resultCode++;
                stringExtra += "->BR";

                String toastText = "BR\n"+
                        "resultCode:"+resultCode+"\n"+
                        "resultData:"+resultData+"\n"+
                        "stringExtra:"+stringExtra;

                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
            }
        }
    };
}
