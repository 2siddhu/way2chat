package com.siddhartha.way2talk.adapter;

import java.util.ArrayList;

import com.siddhartha.way2talk.R;
import com.siddhartha.way2talk.data.SMSData;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomUsersAdapter extends ArrayAdapter<SMSData> {
	 private final ArrayList<SMSData> smsList;
	 Boolean isDetailView=false;
    public CustomUsersAdapter(Context context, ArrayList<SMSData> users,Boolean isDetail) {
        super(context, 0, users);
        this.isDetailView=isDetail;
        smsList=users;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
    	 
        if (convertView == null) {
        	if(isDetailView){
        		 convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_msgdetail, parent, false);
        	}
        	else{
           convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_msglist, parent, false);
           }
        }
        if(smsList.get(position).getIsFromContacts()==true){
        	convertView.setBackgroundColor(Color.CYAN);	
        }
        else{
        	convertView.setBackgroundColor(Color.WHITE);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.tvAddress);
        TextView tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
        tvName.setText(smsList.get(position).getAddress());
        tvMsg.setText(smsList.get(position).getMsg());
        if(smsList.get(position).getReadState().equalsIgnoreCase("1")){
        	tvMsg.setTextColor(Color.MAGENTA);
        }
        return convertView;
    }
}