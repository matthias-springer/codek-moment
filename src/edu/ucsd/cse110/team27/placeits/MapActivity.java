package edu.ucsd.cse110.team27.placeits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.FileManager;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;


public class MapActivity extends FragmentActivity
        implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener{

    private GoogleMap mMap;

    private LocationClient mLocationClient;
    
    private FileManager fileManager;
    
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(1000)         // 1s
            .setFastestInterval(16)    // 60fps
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    private LatLng lastLocation;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        
        setupCallbacks();
        
        fileManager = new FileManager(getApplicationContext());
        /* Mattias - fileManager.loadPlaceIts returns a list of all active placeits in persistent memory
         *  - please link this to your data structure somehow
         * try {
         
			fileManager.loadPlaceits(PlaceItType.ACTIVE);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
    }

    private void setupCallbacks() {
        final EditText searchBox = (EditText) findViewById(R.id.location);
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
    	    		LatLng location = new LatLng(addresses.get(i).getLatitude(), addresses.get(i).getLongitude());
    	    		Marker marker = mMap.addMarker(new MarkerOptions()
	    				.title(searchBox.getText().toString())
	    				.position(location));
    	    		
    	    		locationMarkers.add(marker);
    	    		boundsBuilder.include(marker.getPosition());	
    	    	}
    	    	
    	    	LatLngBounds bounds = boundsBuilder.build();
    	    	CameraUpdate cameraLocationUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 5);
    	    	mMap.animateCamera(cameraLocationUpdate);	
        	}
        	
        	private List<Address> getAddressesFromString(String address) {
    			List<Address> addresses = new ArrayList<Address>();
    			
    			try {
    				addresses = (new Geocoder(MapActivity.this)).getFromLocationName(address, Integer.MAX_VALUE);
    			} catch (IOException exc) {
    				Toast.makeText(MapActivity.this, "ERROR: " + exc.getMessage(), Toast.LENGTH_LONG).show();
    			}
    			
    			if (addresses.size() == 0) {
    				Toast.makeText(MapActivity.this, "'" + address + "' does not exist.", Toast.LENGTH_LONG).show();
    			}
    			
    			return addresses;
        	}
        	
        	@Override
			public boolean onEditorAction(TextView arg0, int actionId, KeyEvent event) {
        		if (actionId == EditorInfo.IME_ACTION_SEARCH) {
        	    	List<Address> addresses = getAddressesFromString(searchBox.getText().toString());
        			
        	    	if (addresses.size() > 0) {
        	    		removeMarkers();
        	    		addMarkers(addresses);
        	    	}
        	    }
        		
				return false;
			}
			
        });
        
        final EditText placeItTitle = (EditText) findViewById(R.id.placeItTitle);
        final EditText placeItDescription = (EditText) findViewById(R.id.placeItDescription);
        
        final Button cancelCreateButton = (Button) findViewById(R.id.cancelCreateButton);
        cancelCreateButton.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View arg0) {
				hideCreatePlaceItLayout();
			}
		});
        
        final Button createButton = (Button) findViewById(R.id.createPlaceItButton);
        createButton.setOnClickListener(new OnClickListener() {
        	
			@Override
			public void onClick(View arg0) {
				hideCreatePlaceItLayout();
				
				ActivePlaceIts.getInstance(MapActivity.this).add(new PlaceIt(
						placeItTitle.getText().toString(),
						placeItDescription.getText().toString(),
						lastLocation));
			}
		});
    }
    
    private void hideCreatePlaceItLayout() {
		LinearLayout createPlaceItLayout = (LinearLayout) findViewById(R.id.createPlaceItLayout);
		createPlaceItLayout.setVisibility(View.GONE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
    
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
            
            mMap.setOnMapClickListener(new OnMapClickListener() {
    			
    			@Override
    			public void onMapClick(LatLng latLng) {
    				lastLocation = latLng;
    				LinearLayout createPlaceItLayout = (LinearLayout) findViewById(R.id.createPlaceItLayout);
    				createPlaceItLayout.setVisibility(View.VISIBLE);
    			}
    		});
        }
    }

    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    	/* How we can go to the center
    	LatLng locationOfLast = new LatLng(location.getLatitude(), location.getLongitude());
    	CameraUpdate cameraLocationUpdate = CameraUpdateFactory.newLatLng(locationOfLast);
    	mMap.animateCamera( cameraLocationUpdate);*/
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLocationClient.requestLocationUpdates(
                REQUEST,
                this);  
    }

    @Override
    public void onDisconnected() {
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
    }
    
    @Override
    public void onStop() {
    	/* Mattias - please this takes in a List of placeits and writes them to persistent storage,
    		- please link this to your data structures
    	fileManager.savePlaceits(PlaceItList, PlaceItType.ACTIVE)*/
    }

    public void addPlaceIt(PlaceIt placeIt) {
    	// TODO: think about this
		Marker marker = mMap.addMarker(new MarkerOptions()
			.title(placeIt.getTitle())
			.position(placeIt.getLocation()));
    }
}
