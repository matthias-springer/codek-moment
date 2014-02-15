package edu.ucsd.cse110.team27.placeits.tests;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class CreatePlaceItTest extends
		ActivityInstrumentationTestCase2<MapActivity> {

	MapActivity mActivity;
	Instrumentation mInstrumentation;

	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";

	public CreatePlaceItTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Given_TheMapIsShown();
	}

	@Override
	protected void tearDown() throws Exception {
		// NOTE: all manipulations of the place its lists must be done in the UI threads because they trigger changes on the map automatically
		
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
			}
		});
		
		super.tearDown();
	}

	public void testSuccessfulPlaceItCreation() throws InterruptedException {
		When_WeTapTheMap();
		Then_ThePlaceItCreationDialogIsShown();
		When_WeEnterValidDataAndConfirm();
		Then_APlaceItIsAddedToTheActiveList();
		Then_ThePlaceItCreationDialogIsHidden();
	}

	public void testCancelledPlaceItCreation() throws InterruptedException {
		int numberActiveOfPlaceIts = ActivePlaceIts.getInstance().size();
		When_WeTapTheMap();
		Then_ThePlaceItCreationDialogIsShown();
		When_WeTapTheCancelButton();
		Then_ThePlaceItCreationDialogIsHidden();
		Then_TheNumberOfPlaceItsEquals(numberActiveOfPlaceIts);
	}
	
	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
	}

	private void When_WeTapTheMap() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getUIHandlers().onMapClick(new LatLng(12, 100));
			}
		});

		mInstrumentation.waitForIdleSync();
	}
	
	private void When_WeEnterValidDataAndConfirm() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((EditText) mActivity.findViewById(R.id.placeItTitle))
						.setText(DEMO_PLACEIT_TITLE);
				((EditText) mActivity.findViewById(R.id.placeItDescription))
						.setText(DEMO_PLACEIT_DESCRIPTION);
				mActivity.getUIHandlers().onCreateButtonClicked();
			}
		});

		mInstrumentation.waitForIdleSync();
	}
	
	private void When_WeTapTheCancelButton() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mActivity.getUIHandlers().onCancelCreateButtonClicked();
			}
		});

		mInstrumentation.waitForIdleSync();
	}
	
	private void Then_APlaceItIsAddedToTheActiveList() {
		assertTrue(ActivePlaceIts.getInstance().contains(
				new PlaceIt(DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION,
						new LatLng(12, 100))));
	}

	private void Then_ThePlaceItCreationDialogIsShown() {
		assertEquals(((LinearLayout) mActivity
				.findViewById(R.id.createPlaceItLayout)).getVisibility(), View.VISIBLE);
	}
	
	private void Then_ThePlaceItCreationDialogIsHidden() {
		assertEquals(((LinearLayout) mActivity
				.findViewById(R.id.createPlaceItLayout)).getVisibility(), View.GONE);
	}
	
	private void Then_TheNumberOfPlaceItsEquals(int number) {
		assertEquals(ActivePlaceIts.getInstance().size(), number);
	}
}
