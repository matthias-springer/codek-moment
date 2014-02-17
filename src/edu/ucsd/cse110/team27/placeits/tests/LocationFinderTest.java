package edu.ucsd.cse110.team27.placeits.tests;

import java.util.List;

import com.google.android.gms.maps.model.Marker;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;

public class LocationFinderTest extends
		ActivityInstrumentationTestCase2<MapActivity> {

	MapActivity mActivity;
	Instrumentation mInstrumentation;

	public LocationFinderTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Given_TheMapIsShown();
	}

	@Override
	protected void tearDown() throws Exception {
		// NOTE: all manipulations of the place its lists must be done in the UI
		// threads because they trigger changes on the map automatically

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
			}
		});

		super.tearDown();
	}
	
	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
		mInstrumentation.waitForIdleSync();
	}
	
	public void testLocationFinder() {
		When_WeSearchForAnExistingLocationOnTheMap();
		Then_AMarkerIsShownAtThatLocation();
	}
	
	private void When_WeSearchForAnExistingLocationOnTheMap() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((EditText) mActivity.findViewById(R.id.location)).setText("Berlin");
				mActivity.getUIHandlers().onSearchBoxEditorAction(EditorInfo.IME_ACTION_SEARCH, null);
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	
	private void Then_AMarkerIsShownAtThatLocation() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
		
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				List<Marker> markers = mActivity.getUIHandlers().getLocationMarkers();
				
				assertEquals(1, markers.size());
				assertEquals(markers.get(0).getTitle(), "Berlin");
				assertEquals((int) markers.get(0).getPosition().latitude, 52);
				assertEquals((int) markers.get(0).getPosition().longitude, 13);
			}
		});
		mInstrumentation.waitForIdleSync();

	}
}
