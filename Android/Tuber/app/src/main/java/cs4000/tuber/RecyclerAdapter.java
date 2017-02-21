package cs4000.tuber;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ali on 2/7/2017.
 */


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<RecyclerCourseObject> dataList;


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        RecyclerCourseObject ru = dataList.get(position);

        if(ru.type=="one")
        {
            //typecast
            DataViewHolder dataViewHolder=(DataViewHolder) viewHolder;

            dataViewHolder.title.setText(ru.course);
            dataViewHolder.subTitle.setText(ru.subTitle);

        }
        else
        {
            DataViewHolder dataViewHolder=(DataViewHolder) viewHolder;
            dataViewHolder.title.setText(ru.course);
            dataViewHolder.subTitle.setText(ru.subTitle);
        }

    }

    public RecyclerAdapter(ArrayList<RecyclerCourseObject> dataList)
    {
        this.dataList = dataList;
    }


    public void clear() {
        int curSize = getItemCount();
        for(int i = 0; i < curSize; i++) {
            this.remove(0);
        }
    }

    // Add a list of items
    public void addAll(List<RecyclerCourseObject> list) {
        int curSize = getItemCount();
        dataList.addAll(list);
        notifyItemRangeInserted(curSize, list.size());
    }

    // Add a list of items
    public void add(RecyclerCourseObject listItem) {
        dataList.add(listItem);
        notifyItemInserted(dataList.size() - 1);
        //scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Add a list of items
    public void add(RecyclerCourseObject listItem, int i) {
        dataList.add(i, listItem);
        notifyItemInserted(i);
        //scrollToPosition(mAdapter.getItemCount() - 1);
    }

    // Add a list of items
    public void remove(RecyclerCourseObject listItem) {
        int curPos = dataList.indexOf(listItem);
        dataList.remove(listItem);
        notifyItemRemoved(curPos);
    }

    // Add a list of items
    public void remove(int i) {
        dataList.remove(i);
        notifyItemRemoved(i);
    }

//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//
//        View itemView;
//        RecyclerView.ViewHolder viewHold;
//        switch(viewType)
//        {
//            case 0:
//                itemView = LayoutInflater.
//                        from(viewGroup.getContext()).
//                        inflate(R.layout.course_layout_linear_student, viewGroup, false);
//                //itemView.setOnClickListener(mOnClickListener);
//                viewHold= new DataViewHolder(itemView);
//                break;
//
//            default:
//                itemView = LayoutInflater.
//                        from(viewGroup.getContext()).
//                        inflate(R.layout.course_layout_grid, viewGroup, false);
//                viewHold= new DataViewHolder2(itemView);
//                break;
//        }
//
//        return viewHold;
//    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView;
        RecyclerView.ViewHolder viewHold;
        switch(viewType)
        {
            case 0:
                itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.course_layout_linear_student, viewGroup, false);
                //itemView.setOnClickListener(mOnClickListener);
                viewHold= new DataViewHolder(itemView);
                break;

            default:
                itemView = LayoutInflater.
                        from(viewGroup.getContext()).
                        inflate(R.layout.course_layout_linear_tutor, viewGroup, false);
                viewHold= new DataViewHolder(itemView);
                break;
        }

        return viewHold;
    }

    @Override
    public int getItemViewType(int position) {
        //More to come
        if(dataList.get(position).type=="one")
        {
            return 0;
        }
        else
        {
            return 1;
        }

    }


    public static class DataViewHolder extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView subTitle;

        public DataViewHolder(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.iTitle);
            subTitle = (TextView)  v.findViewById(R.id.iSubTitle);
        }
    }

    public static class DataViewHolder2 extends RecyclerView.ViewHolder {

        protected TextView title;
        protected TextView subTitle;

        public DataViewHolder2(View v) {
            super(v);
            title =  (TextView) v.findViewById(R.id.uTitle);
            subTitle = (TextView)  v.findViewById(R.id.uSubTitle);
        }
    }

}
