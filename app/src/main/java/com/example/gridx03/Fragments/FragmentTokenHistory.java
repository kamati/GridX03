package com.example.gridx03.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.gridx03.Adpters.TokenAdapter;
import com.example.gridx03.DBHelper.TokenHistoryDOA;
import com.example.gridx03.Models.TokenHistory;
import com.example.gridx03.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SCHEDULE;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_BROADCAST_SEND;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_SEND_BROADCAST_STATS;
import static com.example.gridx03.utils.BLE_Costants.GRIDX_BLE_STRING;
import static com.example.gridx03.Sevices.BLEService.PAGE;

public class FragmentTokenHistory extends Fragment {
    public static final String GRIDX_BLE_BROADCAST_TOKEN_HISTORY="token_histroy";
    public static final String PAGE_TOKEN_HISTORY= "14";
    private View view;
    private TokenHistoryDOA tokenDOA;
    private TokenHistory tokenHistory;
    private ArrayList<TokenHistory> TokenArrays;
    private ListView listViewTokens;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_token_history, container, false);
        tokenDOA = new TokenHistoryDOA(getContext());
        listViewTokens= view.findViewById(R.id.list_tokens_history);
       //  tokenDOA.deleteAllTokensStats();
        GetBLETokenHistory();
        TokenArrays = new ArrayList<TokenHistory>();
        TokenArrays = tokenDOA.getAlltokens();


        if(tokenDOA!=null){

            TokenAdapter tokenAdapter = new TokenAdapter(getActivity(),TokenArrays);
            listViewTokens.setAdapter(tokenAdapter);
        }else{
            Toast.makeText(getActivity(), "No records stored", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    void GetBLETokenHistory(){
        JSONObject bleString = new JSONObject();
        try {
            bleString.put(PAGE, PAGE_TOKEN_HISTORY);
            Intent BLEIntent = new Intent(GRIDX_BLE_SEND_BROADCAST_STATS);
            BLEIntent.putExtra(GRIDX_BLE_BROADCAST, bleString.toString());
            getActivity().sendBroadcast(BLEIntent);
        } catch (JSONException e){

        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(GRIDX_BLE_BROADCAST_TOKEN_HISTORY.equals(intent.getAction())){
                String BleData= intent.getStringExtra(GRIDX_BLE_STRING);
                if(BleData!=null){
                    JSONObject jObject = null;
                    try {
                        String tokenId= jObject.getString("1");
                        String date= jObject.getString("2");
                        String amount= jObject.getString("3");
                        tokenDOA.CreateTokenItem(tokenId,date,amount);

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
       // GetBLETokenHistory();


    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }
}
