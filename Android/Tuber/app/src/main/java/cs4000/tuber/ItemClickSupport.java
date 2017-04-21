package cs4000.tuber;

/**
 * Created by FahadTmem on 2/15/17.
 */

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Created by FahadTmem on 2/14/17.
 */

/*
  Source: http://www.littlerobots.nl/blog/Handle-Android-RecyclerView-Clicks/
  USAGE:

  ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
      @Override
      public void onItemClicked(RecyclerView recyclerView, int position, View v) {
          // do it
      }
  });
*/
public class ItemClickSupport {
    // member variables
    private final RecyclerView mRecyclerView;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;



    // tutor courses listeners set-up
    private OnItemClickListenerTutor mOnItemClickListenerTutor;
    public interface OnItemClickListenerTutor {
        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }
    public ItemClickSupport setOnItemClickListenerTutor(OnItemClickListenerTutor listener) {
        mOnItemClickListenerTutor = listener;
        return this;
    }
    private View.OnClickListener mOnClickListenerTutor = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListenerTutor != null) {
                RecyclerView.ViewHolder holder = null;
                if(v instanceof LinearLayout){
                    holder = mRecyclerView.getChildViewHolder(v);
                } else {
                    holder = mRecyclerView.getChildViewHolder((View) (v.getParent().getParent().getParent().getParent().getParent()));
                }
                mOnItemClickListenerTutor.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };

    // student courses listeners set-up
    private OnItemClickListenerStudent mOnItemClickListenerStudent;
    public interface OnItemClickListenerStudent {
        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }
    public ItemClickSupport setOnItemClickListenerStudent(OnItemClickListenerStudent listener) {
        mOnItemClickListenerStudent = listener;
        return this;
    }
    private View.OnClickListener mOnClickListenerStudent = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListenerStudent != null) {
                RecyclerView.ViewHolder holder = null;
                if(v instanceof LinearLayout){
                    holder = mRecyclerView.getChildViewHolder(v);
                } else {
                    holder = mRecyclerView.getChildViewHolder((View) (v.getParent().getParent().getParent().getParent().getParent()));
                }
                mOnItemClickListenerStudent.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };



    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(final View view) {
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener);
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
            if (mOnItemClickListenerStudent != null) {
                if(view.findViewById(R.id.ic_study_hotspot_tower) != null) {
                    view.findViewById(R.id.ic_study_hotspot_tower).setOnClickListener(mOnClickListenerStudent);
                }
                if(view.findViewById(R.id.ic_tutor_glasses) != null) {
                    view.findViewById(R.id.ic_tutor_glasses).setOnClickListener(mOnClickListenerStudent);
                }
                if(view.findViewById(R.id.ic_offer_tutor_outline) != null) {
                    view.findViewById(R.id.ic_offer_tutor_outline).setOnClickListener(mOnClickListenerStudent);
                }
                if(view.findViewById(R.id.ic_scheduled_request) != null) {
                    view.findViewById(R.id.ic_scheduled_request).setOnClickListener(mOnClickListenerStudent);
                }
                if(view.findViewById(R.id.ic_message_closed) != null) {
                    view.findViewById(R.id.ic_message_closed).setOnClickListener(mOnClickListenerStudent);
                }
                if(view.findViewById(R.id.ic_red_minus) != null) {
                    view.findViewById(R.id.ic_red_minus).setOnClickListener(mOnClickListenerStudent);
                }
                view.setOnClickListener(mOnClickListenerStudent);
            }
            if (mOnItemClickListenerTutor != null) {
//                if(view.findViewById(R.id.ic_offer_tutor_outline) != null) {
//                    view.findViewById(R.id.ic_offer_tutor_outline).setOnClickListener(mOnClickListenerTutor);
//                }
                if(view.findViewById(R.id.ic_scheduled_request) != null) {
                    view.findViewById(R.id.ic_scheduled_request).setOnClickListener(mOnClickListenerTutor);
                }
                if(view.findViewById(R.id.ic_immediate_service) != null) {
                    view.findViewById(R.id.ic_immediate_service).setOnClickListener(mOnClickListenerTutor);
                }
                if(view.findViewById(R.id.ic_message_closed) != null) {
                    view.findViewById(R.id.ic_message_closed).setOnClickListener(mOnClickListenerTutor);
                }
                if(view.findViewById(R.id.ic_red_minus) != null) {
                    view.findViewById(R.id.ic_red_minus).setOnClickListener(mOnClickListenerTutor);
                }
                view.setOnClickListener(mOnClickListenerTutor);
            }
        }
        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    // Constructor
    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.item_click_support, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    // Methods
    public static ItemClickSupport addTo(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }
    public static ItemClickSupport removeFrom(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }
    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }
    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }
    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    // Interfaces
    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }
}