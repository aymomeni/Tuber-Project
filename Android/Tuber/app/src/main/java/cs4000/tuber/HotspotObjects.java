package cs4000.tuber;

/**
 * Created by Ali on 2/21/2017.
 */

/**
 * Represents instances of the study hotspot students
 */
public class HotspotObjects {


    private String mCourse;
    private String mLongitude;
    private String mLatitude;
    private int mHotspotID;
    private String mOwnerEmail;
    private int mStudentCount;

    public HotspotObjects() {}

    public String getmCourse() {
        return mCourse;
    }

    public void setmCourse(String mCourse) {
        this.mCourse = mCourse;
    }

    public String getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(String mLongitude) {
        this.mLongitude = mLongitude;
    }

    public String getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(String mLatitude) {
        this.mLatitude = mLatitude;
    }

    public int getmHotspotID() {
        return mHotspotID;
    }

    public void setmHotspotID(int mHotspotID) {
        this.mHotspotID = mHotspotID;
    }

    public String getmOwnerEmail() {
        return mOwnerEmail;
    }

    public void setmOwnerEmail(String mOwnerEmail) {
        this.mOwnerEmail = mOwnerEmail;
    }

    public int getmStudentCount() {
        return mStudentCount;
    }

    public void setmStudentCount(int mStudentCount) {
        this.mStudentCount = mStudentCount;
    }


}
