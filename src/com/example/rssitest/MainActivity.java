package com.example.rssitest;

import java.util.ArrayList;

import com.example.rssitest.R;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	Scanner myScanner;
	
	// Accelerometer variables
	private boolean mInitialized;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private float mLastX, mLastY, mLastZ;
	private final float NOISE = (float) 1.0;
	private boolean _scannerEnabled = false;
	private ArrayList<Location> locationList;
	
	private final int NUM_SCANS = 5;
	private final int ACCEL_LEVEL = 7;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		String filePath = this.getFilesDir().getPath().toString() + "/locationsFile.dat";
		locationList = FileIO.deserialzeLocationFile(filePath);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void scanNetwork(View view) {
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}

	// Button handlers
	public void storePoint(View view) {
		if(!_scannerEnabled && myScanner != null) {
			TextView tx = (TextView) this.findViewById(R.id.editText1);
			TextView ty = (TextView) this.findViewById(R.id.editText2);
			Location location = myScanner.createLocation(Integer.parseInt(tx.getText().toString()),Integer.parseInt(ty.getText().toString()));
			locationList.add(location);
		}
		String filePath = this.getFilesDir().getPath().toString() + "/locationsFile.dat";
		FileIO.serializeToLocationFile(locationList, filePath);
	}
	public void deleteData(View view) {
		if(!_scannerEnabled && myScanner != null) {
			TextView tx = (TextView) this.findViewById(R.id.editText1);
			TextView ty = (TextView) this.findViewById(R.id.editText2);
			Location location = myScanner.createLocation(Integer.parseInt(tx.getText().toString()),Integer.parseInt(ty.getText().toString()));
			locationList.add(location);
		}
		String filePath = this.getFilesDir().getPath().toString() + "/locationsFile.dat";
		FileIO.serializeToLocationFile(locationList, filePath);
	}
	public void stopScanning(View view) {
		myScanner._scansRemaining = 0;
		mSensorManager.unregisterListener(this);
	}
	
	private void initiateScans(int numberOfScans) {
		_scannerEnabled = true;
		myScanner = new Scanner(this);
		myScanner.setupScanner(locationList);
		myScanner.setScansRemaining(numberOfScans);
		myScanner.execute();
	}
	
	public void setScannerEnabled(boolean enable) {
		_scannerEnabled = enable;
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
	}

	// Activates the scanner when accelerometer passes a certain level
	@Override
	public void onSensorChanged(SensorEvent arg0) {
		TextView txt = (TextView) findViewById(R.id.accel_txt);
		
		float x = arg0.values[0];
		float y = arg0.values[1];
		float z = arg0.values[2];
		
		float deltaX = 0;
		float deltaY = 0;
		float deltaZ = 0;
		
		if (!mInitialized) {
			mLastX = x;
			mLastY = y;
			mLastZ = z;
			mInitialized = true;
		} else {
			deltaX = Math.abs(mLastX - x);
			deltaY = Math.abs(mLastY - y);
			deltaZ = Math.abs(mLastZ - z);
			if (deltaX < NOISE) deltaX = (float)0.0;
			if (deltaY < NOISE) deltaY = (float)0.0;
			if (deltaZ < NOISE) deltaZ = (float)0.0;
			mLastX = x;
			mLastY = y;
			mLastZ = z;
		}
		if((deltaX > ACCEL_LEVEL || deltaY > ACCEL_LEVEL || deltaZ > ACCEL_LEVEL) && !_scannerEnabled)
			initiateScans(NUM_SCANS);
		
		txt.setText("Accelerometer \nx: "+deltaX+"\ny: "+deltaY+"\nz: "+deltaZ);
	}
}
