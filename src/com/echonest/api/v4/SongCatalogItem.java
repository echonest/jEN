package com.echonest.api.v4;

import java.util.Map;

public class SongCatalogItem extends CatalogItem {

    /**
     * Creates a catalog item with the given item ID
     * @param id
     */
    public SongCatalogItem(String id) {
        super(id);
    }
    
    @SuppressWarnings("unchecked")
    SongCatalogItem(Map m) {
        super(m);
    }    
    
    /**
     * Sets the Song ID
     * @param SongID
     */
    public void setSongID(String songID) {
        set("song_id", songID);
    }
    
    public void setFPCode(String fpCode) {
        set("fp_code", fpCode);
    }
    
    /**
     * Sets the song name
     * @param songName
     */
    public void setSongName(String songName) {
        set("song_name", songName);
    }
    
    public void setArtistName(String artistName) {
        set("artist_name", artistName);
    }
    
    public void setArtistID(String artistID) {
        set("artist_id", artistID);
    }
    
    public void setRelease(String release) {
        set("release", release);
    }
    
    public void setGenre(String genre) {
        set("genre", genre);
    }
    
    public void setTrackNumber(int number) {
        set("track_number", number);
    }
    
    public void setDiscNumber(int number) {
        set("disc_number", number);
    }
    
    public void setUrl(String url) {
        set("url", url);
    }
    
    
    /** 
     * Returns the song id
     * @return
     */
    public String getSongID() {
        return getString("song_id");
    }
    
    /**
     * returns the song name
     * @return
     */
    public String getSongName() {
        return getString("song_name");
    }
}
