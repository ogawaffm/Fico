package com.ogawa.fico.scan.fileformat.image;

public class GeoLocation {

    private final double latitude;
    private final double longitude;
    private final double altitude;

    public GeoLocation(double latitude, double longitude, double altitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

}
