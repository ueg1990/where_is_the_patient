package com.estimote.examples.demos;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.estimote.sdk.BeaconManager.MonitoringListener;

/**
 * Demo that shows how to use region monitoring. Two important steps are:
 * <ul>
 * <li>start monitoring region, in example in {@link #onResume()}</li>
 * <li>respond to monitoring changes by registering {@link MonitoringListener} in {@link BeaconManager}</li>
 * </ul>
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class NotifyDemosActivity extends Activity {

  private static final String TAG = AllDemosActivity.class.getSimpleName();
  private static final int NOTIFICATION_ID = 123;

  private BeaconManager beaconManager;
  private NotificationManager notificationManager;
  private Region region;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    
	  super.onCreate(savedInstanceState);
    setContentView(R.layout.notify_demo);
    getActionBar().setDisplayHomeAsUpEnabled(true);

   // Beacon beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    region = new Region("regionId", "B9407F30-F5F8-466E-AFF9-25556B57FE6D", null, null);
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    beaconManager = new BeaconManager(this);

    // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
    // In order for this demo to be more responsive and immediate we lower down those values.
    beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
   
    try {
    	//beaconManager.startRanging(region);
		beaconManager.startMonitoring(region);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    
    beaconManager.setRangingListener(new RangingListener() {
		
		@Override
		public void onBeaconsDiscovered(Region arg0, List<Beacon> beacons) {
			List<String> myCoords = new ArrayList<String>();
			String allMac= " ";
			for (Beacon rangedBeacon : beacons) {
	    		  allMac+= rangedBeacon.getMacAddress() +" ";
	    		  Intent intent = new Intent(NotifyDemosActivity.this, SMSActivity.class);
	    		  java.util.Date date= new java.util.Date();
	              intent.putExtra("patientName", rangedBeacon.getMacAddress()+' '+new Timestamp(date.getTime()) );
	              intent.putExtra("beacon", rangedBeacon);
	              intent.putExtra("beaconRange", Double.toString(Utils.computeAccuracy(rangedBeacon)) );
	              //intent.putExtra("roomNumber", "0100");
	            	 
	            	  if(!myCoords.contains(rangedBeacon.getMacAddress())){
	            		  myCoords.add(rangedBeacon.getMacAddress());
	            		  startActivity(intent);
	            		 
	            	  }
	            	   
	              
	             

	              
	    		  
	    	   }
		}
	});
    beaconManager.setMonitoringListener(new MonitoringListener() {
      @Override
      public void onEnteredRegion(Region region, List<Beacon> beacons) {
    try {
		beaconManager.startRanging(region);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}	    	
      }

      @Override
      public void onExitedRegion(Region region) {
        
    	  try {
			beaconManager.stopRanging(region);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onResume() {
    super.onResume();

    notificationManager.cancel(NOTIFICATION_ID);
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          beaconManager.startMonitoring(region);
        } catch (RemoteException e) {
          Log.d(TAG, "Error while starting monitoring");
        }
      }
    });
  }

  @Override
  protected void onDestroy() {
  // notificationManager.cancel(NOTIFICATION_ID);
    //beaconManager.disconnect();
    //super.onDestroy();
  }

  private void postNotification(String msg) {
    Intent notifyIntent = new Intent(NotifyDemosActivity.this, AllDemosActivity.class);
    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivities(
    		NotifyDemosActivity.this,
        0,
        new Intent[]{notifyIntent},
        PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notification = new Notification.Builder(NotifyDemosActivity.this)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentTitle("Notify Demo")
        .setContentText(msg)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build();
    notification.defaults |= Notification.DEFAULT_SOUND;
    notification.defaults |= Notification.DEFAULT_LIGHTS;
    notificationManager.notify(NOTIFICATION_ID, notification);

    TextView statusTextView = (TextView) findViewById(R.id.editText1);
    statusTextView.setText(msg);
  }
}
