/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4;

/**
 * Params for basic playlists
 * 
 * @author plamere
 */
public class BasicPlaylistParams extends Params {
    public enum PlaylistType {ARTIST_RADIO, SONG_RADIO }

    
    /**
     * Sets the type of the playlist. Default is ARTIST
     * @param type the type of the playlist.
     */
    public void setType(BasicPlaylistParams.PlaylistType type) {
        String stype = type.toString().toLowerCase().replace("_", "-");
        set("type", stype);
    }


    /**
     * Adds an ID of a seed artist  
     * @param artistID an ID of the seed artist
     */
    public void addArtistID(String artistID) {
        add("artist_id", artistID);
    }
    
    
    /**
     * Adds an ID of a seed song  
     * @param songID an ID of the seed song
     */
    public void addSongID(String songID) {
        add("song_id", songID);
    }
    
    
    /**
     * Adds an ID of a seed track  
     * @param trackID an ID of the seed track
     */
    public void addTrackID(String trackID) {
        add("track_id", trackID);
    }
    
    
    /**
     * Adds the name of a seed artist
     * @param artist the name of the seed artist
     */
    public void addArtist(String artist) {
        add("artist", artist);
    }
    
    
    /**
     * Set the maximum number of songs to return in the playlist
     * @param results the results
     */
    public void setResults(int results) {
        set("results", results);
    }
  

    /**
     * Sets whether or not to limit results to any of the given rosetta id spaces
     * @param limit if true, limit the results
     */
    public void setLimit(boolean limit) {
        set("limit", limit);
    }

    
    /**
     *  If called track information will be included in the playlist
     */
    public void includeTracks() {
        add("bucket", "tracks");
    }
    
   
    /**
     * If called, IDs for the given ID space will be included in the resulting
     * playlist.
     */
    public void addIDSpace(String idspace) {
        if (!idspace.startsWith("id:")) {
            idspace = "id:" + idspace;
        }
        add("bucket", idspace);
    }
   

    /*
     * If true the playlist delivered will meet the DMCA rules
     * @param dmca if true, playlist will meet DMCA rules
     */
    public void setDMCA(boolean dmca) {
        set("dmca", dmca);
    }
    
    
}

