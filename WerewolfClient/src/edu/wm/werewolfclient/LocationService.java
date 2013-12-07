package edu.wm.werewolfclient;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

public class LocationService extends WakefulIntentService {

	public static final String TAG = "LOCATION_SERVICE";
	LocationManager locationManager;
	WebRequestManager manager;
	LocationListener listener;
	
	public LocationService() {
		super("LocationService");
		manager = new WebRequestManager();
	}
	
	public LocationService(String name) {
		super(name);
		manager = new WebRequestManager();
	}
	
	@Override
  	protected void doWakefulWork(Intent intent) {
		Log.v(TAG, "Check out my sweet new service!");
		manager = new WebRequestManager();
		locationManager = (LocationManager) getApplicationContext()
	            .getSystemService(Context.LOCATION_SERVICE);
	    listener = new LocationListener() {

            @Override
            public void onStatusChanged(String provider,
                    int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}

            @Override
            public void onLocationChanged(Location location) {
                // if this is a gps location, we can use it
                    doLocationUpdate(location, true);
                }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000,
                0, listener);
        int x = 0;
        while(x < 100)
			try {
				Thread.sleep(250);
				x++;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	                

	}
	private void doLocationUpdate(Location location, boolean isNew)
	{
		Log.v(TAG, "Doing a location update!");
		Log.v(TAG, "Got a new location!");
		locationManager.removeUpdates(listener);
		SharedPreferences prefs = getSharedPreferences("Stored_Data", 0);
		String username = prefs.getString("user", null);
		String password = prefs.getString("pw", null);
		if(username==null || password==null)
			return;
		HashMap<String, String> payload = new HashMap<String, String>();
		payload.put("lat", String.valueOf(location.getLatitude()));
		payload.put("lon", String.valueOf(location.getLongitude()));
		Log.v(TAG, "Starting the web call");
		manager.makeWebRequest(null, "http://secret-wildwood-3803.herokuapp.com/auth/players/location", username, password, payload, 5);
	}
}
