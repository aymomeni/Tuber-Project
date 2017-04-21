package cs4000.tuber;

/**
 * Created by Ali on 2/21/2017.
 */

/**
 * Represents instances of the study hotspot students
 */
public class HotspotObject {

    private String mCourse;
    private String mTopic;
    private double mdistanceToHotspot;
    private String mHotspotID;
    private double mLongitude;
    private double mLatitude;
    private String mOwnerEmail;
    private String mStudentCount;
    private String mLocationDiscription;

    public HotspotObject() {}

    public String getmCourse() {
        return mCourse;
    }

    public void setmCourse(String mCourse) {
        this.mCourse = mCourse;
    }

    public double getMdistanceToHotspot() {
        return mdistanceToHotspot;
    }

    public void setMdistanceToHotspot(double mdistanceToHotspot) {
        this.mdistanceToHotspot = mdistanceToHotspot;
    }

    public String getmTopic() {
        return mTopic;
    }

    public void setmTopic(String mTopic) {
        this.mTopic = mTopic;
    }

    public String getmLocationDiscription() {
        return mLocationDiscription;
    }

    public void setmLocationDiscription(String mLocationDiscription) {
        this.mLocationDiscription = mLocationDiscription;
    }

    public String getmHotspotID() {
        return mHotspotID;
    }

    public void setmHotspotID(String mHotspotID) {
        this.mHotspotID = mHotspotID;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
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
