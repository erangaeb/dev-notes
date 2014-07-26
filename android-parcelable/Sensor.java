package com.score.senzors.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Plain object to hold sensor data attributes
 * This is a parcelable object since we need to pass sensor
 * objects between activities
 *
 * @author erangaeb@gmail.com (eranga herath)
 */
public class Sensor implements Parcelable {
    String id;
    String sensorName;
    String sensorValue;
    boolean isMySensor;
    User user;
    ArrayList<User> sharedUsers;

    public Sensor(String id, String sensorName, String sensorValue, boolean isMySensor, User user, ArrayList<User> sharedUsers) {
        this.id = id;
        this.sensorName = sensorName;
        this.sensorValue = sensorValue;
        this.isMySensor = isMySensor;
        this.user = user;
        this.sharedUsers = sharedUsers;
    }

    /**
     * Use when reconstructing User object from parcel
     * This will be used only by the 'CREATOR'
     * There are three special scenarios here
     *      1. read boolean value from int since we store boolean as an int value
     *      2. readParcelable method need class loader when reading User object
     *      3. read user list - readTypeList function need User.CREATOR
     * @param in a parcel to read this object
     */
    public Sensor(Parcel in) {
        this.id = in.readString();
        this.sensorName = in.readString();
        this.sensorValue = in.readString();
        this.isMySensor = (in.readInt() != 0);

        // readParcelable need class loader
        this.user = in.readParcelable(User.class.getClassLoader());

        // read list by using User.CREATOR
        this.sharedUsers = new ArrayList<User>();
        in.readTypedList(sharedUsers, User.CREATOR);
    }

    /**
     * Define the kind of object that you gonna parcel,
     * You can use hashCode() here
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Actual object serialization happens here, Write object content
     * to parcel one by one, reading should be done according to this write order
     * There are two special scenarios here
     *      1. write boolean value - we write boolean as int since no 'writeBoolean' method
     *         available. When reading it convert the int boolean
     *      2. write list - since user object also parcelable we can use 'writeTypedList'
     *         method to parcel user list
     * @param dest parcel
     * @param flags Additional flags about how the object should be written
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(sensorName);
        dest.writeString(sensorValue);
        dest.writeInt(isMySensor ? 1 : 0);
        dest.writeParcelable(user, flags);
        dest.writeTypedList(sharedUsers);
    }

    /**
     * This field is needed for Android to be able to
     * create new objects, individually or as arrays
     *
     * If you don’t do that, Android framework will through exception
     * Parcelable protocol requires a Parcelable.Creator object called CREATOR
     */
    public static final Parcelable.Creator<Sensor> CREATOR = new Parcelable.Creator<Sensor>() {

        public Sensor createFromParcel(Parcel in) {
            return new Sensor(in);
        }

        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public String getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }

    public boolean isMySensor() {
        return isMySensor;
    }

    public void setMySensor(boolean mySensor) {
        isMySensor = mySensor;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<User> getSharedUsers() {
        return sharedUsers;
    }

    public void setSharedUsers(ArrayList<User> sharedUsers) {
        this.sharedUsers = sharedUsers;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Sensor) {
            Sensor toCompare = (Sensor) obj;
            return (this.getUser().getUsername().equalsIgnoreCase(toCompare.getUser().getUsername()) && this.sensorName.equalsIgnoreCase(toCompare.getSensorName()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (this.user + this.sensorName).hashCode();
    }

}
