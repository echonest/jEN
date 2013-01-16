package com.echonest.api.v4;


public class IdentifySongParams extends Params {
    
    public void setTitle(String title) {
        add("title", title);
    }
    
    public void setArtist(String artist) {
        add("artist", artist);
    }
    
    public void setRelease(String release) {
        add("release", release);
    }
    
    public void setCode(String code) {
        add("code", code);
    }

    // TBD soon
    private void setLimit(boolean limit) {
        add("limit", limit);
    }
    
    public void includeAudioSummary() {
        add("bucket", "audio_summary");
    }
    
    public void includeTracks() {
        add("bucket", "tracks");
    }
    
    public void includeSongHotttnesss() {
        add("bucket", "song_hotttnesss");
    }
    
    public void includeArtistHotttnesss() {
        add("bucket", "artist_hotttnesss");
    }
    
    public void includeArtistFamiliarity() {
        add("bucket", "artist_familiarity");
    }
    
    public void includeArtistLocation() {
        add("bucket", "artist_location");
    }
    
    
    // TBD soon
    private void addIDSpace(String idspace) {
        if (!idspace.startsWith("id:")) {
            idspace = "id:" + idspace;
        }
        add("bucket", idspace);
    }
}
