package com.echonest.api.v4;

public class DynamicPlaylistParams extends PlaylistParams {
  
    public void setRating(int rating) {
        set("rating", rating);
    }
    
    public void setChainXSPF(boolean chain) {
        set("chain_xspf", chain);
    }
    
    public void setResults(int results) {
        throw new IllegalArgumentException("can't specify the length of a dynamic playlist");
    }

    /**
     * Sets the boost for a particular term
     * @param term the term to boost
     * @param boost the boost 
     */
    public void setSteerDescription(String term, float boost) {
        String tboost = term + "^" + boost;
        set("steer_description", tboost);
    }
    
    /**
     * Bans the given term from appearing on the session
     * @param term the term to ban
     */
    public void banTerm(String term) {
        String tboost = "-" + term;
        set("steer_description", tboost);
    }
    
    /**
     * Requires a term to be present in the session
     * @param term the term of interest
     */
    public void requireTerm(String term) {
        String tboost = '^' + term;
        set("steer_description", tboost);
    }
    
    
    /**
     * Steer by tempo.
     * Using the previously played track as the basis, make all 
     * future tracks some multiplier of the selected attribute(s). 
     * required terms use a multiplier in a boost-like 
     * format - e.g. energy^.5 to make all future songs have half 
     * the energy of the previously played song, or 
     * loudness^1.3 to add 30%.
     * @param boost
     */
    public void steerTempo(float boost) {
         String tboost = "tempo" + "^" + boost;
         set("steer", tboost);       
    }
    /**
     * Steer by loudness.
     * Using the previously played track as the basis, make all 
     * future tracks some multiplier of the selected attribute(s). 
     * required terms use a multiplier in a boost-like 
     * format - e.g. energy^.5 to make all future songs have half 
     * the energy of the previously played song, or 
     * loudness^1.3 to add 30%.
     * @param boost
     */
    public void steerLoudness(float boost) {
        String tboost = "loudness" + "^" + boost;
        set("steer", tboost);       
   }
    /**
     * Steer by dancability.
     * Using the previously played track as the basis, make all 
     * future tracks some multiplier of the selected attribute(s). 
     * required terms use a multiplier in a boost-like 
     * format - e.g. energy^.5 to make all future songs have half 
     * the energy of the previously played song, or 
     * loudness^1.3 to add 30%.
     * @param boost
     */
    public void steerDanceability(float boost) {
        String tboost = "danceability" + "^" + boost;
        set("steer", tboost);       
   }
    /**
     * Steer by energy.
     * Using the previously played track as the basis, make all 
     * future tracks some multiplier of the selected attribute(s). 
     * required terms use a multiplier in a boost-like 
     * format - e.g. energy^.5 to make all future songs have half 
     * the energy of the previously played song, or 
     * loudness^1.3 to add 30%.
     * @param boost
     */
    public void steerEnergy(float boost) {
        String tboost = "energy" + "^" + boost;
        set("steer", tboost);       
   }
    /**
     * Steer by hotttnesss.
     * Using the previously played track as the basis, make all 
     * future tracks some multiplier of the selected attribute(s). 
     * required terms use a multiplier in a boost-like 
     * format - e.g. energy^.5 to make all future songs have half 
     * the energy of the previously played song, or 
     * loudness^1.3 to add 30%.
     * @param boost
     */
    public void steerHotttnesss(float boost) {
        String tboost = "song_hotttnesss" + "^" + boost;
        set("steer", tboost);       
   }
    
    /**
     * Bans the current song
     */
    public void banSong() {
        set("ban", "song");
    }
    
    
    /**
     * Bands the current artist
     */
    
    public void banArtist() {
        set("ban", "artist");
    }
    
}
