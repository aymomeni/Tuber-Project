package cs4000.tuber;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


/*
 * Displays the students's location and the tutors location on a google map
 * using information that is send through the ImmediateStudentRequestActivity
 */
public class StudentMapActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;
  Intent intent;


  public void acceptTutorService(View view){

	ParseQuery<ParseObject> query = ParseQuery.getQuery("TutorServices");

	query.whereEqualTo("username", intent.getStringExtra("username"));

	query.findInBackground(new FindCallback<ParseObject>(){
	  @Override
	  public void done(List<ParseObject>objects, ParseException e){

		if(e == null) {

		  if(objects.size() > 0) {

			for(ParseObject object : objects) {

			  object.put("studentUsername", ParseUser.getCurrentUser().getUsername());

			  object.saveInBackground(new SaveCallback() {
				@Override
				public void done(ParseException e) {

				  	if(e == null){

					  // pairing complete
					  // Intent where something occures after the pairing

					}
				}
			  });
			}
		  }
		}
	  }
	});


  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.activity_student_map);


	intent = getIntent();

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



  }
}
