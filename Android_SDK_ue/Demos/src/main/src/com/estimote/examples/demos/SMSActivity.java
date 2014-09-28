package com.estimote.examples.demos;

import android.app.Activity;
import android.os.Bundle;
import android.telephony.SmsManager;
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
		String sms = "Patient " + extras.getString("patientName") + " outside range!!!";
		
		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("4388222753", null, sms, null, null);
			Toast.makeText(getApplicationContext(), "SMS Sent!",
						Toast.LENGTH_LONG).show();
		  } catch (Exception e) {
			Toast.makeText(getApplicationContext(),
				"SMS faild, please try again later!",
				Toast.LENGTH_LONG).show();
			e.printStackTrace();
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
