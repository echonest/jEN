/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4;

import java.util.Map;

/**
 *
 * @author plamere
 */
public class ArtistLocation {
    private String country;
    private String region;
    private String city;
    private String location;

    
    
    public ArtistLocation(Map map) {
        this.country = (String) map.get("country");
        this.region = (String) map.get("region");
        this.city = (String) map.get("city");
        this.location = (String) map.get("location");
    }

    public String getCountry() {
        return country;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.country != null ? this.country.hashCode() : 0);
        hash = 59 * hash + (this.region != null ? this.region.hashCode() : 0);
        hash = 59 * hash + (this.city != null ? this.city.hashCode() : 0);
        hash = 59 * hash + (this.location != null ? this.location.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ArtistLocation other = (ArtistLocation) obj;
        if ((this.country == null) ? (other.country != null) : !this.country.equals(other.country)) {
            return false;
        }
        if ((this.region == null) ? (other.region != null) : !this.region.equals(other.region)) {
            return false;
        }
        if ((this.city == null) ? (other.city != null) : !this.city.equals(other.city)) {
            return false;
        }
        if ((this.location == null) ? (other.location != null) : !this.location.equals(other.location)) {
            return false;
        }
        return true;
    }
    
    
}
