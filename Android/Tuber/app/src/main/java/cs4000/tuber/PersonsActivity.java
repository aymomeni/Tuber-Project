package cs4000.tuber;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

public class PersonsActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;

    ArrayList<Person> persons;

    PersonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons);


        // Lookup the recyclerview in activity layout
        RecyclerView Persons_rv = (RecyclerView) findViewById(R.id.persons_rv);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

//        // Item devider
//        RecyclerView.ItemDecoration itemDecoration = new
//                DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
//        Persons_rv.addItemDecoration(itemDecoration);

        // onClick Listner
        ItemClickSupport.addTo(Persons_rv).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        if (position != RecyclerView.NO_POSITION) { // Check if an item was deleted, but the user clicked it before the UI removed it
//                            TextView temoV = (TextView) v.findViewById(R.id.contact_name);
//                            //Contact temp = contacts.get(position);
//                            // We can access the data within the views
//                            temoV.setText("Changed");
//                            Toast.makeText(getBaseContext(), temoV.getText(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
        );

        // Swipe detection
        Persons_rv.setOnFlingListener(new RecyclerViewSwipeListener(true) {
            @Override
            public void onSwipeDown() {
                Toast.makeText(PersonsActivity.this, "swipe down", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSwipeUp() {
                Toast.makeText(PersonsActivity.this, "swipe up", Toast.LENGTH_SHORT).show();
            }
        });


        // Initialize contacts
        persons = Person.createPersonsList(20);
        // Create adapter passing in the sample user data
        adapter = new PersonAdapter(this, persons);
        // Set layout manager to position the items
        Persons_rv.setLayoutManager(new LinearLayoutManager(this));
        // Attach the adapter to the recyclerview to populate items
        Persons_rv.setAdapter(adapter);
    }

    public void fetchTimelineAsync(int page) {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.

        ConnectionTask task = new ConnectionTask(new JSONObject());
        task.find_available_tutors(new ConnectionTask.CallBack() {
            @Override
            public void Done(JSONObject result) {
                // Remember to CLEAR OUT old items before appending in the new ones
                //adapter.clear();
                // ...the data has come back, add new items to your adapter...
                //adapter.addAll(...);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });


//        client.getHomeTimeline(0, new JsonHttpResponseHandler() {
//            public void onSuccess(JSONArray json) {
//                // Remember to CLEAR OUT old items before appending in the new ones
//                adapter.clear();
//                // ...the data has come back, add new items to your adapter...
//                adapter.addAll(...);
//                // Now we call setRefreshing(false) to signal refresh has finished
//                swipeContainer.setRefreshing(false);
//            }
//
//            public void onFailure(Throwable e) {
//                Log.d("DEBUG", "Fetch timeline error: " + e.toString());
//            }
//        });
    }
}
