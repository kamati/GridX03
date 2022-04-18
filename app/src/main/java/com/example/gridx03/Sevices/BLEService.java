package com.example.gridx03.Sevices;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.example.gridx03.Activities.MainActivity;
import com.example.gridx03.DBHelper.TokenHistoryDOA;
import com.example.gridx03.Fragments.FragmentTimer;
import com.example.gridx03.R;
import com.example.gridx03.utils.CryptoManager;
import com.example.gridx03.utils.NotificationUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static com.example.gridx03.Fragments.FragmentTokenHistory.GRIDX_BLE_BROADCAST_TOKEN_HISTORY;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_GEYSER_MANUAL_MODE;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_GEYSER_SCHEDULE_MODE;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_GEYSER_TIMER_MODE;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_LONG_DATA;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_STS_TOKEN;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_TOKEN_HISTORY;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_UUID_RX;
import static com.example.gridx03.utils.BLE_Costants.CHARACTERISTIC_UUID_TX;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_HOME;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_MANUAL;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_READ;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SCHEDULE;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_STATS;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_TIMER;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_TOKEN;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_CONNECT;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_DISCONNECT;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_HOME;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_MANUAL;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_SCHEDULE;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_STATS;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_TIMER;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_TOKEN;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_STRING;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_DEVICE_NAME;
import static com.example.gridx03.utils.BLE_Costants.METER_SERVICE_UUID;
import static com.example.gridx03.utils.NotificationUtils.CHANNEL_1_ID;

public class BLEService extends Service {


    public static final String PAGE="page";

    Boolean MeterFound = false;
    private BleDevice bleDevice;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattCharacteristic characteristic;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothGattService> bluetoothGattServices;
    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;


    private Handler mainHandler = new Handler();
    private volatile boolean stopThread = false;
    private String mBLECyhper="";
    String mBLEJsonString = "";
    String BleDataToBeSend;

    String currentCharacteristics=CHARACTERISTIC_UUID_RX;
    private enum BLEStatus {
        CONNECTED,
        DISCONNECTED
    }

    private BLEStatus timerStatus = BLEStatus.DISCONNECTED;

    public BLEService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationUpdate("Bluetooth connecting...","GRIDx bluetooth connection");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        currentCharacteristics = CHARACTERISTIC_UUID_TX;

        if(!BleManager.getInstance().isConnected(bleDevice)){
            initData(intent);
        }else{
            BleManager.getInstance().disconnectAllDevice();
            BleManager.getInstance().destroy();
            Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
            BLEIntent.putExtra(GRIDX_BLE_STRING,GRIDX_BLE_DISCONNECT);
            sendBroadcast(BLEIntent);

            Toast.makeText(getApplicationContext(), getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
            Intent serviceIntent = new Intent(getBaseContext(), BLEService.class);
            stopService(serviceIntent);

        }

        return START_NOT_STICKY;
    }

    private void initData(Intent intent) {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();

        }else{

            // Initializes Bluetooth adapter.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            // Actually set it in response to ACTION_PAIRING_REQUEST.
            final IntentFilter pairingRequestFilter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
            pairingRequestFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
            this.getApplicationContext().registerReceiver(mPairingRequestRecevier, pairingRequestFilter);
            ConnectToBLE();


        }
    }

    private void bleMonitor(BleDevice bleDevice2){
        bleDevice = bleDevice2;
        if(!BleManager.getInstance().isConnected(bleDevice)){

        }else{
            showData();
            IntentFilter filter = new IntentFilter();
            filter.addAction(GRIDX_BLE_BROADCAST_SEND);
            filter.addAction(GRIDX_BLE_SEND_BROADCAST_HOME);
            filter.addAction(GRIDX_BLE_SEND_BROADCAST_TIMER);
            filter.addAction(GRIDX_BLE_SEND_BROADCAST_MANUAL);
            filter.addAction(GRIDX_BLE_SEND_BROADCAST_STATS);
            filter.addAction(GRIDX_BLE_SEND_BROADCAST_SCHEDULE);
            filter.addAction(GRIDX_BLE_SEND_BROADCAST_TOKEN);
            registerReceiver(broadcastReceiver, filter);
            notificationUpdate("GRIDx  connected","GRIDx bluetooth connection");

            Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
            BLEIntent.putExtra(GRIDX_BLE_STRING,GRIDX_BLE_CONNECT);
            sendBroadcast(BLEIntent);
        }
    }
    private final BroadcastReceiver mPairingRequestRecevier = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(intent.getAction())) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);

                if (type == BluetoothDevice.PAIRING_VARIANT_PIN) {
                    //device.setPin(Util.IntToPasskey(pinCode()));
                    //abortBroadcast();
                    Toast.makeText(getApplication(), "Requesting password", Toast.LENGTH_LONG);
                } else {
                    Toast.makeText(getApplication(), "password failed", Toast.LENGTH_LONG);

                }
            }
        }
    };

    private void ConnectToBLE() {
        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
        startScan();

    }
    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                if(success){
                    notificationUpdate("Started bluetooth scan. ","Bluetooth connection");
                }else {
                    notificationUpdate("failed to start bluetooth scan. ","Bluetooth connection");
                }

            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                notificationUpdate("Searching for GRIDx...","Bluetooth connection");
                try {

                    if (bleDevice.getName().regionMatches(true, 0,
                            GRIDX_DEVICE_NAME, 0, 5)) {

                        Log.d("Bluetooth", "Found the bluetooth device");
                        MeterFound = true;
                        if (!BleManager.getInstance().isConnected(bleDevice)) {
                            BleManager.getInstance().cancelScan();
                            connect(bleDevice);
                        }

                    }
                } catch (Exception e) {
                    Log.d("Bluetooth", "Something went wrong while scanning BLE devices.");
                    Log.d("Bluetooth Error", e.getMessage());


                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

                if(!MeterFound){

                    //Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                    //BLEIntent.putExtra(GRIDX_BLE_STRING,GRIDX_BLE_DISCONNECT);
                    // sendBroadcast(BLEIntent);

                    notificationUpdate("Out of GRIDx bleutooth range","bluetooth Connection");
                    Toast.makeText(getBaseContext(), "Out of GRIDx bluetooth range", Toast.LENGTH_SHORT).show();
                    Intent serviceIntent = new Intent(getApplicationContext(), BLEService.class);
                    stopService(serviceIntent);

                }
            }
        });
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                notificationUpdate("Bluetooth Connecting...","Bleutooth Connection");

            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {

                Toast.makeText(getApplicationContext(), getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
                notificationUpdate("GridX Bluetooth Connection Fail","Bleutooth Connection");

            }

            @Override
            public void onConnectSuccess(BleDevice BleDevice2, BluetoothGatt gatt, int status) {
                if (BleManager.getInstance().isConnected(BleDevice2)) {
                    bleMonitor(BleDevice2);
                }

            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                //progressDialog.dismiss();

                if (isActiveDisConnected) {

                } else {
                    unregisterReceiver(broadcastReceiver);
                    Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                    BLEIntent.putExtra(GRIDX_BLE_STRING,GRIDX_BLE_DISCONNECT);
                    sendBroadcast(BLEIntent);

                    Toast.makeText(getApplicationContext(), getString(R.string.active_disconnected), Toast.LENGTH_LONG).show();
                    Intent serviceIntent = new Intent(getBaseContext(), BLEService.class);
                    stopService(serviceIntent);
                }

            }
        });
    }


    @SuppressLint("ShowToast")
    private void showData() {
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);

        for (BluetoothGattService service : gatt.getServices()) {
            if (METER_SERVICE_UUID.equalsIgnoreCase(service.getUuid().toString())) {
                bluetoothGattService = service;
                for (BluetoothGattCharacteristic Characteristic : service.getCharacteristics()) {
                    characteristic = Characteristic;
                    Log.d("serverSereach", "METER_SERVICE_UUID");
                    if (CHARACTERISTIC_UUID_TX.equalsIgnoreCase(characteristic.getUuid().toString())) {
                        NotificationData(characteristic);
                        Log.d("serverSereach", "CHARACTERISTIC_UUID_RX");
                    }


                }
            }
        }
    }


    private void onreadCharacteristic(final BluetoothGattCharacteristic Characteristic){
        BleManager.getInstance().read(bleDevice, Characteristic.getService().getUuid().toString(), Characteristic.getUuid().toString(),
                new BleReadCallback() {
                    @Override
                    public void onReadSuccess(byte[] data) {
                        String incomingBLE =  new String(Characteristic.getValue(), StandardCharsets.UTF_8);
                        Log.d("incomingBLE", incomingBLE);

                    }

                    @Override
                    public void onReadFailure(BleException exception) {
                        Log.d("incomingBLE", exception.toString());

                    }
                });
    }

    private void NotificationData(final BluetoothGattCharacteristic Characteristic) {
        BleManager.getInstance().notify(
                bleDevice,
                Characteristic.getService().getUuid().toString(),
                Characteristic.getUuid().toString(),

                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Handler threadHandler = new Handler(Looper.getMainLooper());
                        threadHandler.post(() -> {


                        });
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        Handler threadHandler = new Handler(Looper.getMainLooper());
                        threadHandler.post(() -> {

                        });
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        Handler threadHandler = new Handler(Looper.getMainLooper());
                        threadHandler.post(() -> {
                            // Toast.makeText(getBaseContext(), "test input", Toast.LENGTH_SHORT).show();
                            String incomingBLE =  new String(Characteristic.getValue(), StandardCharsets.UTF_8);
                            Log.d("incomingBLE", incomingBLE);

                            if(String.valueOf(incomingBLE).contains("@") == true){
                                String[] parts = incomingBLE.split("@");
                                String part1 = parts[0];
                                mBLECyhper = mBLECyhper + part1;
                                if(mBLECyhper.length()>19){
                                    processEncrypedData(mBLECyhper);
                                    Log.d("mBLECyhper", mBLECyhper);
                                }

                                mBLECyhper="";

                            }
                            else{
                                mBLECyhper = mBLECyhper + incomingBLE;

                            }

                        });
                    }
                });
    }

    private void notificationUpdate(String data,String title){

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);


        Notification notification = new NotificationCompat.Builder(getBaseContext(), CHANNEL_1_ID)
                .setSmallIcon(R.drawable.map_icon_green)
                .setContentTitle(title)
                .setContentText(data)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(false)
                .setOnlyAlertOnce(true)
                .build();
        startForeground(1, notification);

    }

    private void processEncrypedData(String data){

        CryptoManager cryptoManager = new CryptoManager();
        Log.d("mBLECyhper2", "processEncrypedData: " + mBLECyhper);
        String mBLEJsonString = cryptoManager.decrypt(mBLECyhper);
        Log.d("mBLECyhper", "processEncrypedData: " + mBLEJsonString);

        if(mBLEJsonString!=null){


            try {
                JSONObject jObject = new JSONObject(mBLEJsonString);
                int page = jObject.getInt(PAGE);
                Intent BLEIntent;
                String unitInfom;
                switch (page){
                    case 0:
                        BLESendThread thread = new BLESendThread(BleDataToBeSend);
                        thread.start();
                        break;
                    case 1:
                        if(jObject.getInt("g_state")==0){
                            unitInfom = "Geyser turned OFF";
                        }else{
                            unitInfom = "Geyser turned ON";
                        }

                        notificationUpdate(unitInfom,"Geyser on state");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_MANUAL);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        /*unitInfom = "Units:"+jObject.getString("units")+"   Consumption: "+jObject.getString("consm");
                        notificationUpdate(unitInfom,"GRIDx meter unit ");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_HOME);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);*/
                        break;
                    case 21:
                        unitInfom = "Units:"+jObject.getString("units")+"   Consumption: "+jObject.getString("consm");
                        notificationUpdate(unitInfom,"GRIDx meter unit ");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_HOME);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        break;
                    case 3:
                        unitInfom = jObject.getString("hrs")+":"+jObject.getString("mins");
                        notificationUpdate(unitInfom,"Geyser timer");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_TIMER);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        BleDataToBeSend = mBLEJsonString;
                        break;
                    case 4:
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_STATS);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        break;
                    case 5:
                        notificationUpdate(mBLEJsonString,"Geyser timer");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SCHEDULE);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        break;
                    case 11:
                        if(jObject.getInt("OK")==0){
                            unitInfom = "Meter token update failed ";
                        }else{
                            unitInfom = "Meter token updated successfully ";
                        }
                        notificationUpdate(unitInfom,"Meter Token response");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_TOKEN);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        break;
                    case 13:
                        if(jObject.getInt("OK")==0){
                            unitInfom = " Failed to set schedule";
                        }else{
                            unitInfom = "Successfully set Schedule ";
                        }
                        notificationUpdate(unitInfom,"Geyser schedule");
                        BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SCHEDULE);
                        BLEIntent.putExtra(GRIDX_BLE_STRING,mBLEJsonString);
                        sendBroadcast(BLEIntent);
                        break;
                    case 14:
                        if(mBLEJsonString!=null){

                            try {

                                TokenHistoryDOA tokenDOA = new TokenHistoryDOA(getApplicationContext());
                                String tokenId= jObject.getString("1");
                                String date= jObject.getString("2");
                                String amount= jObject.getString("3");
                                // tokenDOA.deleteAllTokensStats();

                                tokenDOA.CreateTokenItem(tokenId,date,amount);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        break;
                    default:
                        //BLESendThread thread = new BLESendThread(BleDataToBeSend);
                        //thread.start();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else{
            Log.d("mBLECyhper", "Returned Null ");
            BLESendThread thread = new BLESendThread(BleDataToBeSend);
            thread.start();
        }
    }

    @SuppressLint("ShowToast")
    private void sendData(String data) {
        String name = bleDevice.getName();
        String mac = bleDevice.getMac();
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);

        for (BluetoothGattService service : gatt.getServices()) {
            if (METER_SERVICE_UUID.equalsIgnoreCase(service.getUuid().toString())) {
                Log.d("sendmsg", "Devcie descovery");
                //  Toast.makeText(getApplication(),"METER_SERVICE_UUID" +data, Toast.LENGTH_LONG);
                bluetoothGattService = service;
                for (BluetoothGattCharacteristic Characteristic : service.getCharacteristics()) {
                    characteristic = Characteristic;
                    if (CHARACTERISTIC_GEYSER_MANUAL_MODE.equalsIgnoreCase(characteristic.getUuid().toString())) {
                        Log.d("sendmsg", "Devcie Send for write");
                       // WriteData(characteristic, data);
                    }
                }
            }
        }
    }

    private void WriteData( String data) {

        BleManager.getInstance().write(
                bleDevice,
                METER_SERVICE_UUID,
                currentCharacteristics,
                stringToHex(data),
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                        Log.d("onWriteSuccess", "onWriteSuccess");
                    }

                    @Override
                    public void onWriteFailure(final BleException exception) {
                        Log.d("onWriteSuccess", "onWriteFailure");
                    }
                });

    }

    private byte[] stringToHex(String data) {
        byte[] temp;
        char[] chartemp = data.toCharArray();
        temp = new byte[chartemp.length];
        for (int i = 0; i < chartemp.length; i++) {
            temp[i] = (byte) chartemp[i];
        }
        return temp;
    }

    class BLESendThread extends Thread {
        String mEncrypted = "";


        BLESendThread(String encrypted) {
            this.mEncrypted = encrypted;

        }
        @Override
        public void run() {
            CryptoManager cryptoManager = new CryptoManager();
            String encrypted = cryptoManager.encrypt(mEncrypted);
            String encryptedBLE = encrypted + "@";
            String decrpyted = cryptoManager.decrypt(encrypted);
            Log.d("  BLE_SERCIVR_encrypted", encrypted);
            int bleBlock = 20;
            if (mEncrypted.equals(decrpyted)) {
                for (int i = 0; i < encryptedBLE.length(); i = i + bleBlock) {

                    int y = i + bleBlock;
                    if (y < encryptedBLE.length()) {
                        String part = encryptedBLE.substring(i, y).replaceAll("\\s+", "");
                        Log.d(" ESP32 encrypted", part);
                        WriteData(part);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else if (y > encryptedBLE.length()) {
                        String part = encryptedBLE.substring(i).replaceAll("\\s+", "");
                        WriteData(part);
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(" ESP32 encrypted small", part);
                    }

                }

            }
        }
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

           Log.d("GRIDX_BLE_BROADCAST_TOKEN", "BroadcastReceiver registered");
            if(GRIDX_BLE_BROADCAST_SEND.equals(intent.getAction())){
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);

            }
            else if(GRIDX_BLE_SEND_BROADCAST_TIMER.equals(intent.getAction())){
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);
                currentCharacteristics = CHARACTERISTIC_GEYSER_TIMER_MODE;
                Log.d("GRIDX_BLE_BROADCAST_TOKEN", BleDataToBeSend);

            }
            else if(GRIDX_BLE_SEND_BROADCAST_MANUAL.equals(intent.getAction())){
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);
                currentCharacteristics = CHARACTERISTIC_GEYSER_MANUAL_MODE;
                Log.d("GRIDX_BLE_BROADCAST_TOKEN", BleDataToBeSend);
            }
            else if(GRIDX_BLE_SEND_BROADCAST_SCHEDULE.equals(intent.getAction())){
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);
                currentCharacteristics = CHARACTERISTIC_GEYSER_SCHEDULE_MODE;
                Log.d("GRIDX_BLE_BROADCAST_TOKEN", BleDataToBeSend);
            }
            else if(GRIDX_BLE_SEND_BROADCAST_TOKEN.equals(intent.getAction())){
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);
                Log.d("GRIDX_BLE_BROADCAST_TOKEN", BleDataToBeSend);
                currentCharacteristics = CHARACTERISTIC_STS_TOKEN;
            }
            else if(GRIDX_BLE_SEND_BROADCAST_STATS.equals(intent.getAction())){
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);
                currentCharacteristics = CHARACTERISTIC_TOKEN_HISTORY;
                Log.d("GRIDX_BLE_BROADCAST_TOKEN", BleDataToBeSend);
            }


            if(BleDataToBeSend!=null){
                BLESendThread thread = new BLESendThread(BleDataToBeSend);
                thread.start();
            }

            /*
            if(GRIDX_BLE_BROADCAST_READ.equals(intent.getAction())){
                BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);
                BleDataToBeSend= intent.getStringExtra(GRIDX_BLE_BROADCAST);
                if(BleDataToBeSend!=null){
                    BLESendThread thread = new BLESendThread();
                    thread.start();
                }

            }*/

        }
    };


}