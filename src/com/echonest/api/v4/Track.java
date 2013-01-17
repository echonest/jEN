package com.echonest.api.v4;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.echonest.api.v4.util.Commander;

public class Track extends ENItem {

    private final static String PATH = "track";
    private final static String TYPE = "track";

    /**
     * The status of an analysis
     */
    public enum AnalysisStatus {

        /**
         * track is unknown
         */
        UNKNOWN,
        /**
         * track analysis is underway
         */
        PENDING,
        /**
         * track analysis is complete
         */
        COMPLETE,
        /**
         * track analysis is unavailable
         */
        UNAVAILABLE,
        /**
         * track analysis failed
         */
        ERROR
    };
    private TrackAnalysis analysis = null;
    private AnalysisStatus currentStatus = AnalysisStatus.UNKNOWN;

    Track(EchoNestAPI en, String idOrMD5, String type) throws EchoNestException {
        super(en, TYPE, PATH, idOrMD5, type);
    }

    @SuppressWarnings("unchecked")
    Track(EchoNestAPI en, Map data) throws EchoNestException {
        super(en, TYPE, PATH, data);
    }

    /**
     * Creates a track given an ID
     *
     * @param en the EchoNest API
     * @param id the ID of the track
     * @return the track
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    static Track createTrack(EchoNestAPI en, String id) throws EchoNestException {
        Map data = new HashMap();
        data.put("id", id);
        Track track = new Track(en, data);
        return track;
    }

    /**
     * Gets the analysis url for the track
     *
     * @return
     * @throws EchoNestException
     */
    public String getAnalysisURL() throws EchoNestException {
        fetchBucket("audio_summary");
        String url = getString("audio_summary.analysis_url");
        return url;
    }

    /**
     * Gets the analysis status for the track
     *
     * @return the analysis status
     */
    public AnalysisStatus getStatus() throws EchoNestException {

        if (currentStatus != AnalysisStatus.COMPLETE
                && currentStatus != AnalysisStatus.ERROR) {
            refresh();
            String status = getString("status");
            // if we have no status, this is a pre-analyzed track
            if (status == null) {
                status = "complete";
            }
            status = status.toLowerCase();
            for (AnalysisStatus as : AnalysisStatus.values()) {
                if (as.name().toLowerCase().equals(status)) {
                    currentStatus = as;
                    break;
                }
            }
        }
        // System.out.println("GS " + currentStatus);
        return currentStatus;
    }

    /**
     * Gets the title of the track
     *
     * @return the title of the track
     * @throws EchoNestException
     */
    public String getTitle() throws EchoNestException {
        return getTopLevelItem("title");
    }

    /**
     * Gets the artist name for the track
     *
     * @return
     * @throws EchoNestException
     */
    public String getArtistName() throws EchoNestException {
        return getTopLevelItem("artist");
    }

    /**
     * Gets the preview url
     *
     * @return
     * @throws EchoNestException
     */
    public String getPreviewUrl() throws EchoNestException {
        return getTopLevelItem("preview_url");
    }

    /**
     * Gets the audio url
     *
     * @return
     * @throws EchoNestException
     */
    public String getAudioUrl() throws EchoNestException {
        return getTopLevelItem("audio_url");
    }

    /**
     * Gets the release name for the track
     *
     * @return
     * @throws EchoNestException
     */
    public String getReleaseName() throws EchoNestException {
        return getTopLevelItem("release");
    }

    /**
     * Gets the MD5 of the audio for the track
     *
     * @return
     * @throws EchoNestException
     */
    public String getAudioMD5() throws EchoNestException {
        return getTopLevelItem("audio_md5");
    }

    /**
     * Gets the foreign ID for the track
     *
     * @return
     * @throws EchoNestException
     */
    public String getForeignID() throws EchoNestException {
        return getTopLevelItem("foreign_id");
    }

    /**
     * Gets the song ID associated with this track
     *
     * @return the song id
     * @throws EchoNestException
     */
    public String getSongID() throws EchoNestException {
        return getTopLevelItem("song_id");
    }

    private String getTopLevelItem(String itemName) throws EchoNestException {
        if (getStatus() == null) {
            refresh();
        }
        return getString(itemName);
    }

    /**
     * Wait for an analysis to finish
     *
     * @param timeoutMillis maximum milliseconds to wait for the analysis
     * @return the status
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    public AnalysisStatus waitForAnalysis(long timeoutMillis)
            throws EchoNestException {
        long startTime = System.currentTimeMillis();
        long elapsed = 0;
        AnalysisStatus status = AnalysisStatus.UNKNOWN;
        do {
            status = getStatus();
            elapsed = System.currentTimeMillis() - startTime;
        } while (status == AnalysisStatus.PENDING && elapsed < timeoutMillis);
        return status;
    }

    @SuppressWarnings("unchecked")
    public TrackAnalysis getAnalysis() throws EchoNestException {
        fetchBucket("audio_summary");
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

    /**
     * Gets the key for the track
     *
     * @return
     * @throws EchoNestException
     */
    public int getKey() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.key");
    }

    /**
     * Gets the tempo for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getTempo() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.tempo");
    }

    /**
     * Gets the mode for the track
     *
     * @return
     * @throws EchoNestException
     */
    public int getMode() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.mode");
    }

    /**
     * Gets the time signature for the track
     *
     * @return
     * @throws EchoNestException
     */
    public int getTimeSignature() throws EchoNestException {
        fetchBucket("audio_summary");
        return getInteger("audio_summary.time_signature");
    }

    /**
     * Gets the duration for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getDuration() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.duration");
    }

    /**
     * Gets the loudness for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getLoudness() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.loudness");
    }

    /**
     * Gets the energy for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getEnergy() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.energy");
    }

    /**
     * Gets the danceability for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getDanceability() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.danceability");
    }

    /**
     * Gets the speechiness for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getSpeechiness() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.speechiness");
    }

    /**
     * Gets the liveness for the track
     *
     * @return
     * @throws EchoNestException
     */
    public double getLiveness() throws EchoNestException {
        fetchBucket("audio_summary");
        return getDouble("audio_summary.liveness");
    }
    
    /**
     * Analyze a previously uploaded track with the latest version of the
     * analyzer.
     *
     * @param wait if true, wait for the analysis
     * @return the ID of the track
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    @SuppressWarnings("unchecked")
    public AnalysisStatus reanalyze(boolean wait) throws EchoNestException,
            IOException {

        Params p = new Params();

        p.add("wait", wait ? "true" : "false");
        p.add("id", getID());
        if (wait) {
            p.add("bucket", "audio_summary");
        }
        int tmo = en.getCmd().getTimeout();
        // for track uploads set the timeout to 5 mins
        en.getCmd().setTimeout(300000);
        try {
            Map results = en.getCmd().sendCommand("track/analyze", p, true);
            Map response = (Map) results.get("response");
            @SuppressWarnings("unused")
            Map trackData = (Map) response.get("track");
        } finally {
            en.getCmd().setTimeout(tmo);
        }
        return getStatus();
    }

    public void showAll() throws EchoNestException {
        System.out.println(" ====== " + getID() + " =======");
        System.out.println("Title   : " + getTitle());
        System.out.println("audio   : " + getAudioUrl());
        System.out.println("foreign : " + getForeignID());
        System.out.println("Analysis: " + getAnalysisURL());
        System.out.println("Artist  : " + getArtistName());
        System.out.println("MD5     : " + getAudioMD5());
        System.out.println("Duration: " + getDuration());
        System.out.println("Key     : " + getKey());
        System.out.println("Loudness: " + getLoudness());
        System.out.println("Mode    : " + getMode());
        System.out.println("Preview : " + getPreviewUrl());
        System.out.println("Release : " + getReleaseName());
        System.out.println("Status  : " + getStatus());
        System.out.println("Tempo   : " + getTempo());
        System.out.println("Time Sig: " + getTimeSignature());
        System.out.println();
    }
}
