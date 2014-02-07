package edu.ucsd.cse110.team27.placeits.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;

public class FileManager {
	private static final String ACTIVE_FILE_NAME = "ActivePlaceits.txt";
	private static final String PULLED_DOWN_FILE_NAME = "PulledDownPlaceits.txt";
	private static final String DELIM = "~;~";
	private static final String NL = System.getProperty("line.separator");
	private OutputStreamWriter fileOut;
	private BufferedReader fileIn;
	private Context appContext;
	
	public FileManager(Context context) {
		this.appContext = context;
	}
	
	public void savePlaceits(List<PlaceIt> placeits, PlaceItType type) throws IOException {
		switch(type) {
			case ACTIVE:
				fileOut = new OutputStreamWriter(appContext.openFileOutput(ACTIVE_FILE_NAME, Context.MODE_PRIVATE));
				break;
			case PULLED_DOWN:
				fileOut = new OutputStreamWriter(appContext.openFileOutput(PULLED_DOWN_FILE_NAME, Context.MODE_PRIVATE));
				break;
		}
		
		for(PlaceIt placeit : placeits) {
			String placeItData = 
					placeit.getTitle() + DELIM + placeit.getDescription() 
					+ DELIM + placeit.getLocation().getLatitude() + DELIM +
					placeit.getLocation().getLongitude() + NL;
			fileOut.write(placeItData);
		}
		
		fileOut.close();
	}
	
	public List<PlaceIt> loadPlaceits(PlaceItType type) throws IOException{ 	
		switch(type) {
			case ACTIVE:
				fileIn = new BufferedReader(new InputStreamReader (appContext.openFileInput(ACTIVE_FILE_NAME)));
				break;
			case PULLED_DOWN:
				fileIn = new BufferedReader(new InputStreamReader (appContext.openFileInput(PULLED_DOWN_FILE_NAME)));
				break;
		}
		
		List<PlaceIt> placeits = new ArrayList<PlaceIt>();
		
		String line = "";
		while ((line = fileIn.readLine()) != null) {
			String [] placeitData = line.split(DELIM);
			Location location = new Location("FileManager");
			location.setLatitude(Double.parseDouble(placeitData[2]));
			location.setLongitude(Double.parseDouble(placeitData[3]));
			placeits.add(new PlaceIt(placeitData[0],placeitData[1],location));
		}
		
		fileIn.close();
		return placeits;
	}
	

}
