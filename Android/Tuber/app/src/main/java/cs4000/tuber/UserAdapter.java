package cs4000.tuber;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by FahadTmem on 2/14/17.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class UserAdapter extends
        RecyclerView.Adapter<UserAdapter.ViewHolder> {

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

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.iTitle);
            userDistance = (TextView) itemView.findViewById(R.id.iSubTitle);
        }
    }


    // Store a member variable for the contacts
    private List<User> users;
    // Store the context for easy access
    private Context Context;

    // Pass in the contact array into the constructor
    public UserAdapter(Context context, List<User> users) {
        this.users = users;
        Context = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return Context;
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return users.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        int curSize = getItemCount();
        //users.clear();
        //notifyDataSetChanged();
        for(int i = 0; i < curSize; i++) {
            this.remove(0);
        }
    }

    // Add a list of items
    public void addAll(List<User> list) {

//        for (User p: list) {
//            this.add(p);
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

        int curSize = getItemCount();
        users.addAll(list);
        notifyItemRangeInserted(curSize, list.size());
    }

    // Add a list of items
    public void add(User user) {
        users.add(user);
        notifyItemInserted(users.size() - 1);
        //scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Add a list of items
    public void add(User user, int i) {
        users.add(i, user);
        notifyItemInserted(i);
        //scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Add a list of items
    public void remove(User user) {
        int curPos = users.indexOf(user);
        users.remove(user);
        notifyItemRemoved(curPos);
        //notifyDataSetChanged();
    }

    // Add a list of items
    public void remove(int i) {
        users.remove(i);
        notifyItemRemoved(i);
        //notifyDataSetChanged();
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View personView = inflater.inflate(R.layout.person_layout, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(personView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        User user = users.get(position);

        // Set item views based on your views and data model
        TextView textView1 = viewHolder.userName;
        textView1.setText(user.getUserEmail());

        TextView textView2 = viewHolder.userDistance;
        textView2.setText(String.valueOf(user.getDistance()) + " miles");
    }
}