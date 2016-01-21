package com.siddhartha.way2talk;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ComposeMsgActivity extends Activity{
	private static final int PICK_CONTACT = 0;
	ArrayList<String> phoneNumbers = new ArrayList<String>();
	String name = "";
    String number = "";
	EditText to;
	String phoneNumber="";
	AutoCompleteTextView msgBody;
@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_compose);
	to = (EditText) findViewById(R.id.et_Contacts);
	 msgBody = (AutoCompleteTextView) findViewById(R.id.et_MsgBody);
	Button Send= (Button) findViewById(R.id.btn_Send);
	Send.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
				sendSMS();
		}
	});
	to.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			doLaunchContactPicker();
		}
	});
}
protected void sendSMS() {
    String smsMessage = msgBody.getText().toString();
    try {
    	if(android.util.Patterns.PHONE.matcher(phoneNumber).matches()){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, smsMessage, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
        Intent intent = new Intent(ComposeMsgActivity.this,MainActivity.class);
        startActivity(intent);
        this.finish();
    	}
    	else{
    		Toast.makeText(getApplicationContext(),
                    "Please select Valid No.",
                    Toast.LENGTH_LONG).show();
    	}
    } catch (Exception e) {
        Toast.makeText(getApplicationContext(),
                "Sending SMS failed.",
                Toast.LENGTH_LONG).show();
        e.printStackTrace();
    }
}
public void doLaunchContactPicker() {
    Intent pickContactIntent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
    startActivityForResult(pickContactIntent, PICK_CONTACT);
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	super.onActivityResult(requestCode, resultCode, data);
	if((requestCode==PICK_CONTACT)&&(resultCode==RESULT_OK)){
		getContactInfo(data);
	}
	
}

public void getContactInfo(Intent intent) {
    Cursor cursor = getContentResolver().query(intent.getData(), null, null, null, null);
    while (cursor.moveToNext()) {
       // String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
        phoneNumber=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        to.setText(name+":"+phoneNumber);
    }

} 

}
