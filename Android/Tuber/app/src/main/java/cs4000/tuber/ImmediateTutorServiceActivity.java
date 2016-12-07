package cs4000.tuber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/*
 * Displays all students that created a immediate tutor request
 * In list form where each element can be clicked
 */
public class ImmediateTutorServiceActivity extends Activity {


  //ListView
  ListView requestListView;
  ArrayList<String> requests = new ArrayList<String>();
  ArrayAdapter arrayAdapter;

  ArrayList<Double> requestLatitudes = new ArrayList<Double>();
  ArrayList<Double> requestLongitudes = new ArrayList<Double>();

  ArrayList<String> usernames = new ArrayList<String>();
  LocationManager locationManager;
  LocationListener locationListener;
  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  private GoogleApiClient client;

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

	// pass user current locaton and
	requestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	  // i is the number the user pressed on
	  @Override
	  public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){

		if(Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(ImmediateTutorServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

		  Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		  if(requestLatitudes.size() > i && requestLongitudes.size() > i && usernames.size() > i && lastKnownLocation != null) {

			Intent intent = new Intent(getApplicationContext(), TutorLocationActivity.class);

			intent.putExtra("requestLatitude", requestLatitudes.get(i));
			intent.putExtra("requestLongitude", requestLongitudes.get(i));
			intent.putExtra("tutorLatitude", lastKnownLocation.getLatitude());
			intent.putExtra("tutorLongitude", lastKnownLocation.getLongitude());
			intent.putExtra("username", usernames.get(i));

			startActivity(intent);
		  }
		}

	  }

	});


	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


	locationListener = new LocationListener() {
	  @Override
	  public void onLocationChanged(Location location) {

		updateListView(location);

		ParseUser.getCurrentUser().put("location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));

		ParseUser.getCurrentUser().saveInBackground();
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
	if (Build.VERSION.SDK_INT < 23) {

	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

	} else {

	  if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

		ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


	  } else {

		// else we have permission
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		// gets last knwon location else it'll update the location based on the current location
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lastKnownLocation != null) {

		  updateListView(lastKnownLocation);
		}
	  }
	}
	// ATTENTION: This was auto-generated to implement the App Indexing API.
	// See https://g.co/AppIndexing/AndroidStudio for more information.
	client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
  }


  // Updates the list view that the tutor can view
  public void updateListView(Location location) {

	if (location != null) {

	  ParseQuery<ParseObject> query = ParseQuery.getQuery("Request");

	  final ParseGeoPoint geoPointLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
	  query.whereNear("location", geoPointLocation);
	  query.setLimit(10); // usually driver selects one out of the 10 closest locations
	  query.whereDoesNotExist("tutorUsername");
	  query.findInBackground(new FindCallback<ParseObject>() {
		@Override
		public void done(List<ParseObject> objects, ParseException e) {

		  if (e == null) {

			requests.clear();
			requestLatitudes.clear();
			requestLongitudes.clear();

			if (objects.size() > 0) {

			  for (ParseObject object : objects) {

				ParseGeoPoint requestLocation = (ParseGeoPoint)object.get("location");

				if(requestLocation != null ){

				  Double distanceInMiles = geoPointLocation.distanceInMilesTo((ParseGeoPoint) object.get("location"));
				  Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;
				  requests.add(object.get("username").toString() + " - Distance: " + distanceOneDP.toString() + " miles");

				  requestLatitudes.add(requestLocation.getLatitude());
				  requestLongitudes.add(requestLocation.getLongitude());
				  usernames.add(object.getString("username"));

				}

			  }

			} else {

			  requests.add("No active requests nearby");
			}

			arrayAdapter.notifyDataSetChanged();
		  }
		}
	  });

	}
  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
	super.onRequestPermissionsResult(requestCode, permissions, grantResults);

	if (requestCode == 1) {

	  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

		  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		  Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		  updateListView(lastKnownLocation);

		}
	  }
	}
  }


  /**
   * ATTENTION: This was auto-generated to implement the App Indexing API.
   * See https://g.co/AppIndexing/AndroidStudio for more information.
   */
  public Action getIndexApiAction() {
	Thing object = new Thing.Builder()
			.setName("ImmediateTutorService Page") // TODO: Define a title for the content shown.
			// TODO: Make sure this auto-generated URL is correct.
			.setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
			.build();
	return new Action.Builder(Action.TYPE_VIEW)
			.setObject(object)
			.setActionStatus(Action.STATUS_TYPE_COMPLETED)
			.build();
  }

  @Override
  public void onStart() {
	super.onStart();

	// ATTENTION: This was auto-generated to implement the App Indexing API.
	// See https://g.co/AppIndexing/AndroidStudio for more information.
	client.connect();
	AppIndex.AppIndexApi.start(client, getIndexApiAction());
  }

  @Override
  public void onStop() {
	super.onStop();

	// ATTENTION: This was auto-generated to implement the App Indexing API.
	// See https://g.co/AppIndexing/AndroidStudio for more information.
	AppIndex.AppIndexApi.end(client, getIndexApiAction());
	client.disconnect();
  }
}
