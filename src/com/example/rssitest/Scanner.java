package com.example.rssitest;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;
import com.example.rssitest.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.os.AsyncTask;
import android.widget.TextView;

public class Scanner extends AsyncTask<Void, Void, Boolean> {

	// WiFi variables
	public int _scansRemaining;
    private MainActivity context;
	WifiManager mWifiManager;
	WifiLock wifiLock;
	List<ScanResult> wirelessResults;
	boolean waiting;
	
	// Location variables
	private List<Location> locationList;
	Location currentLocation = new Location();

    public Scanner(MainActivity c) {
    	context = c;
    }

   	// Mark a location (x,y) based on current signal reading
    public Location createLocation(int x, int y) {
    	Location sampleLoc = new Location(x,y);
    	sampleLoc.setMAC1Name(context.getString(R.string.MAC1));
		sampleLoc.setMAC2Name(context.getString(R.string.MAC2));
		sampleLoc.setMAC3Name(context.getString(R.string.MAC3));
    	sampleLoc.setMAC1Range(currentLocation.getMinLevel1(), currentLocation.getMaxLevel1());
    	sampleLoc.setMAC2Range(currentLocation.getMinLevel2(), currentLocation.getMaxLevel2());
    	sampleLoc.setMAC3Range(currentLocation.getMinLevel3(), currentLocation.getMaxLevel3());
    	
    	return sampleLoc;
    }
    
    // Configure the WiFi scanning service to run
    public void setupScanner(ArrayList<Location> locationList) {
   
    	this.locationList = locationList;

    	currentLocation.setMAC1Name(context.getString(R.string.MAC1));
		currentLocation.setMAC2Name(context.getString(R.string.MAC2));
		currentLocation.setMAC3Name(context.getString(R.string.MAC3));
  
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiLock = mWifiManager.createWifiLock(WifiManager.WIFI_MODE_SCAN_ONLY , "MyWifiLock");
		if(!wifiLock.isHeld()){
		    wifiLock.acquire();
		}

		IntentFilter i = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		
		context.registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context c, Intent i){
				// Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
				
				wirelessResults =  mWifiManager.getScanResults(); // Returns a <list> of scanResults
				waiting = false;
				
				publishProgress();
			}
		}
		,i);
    }

	
    // Each time a WiFi scan posts progress, process the results for the specified APs
	@Override
    protected void onProgressUpdate(Void... values) {
		TextView rtxt = (TextView) context.findViewById(R.id.readings_txt);
		Iterator<ScanResult> it = wirelessResults.iterator();

		// Iterate over found APs, set the level for those APs we're interested in
		while(it.hasNext()) {
			ScanResult next = it.next();
			if(next.BSSID.equals(context.getString(R.string.MAC1))) {
				currentLocation.setMAC1Level(next.level);
			}
			if(next.BSSID.equals(context.getString(R.string.MAC2))) {
				currentLocation.setMAC2Level(next.level);
			}
			if(next.BSSID.equals(context.getString(R.string.MAC3))) {
				currentLocation.setMAC3Level(next.level);
			}
		}
		
		// Re-scan as there are more remaining
		if(_scansRemaining > 0) {
			rtxt.setText("MAC1 max: "+currentLocation.maxLevel1 + "  MAC1 min: "+currentLocation.minLevel1 + 
					"\nMAC2 max: "+currentLocation.maxLevel2 + "  MAC2 min: "+currentLocation.minLevel2 + 
					"\nMAC3 max: "+currentLocation.maxLevel3 + "  MAC3 min: "+currentLocation.minLevel3);
			_scansRemaining--;
			mWifiManager.startScan();
		}
		// After the final scan, determine location
		else {
			String results = "";
			TextView txt = (TextView) context.findViewById(R.id.results_txt);
			rtxt.setText("MAC1 max: "+currentLocation.maxLevel1 + "  MAC1 min: "+currentLocation.minLevel1 + 
					"\nMAC2 max: "+currentLocation.maxLevel2 + "  MAC2 min: "+currentLocation.minLevel2 + 
					"\nMAC3 max: "+currentLocation.maxLevel3 + "  MAC3 min: "+currentLocation.minLevel3);
			if(wifiLock.isHeld()){
			    wifiLock.release();
			}

			// For each of our APs, find which one is closest
			Iterator<Location> it2 = locationList.iterator();
			if(!it2.hasNext())
				return;
			
			Location nearestNeighbor = it2.next();
			float currentShortestDistance = nearestNeighbor.distanceFrom(currentLocation);
			boolean withinBounds = true;
			
	    	while(it2.hasNext()) {
	    		float distance;
	    		Location nextLoc = it2.next();
	    		distance = nextLoc.distanceFrom(currentLocation);
	    		if(distance < currentShortestDistance) {
	    			nearestNeighbor = nextLoc;
	    			currentShortestDistance = distance;
	    		}
	    	}
	    	// If we have found the correct location marker, it should also be inside the original signal bounds measured
	    	withinBounds = nearestNeighbor.boundsAreContaining(currentLocation);
	    	
			results = "Predicted location: "+nearestNeighbor.toString()+"\nRead average: "+currentLocation.getAverageLevel1()+" , "+currentLocation.getAverageLevel2()+" , "+currentLocation.getAverageLevel3()+
					"\nTolerance: "+currentShortestDistance + "\nIn bounds of nearest location?: "+ (withinBounds ? "Yes" : "No");
			txt.setText(results); // txt.setText(result);
			context.setScannerEnabled(false);
		}
    }
	
	public int getScansRemaining() {
		return _scansRemaining;
	}

	public void setScansRemaining(int _scansRemaining) {
		this._scansRemaining = _scansRemaining;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		mWifiManager.startScan();
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {

    }


	
}
