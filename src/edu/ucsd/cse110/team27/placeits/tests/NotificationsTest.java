package edu.ucsd.cse110.team27.placeits.tests;

//import static org.junit.Assert.*;

import org.junit.Test;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
//import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
//import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;

import android.app.Instrumentation;
import android.location.Location;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

public class NotificationsTest extends ActivityInstrumentationTestCase2<MapActivity> {
	
	MapActivity mActivity;
	Instrumentation mInstrumentation;
	LocationClient mLocationClient;
	
    private static final String PROVIDER = "flp";
    private static final double USER_LAT = 90;
    private static final double USER_LNG = -90;
    private static final float ACCURACY = 3.0f;
	
	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";
	
	private final String DEMO_DONT_NOTIFY = "Should not notify";
	private final String DEMO_FALSE_NOTIFICATION = "Dont print me";
	
	//private final LatLng USER_LOCATION = new LatLng(80,80);
	private final LatLng IN_RANGE1 =  new LatLng(80,80);
	private final LatLng IN_RANGE2 =  new LatLng(79,79);
	private final LatLng NOT_IN_RANGE = new LatLng(-80, -80);

	public NotificationsTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mLocationClient.connect();
		mLocationClient.setMockMode(true);
		ActivePlaceIts.getInstance().clear();
		Given_TheMapIsShown();
	}
	
	@Override
	protected void tearDown() throws Exception {
		
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
			}
		});
		super.tearDown();
	}
	
	@Test
	public void testNotifications() {
		When_InRangeOfAPlaceIt();
		Then_UserGetsNotification();
		When_NotInRangeOfPlaceIt();
		Then_UserNotNotified();
		When_PlaceItNotifiesUser();
		Then_UserIsNotifiedSpecificPlaceIt();
	}
	
	private void Then_UserIsNotifiedSpecificPlaceIt() {
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getPrint(), false);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getTitle(), DEMO_DONT_NOTIFY);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(1).getPrint(), true);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(1).getTitle(), DEMO_PLACEIT_TITLE);	
	}

	private void When_PlaceItNotifiesUser() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				
				//simulate user location at 90, 90 (USER_LOCATION)
				mLocationClient.setMockLocation(createLocation(USER_LAT, USER_LNG, ACCURACY));
				
				//not in range placeit created
				mActivity.getUIHandlers().onMapClick(NOT_IN_RANGE);
				((EditText) mActivity.findViewById(R.id.placeItTitle))
				.setText(DEMO_DONT_NOTIFY);
		        ((EditText) mActivity.findViewById(R.id.placeItDescription))
				.setText(DEMO_FALSE_NOTIFICATION);
		       mActivity.getUIHandlers().onCreateButtonClicked();
		       
				//placeit 2 created
				mActivity.getUIHandlers().onMapClick(IN_RANGE2);
				((EditText) mActivity.findViewById(R.id.placeItTitle))
				.setText(DEMO_PLACEIT_TITLE);
		        ((EditText) mActivity.findViewById(R.id.placeItDescription))
				.setText(DEMO_PLACEIT_DESCRIPTION);
		       mActivity.getUIHandlers().onCreateButtonClicked();
			
			}
			
		});
		mInstrumentation.waitForIdleSync();
		
	}

	private void Then_UserNotNotified() {
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getPrint(), false);		
	}

	private void When_NotInRangeOfPlaceIt() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				
				//simulate user location at 90, 90 (USER_LOCATION)
				mLocationClient.setMockLocation(createLocation(USER_LAT, USER_LNG, ACCURACY));
				
				//not in range placeit created
				mActivity.getUIHandlers().onMapClick(NOT_IN_RANGE);
				((EditText) mActivity.findViewById(R.id.placeItTitle))
				.setText(DEMO_DONT_NOTIFY);
		        ((EditText) mActivity.findViewById(R.id.placeItDescription))
				.setText(DEMO_FALSE_NOTIFICATION);
		       mActivity.getUIHandlers().onCreateButtonClicked();
			}
		});
		mInstrumentation.waitForIdleSync();
		
	}

	private void Then_UserGetsNotification() {
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getPrint(), true);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(1).getPrint(), true);
	}

	private void When_InRangeOfAPlaceIt() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				
				//simulate user location at 90, 90 (USER_LOCATION)
				mLocationClient.setMockLocation(createLocation(USER_LAT, USER_LNG, ACCURACY));
				
				//placeit 1 created
				mActivity.getUIHandlers().onMapClick(IN_RANGE1);
				((EditText) mActivity.findViewById(R.id.placeItTitle))
				.setText(DEMO_PLACEIT_TITLE);
		        ((EditText) mActivity.findViewById(R.id.placeItDescription))
				.setText(DEMO_PLACEIT_DESCRIPTION);
		       mActivity.getUIHandlers().onCreateButtonClicked();
		       
				//placeit 2 created
				mActivity.getUIHandlers().onMapClick(IN_RANGE2);
				((EditText) mActivity.findViewById(R.id.placeItTitle))
				.setText(DEMO_PLACEIT_TITLE);
		        ((EditText) mActivity.findViewById(R.id.placeItDescription))
				.setText(DEMO_PLACEIT_DESCRIPTION);
		       mActivity.getUIHandlers().onCreateButtonClicked();
			}
		});
		mInstrumentation.waitForIdleSync();
		
	}

	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
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
