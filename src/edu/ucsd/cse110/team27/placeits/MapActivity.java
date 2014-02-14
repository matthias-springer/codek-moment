package edu.ucsd.cse110.team27.placeits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
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

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MapActivity extends FragmentActivity implements
		ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

	private GoogleMap mMap;

	private LocationClient mLocationClient;

	private UIHandlers uiHandlers;
	
	private static final LocationRequest REQUEST = LocationRequest.create()
			.setInterval(1000) // 1s
			.setFastestInterval(16) // 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private UIHandlers getUIHandlers() {
		// NOTE: cannot move Singleton creation to the class itself due to Java syntax restrictions
		
		if (uiHandlers == null) {
			uiHandlers = new UIHandlers();
		}
		
		return uiHandlers;
	}
	
	private class UIHandlers {
		private final EditText placeItTitle;
		private final EditText placeItDescription;
		private final Button cancelCreateButton;
		private final EditText searchBox;
		private final Button createButton;
		private final LinearLayout createPlaceItLayout;

		private LatLng lastLocation;

		public UIHandlers() {
			placeItTitle = (EditText) findViewById(R.id.placeItTitle);
			placeItDescription = (EditText) findViewById(R.id.placeItDescription);
			cancelCreateButton = (Button) findViewById(R.id.cancelCreateButton);
			searchBox = (EditText) findViewById(R.id.location);
			createButton = (Button) findViewById(R.id.createPlaceItButton);
			createPlaceItLayout = (LinearLayout) findViewById(R.id.createPlaceItLayout);
		}

		public void setUpCallbacks() {
			setUpMapIfNeeded();
			setUpSearchBoxHandler();
			setUpCreatePlaceItButtons();
		}

		private void hideCreatePlaceItLayout() {
			createPlaceItLayout.setVisibility(View.GONE);
			placeItTitle.setText("");
			placeItDescription.setText("");
			searchBox.setVisibility(View.VISIBLE);
		}

		private void setUpCreatePlaceItButtons() {
			cancelCreateButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					hideCreatePlaceItLayout();
				}
			});

			createButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					ActivePlaceIts.getInstance(MapActivity.this).add(
							new PlaceIt(placeItTitle.getText().toString(),
									placeItDescription.getText().toString(),
									lastLocation));

					hideCreatePlaceItLayout();
				}
			});
		}

		private void setUpSearchBoxHandler() {
			searchBox.setOnEditorActionListener(new OnEditorActionListener() {

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
						LatLng location = new LatLng(addresses.get(i)
								.getLatitude(), addresses.get(i).getLongitude());
						Marker marker = mMap.addMarker(new MarkerOptions()
								.title(searchBox.getText().toString())
								.position(location));

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
						Toast.makeText(MapActivity.this,
								"ERROR: " + exc.getMessage(), Toast.LENGTH_LONG)
								.show();
					}

					if (addresses.size() == 0) {
						Toast.makeText(MapActivity.this,
								"'" + address + "' does not exist.",
								Toast.LENGTH_LONG).show();
					}

					return addresses;
				}

				@Override
				public boolean onEditorAction(TextView arg0, int actionId,
						KeyEvent event) {
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

			});
		}

		private void setUpMapIfNeeded() {
			if (mMap == null) {
				mMap = ((SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map)).getMap();
				if (mMap != null) {
					mMap.setMyLocationEnabled(true);
				}

				mMap.setOnMapClickListener(new OnMapClickListener() {

					@Override
					public void onMapClick(LatLng latLng) {
						lastLocation = latLng;
						createPlaceItLayout.setVisibility(View.VISIBLE);
						searchBox.setVisibility(View.GONE);
					}
				});

				loadPlaceIts();
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEn = service
          .isProviderEnabled(LocationManager.GPS_PROVIDER);
        
        LocationManager network  = (LocationManager) getSystemService(LOCATION_SERVICE);
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
		ActivePlaceIts.getInstance(this).load();
		PulledDownPlaceIts.getInstance(this).load();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		getUIHandlers().setUpCallbacks();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
		
		ActivePlaceIts.getInstance(this);
		startService(new Intent(this, distanceService.class));
	}

	@Override
	public void onPause() {
		super.onPause();
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

		ActivePlaceIts.getInstance(this).save();
		PulledDownPlaceIts.getInstance(this).save();
	}

	public void addPlaceIt(PlaceIt placeIt) {
		try {
			Marker marker = mMap.addMarker(new MarkerOptions()
					.title(placeIt.getTitle())
					.position(placeIt.getLocation())
					.snippet(placeIt.getDescription())
					.icon(BitmapDescriptorFactory.fromResource(getResources().getIdentifier("posticon", "drawable", getPackageName()))));

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.map_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch(item.getItemId()) {
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
		}
		
		return true;
	}

}
