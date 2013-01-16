package com.echonest.api.v4;

import java.util.Map;

/**
 * Represent items for an artist catalog
 * @author plamere
 *
 */
public class ArtistCatalogItem extends CatalogItem {

    /**
     * Creates a catalog item with the given item ID
     * @param id
     */
    public ArtistCatalogItem(String id) {
        super(id);
    }
    
    @SuppressWarnings("unchecked")
    ArtistCatalogItem(Map m) {
        super(m);
    }    
    
    /**
     * Sets the artist ID
     * @param artistID
     */
    public void setArtistID(String artistID) {
        set("artist_id", artistID);
    }
    
    /**
     * Sets the artist name
     * @param artistName
     */
    public void setArtistName(String artistName) {
        set("artist_name", artistName);
    }
    
    /** 
     * Returns the artist id
     * @return
     */
    public String getArtistID() {
        return getString("artist_id");
    }
    
    /**
     * returns the artist name
     * @return
     */
    public String getArtistName() {
        return getString("artist_name");
    }
}
