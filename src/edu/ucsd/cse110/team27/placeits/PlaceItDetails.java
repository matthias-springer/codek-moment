package edu.ucsd.cse110.team27.placeits;

import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PlaceItDetails extends Activity {

	private PlaceIt placeit;
	private int placeItType;
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show the Up button in the action bar.
		setupActionBar();

		placeItType = (int) getIntent().getIntExtra(PlaceIt.PLACEIT_TYPE_KEY, -1);

		switch (placeItType) {
		case PlaceIt.PLACE_IT_ACTIVE:
			placeit = ActivePlaceIts.getInstance().
			getAtPosition(getIntent().getIntExtra(PlaceIt.PLACEIT_POS_KEY, 0));
			setContentView(R.layout.activity_place_it_details_active);
			break;
		case PlaceIt.PLACE_IT_PULLED:
			placeit = PulledDownPlaceIts.getInstance().
			getAtPosition(getIntent().getIntExtra(PlaceIt.PLACEIT_POS_KEY, 0));
			setContentView(R.layout.activity_place_it_details_pulled);
			break;
		case PlaceIt.PLACE_IT_PROTOTYPE:
			placeit = RecurringPlaceIts.getInstance().
			getAtPosition(getIntent().getIntExtra(PlaceIt.PLACEIT_POS_KEY, 0));
			setContentView(R.layout.activity_place_it_details_recurring);
			break;
		}

		((TextView) findViewById(R.id.detailsDescription_fill)).setText(placeit.getDescription());
		((TextView) findViewById(R.id.detailsTitle_fill)).setText(placeit.getTitle());
		
		placeit.setPrint(true);
	}

	public static boolean redirectView = true;
	
	/*
	 * Methods for buttons
	 */
	public void discardPlaceIt(View view) {
		NotificationManager notifyMgr = 
    	        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notifyMgr.cancel(placeit.getID());
		
		intent = NavUtils.getParentActivityIntent(this);
		switch (placeItType) {
		case PlaceIt.PLACE_IT_ACTIVE:
			ActivePlaceIts.getInstance().remove(placeit);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
			break;
		case PlaceIt.PLACE_IT_PULLED:
			PulledDownPlaceIts.getInstance().remove(placeit);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PULLED);
			break;
		}
		NavUtils.navigateUpTo(this, intent);
		
		if (redirectView) {
			startActivity(new Intent(this, MapActivity.class));
		}
		
		
	}

	public void snoozePlaceIt(View view) {
		snoozePlaceIt(view, 600000);
	}
	
	public class BoolWrapper {
		public boolean value = false;
	}
	
	public final BoolWrapper shownAgain = new BoolWrapper();
	
	public void snoozePlaceIt(View view, int time) {
		PulledDownPlaceIts.getInstance().add(placeit);
		ActivePlaceIts.getInstance().remove(placeit);
		
		NotificationManager notifyMgr = 
    	        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		shownAgain.value = true;
		notifyMgr.cancel(placeit.getID());
		
		if (time != -1) {
		// after clicking snooze navigate back to the map so list refreshes.
			startActivity(new Intent(this, MapActivity.class));
		}
		
		if (time == -1) time = 1000;
		
		// timeer 5000 = 5 sec so 600000 = 10 min
		new CountDownTimer(time, time) {

			// on tick the pulled down will go back to active
			public void onTick(long millisUntilFinished) {
				// do nothing 
				ActivePlaceIts.getInstance().add(placeit);
				PulledDownPlaceIts.getInstance().remove(placeit);
				shownAgain.value = true;
			}
			
			// on finish repost the place it. 
			public void onFinish() {
				shownAgain.value = true;
				placeit.setPrint(false);
				repostPlaceIt(null);

			}
		}.start();
		
		// reprint post it on the map, also update the activity window after snoozing
		// clicking snooze after clicking snooze gives error
		// stayig on snooze page after clicking will take me to pulldown placeit list
		// snooze button in pulled down list gives error/ add duplicate since it calls snooze
		//
		
	}

	public void pullDownPlaceIt(View view) {
		NotificationManager notifyMgr = 
    	        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notifyMgr.cancel(placeit.getID());
		
		intent = NavUtils.getParentActivityIntent(this);
		ActivePlaceIts.getInstance().remove(placeit);
		PulledDownPlaceIts.getInstance().add(placeit);
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
		NavUtils.navigateUpTo(this, intent);
	}

	public void repostPlaceIt(View view) {
		intent = NavUtils.getParentActivityIntent(this);
		ActivePlaceIts.getInstance().add(placeit);
		PulledDownPlaceIts.getInstance().remove(placeit);
		placeit.setPrint(false);
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PULLED);
		NavUtils.navigateUpTo(this, intent);
	}

	public void deletePlaceIt(View view) {
		intent = NavUtils.getParentActivityIntent(this);
		RecurringPlaceIts.getInstance().remove((PlaceItPrototype) placeit);
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PROTOTYPE);
		NavUtils.navigateUpTo(this, intent);		
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.place_it_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
