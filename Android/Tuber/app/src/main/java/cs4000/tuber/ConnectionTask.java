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
 *
 * This is a connector class that works as the middleman between the app and the server.
 *
 * It connects the app to the server by taking two parameters:
 *  - Type of request (String)
 *  - JSON object in string form (String)
 * And return one JSON object that the caller can use.
 *
 * Each request must be initialize using a new task.
 *
 *
 * Example of how to use this class:
 * # Create your JSON ogject that you want to send to server, call it <MyJSON>
 * # Initialize new task
 * ConnectionTask <TASK_NAME> = new ConnectionTask(new ConnectionTask.TaskListener() {
 *      @Override
 *      public void onFinished(JSONObject <NAME_OF_OBJECT>) {
 *          // Do Something after the task has finished...
 *      }
 * });
 * <TASK_NAME>.execute(<TYPE_OF_REQUEST>, <MyJSON>.toString());
 *
 * Example of type of request: "/verifyuser"
 *
 */

public class ConnectionTask extends AsyncTask<String, Void, JSONObject> {
    public interface TaskListener {
        public void onFinished(JSONObject result);
    }

    // This is the reference to the associated listener
    private final ConnectionTask.TaskListener taskListener;

    private final String server_url = "http://tuber-test.cloudapp.net/ProductRESTService.svc";
    private JSONObject jsonParam = null;

    public ConnectionTask(ConnectionTask.TaskListener taskListener) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            //Set-up connection
            url = new URL(newURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput (true);
            urlConnection.setDoOutput (true);
            urlConnection.setUseCaches (false);
            urlConnection.setRequestProperty("Content-Type","application/json");
            urlConnection.connect();

            // Send POST output.
            printout = new DataOutputStream(urlConnection.getOutputStream ());
            printout.writeBytes(jsonParam.toString());
            printout.flush ();
            printout.close ();

            // Receive response
            int HttpResult = urlConnection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK) {
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

            Log.i("ServerResponse",  urlConnection.getResponseMessage());
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
        if(this.taskListener != null) {
            // And if it is we call the callback function on it.
            this.taskListener.onFinished(result);
        }
    }
}
