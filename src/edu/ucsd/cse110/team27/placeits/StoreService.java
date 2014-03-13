package edu.ucsd.cse110.team27.placeits;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.PlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.User;
import edu.ucsd.cse110.team27.placeits.data.location.PlaceItLocationStrategy;

public class StoreService extends Service {

	private Runnable pushRun;
	private Handler pushHandler = new Handler();
	public static final String TAG = "StoreService";

	protected static final String DELIM = "~�����;�����~";

	protected PlaceItLocationStrategy locationStrategy;

	private User user;

	private List<PlaceIt> activeList;
	private List<PlaceIt> pulledList;
	private List<PlaceItPrototype> recurringList;
	private List<PlaceIt> categoryList;

	private List<PlaceIt> NactiveList;
	private List<PlaceIt> NpulledList;
	private List<PlaceItPrototype> NrecurringList;
	private List<PlaceIt> NcategoryList;

	// check it the list was updated. 
	//				NactiveList = ActivePlaceIts.getInstance().getList();
	//				NpulledList = PulledDownPlaceIts.getInstance().getList();
	//				NrecurringList = RecurringPlaceIts.getInstance().getList();
	//				NcategoryList = ActivePlaceIts.getInstance().getList();

	private String ALIST; 
	private String PLIST; 
	private String SLIST; 
	private String CLIST;

	PlaceIts placeits;
	PlaceIt placeit;
	PlaceItPrototype prototype;

	private String[] updates;
	protected URI[] url;


	public void onCreate(){		
		pushRun = new Runnable(){			
			@Override
			public void run(){


				activeList = ActivePlaceIts.getInstance().getList();
				pulledList = PulledDownPlaceIts.getInstance().getList();
				recurringList = RecurringPlaceIts.getInstance().getList();
				categoryList = ActivePlaceIts.getInstance().getList();

				ALIST = stringPlaceits(activeList); 
				PLIST = stringPlaceits(pulledList); 
				SLIST = stringProPlaceits(recurringList); 
				CLIST = stringPlaceits(activeList);


				// if there was no changes in our list
				if( (activeList.equals(NactiveList) && pulledList.equals(NpulledList) && recurringList.equals(NrecurringList))){
					// PULL DATA FROM SERVER ONLY
				}
				// if changes were made push to server
				else{
				}


				// transform all my lists to String representation. 

				new getListTask().execute(User.DatastoreURI);

				pushHandler.postDelayed(pushRun, 10000); // run every 10 sec
			}
		};

		// update the list. 
		//		activeList = NactiveList;
		//		pulledList = NpulledList;
		//		recurringList = NrecurringList;

	}

	@Override
	public void onStart(Intent intent, int startid) {

		// inintialize all my list
		//		activeList = ActivePlaceIts.getInstance().getList();
		//		pulledList = PulledDownPlaceIts.getInstance().getList();
		//		recurringList = RecurringPlaceIts.getInstance().getList();
		//		categoryList = ActivePlaceIts.getInstance().getList();

		//		postList();
		pushRun.run();
	}

	// turns the List<PlaceIt> to string representation.
	public String stringPlaceits(List<PlaceIt> list){

		String pstring = "";

		for(int i = 0; i < list.size(); i++){
			pstring = pstring + list.get(i).toFileString() + DELIM;
		}

		return pstring; 
	}

	public String stringProPlaceits(List<PlaceItPrototype> list){

		String lstring = "";

		for(int i = 0; i < list.size(); i++){
			lstring = lstring + list.get(i).toFileString() + DELIM;
		}

		return lstring;
	}


	// this converts and returns type List<PlaceIt> of server String representation of list. 
	public List<PlaceIt> load(String line) {

		PlaceIt place = null;
		List<PlaceIt> serverList = new ArrayList<PlaceIt>();

		while (line != null) {
			String[] partition = line.split(DELIM);
			for(int i = 0; i < partition.length; i++){
				place = (PlaceIt) place.load(partition[i]);
				serverList.add(place);
			}
		}
		return serverList; 
	}

	public List<PlaceItPrototype> loadpro(String line){
		List<PlaceItPrototype> list = new ArrayList<PlaceItPrototype>();
		PlaceItPrototype place = null;

		while (line != null) {
			String[] partition = line.split(DELIM);
			for(int i = 0; i < partition.length; i++){
				place =  (PlaceItPrototype) place.load(partition[i]);
				list.add(place);
			}
		}

		return list;
	}

	// take the active and pulled string from server and part it to string array
	public String[] stringToArray(String list){
		String[] partition = {};

		while (list != null) {
			partition = list.split(DELIM);
		}
		return partition;
	}

	// take the current list and create string array
	public String[] objectToArray(List<PlaceIt> list){
		String[] partition1 = {};

		while (list != null) {
			for(int i = 0; i < list.size(); i++){
				partition1[i] = list.get(i).toString();
			}
		}	
		return partition1;
	}

	// take the recurring place it from server and part it to string array. 
	public String[] ProtoToArray(List<PlaceItPrototype> list){
		String[] partition1 = {};

		while (list != null) {
			for(int i = 0; i < list.size(); i++){
				partition1[i] = list.get(i).toString();
			}
		}	
		return partition1;
	}


	//compare the unique ids before posting
	private class getListTask extends AsyncTask<String, Void, List<String>>{

		@Override
		protected List<String> doInBackground(String... url) {
			// TODO Auto-generated method stub

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(User.DatastoreURI);
			User user = User.getCurrentUser();
			String username = user.getName();
			List<String> userData = new ArrayList<String>();


			try{
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);
				Log.d(TAG, data);
				JSONObject myjson;
				try{
					myjson = new JSONObject(data);
					JSONArray array = myjson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if(username.equals(obj.get("name").toString())){
							userData.add(0, obj.getString("active"));
							userData.add(1, obj.getString("pulled"));
							userData.add(2, obj.getString("scheduled"));
							userData.add(3, obj.getString("categorized"));
						}
					}
				}
				catch (JSONException e) {

				}

			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}



			postList();




			return userData;
		}	
	}


	private void postList(){

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(User.DatastoreURI);
		User user = User.getCurrentUser();
		try{

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("user", user.getName()));
			nameValuePairs.add(new BasicNameValuePair("active", ALIST));
			nameValuePairs.add(new BasicNameValuePair("scheduled", SLIST));
			nameValuePairs.add(new BasicNameValuePair("categorized", CLIST));
			nameValuePairs.add(new BasicNameValuePair("pulled", PLIST));
			nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));

			nameValuePairs.add(new BasicNameValuePair("action","put"));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			client.execute(post);

		}
		catch(IOException e){

			Log.d(TAG, "IOException while trying to post place it Lists");
		}

	}


	protected void onPostExecute(List<String> listofList) {

		//		activeList = ActivePlaceIts.getInstance().getList();
		//		pulledList = ActivePlaceIts.getInstance().getList();
		//		recurringList = ActivePlaceIts.getInstance().getList();
		//		categoryList = ActivePlaceIts.getInstance().getList();

		// new instance of our current list
		NactiveList = ActivePlaceIts.getInstance().getList();
		NpulledList = PulledDownPlaceIts.getInstance().getList();
		NrecurringList = RecurringPlaceIts.getInstance().getList();
		//		NcategoryList = ActivePlaceIts.getInstance().getList();

		String[] aLIST = stringToArray(listofList.get(0));
		String[] pLIST = stringToArray(listofList.get(1));
		String[] sLIST = stringToArray(listofList.get(2));
		String[] cLIST = stringToArray(listofList.get(3));

		String[] currentAlist = objectToArray(NactiveList);
		String[] currentPlist = objectToArray(NpulledList);
		String[] currentSlist = ProtoToArray(NrecurringList);
		String[] currentClist = objectToArray(NcategoryList);

		ALIST = compareList(aLIST, currentAlist); // returns a string
		PLIST = compareList(pLIST, currentPlist);
		SLIST = compareList(sLIST, currentSlist);
		CLIST = compareList(cLIST, currentClist);

//		ActivePlaceIts.getInstance().clear();
//		PulledDownPlaceIts.getInstance().clear();
//		RecurringPlaceIts.getInstance().clear();
		//		NcategoryList = ActivePlaceIts.getInstance().getList();
//
//		NactiveList = load(ALIST);
//		NpulledList = load(PLIST);
//		NrecurringList = loadpro(SLIST);
//		NcategoryList = load(CLIST);


	}

	// should return union 
	protected String compareList(String[] serverList, String[] myList){

		String updateList = "";

		Set<String> union = new HashSet<String>();

		if(serverList == null){
			return null;
		}

		for(int i = 0; i < myList.length; i++){
			union.add(myList[i]);
		}
		for(int j = 0; j < serverList.length; j++){
			union.add(serverList[j]);
		}
		updates = (String[]) union.toArray();

		for(int k = 0; k < updates.length; k++){
			updateList = updateList + updates[k].toString()	+ DELIM;

		}
		Toast toast = Toast.makeText(getApplicationContext(), updateList, Toast.LENGTH_SHORT);
		toast.show();
		return updateList;
	}



	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
