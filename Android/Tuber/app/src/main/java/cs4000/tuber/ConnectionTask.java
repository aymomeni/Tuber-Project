package cs4000.tuber;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by FahadTmem on 1/26/17.
 */

/**
 * README:
 * <p>
 * This is a connector class that works as the middleman between the app and the server.
 * <p>
 * It connects the app to the server using methods that each represents
 * a functionality that is supported by the server and takes a JSON object
 * as its parameter to be sent to the server.
 * <p>
 * Some methods return JSON object upon completion and others return nothing.
 * <p>
 * All methods return "OK" if connection was success and "Bad Request" or null
 * if the connection failed or there is a problem with the request.
 * <p>
 * Each request must be initialize using a new Connection task.
 * <p>
 * <p>
 * Example of how to use this class:
 * # Create your JSON object that you want to send to server, call it <MyJSON>
 * # Initialize new task:
 * ConnectionTask <TASK_NAME> = new ConnectionTask(new ConnectionTask.CallBack() {
 *
 * @Override public void Done(JSONObject <NAME_OF_OBJECT>) {
 * if(<NAME_OF_OBJECT> != null) {
 * // Do Something after the task has finished...
 * } else {
 * // Handle the exception here...
 * }
 * }
 * });
 * <TASK_NAME>.<DESIRED_METHOD>(<MyJSON>);
 * <p>
 * <p>
 * Example of creating JSON object:
 * JSONObject <MyJSON> = new JSONObject();
 * try {
 * <MyJSON>.put("userEmail", "u0000002@utah.edu");
 * <MyJSON>.put("userPassword", "testing799");
 * } catch (JSONException e) {
 * e.printStackTrace();
 * }
 * <p>
 * <p>
 * Example of extracting a JSON object:
 * try {
 * String usrname = <MyJSON>.getString("userEmail");
 * String password = <MyJSON>.getString("userPassword");
 * } catch (JSONException e) {
 * e.printStackTrace();
 * }
 */

public class ConnectionTask extends AsyncTask<String, Void, JSONObject> {
	public interface CallBack {
		public boolean Done(JSONObject result);
	}

	// This is the reference to the associated listener
	private final ConnectionTask.CallBack taskListener;
	private boolean isOfferingToTutor = false; // EDIT: ALI - Needed for offering to tutor
	private final String server_url = "http://tuber-test.cloudapp.net/ProductRESTService.svc";
	private JSONObject jsonParam = null;

	public ConnectionTask(ConnectionTask.CallBack taskListener) {
		// The listener reference is passed in through the constructor
		this.taskListener = taskListener;
	}

	@Override
	protected JSONObject doInBackground(String... params) {

		String result = "";
		URL url;
		HttpURLConnection urlConnection = null;
		DataOutputStream printout = null;
		String newURL = server_url + params[0];

		try {
			jsonParam = new JSONObject(params[1]);

			//Set-up connection
			url = new URL(newURL);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.connect();

			// Send POST output.
			printout = new DataOutputStream(urlConnection.getOutputStream());
			printout.writeBytes(jsonParam.toString());
			printout.flush();
			printout.close();

			// Receive response
			int HttpResult = urlConnection.getResponseCode();

			// EDIT: ALI - If bad request and /checkpai
			if(HttpResult == HttpURLConnection.HTTP_BAD_REQUEST && params[0].equals("/checkpairedstatus")) {
				isOfferingToTutor = false;
				return null;
			}

			if (HttpResult == HttpURLConnection.HTTP_OK) {
				// Receiving JSON response
				InputStream in = urlConnection.getInputStream();
				InputStreamReader reader = new InputStreamReader(in);
				int data = reader.read();

				while (data != -1) {
					char current = (char) data;
					result += current;
					data = reader.read();
				}
			}

			// EDIT: ALI - If ok is returned for /checkpairedstatus and json is null the tutor is currently offering to tutor (but isn't paired yet)
			if(HttpResult == HttpURLConnection.HTTP_OK && params[0].equals("/checkpairedstatus")) {
				if(result == null){
					isOfferingToTutor = true;
					return null;
				} else {
					isOfferingToTutor = false;
				}
			}

			Log.i("ServerResponse", urlConnection.getResponseMessage());
			return new JSONObject(result);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);

		// In onPostExecute we check if the listener is valid
		if (this.taskListener != null) {
			// And if it is we call the callback function on it.
			this.taskListener.Done(result);
		}
	}


	/**
	 * EDIT: ALI
	 * Returns true if a tutor is currently offering to tutor
	 *
	 * @return isOfferingToTutor
	 */
	public boolean getIsOfferingToTutor() {
		return isOfferingToTutor;
	}

	/************************************************************
	 *  USER ACCOUNT FUNCTIONS                                  *
	 ************************************************************/
	/**
	 * @Send POST
	 * {
	 * "userEmail" : "u0820304@utah.edu",
	 * "userPassword" : "testing123",
	 * "userFirstName" : "Brandon",
	 * "userLastName" : "Tobin",
	 * "userBillingAddress" : "219 Clayton Dr.",
	 * "userBillingCity" : "Yorktown",
	 * "userBillingState" : "VA",
	 * "userBillingCCNumber" : "1234567890112233",
	 * "userBillingCCExpDate" : "2018-01-19",
	 * "userBillingCCV" : "801"
	 * }
	 * @Returns 200 OK
	 * {
	 * "userEmail": "u0820304@utah.edu",
	 * "userPassword": "testing123"
	 * }
	 */
	public void create_user(JSONObject obj) {
		this.execute("/createuser", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin@cox.net",
	 * "userPassword" : "testing"
	 * }
	 * @Returns 200 OK
	 * {
	 * "userEmail": "brandontobin@cox.net",
	 * "userPassword": "testing",
	 * "userStudentCourses": [
	 * "CS 4000"
	 * ],
	 * "userToken": "6886bfe7-7d99-41f4-b569-b689e8d627c3",
	 * "userTutorCourses": [
	 * "CS 3500"
	 * ],
	 * }
	 */
	public void verify_user(JSONObject obj) {
		this.execute("/verifyuser", obj.toString());
	}

	/************************************************************
	 *  IMMEDIATE TUTOR FUNCTIONS                               *
	 ************************************************************/
	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "tutorCourse" : "CS 4000",
	 * "latitude" : "40.867700",
	 * "longitude" : "111.845200"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void make_tutor_available(JSONObject obj) {
		this.execute("/maketutoravailable", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void delete_tutor_available(JSONObject obj) {
		this.execute("/deletetutoravailable", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 *  "userEmail" : "anne@cox.net",
	 *  "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 *  "tutorCourse" : "CS 4000",
	 *  "latitude" : "40.867700",
	 *  "longitude" : "111.845200"
	 * }
	 * @Returns 200 OK
	 * [
	 *  {
	 *   "distanceFromStudent": 2.709236634958018,
	 *   "latitude": 40.7677,
	 *   "longitude": 111.8452,
	 *   "tutorCourse": "CS 3500",
	 *   "userEmail": "brandontobin@cox.net"
	 *  }
	 * ],
	 */
	public void find_available_tutors(JSONObject obj) {
		this.execute("/findavailabletutors", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "requestedTutorEmail" : "brandontobin@cox.net",
	 * "studentLatitude" : "40.735140",
	 * "studentLongitude" : "111.816439"
	 * }
	 * @Returns: 200 OK
	 * {
	 * "requestedTutorEmail": "brandontobin@cox.net",
	 * "session_status": 0,
	 * "studentLatitude": "40.735140",
	 * "studentLongitude": "111.816439",
	 * "tutorCourse": "CS 3500",
	 * "tutorLatitude": "40.867700",
	 * "tutorLongitude": "111.845200",
	 * "userEmail": "anne@cox.net",
	 * "userToken": "127ef466-2210-4b87-9f39-06cacd4b6cf5"
	 * }
	 */
	public void pair_student_tutor(JSONObject obj) {
		this.execute("/pairstudenttutor", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin@cox.net",
	 * "userToken" : "be809b50-dbbb-4e41-ac83-d2f817d9f957"
	 * }
	 *
	 * @Returns: 200 OK
	 * IF student has not been paired with tutor
	 * empty JSON OBJECT
	 * ELSE
	 * {
	 * "session_status": 0,
	 * "studentEmail": "brandontobin2@cox.net",
	 * "studentLatitude": "40.867700",
	 * "studentLongitude": "111.845201",
	 * "tutorCourse": "CS 3500",
	 * "tutorLatitude": "40.867700",
	 * "tutorLongitude": "111.845200",
	 * "userEmail": "brandontobin@cox.net",
	 * "userToken": null
	 * }
	 *
	 * RETURNS 400 BAD request/null JSON if tutor has not been offering to tutor TODO: Could change Feb 3
	 * RETURNS 200 OK/null JSON if tutor has been offering but is not paired yet
	 * RETURNS 200 OK/non-empty JSON if tutor has been paired
	 */
	public void check_paired_status(JSONObject obj) {
		this.execute("/checkpairedstatus");
	}


	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "course" : "CS 3500"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void start_tutor_session(JSONObject obj) {
		this.execute("/starttutorsession", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "course" : "CS 3500"
	 * }
	 * @Returns 200 OK
	 * {
	 * "course": "CS 3500",
	 * "sessionCost": 16.11,
	 * "sessionEndTime": "01,31,2017 12:49:46 PM",
	 * "sessionStartTime": "01,31,2017 11:45:19 AM",
	 * "studentEmail": "brandontobin2@cox.net",
	 * "userEmail": "brandontobin@cox.net"
	 * }
	 */
	public void end_tutor_session(JSONObject obj) {
		this.execute("/endtutorsession", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin2@cox.net",
	 * "userToken" : "3762e8ed-9112-4964-99e6-0b1ce4da18e9",
	 * "latitude" : "41.867700",
	 * "longitude" : "111.845200"
	 * }
	 * @Returns 200 OK
	 * {
	 * "tutorEmail": "brandontobin@cox.net",
	 * "tutorLatitude": "40.867700",
	 * "tutorLongitude": "111.845200"
	 * }
	 */
	public void update_student_location(JSONObject obj) {
		this.execute("/updatestudentlocation", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin2@cox.net",
	 * "userToken" : "3762e8ed-9112-4964-99e6-0b1ce4da18e9",
	 * "latitude" : "41.867700",
	 * "longitude" : "111.845200"
	 * }
	 * @Returns 200 OK
	 * {
	 * "studentEmail": "brandontobin@cox.net",
	 * "studentLatitude": "40.867700",
	 * "studentLongitude": "111.845200"
	 * }
	 */
	public void update_tutor_location(JSONObject obj) {
		this.execute("/updatetutorlocation", obj.toString());
	}

	/************************************************************
	 *  STUDY HOTSPOT FUNCTIONS                                 *
	 ************************************************************/
	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "course" : "CS 4000",
	 * "latitude" : "40.867700",
	 * "longitude" : "111.845200"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void create_study_hotspot(JSONObject obj) {
		this.execute("/createstudyhotspot", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "course" : "CS 4000",
	 * "latitude" : "40.867700",
	 * "longitude" : "111.845200"
	 * }
	 * @Returns 200 OK
	 * {
	 * "studyHotspots": [
	 * {
	 * "course": "CS 4000",
	 * "distanceToHotspot": 0.00005229515916537725,
	 * "hotspotID": "11",
	 * "latitude": 40.867701,
	 * "longitude": 111.8452,
	 * "ownerEmail": "brandontobin@cox.net",
	 * "student_count": "1"
	 * },
	 * ],
	 * }
	 */
	public void find_study_hotspots(JSONObject obj) {
		this.execute("/findstudyhotspots", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "hotspotID" : "8"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void join_study_hotspots(JSONObject obj) {
		this.execute("/joinstudyhotspot", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void leave_study_hotspots(JSONObject obj) {
		this.execute("/leavestudyhotspot", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "studyHotspotID" : "8"
	 * }
	 * @Returns 200 OK
	 * [
	 * {
	 * "firstName": "Anne",
	 * "lastName": "Aoki",
	 * "userEmail": "anne@cox.net"
	 * },
	 * {
	 * "firstName": "Brandon",
	 * "lastName": "Tobin",
	 * "userEmail": "u0820304@utah.edu"
	 * }
	 * ],
	 */
	public void get_study_hotspot_members(JSONObject obj) {
		this.execute("/getstudyhotspotmembers", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "hotspotID" : "8"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void delete_study_hotspots(JSONObject obj) {
		this.execute("/deletestudyhotspot", obj.toString());
	}

	/************************************************************
	 *  SCHEDULE TUTOR FUNCTIONS                                *
	 ************************************************************/
	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "5d6378a7-f2e5-47bd-b36b-77317dd269cc",
	 * "course" : "CS 4000",
	 * "topic" : "Capstone Help",
	 * "dateTime" : "2017-02-14 12:00",
	 * "duration" : "2.5"
	 * }
	 * @Note dateTime must be in format YYYY-MM-DD HH:MM
	 * The time component is on a 24-hour clock (no AM/PM)
	 * @Returns 200 OK
	 * Nothing...
	 */
	public void schedule_tutor(JSONObject obj) {
		this.execute("/scheduletutor", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * "course" : "CS 4000"
	 * }
	 * @Returns 200 OK
	 * {
	 * "tutorRequestItems": [
	 * {
	 * "course": "CS 4000",
	 * "dateTime": "02,15,2017 1:45:00 PM",
	 * "duration": "2",
	 * "studentEmail": "u0820304@utah.edu",
	 * "topic": "Malloc"
	 * }
	 * ],
	 * }
	 */
	public void find_all_scheduled_tutor_requests(JSONObject obj) {
		this.execute("/findallscheduletutorrequests", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin2@cox.net",
	 * "userToken" : "289569ba-3022-44fe-b568-6dcfd88e0933",
	 * "studentEmail" : "anne@cox.net",
	 * "course" : "CS 4000"
	 * }
	 * @Returns 200 OK
	 * {
	 * "course": "CS 4000",
	 * "dateTime": "2017-02-15 15:45:00",
	 * "duration": "2.5",
	 * "student_email": "anne@cox.net",
	 * "topic": "Capstone Help",
	 * "tutor_email": "brandontobin@cox.net"
	 * }
	 */
	public void accept_student_scheduled_request(JSONObject obj) {
		this.execute("/acceptstudentscheduledrequest", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * }
	 * @Returns 200 OK
	 * "requests": [
	 * {
	 * "course": "CS 3500",
	 * "dateTime": "2017-02-15 15:45:00",
	 * "duration": "2.5",
	 * "isPaired": false,
	 * "studentEmail": "anne@cox.net",
	 * "topic": "Capstone Help",
	 * "tutorEmail": null
	 * }
	 * ],
	 * }
	 */
	public void check_scheduled_paired_status(JSONObject obj) {
		this.execute("/checkscheduledpairedstatus", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 *
	 * @Note Use the same /endtutorsession in the Immediate tutor
	 * functions section to end the tutoring session.
	 */
	public void start_scheduled_tutor_session(JSONObject obj) {
		this.execute("/scheduletutor", obj.toString());
	}

	/************************************************************
	 *  POST TUTOR SESSION FUNCTIONS                            *
	 ************************************************************/
	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin2@cox.net",
	 * "userToken" : "3762e8ed-9112-4964-99e6-0b1ce4da18e9",
	 * "tutorSessionID" : "2",
	 * "tutorEmail" : "brandontobin@cox.net",
	 * "rating" : "0.5"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 *
	 * @Note Make sure the rating is in the form of 2.5 and the rating number is less than 5.
	 * If you receive response code "406 : Not Acceptable", then there is already an entry in
	 * the tutor_ratings table for the tutor session ID provided.
	 */
	public void rate_tutor(JSONObject obj) {
		this.execute("/ratetutor", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin@cox.net",
	 * "userToken" : "289569ba-3022-44fe-b568-6dcfd88e0933",
	 * "tutorSessionID" : "2",
	 * "studentEmail" : "brandontobin2@cox.net",
	 * "rating" : "4.7"
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 *
	 * @Note Make sure the rating is in the form of 2.5 and the rating number is less than 5.
	 * If you receive response code "406 : Not Acceptable", then there is already an entry in
	 * the tutor_ratings table for the tutor session ID provided.
	 */
	public void rate_student(JSONObject obj) {
		this.execute("/ratestudent", obj.toString());
	}

	/************************************************************
	 *  REPORT TUTOR FUNCTIONS                                  *
	 ************************************************************/
	/**
	 * @Send POST
	 * {
	 * "userEmail" : "anne@cox.net",
	 * "userToken" : "127ef466-2210-4b87-9f39-06cacd4b6cf5",
	 * }
	 * @Returns 200 OK
	 * {
	 * "tutorList": [
	 * {
	 * "tutorEmail": "brandontobin@cox.net",
	 * "tutorFirstName": "Brandon",
	 * "tutorLastName": "Tobin"
	 * }
	 * ],
	 * }
	 *
	 * @Note Use this method to retrieve all tutors a student has met with, and then
	 * allow them to select the tutor to report.
	 */
	public void reportTutor_get_tutorList(JSONObject obj) {
		this.execute("/reporttutorgettutorlist", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin2@cox.net",
	 * "userToken" : "3762e8ed-9112-4964-99e6-0b1ce4da18e9",
	 * "tutorEmail": "brandontobin@cox.net",
	 * "tutorFirstName": "Brandon",
	 * "tutorLastName": "Tobin"
	 * }
	 * @Returns 200 OK
	 * {
	 * "tutorList": [
	 * {
	 * "course": "CS 3500",
	 * "sessionCost": "332.90",
	 * "sessionEndTime": "02,01,2017 9:56:56 AM",
	 * "sessionStartTime": "01,31,2017 11:45:19 AM",
	 * "tutorEmail": "brandontobin@cox.net",
	 * "tutorFirstName": "Brandon",
	 * "tutorLastName": "Tobin",
	 * "tutorSessionID": "1"
	 * },
	 * {
	 * "course": "CS 3500",
	 * "sessionCost": "334.60",
	 * "sessionEndTime": "02,01,2017 10:03:43 AM",
	 * "sessionStartTime": "01,31,2017 11:45:19 AM",
	 * "tutorEmail": "brandontobin@cox.net",
	 * "tutorFirstName": "Brandon",
	 * "tutorLastName": "Tobin",
	 * "tutorSessionID": "2"
	 * }
	 * ],
	 * }
	 *
	 * @Note Use this method to retrieve all sessions the student and selected tutor have had,
	 * and then allow the student to select the session to report.
	 */
	public void reportTutor_get_sessionList(JSONObject obj) {
		this.execute("/reporttutorgetsessionlist", obj.toString());
	}

	/**
	 * @Send POST
	 * {
	 * "userEmail" : "brandontobin2@cox.net",
	 * "userToken" : "3762e8ed-9112-4964-99e6-0b1ce4da18e9",
	 * "tutorEmail": "brandontobin@cox.net",
	 * "tutorSessionID": "2",
	 * "message": "Tutor was not up to par with the topic and was not helpful in any way."
	 * }
	 * @Returns 200 OK
	 * Nothing...
	 *
	 * @Note If the tutor has 5 or more reports in the DB, then the tutor's eligibility will be set to
	 * 0 (can't tutor anymore)
	 */
	public void report_tutor(JSONObject obj) {
		this.execute("/reporttutor", obj.toString());
	}
}