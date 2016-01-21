package com.siddhartha.way2talk;

import java.util.ArrayList;

import com.siddhartha.way2talk.adapter.CustomUsersAdapter;
import com.siddhartha.way2talk.data.SMSData;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DetailMsgActivity extends Activity{
ListView lv_detailView ;
final static Uri uri1 = Uri.parse("content://sms");
Long threadId;
CustomUsersAdapter adapter;
ArrayList<SMSData> smsDetails = new ArrayList<SMSData>();
	@Override
	   protected void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.activity_detail);
	       lv_detailView = (ListView) findViewById(R.id.lvDetails);
	       Intent intent = getIntent();
	       threadId=intent.getLongExtra("threadId", 0);
	       populateUsersList();
	       lv_detailView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if(smsDetails.get(position).getIsFromContacts()==false){	
					
					deleteSms(smsDetails.get(position).getId());
						adapter.remove(smsDetails.get(position));
						adapter.notifyDataSetChanged();	
					}
					else if (smsDetails.get(position).getIsFromContacts()==true && smsDetails.get(position).getReadState().equalsIgnoreCase("0")){
						TextView tv=(TextView) view.findViewById(R.id.tvAddress);
						tv.setTextColor(Color.MAGENTA);
						updateSms(smsDetails.get(position).getId());
					}
					
				}
			});
			}
	public void updateSms(String smsId){
		ContentValues values = new ContentValues();
        values.put("read", true);
        try {
        	this.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "_id=" + smsId, null);
		} 
        catch (Exception e) {
		}
	}
	public boolean deleteSms(String smsId) {
        boolean isSmsDeleted = false;
        try {
            this.getContentResolver().delete(
                    Uri.parse("content://sms/" + smsId), null, null);
            isSmsDeleted = true;

        } catch (Exception ex) {
            isSmsDeleted = false;
        }
        return isSmsDeleted;
    }
	private void populateUsersList() {
		
		 Uri uri = Uri.parse("content://sms/");
		    String[] reqCols = new String[] { "_id", "body", "address", "read", "date", "type", "thread_id" };
		    Cursor c = getContentResolver().query(uri, reqCols, "thread_id = '" + threadId + "'" , null, "date ASC");
		 if(c.moveToFirst()) {
	           for(int i=0; i < c.getCount(); i++) {	        	  
	               SMSData sms = new SMSData();
	               sms.setMsg(c.getString(c.getColumnIndexOrThrow("body")).toString());
	               String address = c.getString(c.getColumnIndex("address"));
	               sms.setReadState(c.getString(c.getColumnIndex("read")));
	               sms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
	               sms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
	               String contactName = address;
	   	        Uri Nameuri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(address));  
	   	        Cursor cs= getContentResolver().query(Nameuri, new String[]{PhoneLookup.DISPLAY_NAME},PhoneLookup.NUMBER+"='"+address+"'",null,null);

	   	        if(cs.getCount()>0)
	   	        {
	   	            cs.moveToFirst();
	   	            contactName = cs.getString(cs.getColumnIndex(PhoneLookup.DISPLAY_NAME));
	   	        } 
	               
	              sms.setAddress(contactName); 
	              if(address.equalsIgnoreCase(contactName)){
	            	  sms.setIsFromContacts(false);
	              }
	              else{
	            	  sms.setIsFromContacts(true);
	              }
	               smsDetails.add(sms);
	               c.moveToNext();
	           }
	       }
		 c.close();
		 adapter = new CustomUsersAdapter(this,smsDetails,true);
		lv_detailView.setAdapter(adapter);
	}
}
