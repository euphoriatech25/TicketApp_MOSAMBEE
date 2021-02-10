package com.technosales.net.buslocationannouncement.getcurrentlocation;

import com.pax.market.api.sdk.java.base.dto.SdkObject;

/**
 * Created by zcy on 2019/4/29 0029.
 */

public class LocationInfo extends SdkObject{
    private String longitude;
    private String latitude;
    private String accuracy;
    private Long lastLocateTime;

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public Long getLastLocateTime() {
        return lastLocateTime;
    }

    public void setLastLocateTime(Long lastLocateTime) {
        this.lastLocateTime = lastLocateTime;
    }

    @Override
    public String toString() {
        return super.toString() + "LocationInfo{" +
                "longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", accuracy='" + accuracy + '\'' +
                ", lastLocateTime=" + lastLocateTime +
                '}';
    }
}
