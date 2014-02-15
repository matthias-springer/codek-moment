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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
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

public class MapActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

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
			ActivePlaceIts.getInstance().add(
					new PlaceIt(placeItTitle.getText().toString(),
							placeItDescription.getText().toString(),
							lastLocation));

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

		private void removeMarkers() {
			for (Marker marker : locationMarkers) {
				marker.remove();
			}

			locationMarkers.clear();
		}

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
			mMap.animateCamera(cameraLocationUpdate);
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
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

	@Override
	public void onLocationChanged(Location location) {
		/*
		 * How we can go to the center LatLng locationOfLast = new
		 * LatLng(location.getLatitude(), location.getLongitude()); CameraUpdate
		 * cameraLocationUpdate = CameraUpdateFactory.newLatLng(locationOfLast);
		 * mMap.animateCamera( cameraLocationUpdate);
		 */
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
		try {
			Marker marker = mMap.addMarker(new MarkerOptions()
					.title(placeIt.getTitle())
					.position(placeIt.getLocation())
					.snippet(placeIt.getDescription())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

			placeIt.setMarker(marker);
		} catch (Exception exc) {
			Toast.makeText(this, "Could not create Place-It.",
					Toast.LENGTH_LONG).show();
		}
	}

	public void removePlaceIt(PlaceIt placeIt) {
		placeIt.getMarker().remove();
		placeIt.setMarker(null);
	}

}
