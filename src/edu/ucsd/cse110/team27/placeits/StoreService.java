package edu.ucsd.cse110.team27.placeits;

import java.io.BufferedReader;

import android.util.Log;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.google.android.gms.maps.MapView;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import edu.ucsd.cse110.team27.placeits.LoginActivity.UserLoginTask;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.PlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.User;
import edu.ucsd.cse110.team27.placeits.data.location.PlaceItLocationStrategy;

public class StoreService extends Service {

	public Runnable post;
	public Runnable pushRun;
	public Handler pushHandler = new Handler();
	public static final String TAG = "StoreService";

	public static final String DELIM = "~;89aj29348;~";

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

	private String ALIST; 
	private String PLIST; 
	private String SLIST; 
	private String CLIST;

	private String ourA;
	private String ourP;
	private String ourS;
	private String ourC;

	PlaceIts placeits;
	PlaceIt placeit;
	PlaceItPrototype prototype;

	private String[] updates;
	protected URI[] url;
	private String[] inters;

	MapActivity.UIHandlers mapui;
	MapActivity map;

	boolean serverConnection = false;


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

				// transform all my lists to String representation. 

				//				new getListTask(null).execute(User.DatastoreURI);
				new getListTask().execute(User.DatastoreURI);

				Log.d("service", "handler");
				pushHandler.postDelayed(pushRun, 15000); // run every 10 sec
			}
		};
	}



	@Override
	public void onStart(Intent intent, int startid) {

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

	

	//compare the unique ids before posting
	public class getListTask extends AsyncTask<String, Void, List<String>>{
		private Context mContext;

		private List<String> userData = new ArrayList<String>();


		

		@Override
		protected List<String> doInBackground(String... url) {
			// TODO Auto-generated method stub
			
//			postList();
			
			Log.d("doin background", " StoreService");

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

				serverConnection = true;

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
					e.printStackTrace();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

//			
			postList();

			return userData;
		}	
		
		
		protected void onPostExecute(List<String> listofList) {
			
			if(listofList.isEmpty())
				return;

			Log.d("post", "EXECUTE");
			//		activeList = ActivePlaceIts.getInstance().getList();
			//		pulledList = ActivePlaceIts.getInstance().getList();
			//		recurringList = ActivePlaceIts.getInstance().getList();
			//		categoryList = ActivePlaceIts.getInstance().getList();

			// all we have to do is set the parameter(servers list) to our current list.
			ourA = listofList.get(0); // string of active list
			ourP = listofList.get(1); // string of pulled
			ourS = listofList.get(2); // string of scheduled
			ourC = listofList.get(3);


			User.getCurrentUser().setActive(listofList.get(0));
			User.getCurrentUser().setPulled(listofList.get(1));
			User.getCurrentUser().setScheduled(listofList.get(2));
			User.getCurrentUser().setCategorized(listofList.get(3));


			try {
				
				Log.d("loadfromserver", "in the postexecute");
				ActivePlaceIts.getInstance().loadFromServer(User.getCurrentUser().getActive());
				PulledDownPlaceIts.getInstance().loadFromServer(User.getCurrentUser().getPulled());
				RecurringPlaceIts.getInstance().loadFromServer(User.getCurrentUser().getScheduled());
			} catch (IOException e) {
				
				Log.d("postExecute", "while trying to create new list");
			}

			
		}
	}

	public void postList(){
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(User.DatastoreURI);
		User user = User.getCurrentUser();
		try{
			
			Log.d("post to server ", "Store servic");

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			nameValuePairs.add(new BasicNameValuePair("user", user.getName()));
			nameValuePairs.add(new BasicNameValuePair("active", ALIST));
			nameValuePairs.add(new BasicNameValuePair("scheduled", SLIST));
			nameValuePairs.add(new BasicNameValuePair("categorized", CLIST));
			nameValuePairs.add(new BasicNameValuePair("pulled", PLIST));
			nameValuePairs.add(new BasicNameValuePair("password", user.getPassword()));

			nameValuePairs.add(new BasicNameValuePair("action","put"));

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = client.execute(post);

		}
		catch(IOException e){

			Log.d(TAG, "IOException while trying to post place it Lists");
		}
	}


	// for method 2
	public void reload() {

		Intent intent = new Intent(getApplicationContext(), MapActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		map.overridePendingTransition(0, 0);

		map.finish();

		map.overridePendingTransition(0, 0);
		startActivity(intent);
	}





	// should return union between 2 lists 
	protected String compareList(String[] serverList, String[] myList){

		String updateList = "";

		Set<String> union = new HashSet<String>();

		if(serverList == null){
			return null;
		}

		//		union.addAll(Arrays.asList(serverList));
		//		union.addAll(Arrays.asList(myList));

		for(int i = 0; i < myList.length; i++){
			union.add(myList[i]);
		}
		for(int j = 0; j < serverList.length; j++){
			union.add(serverList[j]);
		}

		String[] updates = union.toArray(new String[union.size()]);

		//		updates = (String[]) union.toArray(); suppose to be same as above.

		for(int k = 0; k < updates.length; k++){
			updateList = updateList + updates[k].toString()	+ DELIM;

		}

		return updateList;
	}


	// find the intersectin of the lists
	protected String intersectList(String[] serverList, String[] myList){
		String intersect = "";

		Set<String> server = new HashSet<String>(Arrays.asList(serverList));
		Set<String> my = new HashSet<String>(Arrays.asList(myList));
		server.retainAll(my);

		for(int k = 0; k < updates.length; k++){

		}

		String[] inter = server.toArray(new String[server.size()]);

		//		inters = (String[]) server.toArray(); same thing as above?

		for(int k = 0; k < inter.length; k++){
			intersect = intersect + updates[k].toString()	+ DELIM;

		}

		return intersect;
	}

	public void onDestroy(){
		super.onDestroy();

	}

	public void onPause(){
		stopService(new Intent(this, StoreService.class));
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}
