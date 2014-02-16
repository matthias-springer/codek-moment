package edu.ucsd.cse110.team27.placeits.tests;

import com.google.android.gms.maps.model.LatLng;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;

public class MapMarkerTest extends
		ActivityInstrumentationTestCase2<MapActivity> {

	MapActivity mActivity;
	Instrumentation mInstrumentation;

	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";

	public MapMarkerTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
			}
		});
		mInstrumentation.waitForIdleSync();
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
	}

	public void testMarkerShownCorrectly() {
		When_AnActivePlaceItIsCreated();
		Then_AMarkerIsShownOnTheMap();
		When_ThePlaceItIsPulledDown();
		Then_TheMarkerIsDeleted();
	}

	private void When_AnActivePlaceItIsCreated() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().add(
						new PlaceIt(DEMO_PLACEIT_TITLE,
								DEMO_PLACEIT_DESCRIPTION, new LatLng(10, 12)));
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	
	private PlaceIt placeIt;
	
	private void Then_AMarkerIsShownOnTheMap() {
		placeIt = ActivePlaceIts.getInstance().getAtPosition(0);
		
		assertEquals(placeIt.getTitle(), placeIt.getMarker().getTitle());
		assertEquals(placeIt.getDescription(), placeIt.getMarker().getSnippet());
		assertEquals(placeIt.getMarker().getPosition(), placeIt.getLatLng());
	}
	
	private void When_ThePlaceItIsPulledDown() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	
	private void Then_TheMarkerIsDeleted() {
		assertEquals(placeIt.getMarker(), null);
	}
}
