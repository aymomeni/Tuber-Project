package cs4000.tuber;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.location.LocationListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/*
 * Represents
 */
public class ImmediateTutorServiceActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  LocationManager locationManager;
  LocationListener locationListener;
  String userType = "tutor";
  Button offerToTutorButton;
  Boolean requestActive = false;
  Boolean studentActive = true;
  TextView infoTextView;

  Handler handler = new Handler(); // used for polling


  public void checkForUpdate() {

	ParseQuery<ParseObject> query = ParseQuery.getQuery("TutorServices");
	query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

	query.whereExists("studentUsername");


	query.findInBackground(new FindCallback<ParseObject>() {
							 @Override
							 public void done(List<ParseObject> objects, ParseException e) {

							   if (e == null && objects.size() > 0) {

								 studentActive = true;

								 ParseQuery<ParseUser> query2 = ParseUser.getQuery();
								 query2.whereEqualTo("username", objects.get(0).getString("studentUsername"));

								 query2.findInBackground(new FindCallback<ParseUser>() {
								   @Override
								   public void done(List<ParseUser> objects, ParseException e) {

									 if (e == null && objects.size() > 0) {

									   ParseGeoPoint tutorLocation = objects.get(0).getParseGeoPoint("location");
									   if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(ImmediateTutorServiceActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

										 // gets last knwon location else it'll update the location based on the current location
										 Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

										 if (lastKnownLocation != null) {

										   ParseGeoPoint userLocation = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

										   Double distanceInMiles = tutorLocation.distanceInMilesTo(userLocation);

										   if (distanceInMiles < 0.01) {
											 infoTextView.setText("Your student has arrived");


											 ParseQuery<ParseObject> query = ParseQuery.getQuery("TutorServices");
											 query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

											 query.findInBackground(new FindCallback<ParseObject>() {
											   @Override
											   public void done(List<ParseObject> objects, ParseException e) {
												 if (e == null) {
												   for (ParseObject object : objects) {
													 object.deleteInBackground();
												   }
												 }
											   }
											 });

											 handler.postDelayed(new

																		 Runnable() {
																		   @Override
																		   public void run() {
																			 infoTextView.setText("");
																			 offerToTutorButton.setVisibility(View.VISIBLE);
																			 offerToTutorButton.setText("Offer to Tutor");
																			 requestActive = false;
																			 studentActive = false;
																		   }

																		 }

													 , 5000);
										   } else {


											 Double distanceOneDP = (double) Math.round(distanceInMiles * 10) / 10;

											 infoTextView.setText("Your tutor is " + distanceOneDP.toString() + " miles away!");


											 LatLng tutorLocationLatLng = new LatLng(tutorLocation.getLatitude(), tutorLocation.getLongitude());
											 LatLng requestLocationLatLng = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

											 ArrayList<Marker> markers = new ArrayList<>();

											 mMap.clear(); // clears all existing markers
											 markers.add(mMap.addMarker(new MarkerOptions().position(tutorLocationLatLng).title("Tutor Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
											 markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));

											 LatLngBounds.Builder builder = new LatLngBounds.Builder();
											 for (Marker marker : markers) {
											   builder.include(marker.getPosition());
											 }
											 LatLngBounds bounds = builder.build();

											 int padding = 60;
											 CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

											 mMap.animateCamera(cu);

											 offerToTutorButton.setVisibility(View.INVISIBLE);

											 handler.postDelayed(new Runnable() {

											   @Override

											   public void run() {

												 checkForUpdate();
												 Log.i("Debug ", "1");

											   }
											 }, 2000);

										   }
										 }

									   }

									 }

								   }

								 });


							   } else {

								 handler.postDelayed(new Runnable() {

								   @Override

								   public void run() {

									 checkForUpdate();

								   }
								 }, 2000);
							   }
							 }
	});
  }


  public void logout(View view) {

	Log.i("Info", "Logout Tutor Immediate Service");
	ParseUser.logOut();

	Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
	startActivity(intent);

  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
	super.onRequestPermissionsResult(requestCode, permissions, grantResults);

	if (requestCode == 1) {

	  if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

		  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		  Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		  updateMap(lastKnownLocation);

		}
	  }
	}
  }


  public void offerToTutor(View view) {

	Log.i("Info", "Tutor Requested");

	// checking if a request is active
	if (requestActive) {

	  ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TutorServices");
	  query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

	  query.findInBackground(new FindCallback<ParseObject>() {
		@Override
		public void done(List<ParseObject> objects, ParseException e) {

		  if (e == null) {

			if (objects.size() > 0) {

			  // if any active request exists delete them from the database
			  for (ParseObject object : objects) {
				object.deleteInBackground();
			  }

			  requestActive = false;
			  offerToTutorButton.setText("Offer to Tutor");

			}
		  }
		}
	  });

	} else {

	  if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lastKnownLocation != null) {

		  ParseObject request = new ParseObject("TutorServices");
		  request.put("username", ParseUser.getCurrentUser().getUsername());
		  request.put("studentOrTutor", userType);
		  ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
		  request.put("tutorLocation", parseGeoPoint);

		  request.saveInBackground(new SaveCallback() {
			@Override
			public void done(ParseException e) {

			  if (e == null) {
				offerToTutorButton.setText("Cancel Offer");
				requestActive = true;
				Log.i("Info", "Tutor Service Cancelled");

				checkForUpdate();

//				handler.postDelayed(new Runnable() {
//				  @Override
//				  public void run() {
//					checkForUpdate();
//				  }
//
//				}, 2000);
			  }
			}
		  });

		} else {

		  Toast.makeText(this, "Could not find your location. Please try again later.", Toast.LENGTH_SHORT).show();

		}
	  }

	}
  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_immediate_tutor_service);
	// Obtain the SupportMapFragment and get notified when the map is ready to be used.
	SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
			.findFragmentById(R.id.map);
	mapFragment.getMapAsync(this);

	ParseUser.getCurrentUser().put("studentOrTutor", userType);

	ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
	  @Override
	  public void done(ParseException e) {
		// TODO: empty for now
		//redirectActivity?
	  }
	});

	offerToTutorButton = (Button) findViewById(R.id.offerToTutorButton);
	infoTextView = (TextView) findViewById(R.id.infoTextView);


	ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("TutorServices");
	query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

	query.findInBackground(new FindCallback<ParseObject>() {
	  @Override
	  public void done(List<ParseObject> objects, ParseException e) {

		if (e == null) {
		  if (objects.size() > 0) {

			requestActive = true;
			offerToTutorButton.setText("Cancel Offer");

			checkForUpdate();

		  }
		}
	  }
	});

  }


  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */
  @Override
  public void onMapReady(GoogleMap googleMap) {
	mMap = googleMap;

	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

	locationListener = new LocationListener() {
	  @Override
	  public void onLocationChanged(Location location) {

		updateMap(location);
//		ParseUser.getCurrentUser().put("location", new ParseGeoPoint(location.getLatitude(), location.getLongitude()));
//		ParseUser.getCurrentUser().saveInBackground();
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
		  updateMap(lastKnownLocation);
		}
	  }
	}
  }

  // Updates the map when location of user changes
  public void updateMap(Location location) {

	if (studentActive != false) {

	  LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

	  mMap.clear(); // clears all existing markers
	  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
	  mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

	}

  }
}
