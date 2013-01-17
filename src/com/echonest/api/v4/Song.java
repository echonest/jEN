package com.echonest.api.v4;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.echonest.api.v4.util.Commander;

public class Song extends ENItem {

    /** 
     * Possible song types
     */
    public enum SongType {

        christmas, live, studio
    }

    /**
     * Possible states for song type
     */
    public enum SongTypeFlag {

        True, False, seed, any
    }
    
    private final static String PATH = "songs[0]";
    private Map<String, Track> trackMap = new HashMap<String, Track>();
    private TrackAnalysis analysis = null;

    @SuppressWarnings("unchecked")
    Song(EchoNestAPI en, Map map) throws EchoNestException {
        super(en, "song", PATH, map);
    }

    public Song(EchoNestAPI en, String id) throws EchoNestException {
        super(en, "song", PATH, id);
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public String getTitle() {
        return getString("title");
    }

    public String getArtistName() {
        return getString("artist_name");
    }

    //TBD - switch to release name when we have it
    public String getReleaseName() {
        return getTitle();
        // return getString("release_name");
    }

    public String getArtistID() {
        return getString("artist_id");
    }

    public String getAudio() {
        return getString("audio");
    }

    public String getCoverArt() {
        return getReleaseImage();
    }

    public String getReleaseImage() {
        return getString("release_image");
    }

    public Location getArtistLocation() throws EchoNestException {
        fetchBucket("artist_location");
        Double latitude = getDouble("artist_location.latitude");
        Double longitude = getDouble("artist_location.longitude");
        String placeName = getString("artist_location.location");
        return new Location(latitude, longitude, placeName);
    }

    public double getSongHotttnesss() throws EchoNestException {
        fetchBucket("song_hotttnesss");
        return getDouble("song_hotttnesss");
    }

    public double getArtistHotttnesss() throws EchoNestException {
        fetchBucket("artist_hotttnesss");
        return getDouble("artist_hotttnesss");
    }

    public double getArtistFamiliarity() throws EchoNestException {
        fetchBucket("artist_familiarity");
        return getDouble("artist_familiarity");
    }

    public double getDuration() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.duration");
    }

    public double getLoudness() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.loudness");
    }

    public double getTempo() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.tempo");
    }

    public double getEnergy() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.energy");
    }

    public double getDanceability() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.danceability");
    }

    public String getAnalysisURL() throws EchoNestException {
        fetchBucket("audio_summary");
        return getString("audio_summary.analysis_url");
    }

    @SuppressWarnings("unchecked")
    public TrackAnalysis getAnalysis() throws EchoNestException {
        try {
            if (analysis == null) {
                Map analysisMap = Commander.fetchURLAsJSON(getAnalysisURL());
                analysis = new TrackAnalysis(analysisMap);
            }
        } catch (IOException e) {
            throw new EchoNestException(e);
        }
        return analysis;
    }

    public int getTimeSignature() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.time_signature");
    }

    public int getMode() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.mode");
    }

    public int getKey() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.key");
    }

    @SuppressWarnings("unchecked")
    public Track getTrackOld(String idSpace) throws EchoNestException {
        Track track = trackMap.get(idSpace);
        if (track == null) {
            // see if we already have the track data
            List tlist = (List) getObject("tracks");
            if (tlist == null) {
                String[] buckets = {"tracks", "id:" + idSpace};
                fetchBuckets(buckets, true);
                tlist = (List) getObject("tracks");
            }
            for (int i = 0; tlist != null && i < tlist.size(); i++) {
                Map tmap = (Map) tlist.get(i);
                String tidSpace = (String) tmap.get("catalog");
                if (idSpace.equals(tidSpace)) {
                    track = new Track(en, tmap);
                    trackMap.put(idSpace, track);
                }
            }
        }
        return track;
    }

    /**
     * Gets a track for the given idspace
     *
     * @param idSpace the idspace of interest
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Track getTrack(String idSpace) throws EchoNestException {
        Track track = trackMap.get(idSpace);
        if (track == null) {
            // nope, so go grab it.
            String[] buckets = {"tracks", "id:" + idSpace};
            fetchBuckets(buckets, true);
            List tlist = (List) getObject("tracks");
            if (tlist != null) {
                for (Object item : tlist) {
                    Map tracks = (Map) item;
                    String catalog = (String) tracks.get("catalog");
                    String trid = (String) tracks.get("id");
                    if (!trackMap.containsKey(catalog)) {
                        track = en.newTrackByID(trid);
                        trackMap.put(catalog, track);
                    }
                }
            }
        }
        return track;
    }

    @SuppressWarnings("unchecked")
    public Track getTrackNew(String idSpace) throws EchoNestException {
        Track track = trackMap.get(idSpace);
        if (track == null) {
            // see if we already have the track data
            Map map = (Map) getObject("tracks");
            if (map == null) {
                // nope, so go grab it.
                String[] buckets = {"tracks", "id:" + idSpace};
                fetchBuckets(buckets, true);
                map = (Map) getObject("tracks");
            }
            if (map != null) {
                for (Object key : map.keySet()) {
                    String catalog = (String) key;
                    String trid = (String) map.get(catalog);
                    track = en.newTrackByID(trid);
                    trackMap.put(catalog, track);
                }
            }
        }
        return track;
    }
    
    

    public void showAll() throws EchoNestException {
        String[] buckets = {"audio_summary", "song_hotttnesss",
            "artist_hotttnesss", "artist_familiarity", "artist_location"};

        fetchBuckets(buckets);
        System.out.println("Title      : " + getTitle());
        System.out.println("ID         : " + getID());

        System.out.println("Artist     : " + getArtistName());
        System.out.println("ArtistID   : " + getArtistID());
        System.out.println("Location   : " + getArtistLocation());
        System.out.println("Familiarity: " + getArtistFamiliarity());
        System.out.println("Hotttnesss : " + getArtistHotttnesss());
        System.out.println("Duration   : " + getDuration());
        System.out.println("Key        : " + getKey());
        System.out.println("Loudness   : " + getLoudness());
        System.out.println("SHotttnesss: " + getSongHotttnesss());
        System.out.println("Tempo      : " + getTempo());
        System.out.println("Danceability: " + getDanceability());
        System.out.println("Energy     : " + getEnergy());

        System.out.println("TimeSig    : " + getTimeSignature());
        System.out.println();
    }
}
