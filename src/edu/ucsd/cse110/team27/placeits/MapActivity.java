package edu.ucsd.cse110.team27.placeits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.PlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype.RepeatMode;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.User;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MapActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener,
		PlaceItsChangeListener {

	private GoogleMap mMap;

	private LocationClient mLocationClient;

	private UIHandlers uiHandlers;

	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(1000) // 1s
			.setFastestInterval(16) // 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	public UIHandlers getUIHandlers() {
		// NOTE: cannot move Singleton creation to the class itself due to Java
		// syntax restrictions

		if (uiHandlers == null) {
			uiHandlers = new UIHandlers();
		}

		return uiHandlers;
	}

	/**
	 * This class handles all UI event stuff. Contains callbacks and references
	 * to views.
	 */
	public class UIHandlers {
		private final EditText placeItTitle;
		private final EditText placeItDescription;
		private final Button cancelCreateButton;
		private final EditText searchBox;
		private final Button createButton;
		private final LinearLayout createPlaceItLayout;

		private final CheckBox checkRepeating;
		private final LinearLayout repeatingBox;
		private final RadioButton optionRepeatDayWeek;
		private final RadioButton optionRepeatMinutes;
		private final LinearLayout dayWeekBox;
		private final LinearLayout minutesBox;
		private final EditText textRepeatWeeks;
		private final Spinner spinnerRepeatDay;
		private final EditText textRepeatMinutes;

		private final Spinner cat1;
		private final Spinner cat2;
		private final Spinner cat3;

		private final Button debugClear;

		private LatLng lastLocation;

		public UIHandlers() {
			placeItTitle = (EditText) findViewById(R.id.placeItTitle);
			placeItDescription = (EditText) findViewById(R.id.placeItDescription);
			cancelCreateButton = (Button) findViewById(R.id.cancelCreateButton);
			searchBox = (EditText) findViewById(R.id.location);
			createButton = (Button) findViewById(R.id.createPlaceItButton);
			createPlaceItLayout = (LinearLayout) findViewById(R.id.createPlaceItLayout);

			checkRepeating = (CheckBox) findViewById(R.id.checkRepeating);
			repeatingBox = (LinearLayout) findViewById(R.id.repeatingBox);
			optionRepeatDayWeek = (RadioButton) findViewById(R.id.optionRepeatDayWeek);
			optionRepeatMinutes = (RadioButton) findViewById(R.id.optionRepeatMinute);
			dayWeekBox = (LinearLayout) findViewById(R.id.repeatDayWeekBox);
			minutesBox = (LinearLayout) findViewById(R.id.repeatMinutesBox);
			textRepeatWeeks = (EditText) findViewById(R.id.repeatWeeks);
			spinnerRepeatDay = (Spinner) findViewById(R.id.repeatDay);
			textRepeatMinutes = (EditText) findViewById(R.id.repeatMinutes);

			cat1 = (Spinner) findViewById(R.id.cat1);
			cat2 = (Spinner) findViewById(R.id.cat2);
			cat3 = (Spinner) findViewById(R.id.cat3);

			debugClear = (Button) findViewById(R.id.debugClear);
		}

		public void setUpCallbacks() {
			setUpMapIfNeeded();
			setUpSearchBoxHandler();
			setUpCreatePlaceItButtons();
			setUpRepeatingBox();
		}

		public void onCheckRepeatingCheckChanged(boolean isChecked) {
			repeatingBox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
		}

		public void onOptionRepeatDayWeekCheckChanged(boolean isChecked) {
			dayWeekBox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			minutesBox.setVisibility(isChecked ? View.GONE : View.VISIBLE);
			optionRepeatMinutes.setChecked(!isChecked);
		}

		public void onOptionRepeatMinutesCheckChanged(boolean isChecked) {
			optionRepeatDayWeek.setChecked(!isChecked);
		}

		public void onDebugClearClicked() {
			ActivePlaceIts.getInstance().clear();
		}

		private void setUpRepeatingBox() {
			checkRepeating
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							onCheckRepeatingCheckChanged(isChecked);
						}
					});

			optionRepeatDayWeek
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							onOptionRepeatDayWeekCheckChanged(isChecked);
						}
					});

			optionRepeatMinutes
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							onOptionRepeatMinutesCheckChanged(isChecked);
						}
					});

			debugClear.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onDebugClearClicked();
				}
			});
		}

		private void hideCreatePlaceItLayout() {
			createPlaceItLayout.setVisibility(View.GONE);
			placeItTitle.setText("");
			placeItDescription.setText("");
			searchBox.setVisibility(View.VISIBLE);
		}

		public Marker addMarker(MarkerOptions options) {
			return mMap.addMarker(options);
		}

		public void onCancelCreateButtonClicked() {
			hideCreatePlaceItLayout();
		}

		public void onCreateButtonClicked() {
			PlaceIt placeIt;
			if (lastLocation != null) {
				// create static (non-categorized Place-It)
				placeIt = new PlaceIt(placeItTitle.getText().toString(),
						placeItDescription.getText().toString(), lastLocation);
			} else {
				placeIt = new PlaceIt(placeItTitle.getText().toString(),
						placeItDescription.getText().toString(), cat1
								.getSelectedItem().toString(), cat2
								.getSelectedItem().toString(), cat3
								.getSelectedItem().toString());
			}

			ActivePlaceIts.getInstance().add(placeIt);

			if (checkRepeating.isChecked()) {
				// set up repeating event
				PlaceItPrototype prototype = new PlaceItPrototype(
						placeItTitle.getText().toString(),
						placeItDescription.getText().toString(),
						lastLocation,
						Integer.parseInt(textRepeatWeeks.getText().toString()),
						(int) spinnerRepeatDay.getSelectedItemId(),
						Integer.parseInt(textRepeatMinutes.getText().toString()),
						optionRepeatDayWeek.isChecked() ? RepeatMode.DAY_WEEK
								: RepeatMode.MINUTES);

				RecurringPlaceIts.getInstance().add(prototype);
			}

			hideCreatePlaceItLayout();
		}

		private void setUpCreatePlaceItButtons() {
			cancelCreateButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onCancelCreateButtonClicked();
				}
			});

			createButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					onCreateButtonClicked();
				}
			});
		}

		private List<Marker> locationMarkers = new ArrayList<Marker>();
		private final int MAX_MARKERS = 5;

		public List<Marker> getLocationMarkers() {
			return locationMarkers;
		}

		private void removeMarkers() {
			for (Marker marker : locationMarkers) {
				marker.remove();
			}

			locationMarkers.clear();
		}

		/**
		 * Adds address markers to the map.
		 */
		private void addMarkers(List<Address> addresses) {
			LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
			for (int i = 0; i < Math.min(MAX_MARKERS, addresses.size()); i++) {
				LatLng location = new LatLng(addresses.get(i).getLatitude(),
						addresses.get(i).getLongitude());
				Marker marker = mMap.addMarker(new MarkerOptions().title(
						searchBox.getText().toString()).position(location));

				locationMarkers.add(marker);
				boundsBuilder.include(marker.getPosition());
			}

			LatLngBounds bounds = boundsBuilder.build();
			CameraUpdate cameraLocationUpdate = CameraUpdateFactory
					.newLatLngBounds(bounds, 5);
			try {
				mMap.animateCamera(cameraLocationUpdate);
			} catch (Exception e) {
			}
		}

		private List<Address> getAddressesFromString(String address) {
			List<Address> addresses = new ArrayList<Address>();

			try {
				addresses = (new Geocoder(MapActivity.this))
						.getFromLocationName(address, Integer.MAX_VALUE);
			} catch (IOException exc) {
				Toast.makeText(MapActivity.this, "ERROR: " + exc.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (addresses.size() == 0) {
				Toast.makeText(MapActivity.this,
						"'" + address + "' does not exist.", Toast.LENGTH_LONG)
						.show();
			}

			return addresses;
		}

		public boolean onSearchBoxEditorAction(int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				List<Address> addresses = getAddressesFromString(searchBox
						.getText().toString());

				if (addresses.size() > 0) {
					removeMarkers();
					addMarkers(addresses);
				}
			}

			return false;
		}

		private void setUpSearchBoxHandler() {
			searchBox.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView arg0, int actionId,
						KeyEvent event) {
					return onSearchBoxEditorAction(actionId, event);
				}
			});
		}

		public void onMapClick(LatLng latLng) {
			lastLocation = latLng;
			createPlaceItLayout.setVisibility(View.VISIBLE);
			searchBox.setVisibility(View.GONE);
			cat1.setVisibility(View.GONE);
			cat2.setVisibility(View.GONE);
			cat3.setVisibility(View.GONE);
		}

		public void showCatPlaceItCreationDialog() {
			lastLocation = null;
			createPlaceItLayout.setVisibility(View.VISIBLE);
			searchBox.setVisibility(View.GONE);
			cat1.setVisibility(View.VISIBLE);
			cat2.setVisibility(View.VISIBLE);
			cat3.setVisibility(View.VISIBLE);
		}

		private void setUpMapIfNeeded() {
			// if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				mMap.setMyLocationEnabled(true);
			}

			mMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng latLng) {
					UIHandlers.this.onMapClick(latLng);
				}
			});

			loadPlaceIts();
			// }
		}
	}

	public LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean gpsEn = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

		locationManager = service;

		LocationManager network = (LocationManager) getSystemService(LOCATION_SERVICE);
		boolean networkEn = service
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!gpsEn) {
			Context context = getApplicationContext();
			CharSequence text = "GPS is Disabled";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}
		if (!networkEn) {
			Context context = getApplicationContext();
			CharSequence text = "Network Not Connected";
			int duration = Toast.LENGTH_SHORT;

			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);

	}

	private void loadPlaceIts() {
		try {
			ActivePlaceIts.getInstance().load();
			PulledDownPlaceIts.getInstance().load();
			RecurringPlaceIts.getInstance().load();
		} catch (IOException e) {
			// TODO: error handling
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		PlaceIts.activity = this;

		getUIHandlers().setUpCallbacks();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();

		RecurringPlaceIts.getInstance();
		startService(new Intent(this, RecurringScheduler.class));
		ActivePlaceIts.getInstance();
		PulledDownPlaceIts.getInstance();
		startService(new Intent(this, StoreService.class));
	}

	@Override
	public void onPause() {
		super.onPause();

		try {
			ActivePlaceIts.getInstance().save();
			PulledDownPlaceIts.getInstance().save();
			RecurringPlaceIts.getInstance().save();
		} catch (IOException e) {
			// TODO: error handling
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext(), this, // ConnectionCallbacks
					this); // OnConnectionFailedListener
		}
	}

	public static Location lastLocation = null;

	private boolean distanceServiceStarted = false;

	@Override
	public void onLocationChanged(Location location) {
		/*
		 * How we can go to the center LatLng locationOfLast = new
		 * LatLng(location.getLatitude(), location.getLongitude()); CameraUpdate
		 * cameraLocationUpdate = CameraUpdateFactory.newLatLng(locationOfLast);
		 * mMap.animateCamera( cameraLocationUpdate);
		 */
		lastLocation = location;

		if (!distanceServiceStarted) {
			startService(new Intent(this, DistanceService.class));
			distanceServiceStarted = true;
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void addPlaceIt(PlaceIt placeIt) {
		if (!placeIt.isCategorizedPlaceIt()) {
			try {
				Marker marker = mMap.addMarker(new MarkerOptions()
						.title(placeIt.getTitle())
						.position(placeIt.getLatLng())
						.snippet(placeIt.getDescription())
						.icon(BitmapDescriptorFactory
								.fromResource(getResources().getIdentifier(
										"posticon", "drawable",
										getPackageName()))));

				placeIt.setMarker(marker);
			} catch (Exception exc) {
				Toast.makeText(this, "Could not create Place-It.",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	public LocationClient getLocationClient() {
		return mLocationClient;
	}

	public void removePlaceIt(PlaceIt placeIt) {
		if (placeIt.getMarker() != null)
			placeIt.getMarker().remove();
		placeIt.setMarker(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void onOptionListSelected(int id) {
		Intent intent;

		switch (id) {
		case R.id.dropDownActiveList:
			intent = new Intent(this, PlaceItsList.class);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_ACTIVE);
			startActivity(intent);
			break;
		case R.id.dropDownPulledList:
			intent = new Intent(this, PlaceItsList.class);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY, PlaceIt.PLACE_IT_PULLED);
			startActivity(intent);
			break;
		case R.id.dropDownRecurringList:
			intent = new Intent(this, PlaceItsList.class);
			intent.putExtra(PlaceIt.PLACEIT_TYPE_KEY,
					PlaceIt.PLACE_IT_PROTOTYPE);
			startActivity(intent);
			break;
		case R.id.catPlaceIt:
			getUIHandlers().showCatPlaceItCreationDialog();
			break;
		case R.id.action_logout:
			LoginActivity.logout(getApplicationContext());
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		onOptionListSelected(item.getItemId());
		return true;
	}

}
