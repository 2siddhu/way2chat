package com.siddhartha.way2talk;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.siddhartha.way2talk.adapter.CustomUsersAdapter;
import com.siddhartha.way2talk.data.SMSData;

public class MainActivity extends SwipeListView {
	ListView listView;
	ImageButton IB_Compose;
	final static Uri uri = Uri.parse("content://sms/inbox");
	ArrayList<SMSData> smsList = new ArrayList<SMSData>();
	 ArrayList<SMSData> smsList1 = new ArrayList<SMSData>();
	 CustomUsersAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populateUsersList();
		IB_Compose=(ImageButton) findViewById(R.id.btnCompose);
		IB_Compose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			Intent intent = new Intent(MainActivity.this,ComposeMsgActivity.class);
			startActivity(intent);	
			}
		});
	}
	
	
	public  ArrayList<SMSData> getSMSList()
	{
	    Uri uriSMSURI = Uri.parse("content://mms-sms/conversations/");
	    Cursor cursor = getContentResolver().query(uriSMSURI, null, null, null, "date desc");
	    while (cursor.moveToNext()) 
	    {	
	    	SMSData sms = new SMSData();
	        String address = cursor.getString(cursor.getColumnIndex("address"));
	        String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
	        String read = cursor.getString(cursor.getColumnIndexOrThrow("read"));
	        long threadId=cursor.getLong(cursor.getColumnIndexOrThrow("thread_id"));
	        String id =cursor.getString(cursor.getColumnIndexOrThrow("_id"));
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
	        sms.setId(id);
	        sms.setMsg(body);
	        sms.setReadState(read);
	        sms.setThreadId(threadId);
        	smsList1.add(sms);
	      }
	    return smsList1;
	} 
	
	
	private void populateUsersList() {
		getSMSList();
		adapter = new CustomUsersAdapter(this,smsList1,false );
		listView = (ListView) findViewById(R.id.lvUsers);
		listView.setAdapter(adapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	
	public boolean deleteSms(Long threadIdIn) {
        boolean isSmsDeleted = false;
        try {
        	Uri uri = Uri.parse("content://sms/");
		    String[] reqCols = new String[] { "_id", "body", "address", "read", "date", "type", "thread_id" };
		    Cursor c = getContentResolver().query(uri, reqCols, "thread_id = '" + threadIdIn + "'" , null, "date ASC");
		    if(c.moveToFirst()) {
	           for(int i=0; i < c.getCount(); i++) {	        	  
	               
	               String smsId =c.getString(c.getColumnIndexOrThrow("_id"));
	               this.getContentResolver().delete(
	                       Uri.parse("content://sms/" + smsId), null, null);    
	               c.moveToNext();
	           }
	       }
		 c.close();        	
        	
            isSmsDeleted = true;

        } catch (Exception ex) {
            isSmsDeleted = false;
        }
        return isSmsDeleted;
    }
	@Override
	public ListView getListView() {
		return listView;
	}


	@Override
	public void getSwipeItem(boolean isRight, int position) {
		if(smsList1.get(position).getIsFromContacts()==false){
		adapter.remove(smsList1.get(position));
		adapter.notifyDataSetChanged();	
		deleteSms(smsList1.get(position).getThreadId());
		}
	}


	@Override
	public void onItemClickListener(ListAdapter adapter, int position) {
		Intent intent = new Intent(MainActivity.this,DetailMsgActivity.class);
		intent.putExtra("threadId", smsList1.get(position).getThreadId());
		startActivity(intent);	
	}


	
}
