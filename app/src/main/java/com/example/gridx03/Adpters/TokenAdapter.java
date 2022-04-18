package com.example.gridx03.Adpters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gridx03.Models.TokenHistory;
import com.example.gridx03.R;

import java.util.ArrayList;

public class TokenAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<TokenHistory> tokenListArray;
    private LayoutInflater mInflater;

    public TokenAdapter(Context mcontext, ArrayList<TokenHistory> TokenLists){
        this.context = mcontext;
        this.mInflater = LayoutInflater.from(mcontext);
        this.tokenListArray = TokenLists;
    }


    @Override
    public int getCount() {
        return tokenListArray.size();
    }

    @Override
    public Object getItem(int position) {
        return tokenListArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;
        holder = new ViewHolder();
        v = mInflater.inflate(R.layout.adpter_token_history, parent, false);


        holder.img_blue =  v.findViewById(R.id.img_blue);
        holder.txt_token_id =  v.findViewById(R.id.adapter_toke_id);
        holder.txt_date =  v.findViewById(R.id.adapter_date_time);
        holder.txt_amount =  v.findViewById(R.id.adapter_token_mount);

        TokenHistory token = tokenListArray.get(position);
        try {

            holder.txt_token_id.setText("Token: " +token.getmTokeID());
            holder.txt_date.setText("Date: " + token.getmDate());
            holder.txt_amount.setText("Amount: " +token.getmAmount() +"kWh");
        }
        catch (NumberFormatException e){
            Toast.makeText(context, "there is an error " , Toast.LENGTH_SHORT).show();
        }
        return v;
    }

    class ViewHolder{
        ImageView img_blue;
        TextView txt_token_id;
        TextView txt_date;
        TextView txt_amount;
    }
}
