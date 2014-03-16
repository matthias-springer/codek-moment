package edu.ucsd.cse110.team27.placeits;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceIt;
import edu.ucsd.cse110.team27.placeits.data.PlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PulledDownPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.User;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity implements PlaceItsChangeListener{

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.username);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
		
		PlaceIts.activity = this;
		if(getSharedPreferences(User.PREFS,0).getBoolean("loggedIn", false)) {		
			mUsername = getSharedPreferences(User.PREFS, 0).getString("user","");
			mPassword = getSharedPreferences(User.PREFS, 0).getString("password","");
			new UserLoginTask(getApplicationContext()).execute((Void) null);
			startActivity(new Intent(this, MapActivity.class));
		}
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = generateLoginTask();
			mAuthTask.execute((Void) null);
		}
	}
	
	public UserLoginTask generateLoginTask() {
		return new UserLoginTask(getApplicationContext());
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	
	public void addPlaceIt(PlaceIt placeIt) {}

	public void removePlaceIt(PlaceIt placeIt) {}
	
	public Context getApplicationContext() {
		return this;
	}
	
	public static void logout(Context context) {
		
		User.getCurrentUser().clear();
		context.getSharedPreferences(User.PREFS, 0).edit().putBoolean("loggedIn", false).commit();	
		try {
			ActivePlaceIts.getInstance().loadFromServer("");
			PulledDownPlaceIts.getInstance().loadFromServer("");
			RecurringPlaceIts.getInstance().loadFromServer("");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setmUsername(String uname) {
		mUsername = uname;
	}
	
	public void setmPassword(String pw) {
		mPassword = pw;
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		private Context mContext;
		private List<String> userData = new ArrayList<String>();
		
		public UserLoginTask(Context context) {
			mContext = context;
			userData.clear();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(User.DatastoreURI);
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);
				JSONObject myjson;

				try {
					myjson = new JSONObject(data);
					JSONArray array = myjson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if (mUsername.equals(obj.get("name").toString())
								&& !mPassword.equals(obj.get("password")
										.toString())) {
							return false;
						} else if(mUsername.equals(obj.get("name").toString())
								&& mPassword.equals(obj.get("password")
										.toString())) {
							userData.add(0, obj.getString("active"));
							userData.add(1, obj.getString("pulled"));
							userData.add(2, obj.getString("scheduled"));
							userData.add(3, obj.getString("categorized"));
							return true;
						}
					}

				} catch (JSONException e) {

				}

			} catch (ClientProtocolException e) {

			} catch (IOException e) {

			}
			
			
			HttpPost post = new HttpPost(User.DatastoreURI);

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
				nameValuePairs.add(new BasicNameValuePair("user", mUsername));
				nameValuePairs.add(new BasicNameValuePair("password", mPassword));
				nameValuePairs.add(new BasicNameValuePair("active", ""));
				nameValuePairs.add(new BasicNameValuePair("pulled", ""));
				nameValuePairs.add(new BasicNameValuePair("scheduled", ""));
				nameValuePairs.add(new BasicNameValuePair("categorized", ""));
				nameValuePairs.add(new BasicNameValuePair("action", "put"));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				client.execute(post);
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(mContext, "Account " + mUsername + " created", Toast.LENGTH_LONG).show();
					}
				});
			} catch (IOException e) {
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				User.getCurrentUser().setName(mUsername);
				User.getCurrentUser().setPassword(mPassword);
				if(!userData.isEmpty()) {
					User.getCurrentUser().setActive(userData.get(0));
					User.getCurrentUser().setPulled(userData.get(1));
					User.getCurrentUser().setScheduled(userData.get(2));
					User.getCurrentUser().setCategorized(userData.get(3));
				}
				getSharedPreferences(User.PREFS, 0).edit().putBoolean("loggedIn", true).commit();
				getSharedPreferences(User.PREFS, 0).edit().putString("user", mUsername).commit();
				getSharedPreferences(User.PREFS, 0).edit().putString("password", mPassword).commit();
				try {
					ActivePlaceIts.getInstance().loadFromServer(User.getCurrentUser().getActive());
					PulledDownPlaceIts.getInstance().loadFromServer(User.getCurrentUser().getPulled());
					RecurringPlaceIts.getInstance().loadFromServer(User.getCurrentUser().getScheduled());
				} catch (IOException e) {}
				Intent intent = new Intent(mContext, MapActivity.class);
				startActivity(intent);
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
}