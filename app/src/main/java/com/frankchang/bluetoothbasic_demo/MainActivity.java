package com.frankchang.bluetoothbasic_demo;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;


public class MainActivity extends AppCompatActivity {


    // 畫面元件
    private TextView tvShow;
    // 物件
    private BluetoothAdapter btAdapter;
    private BroadcastReceiver receiver;
    // 常數
    private static final int DISCOVER_CODE = 800;
    private static final int BT_REQUEST_CODE = 900;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvShow = findViewById(R.id.tvShow);

        // 建立藍牙物件
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, "行動裝置缺少藍牙功能！APP 即將關閉！",
                    Toast.LENGTH_SHORT).show();
            finish();

        } else {
            // 藍牙模組關閉中，要求開啟
            if (!btAdapter.isEnabled()) {
                Intent startBlue = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(startBlue, BT_REQUEST_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 要求回應處
        if (requestCode == BT_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "藍牙開啟！", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "藍牙無法開啟！APP 即將關閉！",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 解除註冊 BroadcastReceiver
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    // 尋找藍牙裝置
    public void discover(View view) {
        // 送出尋找藍牙裝置要求
        Intent discoverIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_CODE);
        startActivity(discoverIntent);
    }

    // 配對藍牙裝置
    public void pair(View view) {
        int i = 1;

        tvShow.setText("BlueTooth Paired Devices : \n");
        // 取得待連接裝置列表
        Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {
            tvShow.append(i++ + ". Name : " + device.getName() + " ; Address : " +
                    device.getAddress() + "\n");

        }
    }

    // 解除配對藍牙裝置
    @SuppressLint("SetTextI18n")
    public void unPair(View view) {
        // 解除藍牙
        btAdapter.startDiscovery();
        // 發出廣播
        IntentFilter filter = new IntentFilter();
        receiver = new BlueToothReceiver();
        registerReceiver(receiver, filter);

        Toast.makeText(this, "Unpaired BlueTooth Devices", Toast.LENGTH_SHORT).show();
        tvShow.setText("Unpaired BlueTooth Devices : ");

    }


    private class BlueToothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                tvShow.append("Name : " + device.getName() + "\nAddress : " + device.getAddress());
            }
        }

    }

}
