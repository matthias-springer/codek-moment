package edu.ucsd.cse110.team27.placeits.data.location;

import com.google.android.gms.internal.da;

public abstract class PlaceItLocationStrategy {

	static final String DELIM = "~☺;☺~";
	
	public abstract String toFileString();
	
	public abstract void load(String data);
	
	// note: Java is ugly, it does not allow adding static methods to interface!
	/**
	 * This method creates new instances of the strategy (it is a factory!) and load the data from the string.
	 * @param data Persisted data
	 * @return Strategy instance
	 */
	public static PlaceItLocationStrategy create(String data) {
		String[] splitted = data.split(DELIM);
		PlaceItLocationStrategy instance;
		
		if (splitted[0].equals("STATIC")) {
			instance = new StaticLocationStrategy();
		}
		else if (splitted[0].equals("CATEGORY")) {
			instance = new CategoryLocationStrategy();
		}
		else {
			throw new RuntimeException("Strategy type " + splitted[0] + " not known to factory.");
		}
		
		instance.load(data);
		return instance;
	}
	
}
