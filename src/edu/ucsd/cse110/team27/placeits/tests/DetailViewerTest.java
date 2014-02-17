package edu.ucsd.cse110.team27.placeits.tests;

import com.google.android.gms.maps.model.LatLng;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import edu.ucsd.cse110.team27.placeits.PlaceItDetails;
import edu.ucsd.cse110.team27.placeits.PlaceItsChangeListener;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;

// this fails atm
public class DetailViewerTest extends
		ActivityInstrumentationTestCase2<PlaceItDetails> {

	PlaceItDetails mActivity;
	Instrumentation mInstrumentation;

	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";

	private PlaceIt placeit;

	public DetailViewerTest() {
		super(PlaceItDetails.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	
	}

	private PlaceItsChangeListener oldListener;
	
	private void given_placeItInActiveList() {
		setActivityInitialTouchMode(false);
		placeit = new PlaceIt(DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION,
				new LatLng(10,12));
		
		PlaceIts.disableIO = true;

		ActivePlaceIts.getInstance().add(placeit);
		Intent intent = new Intent();
		intent.putExtra(PlaceIt.PLACEIT_POS_KEY, ActivePlaceIts.getInstance().getPosition(placeit));
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
		setActivityIntent(intent);
		mActivity = getActivity();
		
		mInstrumentation = getInstrumentation();
		
		PlaceItDetails.redirectView = false;
	}

	@Override
	protected void tearDown() throws Exception {
		PlaceIts.disableIO = false;
		PlaceItDetails.redirectView = true;
		//PlaceIts.activity = oldListener;
		ActivePlaceIts.getInstance().clear();
		PulledDownPlaceIts.getInstance().clear();
		super.tearDown();
	}

	public void testDiscardPlaceIt() {
		given_placeItInActiveList();
		when_DiscardButtonClicked();
		then_PlaceItRemovedFromList();
	}
	
	
	public void testPullDown() {
		given_placeItInActiveList();
		when_pullDownButtonClicked();
		then_placeItInPulledDownList();
	}
	
	public void testRepost() {
		given_placeItInPulledDownList();
		when_repostButtonClicked();
		then_placeItInActiveList();
	}
	
	private void then_placeItInActiveList() {
		assertTrue(ActivePlaceIts.getInstance().contains(placeit));
	}

	private void then_placeItNotInActiveList() {
		assertFalse(ActivePlaceIts.getInstance().contains(placeit));
	}
	
	private void when_repostButtonClicked() {
		mActivity.repostPlaceIt(null);
	}

	private void when_SnoozeButtonClicked() {
		mActivity.snoozePlaceIt(null, -1);
	}
	
	private void when_TimeHasPassed() {
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
	}
	
	
	private void then_NotificationIsShownAgain() {
		assertTrue(mActivity.shownAgain.value);
	}
	
	private void given_placeItInPulledDownList() {
		setActivityInitialTouchMode(false);
		placeit = new PlaceIt(DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION,
				new LatLng(10,12));
		
		PlaceIts.disableIO = true;

		PulledDownPlaceIts.getInstance().add(placeit);
		Intent intent = new Intent();
		intent.putExtra(PlaceIt.PLACEIT_POS_KEY, PulledDownPlaceIts.getInstance().getPosition(placeit));
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PULLED);
		setActivityIntent(intent);
		mActivity = getActivity();
		

		mInstrumentation = getInstrumentation();
	}

	public void testSnooze() {
		given_placeItInActiveList();
		when_SnoozeButtonClicked();
		then_placeItNotInActiveList();
		when_TimeHasPassed();
		then_NotificationIsShownAgain();
	}

	private void then_placeItInPulledDownList() {
		assertTrue(PulledDownPlaceIts.getInstance().contains(placeit));
	}

	private void when_pullDownButtonClicked() {
		mActivity.pullDownPlaceIt(null);
	}

	private void when_DiscardButtonClicked() {
		mActivity.discardPlaceIt(null);
	}

	private void then_PlaceItRemovedFromList() {
		assertFalse(ActivePlaceIts.getInstance().contains(placeit));
	}
}
