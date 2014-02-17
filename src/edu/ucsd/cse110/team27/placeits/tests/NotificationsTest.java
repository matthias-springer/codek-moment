package edu.ucsd.cse110.team27.placeits.tests;

//import static org.junit.Assert.*;

import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
//import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
//import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;

import android.app.Instrumentation;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class NotificationsTest extends
		ActivityInstrumentationTestCase2<MapActivity> {

	MapActivity mActivity;
	Instrumentation mInstrumentation;
	LocationClient mLocationClient;

	private static final String PROVIDER = "flp";
	private static final double USER_LAT = 80;
	private static final double USER_LNG = 80;
	private static final float ACCURACY = 3.0f;

	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";

	private final String DEMO_DONT_NOTIFY = "Should not notify";
	private final String DEMO_FALSE_NOTIFICATION = "Dont print me";

	// private final LatLng USER_LOCATION = new LatLng(80,80);
	private final LatLng IN_RANGE1 = new LatLng(80, 80);
	private final LatLng IN_RANGE2 = new LatLng(79, 79);
	private final LatLng NOT_IN_RANGE = new LatLng(-80, -80);

	public NotificationsTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		ActivePlaceIts.getInstance().clear();
		Given_TheMapIsShown();
	}

	@Override
	protected void tearDown() throws Exception {

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				PulledDownPlaceIts.getInstance().clear();
			}
		});
		mInstrumentation.waitForIdleSync();


		super.tearDown();
	}

	public void testNotifications() {
		mLocationClient = mActivity.getLocationClient();
		
		When_InRangeOfAPlaceIt();
		Then_UserGetsNotification();
		When_NotInRangeOfPlaceIt();
		Then_UserNotNotified();
		When_PlaceItNotifiesUser();
		Then_UserIsNotifiedSpecificPlaceIt();
	}

	private void Then_UserIsNotifiedSpecificPlaceIt() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
		
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getPrint(),
				true);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getTitle(),
				DEMO_PLACEIT_TITLE);
	}

	private void When_PlaceItNotifiesUser() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();

				ActivePlaceIts.getInstance().add(new PlaceIt(DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION, new LatLng(
						MapActivity.lastLocation.getLatitude(), MapActivity.lastLocation.getLongitude())));
			}

		});
		mInstrumentation.waitForIdleSync();

	}

	private void Then_UserNotNotified() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
		
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getPrint(),
				false);
	}

	private void When_NotInRangeOfPlaceIt() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				
				ActivePlaceIts.getInstance().add(new PlaceIt(DEMO_DONT_NOTIFY, DEMO_FALSE_NOTIFICATION, new LatLng(
						MapActivity.lastLocation.getLatitude() + 10, MapActivity.lastLocation.getLongitude() + 10)));
			}
		});
		mInstrumentation.waitForIdleSync();

	}

	private void Then_UserGetsNotification() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
		
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getPrint(),
				true);

	}

	private void When_InRangeOfAPlaceIt() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();

				// simulate user location at 90, 90 (USER_LOCATION)
				mLocationClient.setMockLocation(createLocation(USER_LAT,
						USER_LNG, ACCURACY));

				ActivePlaceIts.getInstance().add(new PlaceIt(DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION, new LatLng(
						MapActivity.lastLocation.getLatitude(), MapActivity.lastLocation.getLongitude())));
				
			}
		});
		mInstrumentation.waitForIdleSync();

	}

	class IntWrapper {
		public int value = 0;
	}

	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();

		//  Wait for GPS to be ready
		while (MapActivity.lastLocation == null ){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getLocationClient().setMockMode(true);
				mActivity.getLocationClient().setMockLocation(createLocation(USER_LAT, USER_LNG, 3));
			}
		});
		mInstrumentation.waitForIdleSync();
		

	}

	public Location createLocation(double lat, double lng, float accuracy) {
		// Create a new Location
		Location newLocation = new Location(PROVIDER);
		newLocation.setLatitude(lat);
		newLocation.setLongitude(lng);
		newLocation.setAccuracy(accuracy);
		return newLocation;
	}

}
