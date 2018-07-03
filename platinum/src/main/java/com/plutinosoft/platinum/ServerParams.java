package com.plutinosoft.platinum;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by huzongyao on 2018/6/11.
 * To Hold Server init params
 */

public class ServerParams implements Parcelable {

    private String friendlyName;
    private boolean showIp;
    private String uuid;

    public ServerParams(String friendlyName, boolean showIp, String uuid) {
        this.friendlyName = friendlyName;
        this.showIp = showIp;
        this.uuid = uuid;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }

    public boolean isShowIp() {
        return showIp;
    }

    public void setShowIp(boolean showIp) {
        this.showIp = showIp;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    protected ServerParams(Parcel in) {
        friendlyName = in.readString();
        showIp = in.readByte() != 0;
        uuid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(friendlyName);
        dest.writeByte((byte) (showIp ? 1 : 0));
        dest.writeString(uuid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ServerParams> CREATOR = new Creator<ServerParams>() {
        @Override
        public ServerParams createFromParcel(Parcel in) {
            return new ServerParams(in);
        }

        @Override
        public ServerParams[] newArray(int size) {
            return new ServerParams[size];
        }
    };
}
