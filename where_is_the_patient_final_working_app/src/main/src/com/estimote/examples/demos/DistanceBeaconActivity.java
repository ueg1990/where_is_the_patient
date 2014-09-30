package com.estimote.examples.demos;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import android.os.Vibrator;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Visualizes distance from beacon to the device.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class DistanceBeaconActivity extends Activity {
	 private NotificationManager notificationManager;

  private static final String TAG = DistanceBeaconActivity.class.getSimpleName();

  // Y positions are relative to height of bg_distance image.
  private static final double RELATIVE_START_POS = 320.0 / 1110.0;
  private static final double RELATIVE_STOP_POS = 885.0 / 1110.0;
  private static final int NOTIFICATION_ID = 123;

  private BeaconManager beaconManager;
  private Beacon beaconIni;
  private Region region;

  private View dotView;
  private int startY = -1;
  private int segmentLength = -1;

  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    getActionBar().setDisplayHomeAsUpEnabled(true);
    setContentView(R.layout.distance_view);
    dotView = findViewById(R.id.dot);

    beaconIni = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    region = new Region("regionid", beaconIni.getProximityUUID(), beaconIni.getMajor(), beaconIni.getMinor());
    if (beaconIni == null) {
      Toast.makeText(this, "Beacon not found in intent extras", Toast.LENGTH_LONG).show();
      finish();
    }

    beaconManager = new BeaconManager(this);
    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
      @Override
      public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons) {
        // Note that results are not delivered on UI thread.
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            // Just in case if there are multiple beacons with the same uuid, major, minor.
            Beacon foundBeacon = null;
            for (Beacon rangedBeacon : rangedBeacons) {
             
            	 updateDistanceView(rangedBeacon);
            	//if (rangedBeacon.getMacAddress().equals(beacon.getMacAddress())) {
                //foundBeacon = rangedBeacon;
              //}
            }
            if (foundBeacon != null) {
             // updateDistanceView(foundBeacon);
            }
          }
        });
      }
    });
    
    
    

    final View view = findViewById(R.id.sonar);
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

        startY = (int) (RELATIVE_START_POS * view.getMeasuredHeight());
        int stopY = (int) (RELATIVE_STOP_POS * view.getMeasuredHeight());
        segmentLength = stopY - startY;

        dotView.setVisibility(View.VISIBLE);
        dotView.setTranslationY(computeDotPosY(beaconIni));
      }
    });
  }

  private void updateDistanceView(Beacon foundBeacon) {
    if (segmentLength == -1) {
      return;
    }

    dotView.animate().translationY(computeDotPosY(foundBeacon)).start();
  }

  private int computeDotPosY(Beacon beacon) {
    // Let's put dot at the end of the scale when it's further than 6m.
    double distance = Math.min(Utils.computeAccuracy(beacon), 6.0);
   
    TextView statusTextView = (TextView) findViewById(R.id.editText1);
    DecimalFormat df = new DecimalFormat("#.##");
    double roundedDistance=  Utils.computeAccuracy(beacon);
    statusTextView.setText(df.format(roundedDistance));
     if(roundedDistance>4){
    	// Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    	// Vibrate for 500 milliseconds
    	//v.vibrate(500);
 postNotification(beacon);
     }
    
    return startY + (int) (segmentLength * (distance / 6.0));
    //editText1
    
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
  protected void onStart() {
    super.onStart();

    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        try {
          beaconManager.startRanging(region);
        } catch (RemoteException e) {
          Toast.makeText(DistanceBeaconActivity.this, "Cannot start ranging, something terrible happened",
              Toast.LENGTH_LONG).show();
          Log.e(TAG, "Cannot start ranging", e);
        }
      }
    });
  }

  @Override
  protected void onStop() {
    beaconManager.disconnect();

    super.onStop();
  }



  private void postNotification(Beacon beaconTrigger) {
	    Intent notifyIntent = new Intent(DistanceBeaconActivity.this, DistanceBeaconActivity.class);
	    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    PendingIntent pendingIntent = PendingIntent.getActivities(
	    		DistanceBeaconActivity.this,
	        0,
	        new Intent[]{notifyIntent},
	        PendingIntent.FLAG_UPDATE_CURRENT);
	    Notification notification = new Notification.Builder(DistanceBeaconActivity.this)
	        .setSmallIcon(R.drawable.beacon_gray)
	        .setContentTitle("Notify Demo")
	        .setContentText("Patient escaped "+beaconTrigger.getMacAddress())
	        .setAutoCancel(true)
	        .setContentIntent(pendingIntent)
	        .build();
	    notification.defaults |= Notification.DEFAULT_SOUND;
	    notification.defaults |= Notification.DEFAULT_LIGHTS;
	    notificationManager.notify(NOTIFICATION_ID, notification);

	   
	  }


}



