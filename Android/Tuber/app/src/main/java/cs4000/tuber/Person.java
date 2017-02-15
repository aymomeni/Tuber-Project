package cs4000.tuber;

import java.util.ArrayList;

/**
 * Created by FahadTmem on 2/15/17.
 */

public class Person {

    private String userEmail;
    private String firstName;
    private String lastName;
    private Double distance;

    private Double Longitude;
    private Double Latitudes;

    public static int lastContactId = 0;

    public Person(String userEmail, Double miles) {
        this.userEmail = userEmail;
        distance = miles;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }


    public static ArrayList<Person> createPersonsList(int numContacts) {
        ArrayList<Person> persons = new ArrayList<Person>();

        for (int i = 1; i <= numContacts; i++) {
            persons.add(new Person("User " + ++lastContactId, i + 5.5));
        }

        return persons;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public void setLongitude(Double longitude) {
        Longitude = longitude;
    }

    public Double getLatitudes() {
        return Latitudes;
    }

    public void setLatitudes(Double latitudes) {
        Latitudes = latitudes;
    }
}