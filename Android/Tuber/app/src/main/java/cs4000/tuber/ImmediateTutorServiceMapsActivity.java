package cs4000.tuber;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Class that depicts the tutor and the student on google maps
 *
 * There is a couple of checks that one should be aware of in onCreate:
 *
 * - is tutor already offering to tutor?
 * - is tutor already paired?
 */
public class ImmediateTutorServiceMapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  private LocationManager _locationManager;
  private LocationListener _locationListener;

  private Button _offerToTutorButton;

  private String _userEmail;
  private String _userToken;
  private String _userCourse;
  private String _userLatitude;
  private String _userLongitude;

  private Boolean _ServiceRequestActive = false;
  private Boolean _pairingActive = false;
  private TextView _infoTextView;

  private ConnectionTask connectionTask;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_immediate_tutor_service_maps);
	// Obtain the SupportMapFragment and get notified when the map is ready to be used.
	SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
			.findFragmentById(R.id.map);
	mapFragment.getMapAsync(this);


	//TODO: Use shared preferences instead of hard coding
	// grab user's email and password from shared preferences as well as the class in which the tutor
	// wants to tutor in
	// user course should come from a message delivered by the prior activity
	_userEmail = "u0665302@utah.edu";
	_userToken = "";
	_userCourse = "CS 4400";
    _userLongitude = "";
	_userLatitude = "";

	_pairingActive = checkIfPaired();
	_ServiceRequestActive = checkIfAlreadyOffering();

	_offerToTutorButton = (Button) findViewById(R.id.offerToTutorButton);
	_infoTextView = (TextView) findViewById(R.id.infoTextView);

	// check if there is already a tutor service request pending
	// set request active to true and depict it
	// set the Offer to tutor button to cancel offer check for updates


	_offerToTutorButton.setOnClickListener(new View.OnClickListener() {
	  @Override
	  public void onClick(View v) {
		// check if I exist and am offering to tutor
		// if yes we just show it

		// if no we send to the server an offer to tutor request

	  }
	});

  }


  /**
   * Checks if already paired (must be implemented by Brandon)
   * @return
   */
  private boolean checkIfPaired() {

	JSONObject jObject = null;
	// checking if the tutor is already actively offering to tutor
	try {
	  jObject = getThisTutorJSONObjectForOfferToTutor();
	} catch(JSONException e) {
	  Log.i("Tutor Service:", "Issue parsing the JSON object");
	  e.printStackTrace();
	}

	connectionTask = new ConnectionTask(new ConnectionTask.CallBack() {
	  @Override
	  public boolean Done(JSONObject result) {
		if(result != null){

		  // TODO: Can be wrong: tutor is not yet available
		  return true;

		} else {

		  // tutor is already available
		  return false;
		}
	  }
	});
	connectionTask.check_paired_status(jObject);

	return false;
  }


  private boolean checkIfAlreadyOffering() {

	JSONObject jObject = null;
	// checking if the tutor is already actively offering to tutor
	try {
	  jObject = getThisTutorJSONObjectForOfferToTutor();
	} catch(JSONException e) {
	  Log.i("Tutor Service:", "Issue parsing the JSON object");
	  e.printStackTrace();
	}

	connectionTask = new ConnectionTask(new ConnectionTask.CallBack() {
	  @Override
	  public boolean Done(JSONObject result) {
		if(result == null && connectionTask.getIsOfferingToTutor()){

		  // TODO: Can be wrong: tutor is not yet available
		  return true;

		} else {

		  // tutor is already available
		  return false;
		}
	  }
	});
	connectionTask.check_paired_status(jObject);

	return false;
  }


  /**
   * Checks for pairing and for location changes once pairing happened
   */
  private void checkForUpdate() {

	// in a loop should check if paired yet
		// if yes should check if locations have changed or are close to each other
			// if changed redraw the information distance
			// if yes redraw locations

  }

  /**
   * Returns a JSON Object based on the make_tutor_available request
   * @return
   */
  private JSONObject getThisTutorJSONObjectForOfferToTutor() throws JSONException {

	JSONObject jO = new JSONObject();
	jO.put("userEmail", _userEmail);
	jO.put("userToken", _userToken);
	jO.put("tutorCourse", _userCourse);

	try {
	  _userLatitude = Double.toString(getMyLocation().getLatitude());
	  _userLongitude = Double.toString(getMyLocation().getLongitude());
	} catch(Exception e){
	  Log.i("Tutor Service Request:", "Error in loading tutor location");
	  e.printStackTrace();
	}

	jO.put("latitude", _userLatitude);
	jO.put("longitude", _userLongitude);

	return jO;
  }


  private JSONObject getThisTutorJSONOBjectCancelTutorRequest() throws JSONException {

	JSONObject jO = new JSONObject();
	jO.put("userEmail", _userEmail);
	jO.put("userToken", _userToken);

	return jO;
  }


  /**
   * Returns the location of the user in the context of this class
   * @return
   */
  private Location getMyLocation() {

	Location lastKnownLocation = null;

	// Checking for GPS permissions
	if (Build.VERSION.SDK_INT < 23) {

	  _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) _locationListener);

	} else {

	  if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
		ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

	  } else {

		// else we have permission
		_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) _locationListener);

		// gets last knwon location else it'll update the location based on the current location
		lastKnownLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lastKnownLocation == null) {
		  return null;
		}
	  }
	}

	return lastKnownLocation;
  }


  /**
   * Cancels a request made prior by the tutor
   */
  private boolean cancelTutorServiceRequest () {

	JSONObject jObject = null;
	// if no active pairing is going on then cancel request
	if(!_pairingActive && checkIfAlreadyOffering()){

	  // checking if the tutor is already actively offering to tutor
	  try {
		jObject = getThisTutorJSONOBjectCancelTutorRequest();
	  } catch(JSONException e) {
		Log.i("Tutor Service:", "Issue parsing the JSON object");
		e.printStackTrace();
	  }

	  connectionTask = new ConnectionTask(new ConnectionTask.CallBack() {

		@Override
		public boolean Done(JSONObject result) {
		  if(result != null){

			// TODO: Can be wrong: tutor is not yet available
			return true;

		  } else {

			// tutor is already available
			return false;
		  }
		}
	  });

	  connectionTask.delete_tutor_available(jObject);

	}

	return false;
  }


  /**
   * if the tutor pushes the offer to tutor button:
   * first check if there is already a active service
   * else notify server that tutor is ready to tutor
   */
  private void offerToTutor() {

	Log.i("OfferToTutor", "Tutor service request processing (offer to tutor method)");

	if(!_ServiceRequestActive){

	  // make_tutor_available and if he is already available should not return 200 OK


	} else {



	}
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

	_locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

	_locationListener = new LocationListener() {
	  @Override
	  public void onLocationChanged(Location location) {
		updateMap(location);
	  }
	};

	// Checking for GPS permissions
	if (Build.VERSION.SDK_INT < 23) {

	  _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) _locationListener);

	} else {

	  if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

		ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

	  } else {

		// else we have permission
		_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (android.location.LocationListener) _locationListener);

		// gets last knwon location else it'll update the location based on the current location
		Location lastKnownLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (lastKnownLocation != null) {
		  updateMap(lastKnownLocation);
		}
	  }
	}
  }


  /**
   * Updated the map by clearing everything and adding location markers
   * @param location
   */
  private void updateMap(Location location){
	LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

	mMap.clear(); // clears all existing markers
	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
	mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

  }



}
