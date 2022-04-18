package com.example.gridx03.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dpro.widgets.WeekdaysPicker;
import com.example.gridx03.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import static com.example.gridx03.Sevices.BLEService.PAGE;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SCHEDULE;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_SCHEDULE;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_STRING;

public class FragmentSchedule extends Fragment {
    @Nullable
    public static final String BLE_GEYSER_MODE_SCHEDULE="g_mode_schedule";
    public static final String DAY_SELECTED= "d";
    private TimePicker pickerStartTime;
    private TimePicker pickerEndTime;
    private ProgressBar progressBLE;
    private WeekdaysPicker selectedWeekDays;
    private Button sendSetting;
    private  View view;
    private JSONObject bleJsonDay;
    private JSONObject bleJsonTime;
    private int totalStartTime=0, totalEndTime=0;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule_mode, container, false);
        pickerStartTime = view.findViewById(R.id.time_picker_start);
        pickerEndTime = view.findViewById(R.id.time_picker_end);
        sendSetting = view.findViewById(R.id.send_schedule_settings);
        selectedWeekDays = view.findViewById(R.id.selected_days_widget);
        progressBLE = view.findViewById(R.id.progressBar_schedule);
        pickerStartTime.setIs24HourView(true);
        pickerEndTime.setIs24HourView(true);
        progressBLE.setVisibility(View.INVISIBLE);

        sendSetting.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    getScheduleSetting();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

    return view;
    }

    private void getScheduleSetting() throws JSONException {
        totalStartTime = (pickerStartTime.getHour()*60) +pickerStartTime.getMinute();
        totalEndTime = (pickerEndTime.getHour()*60) +pickerEndTime.getMinute();
        if(totalStartTime<totalEndTime){
            try{
                ArrayList<Integer> bleArray = new ArrayList<>(Arrays.asList(new Integer[7]));
                ArrayList<Integer> bleTime = new ArrayList<>();
                bleTime.add(pickerStartTime.getHour());
                bleTime.add(pickerStartTime.getMinute());

                bleTime.add(pickerEndTime.getHour());
                bleTime.add(pickerEndTime.getMinute());
                ArrayList<Integer> selectedDays = (ArrayList<Integer>) selectedWeekDays.getSelectedDays();
                if(selectedDays.isEmpty()){
                    Toast.makeText(getActivity(), "No days selected", Toast.LENGTH_LONG).show();
                    Log.d("Day", "No days selected" + bleArray.size());
                    return;
                }
                for (int i = 0; i <bleArray.size(); i++){
                    bleArray.set(i,0);
                }
                for(int i = 0; i <selectedDays.size(); i++){
                    if(selectedDays.get(i)!=null)
                    bleArray.set(selectedDays.get(i)-1,1);
                }

                bleJsonDay = new JSONObject();
                bleJsonTime = new JSONObject();

                bleJsonDay.put(PAGE, "3");
                bleJsonDay.put("d", bleArray);
                Log.d("Day", bleJsonDay.toString());

                bleJsonTime.put(PAGE, "3");
                bleJsonTime.put("t", bleTime);
                Log.d("Day", bleJsonTime.toString());




                Intent BLEIntentTime = new Intent(GRIDX_BLE_SEND_BROADCAST_SCHEDULE);
                BLEIntentTime.putExtra(GRIDX_BLE_BROADCAST, bleJsonTime.toString());
                getActivity().sendBroadcast(BLEIntentTime);
                progressBLE.setVisibility(View.VISIBLE);
                progressBLE.setIndeterminate(true);


            }catch (JSONException e){
                e.printStackTrace();
            }

        }else {
            Toast.makeText(getActivity(), "End time must me greater then start time", Toast.LENGTH_LONG).show();
            Log.d("Day", "End time Must me greater then start tim");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_SCHEDULE);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_SCHEDULE);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GRIDX_BLE_BROADCAST_SCHEDULE.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                if(BleData!=null){

                    JSONObject jObject = null;
                    try {
                        jObject = new JSONObject(BleData);
                        int unitsInt = jObject.getInt("OK");
                        if(unitsInt ==1){

                            progressBLE.setVisibility(View.INVISIBLE);
                            Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                            BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleJsonDay.toString());
                            getActivity().sendBroadcast(BLEIntent);
                        }else{
                            Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                            BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleJsonDay.toString());
                            getActivity().sendBroadcast(BLEIntent);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            }
        }
    };

}
