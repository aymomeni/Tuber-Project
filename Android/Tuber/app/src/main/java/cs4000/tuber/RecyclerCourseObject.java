package cs4000.tuber;

/**
 * Created by Ali on 2/7/2017.
 */

/**
 * Represents the container for Course Android Cards/Adapter
 */
public class RecyclerCourseObject {

    String course; // course name -> CS 1400
    String subTitle; // course subTitle must be done in the future
    String type; // unused right now

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
