package com.dtf.hellobeacon.util;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MoniteringTask extends AsyncTask<BeaconManager, Void, Void> {

	protected final String beaconName = "estimote";
	
	protected Context mContext;
	protected BeaconManager bMan;
	protected SharedPreferences prefs;
	private Boolean hasEntered = false;
	private Boolean hasRetrievedVisits = false;
	long visits;
	
	/**
	 * Constructor - context for getting reference to activity creating this RangingTask, and beaconmanager
	 * to set rangingListener for
	 * @param context
	 * @param bMan
	 */
	public MoniteringTask(Context context, BeaconManager bMan)
	{
		mContext = context;
		this.bMan = bMan;
	}
	
	
	@Override
	protected Void doInBackground(BeaconManager... beaconManager) {
		int count = beaconManager.length;
		BeaconManager b;
		return null;
	}
	
	/**
	 * Run before doInBackground - set ranging listener for beacon manager here, because
	 * preExecute is run on ui thread
	 */
	@Override
	protected void onPreExecute() {

		bMan.setMonitoringListener(new BeaconManager.MonitoringListener() {
			
			@Override
			public void onExitedRegion(Region exitRegion) {
				// TODO Auto-generated method stub
				userLeavesGym();
				
			}
			
			@Override
			public void onEnteredRegion(Region enterRegion, List<Beacon> rangedBeacons) {
				for (Beacon rangedBeacon : rangedBeacons) {
					Log.d("tracking", "tracking beacons mang");
					Log.d("tracking", "beaconName is " + rangedBeacon.getName());
					addVisitToUser();
				}				
			}
		});
		
	}
	
	/**
	 * increment visit value to firebase if 
	 * 		- visit value has been retrieved from Database AND IF
	 * 		- user has not already entered the gym 
	 * TODO - find better way of telling if user has entered and exited the gym
	 */
	public void addVisitToUser()
	{

		if(mContext != null)
		{
			Log.d("context", "tracking beacons mang - context not null");
			prefs = mContext.getSharedPreferences("com.dtf.hellobeacon", 0);
			String firstname = prefs.getString("firstName", "nobody");
			String lastname = prefs.getString("lastName", "nobody");

			Firebase newpushref = new Firebase("https://hellobeacon.firebaseio.com/" + firstname + lastname + "/visits");

			//get current visit value
			newpushref.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot snapshot) {
					if(snapshot.getValue() != null){
						visits = (Long) snapshot.getValue();
						hasRetrievedVisits = true;
						}
					}
					
				
				@Override
				public void onCancelled(FirebaseError error) {
					System.err.println("Listener was cancelled");
				}
				
				
			});
			if(hasRetrievedVisits && !hasEntered)
			{
				newpushref.setValue(visits + 1);
				hasEntered = true;
			}
		}

	}
	
	public void userLeavesGym() {
		hasEntered = false;
	}
	
}
