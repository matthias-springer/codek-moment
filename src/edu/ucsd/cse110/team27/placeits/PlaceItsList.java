package edu.ucsd.cse110.team27.placeits;

import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.NavUtils;

public class PlaceItsList extends Activity {

	private ArrayAdapter<PlaceIt> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.placeitlist_activity);
		// Show the Up button in the action bar.
		setupActionBar();
		
		ListView activeList = (ListView) findViewById(R.id.placeits_list);
		switch(getIntent().getIntExtra(MapActivity.MESSAGE_KEY, -1)) {
			case 0:
				adapter = new ArrayAdapter<PlaceIt>(this,
		                android.R.layout.simple_list_item_1, 
		                (PlaceIt[]) ActivePlaceIts.getInstance(null).toArray());
				setTitle("Active PlaceIts");
				break;
			case 1:
				adapter = new ArrayAdapter<PlaceIt>(this,
		                android.R.layout.simple_list_item_1, 
		                (PlaceIt[]) PulledDownPlaceIts.getInstance(null).toArray());
				setTitle("Pulled Down PlaceIts");
				break;			
		}
		
		
		activeList.setAdapter(adapter);
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
		getMenuInflater().inflate(R.menu.place_its_list, menu);
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
