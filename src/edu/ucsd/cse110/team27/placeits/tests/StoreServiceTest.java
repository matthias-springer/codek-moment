package edu.ucsd.cse110.team27.placeits.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.google.android.gms.maps.model.LatLng;

import android.app.Instrumentation;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.StoreService;
import edu.ucsd.cse110.team27.placeits.StoreService.getListTask;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.User;

public class StoreServiceTest extends ActivityInstrumentationTestCase2<MapActivity> {
	
	MapActivity mActivity;
	Instrumentation mInstrumentation;
	getListTask serverTask;
	StoreService service;
	
	private final String DEMO_PLACEIT_TITLE = "Demo Place It";
	private final String DEMO_PLACEIT_DESCRIPTION = "This is the description.";
	private final LatLng DEMO_LOCATION = new LatLng(10, 12);
	
	public StoreServiceTest() {
		super(MapActivity.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Given_TheMapIsShown();
	}
	

	private void Given_TheMapIsShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
	}
	
	public void testLoadSaveActive() {
		Given_ThereIsAPlaceItInTheActiveList();
		when_interactwithserver();
		When_TheUserMakeChanges();
		when_interactwithserver();
		Then_ThePlaceItIsStillInTheActiveList();
	}

	public void testLoadSavePulledDown() {
		Given_ThereIsAPlaceItInThePulledDownList();
		when_interactwithserver();
		When_TheUserMakeChanges();
		when_interactwithserver();
		Then_ThePlaceItIsStillInThePulledDownList();
	}

	public void testLoadSaveRecurring() {
		Given_ThereIsAPlaceItInTheRecurringList();
		when_interactwithserver();
		When_TheUserMakeChanges();
		when_interactwithserver();
		Then_ThePlaceItIsStillInTheRecurringList();
	}
	
	
	private void When_TheUserMakeChanges() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				ActivePlaceIts.getInstance().clear();
				PulledDownPlaceIts.getInstance().clear();
				RecurringPlaceIts.getInstance().clear();
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	
	
	private void when_interactwithserver() {
		serverTask = service.generateListTask(); 
		serverTask.execute();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	private void Then_ThePlaceItIsStillInTheActiveList() {
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getTitle(), DEMO_PLACEIT_TITLE);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getDescription(), DEMO_PLACEIT_DESCRIPTION);
		assertEquals(ActivePlaceIts.getInstance().getAtPosition(0).getLatLng(), DEMO_LOCATION);
	}
	
	private void Then_ThePlaceItIsStillInThePulledDownList() {
		assertEquals(PulledDownPlaceIts.getInstance().getAtPosition(0).getTitle(), DEMO_PLACEIT_TITLE);
		assertEquals(PulledDownPlaceIts.getInstance().getAtPosition(0).getDescription(), DEMO_PLACEIT_DESCRIPTION);
		assertEquals(PulledDownPlaceIts.getInstance().getAtPosition(0).getLatLng(), DEMO_LOCATION);
	}
	
	private void Then_ThePlaceItIsStillInTheRecurringList() {
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getTitle(), DEMO_PLACEIT_TITLE);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getDescription(), DEMO_PLACEIT_DESCRIPTION);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getLatLng(), DEMO_LOCATION);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getDayOfWeek(), 1);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getMinutes(), 1);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getWeek(), 1);
		assertEquals(RecurringPlaceIts.getInstance().getAtPosition(0).getRepeatMode(), PlaceItPrototype.RepeatMode.DAY_WEEK);
	}
	
	
	private void Given_ThereIsAPlaceItInTheActiveList() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				ActivePlaceIts.getInstance().add(new PlaceIt(
						DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION, DEMO_LOCATION));
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	private void Given_ThereIsAPlaceItInTheRecurringList() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				RecurringPlaceIts.getInstance().clear();
				RecurringPlaceIts.getInstance().add(new PlaceItPrototype (
						DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION, DEMO_LOCATION, 1, 1, 1, PlaceItPrototype.RepeatMode.DAY_WEEK));
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	
	private void Given_ThereIsAPlaceItInThePulledDownList() {
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				PulledDownPlaceIts.getInstance().clear();
				PulledDownPlaceIts.getInstance().add(new PlaceIt(
						DEMO_PLACEIT_TITLE, DEMO_PLACEIT_DESCRIPTION, DEMO_LOCATION));
			}
		});
		mInstrumentation.waitForIdleSync();
	}
	
	

	@Override
	protected void tearDown() throws Exception {
		// NOTE: all manipulations of the place its lists must be done in the UI threads because they trigger changes on the map automatically
		
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				ActivePlaceIts.getInstance().clear();
				PulledDownPlaceIts.getInstance().clear();
				RecurringPlaceIts.getInstance().clear();
			}
		});
		mInstrumentation.waitForIdleSync();
		
		super.tearDown();
	}

}
