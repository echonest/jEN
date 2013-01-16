package com.echonest.api.v4;

public class Location {
    private Double latitude;
    private Double longitude;
    private String placeName;
    
    
    public Location(Double latitude, Double longitude, String placeName) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeName = placeName;
    }
    
    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }
    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }
    /**
     * @return the placeName
     */
    public String getPlaceName() {
        return placeName;
    }
    
    public String toString() {
        return placeName + "(" + latitude + ", " + longitude + ")";
    }
}
