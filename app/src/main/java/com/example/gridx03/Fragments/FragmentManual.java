package com.example.gridx03.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.os.Vibrator;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gridx03.Activities.GeyserNavigationActivity;
import com.example.gridx03.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_HOME;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_MANUAL;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_HOME;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_MANUAL;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_STRING;
import static com.example.gridx03.Sevices.BLEService.PAGE;

public class FragmentManual  extends Fragment {
    public static final String BLE_GEYSER_MODE_MANUAL = "g_state";
    public static final String GEYSER_STATE = "g_state";
    int statevalue =0;



    private ImageView GeyserButtonON;
    private ProgressBar ProgressBarMeterStateON;


    private ImageView GeyserButtonOFF;
    private ProgressBar ProgressBarMeterStateOFF;
    private enum TimerStatus {
        STARTED,
        STOPPED
    }
    private TimerStatus timerStatus = TimerStatus.STOPPED;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manual_mode, container, false);
        GeyserButtonON = view.findViewById(R.id.image_geys_on);
        GeyserButtonOFF = view.findViewById(R.id.image_geys_off);
        ProgressBarMeterStateON = view.findViewById(R.id.progress_manual_main_on);
        ProgressBarMeterStateOFF = view.findViewById(R.id.progress_manual_main_off);
        ProgressBarMeterStateON.setVisibility(View.INVISIBLE);
        ProgressBarMeterStateON.setIndeterminate(true);
        ProgressBarMeterStateOFF.setVisibility(View.INVISIBLE);
        ProgressBarMeterStateOFF.setIndeterminate(true);
        GeyserButtonOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "long press to turn off water heater", Toast.LENGTH_SHORT).show();
            }
        });
        GeyserButtonON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "long press to turn off water heater", Toast.LENGTH_SHORT).show();
            }
        });

        GeyserButtonON.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ProgressBarMeterStateON.setVisibility(View.VISIBLE);
                MotorVibrate();
                sendBlEData(1);

                return false;
            }
        });

        GeyserButtonOFF.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ProgressBarMeterStateOFF.setVisibility(View.VISIBLE);
                MotorVibrate();
                sendBlEData(0);
                return false;
            }
        });

        return view;
    }

    private void sendBlEData(int state){
        String page = "1";
        statevalue =state;
        JSONObject bleString = new JSONObject();
        try {
            bleString.put(PAGE, page);
            bleString.put(BLE_GEYSER_MODE_MANUAL, state);
            Intent BLEIntent = new Intent(GRIDX_BLE_SEND_BROADCAST_MANUAL);

            BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
            getActivity().sendBroadcast(BLEIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void MotorVibrate(){
        Vibrator v = (Vibrator)  getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GRIDX_BLE_BROADCAST_MANUAL.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                if(BleData!=null){

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(BleData);
                        int unitsInt = jObject.getInt(GEYSER_STATE);
                        if(unitsInt ==1){
                            ProgressBarMeterStateOFF.setVisibility(View.INVISIBLE);
                            ProgressBarMeterStateON.setVisibility(View.INVISIBLE);

                            //GeyserButton.setVisibility(View.VISIBLE);
                        }else {
                            sendBlEData(statevalue);

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
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_MANUAL);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_MANUAL);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }
}
