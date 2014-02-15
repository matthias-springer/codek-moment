package edu.ucsd.cse110.team27.placeits.tests;

import java.util.Date;
import java.util.Random;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class RecurringPlaceItTest extends
		ActivityInstrumentationTestCase2<MapActivity> {

	MapActivity mActivity;
	Instrumentation mInstrumentation;

	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";

	public RecurringPlaceItTest() {
		super(MapActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RecurringPlaceIts.getInstance().clear();
		Given_TheMapIsShown();
	}

	@Override
	protected void tearDown() throws Exception {
		// NOTE: all manipulations of the place its lists must be done in the UI
		// threads because they trigger changes on the map automatically

		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				RecurringPlaceIts.getInstance().clear();
			}
		});

		super.tearDown();
	}

	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
	}

	public void testCreateRecurringPlaceIt() {
		When_WeTapTheMap();
		Then_ThePlaceItCreationDialogIsShown();
		When_WeTapTheRecurringPlaceItBox();
		Then_TheSettingsForTheRecurringPlaceItsAreShown();
		When_WeEnterValidDataIncludingRecurringInformationAndConfirm();
		Then_ThePlaceItWillBeInTheRecurringList();
	}

	public void testCreatingPlaceItWhenDue() {
		testCreateRecurringPlaceIt();
		When_TheRecurringThePlaceItIsDue();
		Then_ACopyIsAddedToTheActiveList();
	}

	@SuppressWarnings("deprecation")
	public void testCalculationOfNextScheduledDate() {
		Date now;
		Date nextDate;
		
		for (int i = 0; i < 10; i++) {
			int randomInt = (new Random()).nextInt(10000);
			
			now = new Date();
			nextDate = new Date(now.getTime() + 60 * 1000 * randomInt);
			assertEquals(PlaceItPrototype.nextScheduledTime(now, 0, 0, randomInt, PlaceItPrototype.RepeatMode.MINUTES).getTime(), nextDate.getTime());
		}
		
		/*now = new Date(114, 1, 14, 3, 29);
		nextDate = new Date(114, 1, 26, 3, 29);
		assertEquals(PlaceItPrototype.nextScheduledTime(now, 1, 1, 0, PlaceItPrototype.RepeatMode.DAY_WEEK).getTime(), nextDate.getTime());*/
		
	}
	
	private void When_TheRecurringThePlaceItIsDue() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
			}
		});

		RecurringPlaceIts.getInstance().getAtPosition(0)
				.setNextScheduledTime(new Date());
		try {
			// wait for the scheduler to recreate the place it
			Thread.sleep(4000);
		} catch (InterruptedException e) {
		}
	}

	private void Then_ACopyIsAddedToTheActiveList() {
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getTitle(),
				DEMO_PLACEIT_TITLE);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0)
				.getDescription(), DEMO_PLACEIT_DESCRIPTION);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getLatLng(),
				new LatLng(12, 100));

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

	private void Then_ThePlaceItCreationDialogIsShown() {
		assertEquals(
				((LinearLayout) mActivity.findViewById(R.id.createPlaceItLayout))
						.getVisibility(), View.VISIBLE);
	}

	private void When_WeTapTheRecurringPlaceItBox() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((CheckBox) mActivity.findViewById(R.id.checkRepeating))
						.setChecked(true);
			}
		});

		mInstrumentation.waitForIdleSync();
	}

	private void Then_TheSettingsForTheRecurringPlaceItsAreShown() {
		assertEquals(mActivity.findViewById(R.id.repeatingBox).getVisibility(),
				View.VISIBLE);
	}

	private void When_WeEnterValidDataIncludingRecurringInformationAndConfirm() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				((EditText) mActivity.findViewById(R.id.placeItTitle))
						.setText(DEMO_PLACEIT_TITLE);
				((EditText) mActivity.findViewById(R.id.placeItDescription))
						.setText(DEMO_PLACEIT_DESCRIPTION);
				((RadioButton) mActivity.findViewById(R.id.optionRepeatMinute))
						.setChecked(true);
				((EditText) mActivity.findViewById(R.id.repeatMinutes))
						.setText("1");
				mActivity.getUIHandlers().onCreateButtonClicked();
			}
		});

		mInstrumentation.waitForIdleSync();
	}

	private void Then_ThePlaceItWillBeInTheRecurringList() {
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0)
				.getMinutes(), 1);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0)
				.getRepeatMode(), PlaceItPrototype.RepeatMode.MINUTES);
	}
}