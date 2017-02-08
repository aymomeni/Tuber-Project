package cs4000.tuber;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

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
//import com.parse.FindCallback;
//import com.parse.ParseException;
//import com.parse.ParseObject;
//import com.parse.ParseQuery;
//import com.parse.ParseUser;
//import com.parse.SaveCallback;
import android.Manifest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/*
 * Displays the students's location and the tutors location on a google map
 * using information that is send through the ImmediateStudentRequestActivity
 */
public class StudentMapActivity extends FragmentActivity implements OnMapReadyCallback {

	private SharedPreferences sharedPreferences;
	private String _userEmail;
	private String _userToken;
	private String _studentLat;
	private String _studentLong;

	private String _tutorUsername;

	private GoogleMap mMap;
	Intent intent;
	Button acceptTutorServiceButton;
	LocationManager locationManager;
	LocationListener locationListener;



	public void acceptTutorService(View view){

		acceptTutorServiceButton = (Button)findViewById(R.id.acceptTutorButton);
		acceptTutorServiceButton.setVisibility(View.INVISIBLE);





		JSONObject jsonParam3 = new JSONObject();
		try {
			jsonParam3.put("userEmail", _userEmail);
			jsonParam3.put("userToken", _userToken);
			jsonParam3.put("requestedTutorEmail", _tutorUsername);
			jsonParam3.put("studentLatitude", _studentLat);
			jsonParam3.put("studentLongitude", _studentLong);
			Log.i("@userEmail",_userEmail);
			Log.i("@userToken",_userToken);
			Log.i("@requestedTutorEmail",_tutorUsername);
			Log.i("@studentLatitude",_studentLat);
			Log.i("@studentLongitude",_studentLong);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ConnectionTask task = new ConnectionTask(jsonParam3);
		task.pair_student_tutor(new ConnectionTask.CallBack() {
			@Override
			public void Done(JSONObject result) {
				// Do Something after the task has finished

				if(result != null) {
					// pairing complete
					new AlertDialog.Builder(StudentMapActivity.this)
							.setTitle("Paired")
							.setMessage("You paired successfully with a Tutor.")
							.setCancelable(false)
							.setNeutralButton("Directions", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW,
											Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("tutorLatitude", 0) + "," + intent.getDoubleExtra("tutorLongitude", 0) + "&daddr=" + intent.getDoubleExtra("requestLatitude", 0) + "," + intent.getDoubleExtra("requestLongitude", 0)));
									startActivity(directionsIntent);
								}
							})
							.setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.cancel();
								}
							}).show();
				}
				else {
					Log.i("@acceptTutorService","Pairing failed!");
				}
			}
		});


//	ParseQuery<ParseObject> query = ParseQuery.getQuery("TutorServices");
//
//	query.whereEqualTo("username", intent.getStringExtra("username"));
//
//	query.findInBackground(new FindCallback<ParseObject>(){
//	  @Override
//	  public void done(List<ParseObject>objects, ParseException e){
//
//		if(e == null) {
//
//		  if(objects.size() > 0) {
//
//			for(ParseObject object : objects) {
//
//			  object.put("studentUsername", ParseUser.getCurrentUser().getUsername());
//
//			  object.saveInBackground(new SaveCallback() {
//				@Override
//				public void done(ParseException e) {
//
//				  	if(e == null){
//
//					  // pairing complete
//					  new AlertDialog.Builder(StudentMapActivity.this)
//							  .setTitle("Paired")
//							  .setMessage("You paired successfully with a Tutor.")
//							  .setCancelable(false)
//							  .setNeutralButton("Directions", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//								  Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW,
//										  Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("tutorLatitude", 0) + "," + intent.getDoubleExtra("tutorLongitude", 0) + "&daddr=" + intent.getDoubleExtra("requestLatitude", 0) + "," + intent.getDoubleExtra("requestLongitude", 0)));
//								  startActivity(directionsIntent);
//								}
//							  })
//							  .setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//								  dialog.cancel();
//								}
//							  }).show();
//					}
//				}
//			  });
//			}
//		  }
//		}
//	  }
//	});
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_student_map);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		_userEmail = sharedPreferences.getString("userEmail", "");
		_userToken = sharedPreferences.getString("userToken", "");

		intent = getIntent();

		_studentLat = String.valueOf(intent.getDoubleExtra("studentLatitude", 0));
		_studentLong = String.valueOf(intent.getDoubleExtra("studentLongitude", 0));

		_tutorUsername = intent.getStringExtra("username");


		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);




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

		RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.tutorLocationRelLayout);
		mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout(){

				LatLng studentLocation = new LatLng(intent.getDoubleExtra("studentLatitude", 0), intent.getDoubleExtra("studentLongitude", 0));
				LatLng tutorLocation = new LatLng(intent.getDoubleExtra("tutorLatitude", 0), intent.getDoubleExtra("tutorLongitude", 0));

				ArrayList<Marker> markers = new ArrayList<>();

				markers.add(mMap.addMarker(new MarkerOptions().position(tutorLocation).title("Tutor Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
				markers.add(mMap.addMarker(new MarkerOptions().position(studentLocation).title("Your Location")));

				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				for(Marker marker : markers) {
					builder.include(marker.getPosition());
				}
				LatLngBounds bounds = builder.build();

				int padding = 60;
				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

				mMap.animateCamera(cu);
			}
		});


		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {

				updateMap(location);


			}

			@Override
			public void onStatusChanged(String s, int i, Bundle bundle) {

			}

			@Override
			public void onProviderEnabled(String s) {

			}

			@Override
			public void onProviderDisabled(String s) {

			}
		};

		if (Build.VERSION.SDK_INT < 23) {

			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

		} else {

			if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);


			} else {

				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

				Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (lastKnownLocation != null) {

					updateMap(lastKnownLocation);

				}


			}


		}

	}

	// Updates the map when location of user changes
	public void updateMap(Location location) {

		LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

		mMap.clear(); // clears all existing markers
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
		mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

	}
}
