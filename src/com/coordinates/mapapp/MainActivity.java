package com.coordinates.mapapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity {
	
	private GoogleMap googleMap;
	private List<Marker> markers;
	private SupportMapFragment mapFragment;
	private Handler handler;
	private CameraPosition lastSelectedPosition;
	private static final int DELAY = 2000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		handler = new Handler();
	}
	
	private Runnable moveToCoords = new Runnable() {
		
		@Override
		public void run() {
			googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastSelectedPosition)); 
		}
	};
	
	@Override
	protected void onResume() {
	    super.onResume();
	    setUpMapIfNeeded();
	}
	
	private void setUpMapIfNeeded() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		}
	    googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker marker) {
				StringBuffer position = new StringBuffer("lat ").append(marker.getPosition().latitude)
						.append(", lng ").append(marker.getPosition().longitude);
				Toast.makeText(MainActivity.this, 
						position,
						Toast.LENGTH_SHORT).show();
				lastSelectedPosition = new CameraPosition(marker.getPosition(), 14, 0, 0);
				handler.postDelayed(moveToCoords, DELAY);
				return false;
			}
		});
	    markers = getMarkers();
	}
	
	private List<Marker> getMarkers() {
		BufferedReader reader = null;
		StringBuffer lines = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(this
					.getAssets().open("coords.txt"), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				lines.append(line);
			}
		} catch (IOException e) {
			Log.e("readCoords", e.getMessage());
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					Log.e("readCoords", e.getMessage());
				}
			}
		}
		JSONArray coordsJSONArray = (JSONArray) JSONValue.parse(lines.toString());
		double lat, lng;
		if (markers == null) {
			markers = new ArrayList<>();
		}
		for (int i = 0; i < coordsJSONArray.size(); i++) {
			JSONObject markerObject = (JSONObject) coordsJSONArray.get(i);
			lat = Double.parseDouble((String) markerObject.get("lat"));
			lng = Double.parseDouble((String) markerObject.get("lng"));
			Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)));
			markers.add(marker);
		}
		return markers;
	}

}
