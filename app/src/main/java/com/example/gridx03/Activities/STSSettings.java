package com.example.gridx03.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.gridx03.R;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.gridx03.Fragments.FragmentSendToken.OK;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_TOKEN;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_STRING;
import static com.example.gridx03.Sevices.BLEService.PAGE;

public class STSSettings extends AppCompatActivity {

    public static final String STS_SELECTION="sts";
    public static final String STS_RESET_TOKEN_STACK="0";
    public static final String STS_SET_CREDIT_ZERO="1";
    public static final String STS_SET_CREDIT_MAX="2";
    public static final String STS_FACTORY_RESET="3";
    public static final String STS_CTSE1="4";
    public static final String STS_CTSE2="5";
    public static final String STS_CTSE3="6";
    public static final String STS_CTSF1="7";
    public static final String STS_CTSF2="9";
    public static final String STS_CTSF3="10";
    public static final String STS_CTSD2="11";
    public static final String STS_page="55";



    private ProgressBar progressBar;
    private Button btnResetTokenStack;
    private Button btnSetCreditZero;
    private Button btnSetCreditMax;
    private Button btnFactoryset;

    private Button btnCTSE1;
    private Button btnCTSE2;
    private Button btnCTSE3;

    private Button btnCTSF1;
    private Button btnCTSF2;
    private Button btnCTSF3;

    private Button btnCTSD2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stssettings);
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_TOKEN);
        registerReceiver(broadcastReceiver, filter);

        btnResetTokenStack = findViewById(R.id.btn_reset_token_stack);
        btnSetCreditZero = findViewById(R.id.btn_set_credit_zero);
        btnSetCreditMax =  findViewById(R.id.btn_set_credit_max);
        btnFactoryset =  findViewById(R.id.btn_factory_reset);
        btnCTSE1 =  findViewById(R.id.btn_CTSE1);
        btnCTSE2 =  findViewById(R.id.btn_CTSE2);
        btnCTSE3 =  findViewById(R.id.btn_CTSE3);

        btnCTSF1 =  findViewById(R.id.btn_CTSF1);
        btnCTSF2 =  findViewById(R.id.btn_CTSF2);
        btnCTSF3 =  findViewById(R.id.btn_CTSF3);
        btnCTSD2 =  findViewById(R.id.btn_CTSD2);

        progressBar =  findViewById(R.id.progressBar_STSSettings);


        btnResetTokenStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_RESET_TOKEN_STACK);
            }
        });

        btnSetCreditZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_SET_CREDIT_ZERO);
            }
        });
        btnSetCreditMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_SET_CREDIT_MAX);
            }
        });
        btnFactoryset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_FACTORY_RESET);
            }
        });
        btnCTSE1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSE1);
            }
        });
        btnCTSE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSE2);
            }
        });
        btnCTSE3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSE3);
            }
        });
        btnCTSF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSF1);
            }
        });
        btnCTSF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSF2);
            }
        });
        btnCTSF3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSF3);
            }
        });
        btnCTSD2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBlEData(STS_CTSD2);
            }
        });




    }

    private void sendBlEData(String num){

        JSONObject bleString = new JSONObject();
        try {
            bleString.put(PAGE, STS_page);
            bleString.put(STS_SELECTION, num);
            Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
            BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
            sendBroadcast(BLEIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GRIDX_BLE_BROADCAST_TOKEN.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                if(BleData!=null){
                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(BleData);
                        int unitsInt = jObject.getInt(OK);

                        if(unitsInt==1){
                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.INVISIBLE);

                        }else{
                            progressBar.setIndeterminate(false);
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_TOKEN);
        registerReceiver(broadcastReceiver, filter);
    }
}