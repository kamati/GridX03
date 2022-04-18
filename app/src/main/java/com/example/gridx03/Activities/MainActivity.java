package com.example.gridx03.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.gridx03.R;
import com.example.gridx03.Sevices.BLEService;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_HOME;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_CONNECT;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_DISCONNECT;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_STRING;
import static com.example.gridx03.Sevices.BLEService.PAGE;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    public static final String GEYSERSTATE ="g_state";
    public static final String CONSUMPTION = "consm";
    public static final String UNITS = "units";
    private ProgressBar ProgressBarMeterUnits;
    FloatingActionMenu ConnectionSelection;
    FloatingActionButton ConnectionSelectionButton;
    ImageView BLEConnectionIcon;
    ImageView MetersConnectionIcon;
    ImageView WifiConnectionIcon;

    TextView txtUnits;
    TextView txtConsuption;
    AlertDialog ad, ae;

    static String[] scanContent = {"Wifi", "Bluetooth"};
    BarChart barChart;
    BarData barData;
    BarDataSet barDataSet;
    ArrayList barEntriesArrayList;
    private ImageView GeyserButton;
    private ImageView buttonRecharge;
    private ImageView buttonStats;

    private enum BLEStatus {
        CONNECTED,
        DISCONNECTED
    }
    private BLEStatus timerStatus = BLEStatus.DISCONNECTED;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ProgressBarMeterUnits = findViewById(R.id.progress_bar_main);
        txtUnits = (TextView) findViewById(R.id.text_units);
        txtConsuption = (TextView) findViewById(R.id.text_consuption);
        GeyserButton = findViewById(R.id.image_geyser);
        buttonRecharge = findViewById(R.id.image_recharge);
        buttonStats = findViewById(R.id.image_statistics);
        BLEConnectionIcon = findViewById(R.id.image_ble_connection);
        CheckPhoneBLESupport();
        ProgressBarMeterUnits.setMax(2000);
        BLEConnectionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
            }
        });
        GeyserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GeyserNavigationActivity.class);
                startActivity(intent);
            }
        });
        buttonRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RechargeActivity.class);
                startActivity(intent);
            }
        });
        buttonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatsNavigationActivity.class);
                startActivity(intent);
            }
        });

        setupChart();
        sendBlEData();
    }

    private void CheckPhoneBLESupport(){
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_SHORT).show();
            BLEConnectionIcon.setVisibility(View.INVISIBLE);
        }
    }

    private void checkPermission(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()){
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            return;
        }

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for(String permission:permissions){
            int permissionCheck=ContextCompat.checkSelfPermission(this,permission);
            if(permissionCheck==PackageManager.PERMISSION_GRANTED){
                onPermissionGranted(permission);
            }else {
                permissionDeniedList.add(permission);
            }
        }
        if(!permissionDeniedList.isEmpty()){
            String [] deniedPermissions = permissionDeniedList.toArray( new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, REQUEST_CODE_PERMISSION_LOCATION);
        }

    }

    private void onPermissionGranted(String permission) {
        switch (permission){
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&!checkGPSIsOpen()){
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivityForResult(intent,REQUEST_CODE_OPEN_GPS);
                                }
                            }).setCancelable(false).show();
                }else{
                    Intent serviceIntent = new Intent(getApplicationContext(), BLEService.class);
                    serviceIntent.putExtra("inputExtra","MainActivity");
                    ContextCompat.startForegroundService(getApplicationContext(),serviceIntent);

                }
                break;
        }
    }
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager==null){
            return false;
        }
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);

    }

    private void getBarEntries() {
        // creating a new array list
        barEntriesArrayList = new ArrayList<>();

        // adding new entry to our array list with bar
        // entry and passing x and y axis value to it.
        barEntriesArrayList.add(new BarEntry(1f, 4));
        barEntriesArrayList.add(new BarEntry(2f, 6));
        barEntriesArrayList.add(new BarEntry(3f, 8));
        barEntriesArrayList.add(new BarEntry(4f, 2));
        barEntriesArrayList.add(new BarEntry(5f, 4));
        barEntriesArrayList.add(new BarEntry(6f, 1));
        barEntriesArrayList.add(new BarEntry(7f, 1));
        barEntriesArrayList.add(new BarEntry(8f, 1));
        barEntriesArrayList.add(new BarEntry(9f, 7));
        barEntriesArrayList.add(new BarEntry(10f, 1));
        barEntriesArrayList.add(new BarEntry(11f, 2));
        barEntriesArrayList.add(new BarEntry(12f, 1));
        barEntriesArrayList.add(new BarEntry(13f, 1));
        barEntriesArrayList.add(new BarEntry(14f, 1));
        barEntriesArrayList.add(new BarEntry(15f, 6));
        barEntriesArrayList.add(new BarEntry(16f, 1));
        barEntriesArrayList.add(new BarEntry(17f, 1));
        barEntriesArrayList.add(new BarEntry(18f, 6));
        barEntriesArrayList.add(new BarEntry(19f, 1));
        barEntriesArrayList.add(new BarEntry(20f, 1));
        barEntriesArrayList.add(new BarEntry(21f, 8));
        barEntriesArrayList.add(new BarEntry(22f, 1));
        barEntriesArrayList.add(new BarEntry(23f, 1));
        barEntriesArrayList.add(new BarEntry(24f, 4));
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GRIDX_BLE_BROADCAST_HOME.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                if(BleData!=null){

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(BleData);
                        String cons = jObject.getString(CONSUMPTION);
                        String units = jObject.getString(UNITS);
                        int unitsInt = jObject.getInt(UNITS);
                        txtConsuption.setText(cons);
                        txtUnits.setText(units);
                        ProgressBarMeterUnits.setProgress(unitsInt,true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }

            if(GRIDX_BLE_BROADCAST_SEND.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                if(BleData!=null){

                  if(GRIDX_BLE_CONNECT.equals(BleData)){
                      BLEConnectionIcon.setBackgroundColor(getResources().getColor(R.color.md_blue_100));
                      timerStatus = BLEStatus.CONNECTED;
                      sendBlEData();

                  }
                  else if(GRIDX_BLE_DISCONNECT.equals(BleData)){
                      BLEConnectionIcon.setBackgroundColor(getResources().getColor(R.color.md_white_1000));
                      timerStatus = BLEStatus.DISCONNECTED;

                  }
                }

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_GPS) {
            if (checkGPSIsOpen()) {
                Intent serviceIntent = new Intent(getApplicationContext(), BLEService.class);
                serviceIntent.putExtra("inputExtra","MainActivity");
                ContextCompat.startForegroundService(getApplicationContext(),serviceIntent);
            }
        }
    }


    private void setupChart(){
        // initializing variable for bar chart.
        barChart = findViewById(R.id.idBarChart);
        // calling method to get bar entries.
        getBarEntries();

        /// creating a new bar data set.
        barDataSet = new BarDataSet(barEntriesArrayList, "hourly power consuption");

        // creating a new bar data and
        // passing our bar data set.
        barData = new BarData(barDataSet);

        // below line is to set data
        // to our bar chart.
        barChart.setData(barData);

        // adding color to our bar data set.
        barDataSet.setColors(getColor(R.color.md_blue_A200));

        // setting text color.
        barDataSet.setValueTextColor(getColor(R.color.md_amber_500));

        // setting text size
        barDataSet.setValueTextSize(5f);
        barChart.getDescription().setEnabled(false);
        ProgressBarMeterUnits = (ProgressBar) findViewById(R.id.progress_bar_main);
        ProgressBarMeterUnits.setProgress(60);

        // initFloatingButtons();

        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_HOME);
        registerReceiver(broadcastReceiver, filter);

    }
    private void sendBlEData(){
        String page = "0";
        if(timerStatus == BLEStatus.CONNECTED){
            JSONObject bleString = new JSONObject();
            try {
                bleString.put(PAGE, page);
                Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
                sendBroadcast(BLEIntent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_HOME);
        registerReceiver(broadcastReceiver, filter);
        IntentFilter filterConnected = new IntentFilter(GRIDX_BLE_BROADCAST_SEND);
        registerReceiver(broadcastReceiver, filterConnected);
        sendBlEData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
