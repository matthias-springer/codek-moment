package edu.ucsd.cse110.team27.placeits;

import edu.ucsd.cse110.team27.placeits.data.RecurringPlaceIts;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

public class RecurringScheduler extends Service {

	private Handler handler = new Handler();
	private Runnable recurringLoop;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		recurringLoop = new Runnable() {

			@Override
			public void run() {
				RecurringPlaceIts.getInstance(null).createPlaceIts();
				handler.postDelayed(this, 2000);
			}

		};
	}

	@Override
	public void onDestroy() {
		handler.removeCallbacks(recurringLoop);
	}

	@Override
	public void onStart(Intent intent, int startid) {
		recurringLoop.run();
	}

}
