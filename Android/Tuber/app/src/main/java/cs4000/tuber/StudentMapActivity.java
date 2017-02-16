package cs4000.tuber;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
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
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


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
	private String tutorLatitude;
	private String tutorLongitude;

	Boolean initial_pairing = true;
	//Private Sting

	private GoogleMap mMap;
	Intent intent;
	Button acceptTutorServiceButton;
	LocationManager locationManager;
	LocationListener locationListener;

	TextView infoTextView;

	Handler handler = new Handler(); // used for polling



	private void check_paired_status() {

		JSONObject obj = new JSONObject();
		try{
			obj.put("userEmail", _userEmail);
			obj.put("userToken", _userToken);
			obj.put("latitude", _studentLat);
			obj.put("longitude", _studentLong);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ConnectionTask check_paired_student = new ConnectionTask(obj);
		check_paired_student.update_student_location(new ConnectionTask.CallBack() {
			@Override
			public void Done(JSONObject result) {

				if(result != null) {
//					Log.i("@check_paired_student","GOOD");

					try {

						tutorLatitude = result.getString("tutorLatitude");
						tutorLongitude = result.getString("tutorLongitude");

					} catch (JSONException e) {
						e.printStackTrace();
					}



					if(initial_pairing) {
						final AlertDialog dialog = new AlertDialog.Builder(StudentMapActivity.this)
								.setTitle("Paired")
								.setMessage("You paired successfully with a Tutor.")
								.setCancelable(false)
								.setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.cancel();
									}
								}).show();

						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								dialog.dismiss();
							}
						}, 4000);
					}
					initial_pairing = false;



					if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(StudentMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

						// gets last knwon location else it'll update the location based on the current location
						Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

						if (lastKnownLocation != null) {


							_studentLat = String.valueOf(lastKnownLocation.getLatitude());
							_studentLong = String.valueOf(lastKnownLocation.getLongitude());

//							Location tutorLocation = new Location(LocationManager.GPS_PROVIDER);
//							try {
//								tutorLocation.setLatitude(Double.valueOf(result.getString("tutorLatitude")));
//								tutorLocation.setLongitude(Double.valueOf(result.getString("tutorLongitude")));
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
							double DistanceToStudent = 0;
							try {
								DistanceToStudent = distance(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), Double.valueOf(result.getString("tutorLatitude")), Double.valueOf(result.getString("tutorLongitude")));
							} catch (JSONException e) {
								e.printStackTrace();
							}

							//Double distanceInMiles = Double.parseDouble(result.getString("distanceFromStudent"));

							if (DistanceToStudent < 0.5) { // tutor has arrived!
								infoTextView.setTextColor(Color.GREEN);
								infoTextView.setText("Your tutor has arrived");


								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
//                                            infoTextView.setText("");
//                                            offerToTutorButton.setVisibility(View.VISIBLE);
//                                            offerToTutorButton.setText("Offer to Tutor");
//                                            tutor_offered = false;
//                                            initial_pairing = true;

										Intent intent = new Intent(StudentMapActivity.this, StudentStudySession.class);
										//intent.putExtra("status", "0");
										startActivity(intent);
										finish();
									}
								}, 5000);
							} else { // tutor hasn't arrived yet


//								Log.i("Tutor dist:", String.valueOf(DistanceToStudent));

								Double distanceOneDP = (double) Math.round(DistanceToStudent * 10) / 10;

//								Log.i("Tutor dist (rounded):", distanceOneDP.toString());

								infoTextView.setText("Your tutor is " + distanceOneDP.toString() + " miles away!");


								LatLng requestLocationLatLng = new LatLng(Double.parseDouble(_studentLat), Double.parseDouble(_studentLong));
								LatLng tutorLocationLatLng = new LatLng(Double.parseDouble(tutorLatitude), Double.parseDouble(tutorLongitude));

								ArrayList<Marker> markers = new ArrayList<>();

								mMap.clear(); // clears all existing markers
								markers.add(mMap.addMarker(new MarkerOptions().position(requestLocationLatLng).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
								markers.add(mMap.addMarker(new MarkerOptions().position(tutorLocationLatLng).title("Tutor Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));

								LatLngBounds.Builder builder = new LatLngBounds.Builder();
								for (Marker marker : markers) {
									builder.include(marker.getPosition());
								}
								LatLngBounds bounds = builder.build();

								int padding = 60;
								CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
								mMap.animateCamera(cu);



								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
//										Log.i("@check_paired_status", "CALL 4");
										check_paired_status();
									}
								}, 2000);
							}
						}
					}


				} else { // not paied yet
//					Log.i("@check_paired_student","BAD");
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
//							Log.i("@check_paired_status", "CALL 3");
							check_paired_status();
						}
					}, 2000);
				}
			}
		});
	}



//	private void check_session_status(){
//
//
//		JSONObject obj = new JSONObject();
//		try {
//			obj.put("userEmail", _userEmail);
//			obj.put("userToken", _userToken);
//			//obj.put("requestedTutorEmail", _tutorUsername);
//			//obj.put("studentLatitude", _studentLat);
//			//obj.put("studentLongitude", _studentLong);
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//		ConnectionTask task = new ConnectionTask(obj);
//		task.check_session_activeStatusStudent(new ConnectionTask.CallBack() {
//			@Override
//			public void Done(JSONObject result) {
//				if(result != null) {
//
////					final AlertDialog dialog = new AlertDialog.Builder(StudentMapActivity.this)
////							.setTitle("Paired")
////							.setMessage("You paired successfully with a Tutor.")
////							.setCancelable(false)
////							.setNeutralButton("Directions", new DialogInterface.OnClickListener() {
////								@Override
////								public void onClick(DialogInterface dialog, int which) {
////									Intent directionsIntent = new Intent(android.content.Intent.ACTION_VIEW,
////											Uri.parse("http://maps.google.com/maps?saddr=" + intent.getDoubleExtra("tutorLatitude", 0) + "," + intent.getDoubleExtra("tutorLongitude", 0) + "&daddr=" + intent.getDoubleExtra("requestLatitude", 0) + "," + intent.getDoubleExtra("requestLongitude", 0)));
////									startActivity(directionsIntent);
////								}
////							})
////							.setPositiveButton("Acknowledged", new DialogInterface.OnClickListener() {
////								@Override
////								public void onClick(DialogInterface dialog, int which) {
////									dialog.cancel();
////								}
////							}).show();
//
//
//					Intent intent = new Intent(StudentMapActivity.this, StudentStudySession.class);
//					intent.putExtra("tutorEmail", "");
//					intent.putExtra("tutorSessionID", "");
//					startActivity(intent);
//					finish();
//
//				} else {
//					Log.i("@","");
//					handler.postDelayed(new Runnable() {
//						@Override
//						public void run() {
//							check_session_status();
//						}
//					}, 10000);
//				}
//			}
//		});
//	}

	public void acceptTutorService(View view){
		ImmediateStudentRequestActivity.getInstance().finish();

		acceptTutorServiceButton = (Button)findViewById(R.id.acceptTutorButton);
		acceptTutorServiceButton.setVisibility(View.INVISIBLE);


		JSONObject jsonParam3 = new JSONObject();
		try {
			jsonParam3.put("userEmail", _userEmail);
			jsonParam3.put("userToken", _userToken);
			jsonParam3.put("requestedTutorEmail", _tutorUsername);
			jsonParam3.put("studentLatitude", _studentLat);
			jsonParam3.put("studentLongitude", _studentLong);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		ConnectionTask task = new ConnectionTask(jsonParam3);
		task.pair_student_to_tutor(new ConnectionTask.CallBack() {
			@Override
			public void Done(JSONObject result) {
				// Do Something after the task has finished
				if(result != null) {
//					Log.i("@check_paired_status", "CALL 2");
					check_paired_status();
				}
				else {
//					Log.i("@acceptTutorService","Pairing failed!");
				}
			}
		});
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

		infoTextView = (TextView) findViewById(R.id.infoTextViewStudent);

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



		JSONObject obj = new JSONObject();
		try{
			obj.put("userEmail", _userEmail);
			obj.put("userToken", _userToken);
			obj.put("latitude", _studentLat);
			obj.put("longitude", _studentLong);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ConnectionTask check_session_student = new ConnectionTask(obj);
		check_session_student.update_student_location(new ConnectionTask.CallBack() {
			@Override
			public void Done(JSONObject result) {
				if(result != null) {
//					Log.i("@check_session_student", "check sessionStudent completed");

					acceptTutorServiceButton = (Button)findViewById(R.id.acceptTutorButton);
					acceptTutorServiceButton.setVisibility(View.INVISIBLE);

//					Log.i("@check_paired_status", "CALL 1");
					check_paired_status();
				} else {

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


//					Log.i("@check_session_student", "check sessionStudent failed - no pairing");
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

//		RelativeLayout mapLayout = (RelativeLayout)findViewById(R.id.tutorLocationRelLayout);
//		mapLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//			@Override
//			public void onGlobalLayout(){
//
//				LatLng studentLocation = new LatLng(intent.getDoubleExtra("studentLatitude", 0), intent.getDoubleExtra("studentLongitude", 0));
//				LatLng tutorLocation = new LatLng(intent.getDoubleExtra("tutorLatitude", 0), intent.getDoubleExtra("tutorLongitude", 0));
//
//				ArrayList<Marker> markers = new ArrayList<>();
//
//				markers.add(mMap.addMarker(new MarkerOptions().position(tutorLocation).title("Tutor Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))));
//				markers.add(mMap.addMarker(new MarkerOptions().position(studentLocation).title("Your Location")));
//
//				LatLngBounds.Builder builder = new LatLngBounds.Builder();
//				for(Marker marker : markers) {
//					builder.include(marker.getPosition());
//				}
//				LatLngBounds bounds = builder.build();
//
//				int padding = 60;
//				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
//
//				mMap.animateCamera(cu);
//			}
//		});


		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

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


		JSONObject jO = new JSONObject();
		try{
			jO.put("userEmail", _userEmail);
			jO.put("userToken", _userToken);
			jO.put("latitude", String.valueOf(location.getLatitude()));
			jO.put("longitude", String.valueOf(location.getLongitude()));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ConnectionTask UpdateLocation = new ConnectionTask(jO);
		UpdateLocation.update_student_location(new ConnectionTask.CallBack() {
			@Override
			public void Done(JSONObject result) {
				if(result != null){
//					Log.i("@onLocationChanged","Location updated successfully");
				}
				else {
//					Log.i("@onLocationChanged","Location update failed");
				}
			}
		});
	}




	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(deg2rad(lat1))
				* Math.sin(deg2rad(lat2))
				+ Math.cos(deg2rad(lat1))
				* Math.cos(deg2rad(lat2))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		return (dist);
	}

	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}
}
