package uw.cse.dineon.user.checkin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.parse.ParseUser;
import com.parse.PushService;

import uw.cse.dineon.library.DineOnReceiver;
import uw.cse.dineon.user.DineOnUserActivity;
import uw.cse.dineon.user.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 * TODO
 * @author mhotan
 */
public class CheckInActivity extends DineOnUserActivity 
implements CheckInFragment.CheckInListener{
	
	public static final String EXTRA_CHECKEDIN = "is checked in?";
	
	private boolean mCheckedIn;

	private final static String TAG = CheckInActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
		mCheckedIn = false;
		
		// Set up the broadcast receiver for push notifications
		Map<String, String> m = new HashMap<String, String>();
		DineOnReceiver rec = createDineOnRecevier(
				this.getClass().getMethod("onDiningSessionConfirmation", m.getClass()), 
				this.findViewById(R.layout.activity_checkin).getContext(), 
				this, 
				"uw.cse.dineon.user.CONFIRM_DINING_SESSION", 
				"uw.cse.dineon.user." + ParseUser.getCurrentUser().getUsername());
		IntentFilter iff = new IntentFilter("uw.cse.dineon.user.CONFIRM_DINING_SESSION");
		PushService.subscribe(this, "push", CheckInActivity.class);
		this.registerReceiver(rec, iff);
	}
	
	public void onDiningSessionConfirmation(Map<String, String> attr) {
		
	}

	@Override
	public void onCheckInSuccess() {
		// TODO Auto-generated method stub
		mCheckedIn = true;
		
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
		
		//finish();
	}

	@Override
	public void onCheckInFail() {
		// TODO Auto-generated method stub
		mCheckedIn = false;
	}
	
	@Override
	public void finish(){
		Intent retIntent = new Intent();
		retIntent.putExtra(EXTRA_CHECKEDIN, mCheckedIn);
		super.finish();
		// Send the results back with an intent
	}
	
	@Override
	public void onBackPressed() {
		Log.d(TAG, "Check In Activity back pressed");
		super.onBackPressed();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	  IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
	  if (scanResult != null) {
		  // handle scan result
		  String contents = scanResult.getContents();
		  Log.d("ZXing", contents);
	  } else {
		  // else continue with any other code you need in the method
		  Log.d("ZXing", "Error getting the result");
	  }
	}
}
