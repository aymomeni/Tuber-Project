package cs4000.tuber;

import android.content.Context;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.games.request.Requests;

import java.util.List;

/**
 * Created by FahadTmem on 2/14/17.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class RequestsAdapter extends
        RecyclerView.Adapter<RequestsAdapter.ViewHolder> {

//    public void swapItems(List<Contact> contacts) {
//        // compute diffs
//        final ContactDiffCallback diffCallback = new ContactDiffCallback(this.mContacts, contacts);
//        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);
//
//        // clear contacts and add
//        this.mContacts.clear();
//        this.mContacts.addAll(contacts);
//
//        diffResult.dispatchUpdatesTo(this); // calls adapter's notify methods after diff is computed
//    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView userName;
        public TextView userDistance;
        public TextView numOfRatings;
        public RatingBar userRating;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.iTitle);
            userDistance = (TextView) itemView.findViewById(R.id.iSubTitle);
            userRating = (RatingBar) itemView.findViewById(R.id.userRating);
            numOfRatings = (TextView) itemView.findViewById(R.id.numOfRatings);
        }
    }


    // Store a member variable for the contacts
    private List<Request> Requests;
    // Store the context for easy access
    private android.content.Context Context;

    // Pass in the contact array into the constructor
    public RequestsAdapter(Context context, List<Request> requests) {
        Requests = requests;
        Context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return Context;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return Requests.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        int curSize = getItemCount();
        //Persons.clear();
        //notifyDataSetChanged();
        for(int i = 0; i < curSize; i++) {
            this.remove(0);
        }
    }

    // Add a list of items
    public void addAll(List<Request> list) {

//        for (User p: list) {
//            this.add(p);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        int curSize = getItemCount();
        Requests.addAll(list);
        notifyItemRangeInserted(curSize, list.size());
    }

    // Add a list of items
    public void add(Request request) {
        Requests.add(request);
        notifyItemInserted(Requests.size() - 1);
        //scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Add a list of items
    public void add(Request request, int i) {
        Requests.add(i, request);
        notifyItemInserted(i);
        //scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Add a list of items
    public void remove(Request request) {
        int curPos = Requests.indexOf(request);
        Requests.remove(request);
        notifyItemRemoved(curPos);
        //notifyDataSetChanged();
    }

    // Add a list of items
    public void remove(int i) {
        Requests.remove(i);
        notifyItemRemoved(i);
        //notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public RequestsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View requestView = inflater.inflate(R.layout.person_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(requestView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RequestsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        Request request = Requests.get(position);

        // Set item views based on your views and data model
        TextView textView1 = viewHolder.userName;
        textView1.setText(request.getTopic());

        TextView textView2 = viewHolder.userDistance;
        textView2.setText(String.valueOf(request.getCourse()));

        RatingBar rating_bar = viewHolder.userRating;
        //rating_bar.setRating(user.getUserRating());
        rating_bar.setVisibility(View.INVISIBLE);

        TextView textView3 = viewHolder.numOfRatings;
        //textView3.setText(String.valueOf(user.getNumOfRatings()));
        textView3.setVisibility(View.INVISIBLE);
    }
}