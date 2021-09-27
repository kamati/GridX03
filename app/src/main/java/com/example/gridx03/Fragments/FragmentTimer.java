package com.example.gridx03.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gridx03.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_MANUAL;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_BROADCAST_TIMER;
import static com.example.gridx03.Sevices.BLEService.GRIDX_BLE_STRING;
import static com.example.gridx03.Sevices.BLEService.PAGE;

public class FragmentTimer extends Fragment {
    public static final String PAGE = "page";
    public static final String APP_GTIMER_PAGE = "2";
    public static final String TIME_HOURS = "hrs";
    public static final String TIME_MINUTES = "mins";
    public static final String TIME_SECONDS = "secs";
    public static final String TIME_PLAY_STOP = "ply";
    public static final String TIME_PLAY = "1";
    public static final String TIME_STOP = "0";

    private ProgressBar progressBarCircle;
    private TextView textViewTime;
    private ImageView imageViewStartStop;
    private NumberPicker numHours;
    private NumberPicker numMinutes;

    private long timeCountInMilliSeconds = 1 * 60000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);
        progressBarCircle = (ProgressBar) view.findViewById(R.id.progress_bar_timer);
        textViewTime = (TextView) view.findViewById(R.id.text_timer);
        numHours = (NumberPicker) view.findViewById(R.id.timer_hr_duration_);
        numMinutes = (NumberPicker) view.findViewById(R.id.timer_min_duration);
        imageViewStartStop = (ImageView) view.findViewById(R.id.imageViewStartStop);


        numHours.setMinValue(0);
        numHours.setMaxValue(24);
        numMinutes.setMinValue(0);
        numMinutes.setMaxValue(59);
        initListeners();

        return view;
    }

    private void numberSelectorsListners(){
        numHours.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

            }
        });

    }

    private void initListeners() {

        imageViewStartStop.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MotorVibrate();
                String duringHours;
                String duringMinutes;
                String geyserTimeSetting;
                if (numHours.getValue() < 10) {
                    duringHours = String.valueOf("0" + numHours.getValue());
                } else {
                    duringHours = String.valueOf(numHours.getValue());
                }

                if (numMinutes.getValue() < 10) {
                    duringMinutes = String.valueOf("0" + numMinutes.getValue());
                } else {
                    duringMinutes = String.valueOf(numMinutes.getValue());
                }


                if (timerStatus == TimerStatus.STOPPED) {
                    imageViewStartStop.setImageResource(R.drawable.ic_stop_geyser);

                    RegisterReciever();

                    timeCountInMilliSeconds = (numHours.getValue() * 3600000) + (numMinutes.getValue() * 60000);
                    progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
                    progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);


                    JSONObject bleString = new JSONObject();
                    try {
                        bleString.put(PAGE, APP_GTIMER_PAGE);
                        bleString.put(TIME_HOURS, duringHours);
                        bleString.put(TIME_MINUTES, duringMinutes);
                        bleString.put(TIME_PLAY_STOP, TIME_PLAY);
                        Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                        BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
                        getActivity().sendBroadcast(BLEIntent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                } else {

                    JSONObject bleString = new JSONObject();
                    try {
                        bleString.put(PAGE, APP_GTIMER_PAGE);
                        bleString.put(TIME_HOURS, 0);
                        bleString.put(TIME_MINUTES, 0);
                        bleString.put(TIME_PLAY_STOP, TIME_STOP);
                        Intent BLEIntent = new Intent(GRIDX_BLE_BROADCAST_SEND);
                        BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
                        getActivity().sendBroadcast(BLEIntent);
                        getActivity().unregisterReceiver(broadcastReceiver);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    textViewTime.setText("00:00");
                    timeCountInMilliSeconds = 1 * 60000;
                    progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
                    progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
                    imageViewStartStop.setImageResource(R.drawable.ic_play_geyser_full);
                    timerStatus = TimerStatus.STOPPED;

                }
                return false;
            }
        });

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
            if(GRIDX_BLE_BROADCAST_TIMER.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                timerStatus = TimerStatus.STARTED;
                if(BleData!=null){
                    try {
                        JSONObject jObject = new JSONObject(BleData);
                        int min = jObject.getInt(TIME_MINUTES);
                        int hour = jObject.getInt(TIME_HOURS);
                        int millisUntilFinished = (hour * 3600000) + (min * 60000);
                        if (min <= 0 && hour <= 0) {
                            imageViewStartStop.setImageResource(R.drawable.ic_play_geyser_full);
                            //timerStatus = TimerStatus.STOPPED;
                        }

                        //timerStatus = TimerStatus.STARTED;

                        progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
                        String formattedHours = String.format("%02d", hour);
                        String formattedMin = String.format("%02d", min);
                        textViewTime.setText(formattedHours + ":" + formattedMin);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

        }
    };

    private void RegisterReciever(){
        IntentFilter filter = new IntentFilter(GRIDX_BLE_BROADCAST_TIMER);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        RegisterReciever();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        RegisterReciever();
    }
}
