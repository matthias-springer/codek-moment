package edu.ucsd.cse110.team27.placeits.tests;

import java.util.Date;
import java.util.Random;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.cse110.team27.placeits.LoginActivity;
import edu.ucsd.cse110.team27.placeits.LoginActivity.UserLoginTask;
import edu.ucsd.cse110.team27.placeits.MapActivity;
import edu.ucsd.cse110.team27.placeits.R;
import edu.ucsd.cse110.team27.placeits.data.ActivePlaceIts;
import edu.ucsd.cse110.team27.placeits.data.PlaceItPrototype;
import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;
import edu.ucsd.cse110.team27.placeits.data.User;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class LoginTest extends
		ActivityInstrumentationTestCase2<LoginActivity> {

	private LoginActivity mActivity;
	private UserLoginTask login;
	private Instrumentation mInstrumentation;

	private final String ValidUser = "TestUser";
	private final String ValidPassword = "thisPw";
	private final String InvalidPassword = "thisNoPw";

	public LoginTest() {
		super(LoginActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		given_LoginShown();
		LoginActivity.logout(mActivity.getApplicationContext());
	}

	@Override
	protected void tearDown() throws Exception {
		// NOTE: all manipulations of the place its lists must be done in the UI
		// threads because they trigger changes on the map automatically
		LoginActivity.logout(mActivity.getApplicationContext());
		super.tearDown();
	}
	
	private void given_userEntersValidCreds() {
		mActivity.setmUsername(ValidUser);
		mActivity.setmPassword(ValidPassword);
	}
	
	private void given_userEntersBadCreds() {
		mActivity.setmUsername(ValidUser);
		mActivity.setmPassword(InvalidPassword);
	}
	
	private void when_attemptLogin() {
		login = mActivity.generateLoginTask();
		login.execute((Void) null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void given_userLoggedIn() {
		given_userEntersValidCreds();
		when_attemptLogin();
	}
	
	private void then_UserIsLoggedIn() {
		//assertTrue(ValidUser.equals(User.getCurrentUser().getName()));
		assertTrue(ValidUser.equals(User.getCurrentUser().getName()));
	}
	

	private void given_LoginShown() {
		setActivityInitialTouchMode(false);
		mActivity = getActivity();
		mInstrumentation = getInstrumentation();
	}
	
	public void testValidLogin () {
		given_userEntersValidCreds();
		//call valid login
		when_attemptLogin();
		//check success login (same as above)
		then_UserIsLoggedIn();
	}
	
	public void testAutoLogin (){
		given_userLoggedIn();
		then_LoginScreenSkipped();
	}
	
	private void then_LoginScreenSkipped() {
		assertTrue(mActivity.getSharedPreferences(User.PREFS,0).getBoolean("loggedIn", false));
	}

	public void testBadPassword () {
		given_userEntersBadCreds();
		when_attemptLogin();
		then_notLoggedIn();
	}

	private void then_notLoggedIn() {
		assertFalse(ValidUser.equals(User.getCurrentUser().getName()));
	}
	
	
	
}