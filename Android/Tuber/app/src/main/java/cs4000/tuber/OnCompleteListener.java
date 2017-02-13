package cs4000.tuber;

/**
 * Created by FahadTmem on 2/12/17.
 */

public interface OnCompleteListener {
    public abstract void onTimeComplete(int hour, int minute);
    public abstract void onDateComplete(int month, int day, int year);
}
