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
import android.widget.TextView;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import android.widget.EditText;

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
public class NotifyDemoActivity extends Activity {

  private static final String TAG = NotifyDemoActivity.class.getSimpleName();
  private static final int NOTIFICATION_ID = 123;

  private BeaconManager beaconManager;
  private NotificationManager notificationManager;
  private Region region;
  private double distance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.notify_demo);
    getActionBar().setDisplayHomeAsUpEnabled(true);

    final Beacon beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
//    region = new Region("regionId", beacon.getProximityUUID(), beacon.getMajor(), beacon.getMinor());
    region = new Region("regionId", beacon.getProximityUUID(), null, null);
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    beaconManager = new BeaconManager(this);

    // Default values are 5s of scanning and 25s of waiting time to save CPU cycles.
    // In order for this demo to be more responsive and immediate we lower down those values.
    beaconManager.setBackgroundScanPeriod(TimeUnit.SECONDS.toMillis(1), 0);
    try {
		beaconManager.startMonitoring(region);
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

    beaconManager.setMonitoringListener(new MonitoringListener() {
      @Override
      public void onEnteredRegion(Region region, List<Beacon> beacons) {
    	    postNotification("Entered region" + beacons.size());
    	  }

      @Override
      public void onExitedRegion(Region region) {
        postNotification("Exited region");
      }
    });
    
    
//    beaconManager.setRangingListener(new BeaconManager.RangingListener() {
//        @Override
//        public void onBeaconsDiscovered(Region region, final List<Beacon> rangedBeacons) {
//          // Note that results are not delivered on UI thread.
//          runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//              // Just in case if there are multiple beacons with the same uuid, major, minor.
//              Beacon foundBeacon = null;
//              for (Beacon rangedBeacon : rangedBeacons) {
//                if (rangedBeacon.getMacAddress().equals(beacon.getMacAddress())) {
//                  foundBeacon = rangedBeacon;
//                }
//              }
//              
//              distance = computeDotPosY(beacon);
//              TextView text_distance = (TextView) findViewById(R.id.distance);
//        	  text_distance.setText(Double.toString(distance));
////              if (foundBeacon != null) {
////                
////            	  distance = computeDotPosY(beacon);
////            	  text_distance = (EditText) findViewById(R.id.distance);
////            	  text_distance.setText(Double.toString(distance));
////            	  
////              }
//            }
//          });
//        }
//      });  
  }
  
  private double computeDotPosY(Beacon beacon) {
	    // Let's put dot at the end of the scale when it's further than 6m.
	    double distance = Math.min(Utils.computeAccuracy(beacon), 6.0);
	    return distance;
	    //return startY + (int) (segmentLength * (distance / 6.0));
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
    notificationManager.cancel(NOTIFICATION_ID);
    beaconManager.disconnect();
    super.onDestroy();
  }

  private void postNotification(String msg) {
    Intent notifyIntent = new Intent(NotifyDemoActivity.this, NotifyDemoActivity.class);
    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivities(
        NotifyDemoActivity.this,
        0,
        new Intent[]{notifyIntent},
        PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notification = new Notification.Builder(NotifyDemoActivity.this)
        .setSmallIcon(R.drawable.beacon_gray)
        .setContentTitle("Notify Demo")
        .setContentText(msg)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .build();
    notification.defaults |= Notification.DEFAULT_SOUND;
    notification.defaults |= Notification.DEFAULT_LIGHTS;
    notificationManager.notify(NOTIFICATION_ID, notification);

    TextView statusTextView = (TextView) findViewById(R.id.status);
    statusTextView.setText(msg);
  }
}
