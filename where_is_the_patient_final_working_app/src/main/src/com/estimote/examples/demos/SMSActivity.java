package com.estimote.examples.demos;

import java.sql.Timestamp;

import com.estimote.sdk.Beacon;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SMSActivity extends Activity {
	Button buttonSend;
	EditText textPhoneNo;
	EditText textSMS;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		Bundle extras = getIntent().getExtras();
		PatientRepository patients = new PatientRepository();
		
		try {
		String beaconRange=extras.getString("beaconRange");
		Beacon beacon = (Beacon)extras.get("beacon");
		Double doubleDistance=Double.parseDouble(beaconRange) ;
		
		String patientName = patients.findPatient(SMSActivity.this, beacon.getMacAddress());
		String sms = "Patient " + patientName + " is trying to escape";
		
		try {
			if(doubleDistance<4.00){
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
              	// Vibrate for 500 milliseconds
              	v.vibrate(50);
              	// postNotification("Entered region " + allMac);
           
			SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage("5148871900", null, sms, null, null);
				Toast.makeText(getApplicationContext(), sms,
							Toast.LENGTH_LONG).show();	
              
	    		  
			}
			
			
			finish();
		  } catch (Exception e) {
			Toast.makeText(getApplicationContext(),
				"SMS faild, please try again later!",
				Toast.LENGTH_LONG).show();
			e.printStackTrace();
		  }
		}
		catch(NotFoundException e) {
			e.printStackTrace();
			finish();
		}
 
//		buttonSend = (Button) findViewById(R.id.buttonSend);
//		textPhoneNo = (EditText) findViewById(R.id.editTextPhoneNo);
//		textSMS = (EditText) findViewById(R.id.editTextSMS);
 
//		buttonSend.setOnClickListener(new OnClickListener() {
// 
//			@Override
//			public void onClick(View v) {
// 
//			  String phoneNo = textPhoneNo.getText().toString();
//			  String sms = textSMS.getText().toString();
// 
//			  try {
//				SmsManager smsManager = SmsManager.getDefault();
//				smsManager.sendTextMessage(phoneNo, null, sms, null, null);
//				Toast.makeText(getApplicationContext(), "SMS Sent!",
//							Toast.LENGTH_LONG).show();
//			  } catch (Exception e) {
//				Toast.makeText(getApplicationContext(),
//					"SMS faild, please try again later!",
//					Toast.LENGTH_LONG).show();
//				e.printStackTrace();
//			  }
// 
//			}
//		});
	}
}
