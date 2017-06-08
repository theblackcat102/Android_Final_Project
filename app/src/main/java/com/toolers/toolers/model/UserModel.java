package com.toolers.toolers.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Alan on 2017/6/7.
 */

public class UserModel implements Parcelable{
    public static final String ANONYMOUS = "anonymous";
    private String dormName;
    private String dormNumber;
    private String name;
    private String phone;
    public UserModel() {
        dormName = "";
        dormNumber = "";
        phone = "";
        name = ANONYMOUS;
    }
    public UserModel setName(@NonNull String name) {
        this.name = name;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserModel setDormName(@NonNull String dormName) {
        this.dormName = dormName;
        return this;
    }
    public String getDormName() {
        return dormName;
    }

    public UserModel setDormNumber(@NonNull String dormNumber) {
        this.dormNumber = dormNumber;
        return this;
    }

    public String getDormNumber() {
        return dormNumber;
    }

    public UserModel setPhone(@NonNull String phone) {
        this.phone = phone;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public static final Parcelable.Creator<UserModel> CREATOR = new Parcelable.Creator<UserModel>() {
        public UserModel createFromParcel(Parcel p) {
            return new UserModel().
                    setDormName(p.readString()).
                    setDormNumber(p.readString()).
                    setName(p.readString()).
                    setPhone(p.readString());
        }

        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeString(getDormName());
        p.writeString(getDormNumber());
        p.writeString(getName());
        p.writeString(getPhone());
    }
}
