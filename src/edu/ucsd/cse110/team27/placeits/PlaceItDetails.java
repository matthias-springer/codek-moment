package edu.ucsd.cse110.team27.placeits;

import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Show the Up button in the action bar.
		setupActionBar();

		placeItType = (int) getIntent().getIntExtra(PlaceIt.PLACEIT_TYPE_KEY, -1);

		switch (placeItType) {
		case PlaceIt.PLACE_IT_ACTIVE:
			placeit = ActivePlaceIts.getInstance(null).
			getAtPosition(getIntent().getIntExtra(PlaceIt.PLACEIT_POS_KEY, 0));
			setContentView(R.layout.activity_place_it_details_active);
			break;
		case PlaceIt.PLACE_IT_PULLED:
			placeit = PulledDownPlaceIts.getInstance(null).
			getAtPosition(getIntent().getIntExtra(PlaceIt.PLACEIT_POS_KEY, 0));
			setContentView(R.layout.activity_place_it_details_pulled);
			break;
		}

		((TextView) findViewById(R.id.detailsDescription_fill)).setText(placeit.getDescription());
		((TextView) findViewById(R.id.detailsTitle_fill)).setText(placeit.getTitle());
	}

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
			ActivePlaceIts.getInstance(null).remove(placeit);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
			break;
		case PlaceIt.PLACE_IT_PULLED:
			PulledDownPlaceIts.getInstance(null).remove(placeit);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PULLED);
			break;
		}
		NavUtils.navigateUpTo(this, intent);
		
		startActivity(new Intent(this, MapActivity.class));
		
		
	}

	public void snoozePlaceIt(View view) {
		//TODO: Add snooze functionality
		
		PulledDownPlaceIts.getInstance(null).add(placeit);
		ActivePlaceIts.getInstance(null).remove(placeit);
		
		NotificationManager notifyMgr = 
    	        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notifyMgr.cancel(placeit.getID());
		
		// after clicking snooze navigate back to the map so list refreshes.
		startActivity(new Intent(this, MapActivity.class));
		
		// timeer 5000 = 5 sec so 600000 = 10 min
		new CountDownTimer(5000, 5000) {

			// on tick the pulled down will go back to active
			public void onTick(long millisUntilFinished) {
				// do nothing 
				ActivePlaceIts.getInstance(null).add(placeit);
				PulledDownPlaceIts.getInstance(null).remove(placeit);
			}
			
			// on finish repost the place it. 
			public void onFinish() {
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
		ActivePlaceIts.getInstance(null).remove(placeit);
		PulledDownPlaceIts.getInstance(null).add(placeit);
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
		NavUtils.navigateUpTo(this, intent);
	}

	public void repostPlaceIt(View view) {
		intent = NavUtils.getParentActivityIntent(this);
		ActivePlaceIts.getInstance(null).add(placeit);
		PulledDownPlaceIts.getInstance(null).remove(placeit);
		intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PULLED);
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
