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
    private String mHotspotID;
    private String mOwnerEmail;
    private String mStudentCount;

    public HotspotObjects() {}

    public void setmCourse(String mCourse) {
        this.mCourse = mCourse;
    }

    public String getmCourse() {
        return mCourse;
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

    public String getmHotspotID() {
        return mHotspotID;
    }

    public void setmHotspotID(String mHotspotID) {
        this.mHotspotID = mHotspotID;
    }

    public String getmOwnerEmail() {
        return mOwnerEmail;
    }

    public void setmOwnerEmail(String mOwnerEmail) {
        this.mOwnerEmail = mOwnerEmail;
    }

    public String getmStudentCount() {
        return mStudentCount;
    }

    public void setmStudentCount(String mStudentCount) {
        this.mStudentCount = mStudentCount;
    }


}
