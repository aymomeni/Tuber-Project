package cs4000.tuber;

import android.app.Application;
import android.net.ParseException;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Ali on 11/30/2016.
 */

public class StarterApplication extends Application {

  String appId;
  String clientKey;
  String serverUrl;

  @Override
  public void onCreate() {
	super.onCreate();

	appId = getResources().getString(R.string.parse_app_id);
	clientKey = getResources().getString(R.string.parse_client_key); // should be blank
	serverUrl = getResources().getString(R.string.parse_server);
	Log.i("StarterApplication", "appId=" + appId + ", clientKey=" + clientKey + ", serverUrl=" + serverUrl);

	// Enable Local Datastore.
	Parse.enableLocalDatastore(this);

	// OPTIONAL https://github.com/ParsePlatform/ParseInterceptors-Android/wiki/ParseLogInterceptor
	//Parse.addParseNetworkInterceptor(new ParseLogInterceptor());

	// Add your initialization code here
	Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
			.applicationId(appId)
			.clientKey(clientKey) // can leave out since it is blank
			.server(serverUrl)
			.build()
	);

	ParseUser.enableAutomaticUser();
	ParseACL defaultACL = new ParseACL();
	// Optionally enable public read access.
	//defaultACL.setPublicReadAccess(true);
	//defaultACL.setPublicWriteAccess(true);
	ParseACL.setDefaultACL(defaultACL, true);

	// object = table
	// feel free to enter your own test values here
	ParseObject parseObject = new ParseObject("Test");
	parseObject.put("pasta", "cannelloni");
	parseObject.put("price", 4.99);

	Log.i("StarterApplication", "Attempting to save...");
	parseObject.saveInBackground(new SaveCallback() {
	  @Override
	  public void done(com.parse.ParseException e) {
		if (e == null) {
		  Log.i("saveInBackground", "Success... congrats!");
		} else {
		  Log.i("saveInBackground", "Ooops... " + e.toString());
		}
	  }
	});
  }
}