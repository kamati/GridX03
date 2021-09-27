package com.example.gridx03.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gridx03.Activities.LoginActivity;
import com.example.gridx03.Activities.MainActivity;
import com.example.gridx03.R;

import org.json.JSONException;
import org.json.JSONObject;

import static android.text.TextUtils.isEmpty;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_MANUAL;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_TOKEN;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_STRING;
import static com.example.gridx03.Sevices.BLEService.PAGE;


public class FragmentSendToken extends Fragment {

    public static final String BLE_METER_NUMBER = "number";
    public static final String BLE_METER_TOKEN = "token";
    public static final String OK ="OK";
    public static final String RESPONSE ="res";
    private EditText phoneNumber;
    private EditText txtToken;
    private Button buttonSendToken;
    private ProgressBar progressBar;
    private TextView txtSendTokenResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_send_token, container, false);
        // Inflate the layout for this fragment
        phoneNumber = view.findViewById(R.id.edit_phone_number);
        txtToken =  view.findViewById(R.id.edit_token_number);
        txtSendTokenResponse =  view.findViewById(R.id.txt_token_feedback);
        buttonSendToken =  view.findViewById(R.id.button_send_token);
        progressBar = view.findViewById(R.id.progressBar_token);
        progressBar.setVisibility(View.INVISIBLE);
        txtSendTokenResponse.setVisibility(View.INVISIBLE);


        buttonSendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isEmpty(phoneNumber) && !isEmpty(txtToken)) {
                    sendBlEData(11,phoneNumber.getText().toString(), txtToken.getText().toString());
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setIndeterminate(true);
                } else {
                    Toast.makeText(getActivity(), "Please enter full infromation", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    private void sendBlEData(int page,String num,String token){

        JSONObject bleString = new JSONObject();
        try {
            bleString.put(PAGE, page);
            bleString.put(BLE_METER_NUMBER, num);
            bleString.put(BLE_METER_TOKEN, token);
            Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
            BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
            getActivity().sendBroadcast(BLEIntent);
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
                            txtSendTokenResponse.setVisibility(View.VISIBLE);
                            txtSendTokenResponse.setTextColor(Color.GREEN);
                            txtSendTokenResponse.setText("Token successfully processed ");

                        }else{
                            txtSendTokenResponse.setVisibility(View.VISIBLE);
                            txtSendTokenResponse.setTextColor(Color.RED);
                            txtSendTokenResponse.setText("Invalid Token ");
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
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_TOKEN);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

}