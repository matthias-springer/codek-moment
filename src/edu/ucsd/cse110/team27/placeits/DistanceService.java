package edu.ucsd.cse110.team27.placeits;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.R;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;

public class DistanceService extends Service {

	private List<PlaceIt> activeList;
	private Location placeItLoc;
	private LatLng locationLng;
	private Location locationLoc;
	float halfmile = 805;
	private Handler handler = new Handler();
	private Runnable distanceNoti;
	private List<Integer> printedPlaceIts = new ArrayList<Integer>();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate(){ 
		
			
		// create new runnable
		distanceNoti = new Runnable(){

			@Override
			public void run() {
				// got the inner list call items with .get(int x) method.
				activeList = ActivePlaceIts.getInstance().getList();
				
				for(int i = 0; i < activeList.size(); i++){

					// Location of place it
					
					// convert the LatLng location of the place it and the user location to Location
					placeItLoc = activeList.get(i).getLocation();
					locationLng =  getCurrentPosition();
					locationLoc = convertLatLoc(locationLng);
					
					

					// dont know how to set half mile to float.
					if(placeItLoc.distanceTo(locationLoc) <= halfmile && (!activeList.get(i).getPrint())) {
						
						
						// use i as id, but not sure if i can cast PlaceIt object.
						printNotification(activeList.get(i).getTitle(), activeList.get(i).getDescription(), activeList.get(i));	
					}
				}
				handler.postDelayed(distanceNoti, 1000);
			}
		};
	}

	@Override
	public void onStart(Intent intent, int startid) {

		ActivePlaceIts.getInstance().getList();
		distanceNoti.run();
		

	}

	// useful to conver to location
	public Location convertLatLoc(LatLng point){

		Location location = new Location("Test");
		location.setLatitude(point.latitude);
		location.setLongitude(point.longitude);

		return location;
	}

	private LatLng getCurrentPosition(){
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();             // set criteria for location provider:
		criteria.setAccuracy(Criteria.ACCURACY_FINE);   // fine accuracy
		criteria.setCostAllowed(false);                 // no monetary cost

		String bestProvider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(bestProvider);
		LatLng myPosition= new LatLng(location.getLatitude(), location.getLongitude());
		
		return myPosition;
	}

    private void printNotification(String title, String text, PlaceIt object) {
    	Uri noteSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    	object.setPrint(true);
    	NotificationCompat.Builder note = 
    			new NotificationCompat.Builder(this)
    	        .setSmallIcon(R.drawable.alert_dark_frame)
    	        .setContentTitle(title)
    	        .setContentText(text)
    	        .setSound(noteSound)
    	        .setAutoCancel(true);
    	
        int seed = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
    	Random r = new Random(seed);
    	int notificationID = r.nextInt();
    	object.setID(notificationID);
    	
    	Intent resInt = new Intent(this, PlaceItDetails.class);
    	resInt.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
    	resInt.putExtra(PlaceIt.PLACEIT_POS_KEY, ActivePlaceIts.getInstance().getPosition(object));
    	resInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    	
    	PendingIntent resPendInt = PendingIntent.getActivity(
    			                   this,
    			                   0,
    			                   resInt,
    			                   PendingIntent.FLAG_UPDATE_CURRENT);
    	note.setContentIntent(resPendInt);

    	startActivity(resInt);
    	
    	NotificationManager notifyMgr = 
    	        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	notifyMgr.notify(notificationID, note.build());
    	

    } 

}