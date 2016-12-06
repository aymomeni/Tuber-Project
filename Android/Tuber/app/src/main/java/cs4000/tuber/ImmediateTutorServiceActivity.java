package cs4000.tuber;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ImmediateTutorServiceActivity extends Activity {


  //ListView
  ListView requestListView;
  ArrayList<String> requests = new ArrayList<String>();
  ArrayAdapter arrayAdapter;
  LocationManager locationManager;
  LocationListener locationListener;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_immediate_tutor_service);

	setTitle("Nearby Student Requests");

	requestListView = (ListView) findViewById(R.id.requestListView);

	arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, requests);
	requests.clear();
	requests.add("Getting nearby requests...");

	requestListView.setAdapter(arrayAdapter);

	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	locationListener = new LocationListener() {
	  @Override
	  public void onLocationChanged(Location location) {
		updateListView(location);
	  }

	  @Override
	  public void onStatusChanged(String provider, int status, Bundle extras) {

	  }

	  @Override
	  public void onProviderEnabled(String provider) {

	  }

	  @Override
	  public void onProviderDisabled(String provider) {

	  }
	};

	// Checking for GPS permissions
	if(Build.VERSION.SDK_INT<23) {

	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	} else {

	  if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

		ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);


	  } else {

		// else we have permission
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		// gets last knwon location else it'll update the location based on the current location
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if(lastKnownLocation != null) {

		  updateListView(lastKnownLocation);
		}
	  }
	}
  }


  	// Updates the list view that the tutor can view
	public void updateListView(Location location){

	  requests.clear();

	  if(location != null){

		ParseQuery<ParseObject>  query = ParseQuery.getQuery("Request");

		final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
		query.whereNear("location", geoPointLocation);
		query.setLimit(10); // usually driver selects one out of the 10 closest locations

		query.findInBackground(new FindCallback<ParseObject>() {
		  @Override
		  public void done(List<ParseObject> objects, ParseException e) {
			if(e == null){

			  if(objects.size() > 0){

				for(ParseObject object : objects){

				  Double distanceInMiles = geoPointLocation.distanceInMilesTo((ParseGeoPoint)object.get("location"));
				  Double distanceOneDP = (double) Math.round(distanceInMiles * 10) /10;

				  requests.add(object.get("username").toString() + " - Distance: " + distanceOneDP.toString() + " miles");
				}
				arrayAdapter.notifyDataSetChanged();
			  } else {

				requests.add("No active requests nearby");
			  }
			}
		  }
		});

	  }
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
	  super.onRequestPermissionsResult(requestCode, permissions, grantResults);

	  if(requestCode == 1) {

		if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

		  if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

			Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

			updateListView(lastKnownLocation);

		  }
		}
	  }
	}







}
