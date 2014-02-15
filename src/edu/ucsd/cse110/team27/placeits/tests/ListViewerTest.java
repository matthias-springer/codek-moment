package edu.ucsd.cse110.team27.placeits.tests;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;
import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.PlaceItsList;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;

public class ListViewerTest extends
		ActivityInstrumentationTestCase2<MapActivity> {

	MapActivity mActivity;
	Instrumentation mInstrumentation;

	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";

	public ListViewerTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Given_TheMapIsShown();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
	}

	public void testActiveListShown() {
		when_ActiveListClicked();
		then_ActiveListShown();
	}

	private void when_ActiveListClicked() {
		mActivity.onOptionListSelected(R.id.dropDownActiveList);
	}

	public void then_ActiveListShown() {
		assertEquals(PlaceIt.PLACE_IT_ACTIVE, PlaceItsList.lastList);
	}

	public void testPulledDownListShown () {
		when_PulledDownListClicked();
		then_PulledDownListShown();
	}

	private void when_PulledDownListClicked() {
		mActivity.onOptionListSelected(R.id.dropDownPulledList);
	}

	private void then_PulledDownListShown() {	
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(PlaceIt.PLACE_IT_PULLED, PlaceItsList.lastList);
	}
}
