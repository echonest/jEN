package com.echonest.api.v4;

import com.echonest.api.v4.Track.AnalysisStatus;
import com.echonest.api.v4.util.Commander;
import com.echonest.api.v4.util.MQuery;
import com.echonest.api.v4.util.Utilities;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main interface to the Echo Nest API
 *
 * @author plamere
 *
 */
public class EchoNestAPI {

    private Commander cmd = new Commander("EchoNestAPI");
    private Params stdParams = new Params();

    public enum TermType {

        ANY, STYLE, MOOD
    };

    /**
     * Possible types of artist descriptions
     */
    public enum DescriptionType {

        general {
            public String toString() {
                return "description";
            }
        }, style, mood
    }

    /**
     * Creates a new EchoNestAPI. This method will attempt to get the Echo Nest
     * API Key from the ECHO_NEST_API_KEY system property or environment
     * variable
     *
     * @throws EchoNestException
     */
    public EchoNestAPI() throws EchoNestException {
        this(getApiKey());
        setMinCommandTime(-1);
    }

    /**
     * Creates a new EchoNestAPI with the given API key
     *
     * @param apiKey the developer api key
     */
    public EchoNestAPI(String apiKey) {
        stdParams.add("api_key", apiKey);
        cmd.setStandardParams(stdParams);
    }

    /**
     * Creates a new artist given the artist ID
     *
     * @param id
     * @return
     * @throws EchoNestException
     */
    public Artist newArtistByID(String id) throws EchoNestException {
        return new Artist(this, id, false);
    }

    /**
     * Creates a new artist given the artist name
     *
     * @param name
     * @return
     * @throws EchoNestException
     */
    public Artist newArtistByName(String name) throws EchoNestException {
        return new Artist(this, name, true);
    }

    private static String getApiKey() throws EchoNestException {
        String key = System.getProperty("ECHO_NEST_API_KEY");
        if (key == null) {
            key = System.getenv("ECHO_NEST_API_KEY");
        }

        if (key == null) {
            System.out.println("No API KEY set");
            throw new EchoNestException(EchoNestException.ERR_NO_KEY,
                    "No API Key");
        }
        return key;
    }

    /**
     * Sets the host to use for the API.
     *
     * @param hostName
     */
    public void setHostName(String hostName) {
        cmd.setHost(hostName);
    }

    /**
     * Sets the minimum time between Echo Nest commands
     *
     * @param minTime
     */
    public final void setMinCommandTime(int minTime) {
        cmd.setMinCommandTime(minTime);
    }

    /**
     * Gets the minimum time between commands
     *
     * @return
     */
    public int getMinCommandTime() {
        return cmd.getMinCommandTime();
    }

    /**
     * Enables / disables tracing of sent commands
     *
     * @param traceSends
     */
    public void setTraceSends(boolean traceSends) {
        cmd.setTraceSends(traceSends);
    }

    /**
     * Enables / disables traceing of responses
     *
     * @param traceRecvs
     */
    public void setTraceRecvs(boolean traceRecvs) {
        cmd.setTraceRecvs(traceRecvs);
    }

    /**
     * Shows performance and error statistics for the API
     */
    public void showStats() {
        cmd.showStats();
    }

    /**
     * Gets similar artists given a set of params (See ArtistParams)
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Artist> getSimilarArtists(Params p) throws EchoNestException {
        List<Artist> artistResults = new ArrayList<Artist>();

        Map results = cmd.sendCommand("artist/similar", p);
        Map response = (Map) results.get("response");
        List artistList = (List) response.get("artists");
        for (int i = 0; i < artistList.size(); i++) {
            // BUG: fix the map, service returns a list when it should return a
            // name
            //
            Map artistMap = (Map) artistList.get(i);
            Object oname = artistMap.get("name");
            if (oname instanceof List) {
                List lname = (List) oname;
                oname = (String) lname.get(0);
                artistMap.put("name", oname);
            }

            Artist artist = new Artist(this, (Map) artistList.get(i));
            artistResults.add(artist);
        }
        return artistResults;
    }

    /**
     * Search for artists given a set of params (See ArtistParams)
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Artist> searchArtists(Params p) throws EchoNestException {
        List<Artist> artistResults = new ArrayList<Artist>();
        Map results = cmd.sendCommand("artist/search", p);
        Map response = (Map) results.get("response");
        List artistList = (List) response.get("artists");
        for (int i = 0; i < artistList.size(); i++) {
            Artist artist = new Artist(this, (Map) artistList.get(i));
            artistResults.add(artist);
        }
        return artistResults;
    }

    /**
     * Suggest artists given a set of params (See ArtistParams)
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Artist> suggestArtists(Params p) throws EchoNestException {
        List<Artist> artistResults = new ArrayList<Artist>();
        Map results = cmd.sendCommand("artist/suggest", p);
        Map response = (Map) results.get("response");
        List artistList = (List) response.get("artists");
        for (int i = 0; i < artistList.size(); i++) {
            Artist artist = new Artist(this, (Map) artistList.get(i));
            artistResults.add(artist);
        }
        return artistResults;
    }

    /**
     * Suggest artists by name
     *
     * @param name
     * @return
     * @throws EchoNestException
     */
    public List<Artist> suggestArtists(String name) throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        return suggestArtists(p);
    }

    /**
     * Gets the most frequently occurring top terms
     *
     * @param count the number of terms to return
     * @return a list of top terms
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Term> getTopTerms(int count) throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(count);
        List<Term> terms = new ArrayList<Term>();
        Map results = cmd.sendCommand("artist/top_terms", p);
        Map response = (Map) results.get("response");
        List termList = (List) response.get("terms");
        for (int i = 0; i < termList.size(); i++) {
            Map tmap = (Map) termList.get(i);
            MQuery mq = new MQuery(tmap);
            String tname = mq.getString("name");
            double frequency = mq.getDouble("frequency");
            // there's no separate weight for top terms
            Term term = new Term(tname, frequency, frequency);
            terms.add(term);
        }
        return terms;
    }

    /**
     * list available terms
     *
     * @param type the type of terms desired
     * @return a list of terms
     * @throws EchoNestException
     */
    public List<String> listTerms(TermType type) throws EchoNestException {
        List<String> terms = new ArrayList<String>();
        Params p = new Params();

        if (type != TermType.ANY) {
            p.add("type", type.toString().toLowerCase());
        }


        Map results = cmd.sendCommand("artist/list_terms", p);
        Map response = (Map) results.get("response");
        List termList = (List) response.get("terms");
        for (int i = 0; i < termList.size(); i++) {
            Map tmap = (Map) termList.get(i);
            String term = (String) tmap.get("name");
            terms.add(term);
        }
        return terms;
    }

    /**
     * list available genres
     *
     * @param type the type of terms desired
     * @return a list of terms
     * @throws EchoNestException
     */
    public List<String> listGenres() throws EchoNestException {
        List<String> genres = new ArrayList<String>();
        Params p = new Params();

        Map results = cmd.sendCommand("artist/list_genres", p);
        Map response = (Map) results.get("response");
        List list = (List) response.get("genres");
        for (int i = 0; i < list.size(); i++) {
            Map tmap = (Map) list.get(i);
            String term = (String) tmap.get("name");
            genres.add(term);
        }
        return genres;
    }

    /**
     * Search for artists by name
     *
     * @param name
     * @return
     * @throws EchoNestException
     */
    public List<Artist> searchArtists(String name) throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        return searchArtists(p);
    }

    /**
     * Search for artists by name and count
     *
     * @param name
     * @param count
     * @return
     * @throws EchoNestException
     */
    public List<Artist> searchArtists(String name, int count)
            throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        p.add("results", count);
        return searchArtists(p);
    }

    /**
     * Gets the top hotttest artists
     *
     * @param count
     * @return
     * @throws EchoNestException
     */
    public List<Artist> topHotArtists(int count) throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(count);
        return topHotArtists(p);
    }

    /**
     * Gets the top hotttest artists
     *
     * @param start the starting index
     * @param count the count
     * @return
     * @throws EchoNestException
     */
    public List<Artist> topHotArtists(int start, int count)
            throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(count);
        p.setStart(start);
        return topHotArtists(p);
    }

    /**
     * Gets the top hotttest artists based on params
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Artist> topHotArtists(Params p) throws EchoNestException {
        List<Artist> artistResults = new ArrayList<Artist>();
        Map results = cmd.sendCommand("artist/top_hottt", p);
        Map response = (Map) results.get("response");
        List artistList = (List) response.get("artists");
        for (int i = 0; i < artistList.size(); i++) {
            Artist artist = new Artist(this, (Map) artistList.get(i));
            artistResults.add(artist);
        }
        return artistResults;
    }

    /**
     * Search for songs given a set of params (See SongParams)
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Song> searchSongs(Params p) throws EchoNestException {
        List<Song> songResults = new ArrayList<Song>();
        Map results = cmd.sendCommand("song/search", p);
        Map response = (Map) results.get("response");
        List songList = (List) response.get("songs");
        for (int i = 0; i < songList.size(); i++) {
            Song song = new Song(this, (Map) songList.get(i));
            songResults.add(song);
        }
        return songResults;
    }

    /**
     * Get similar songs based on a set of params (See SongParams)
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Song> similarSongs(Params p) throws EchoNestException {
        List<Song> songResults = new ArrayList<Song>();
        Map results = cmd.sendCommand("song/similar", p);
        Map response = (Map) results.get("response");
        List songList = (List) response.get("songs");
        for (int i = 0; i < songList.size(); i++) {
            Song song = new Song(this, (Map) songList.get(i));
            songResults.add(song);
        }
        return songResults;
    }

    /**
     * Identify a song given codes from the Echo Nest Fingerprinter
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Song> identifySongs(Params p) throws EchoNestException {
        List<Song> songResults = new ArrayList<Song>();
        Map results = cmd.sendCommand("song/identify", p);
        Map response = (Map) results.get("response");
        List songList = (List) response.get("songs");
        for (int i = 0; i < songList.size(); i++) {
            Song song = new Song(this, (Map) songList.get(i));
            songResults.add(song);
        }
        return songResults;
    }

    /**
     * Get info for songs based on a set of params (See SongParams)
     *
     * @param p
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Song> getSongs(Params p) throws EchoNestException {
        List<Song> songResults = new ArrayList<Song>();
        Map results = cmd.sendCommand("song/profile", p);
        Map response = (Map) results.get("response");
        List songList = (List) response.get("songs");
        for (int i = 0; i < songList.size(); i++) {
            Song song = new Song(this, (Map) songList.get(i));
            songResults.add(song);
        }
        return songResults;
    }

    /**
     * Upload a track
     *
     * @param trackUrl the url of the track
     * @param wait if true, wait for the analysis
     * @return the ID of the track
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Track uploadTrack(URL trackUrl, boolean wait)
            throws EchoNestException {

        Params p = new Params();

        p.add("url", trackUrl.toExternalForm());
        p.add("wait", wait ? "true" : "false");
        p.add("upload", (String) null);
        if (wait) {
            p.add("bucket", "audio_summary");
        }
        int tmo = cmd.getTimeout();
        // for track uploads set the timeout to 5 mins
        cmd.setTimeout(300000);
        try {
            Map results = cmd.sendCommand("track/upload", p, true);
            Map response = (Map) results.get("response");
            Map trackData = (Map) response.get("track");
            Track track = new Track(this, trackData);
            return track;
        } finally {
            cmd.setTimeout(tmo);
        }
    }

    /**
     * Upload a track
     *
     * @param trackFile the file to upload
     * @param wait if true, wait for the analysis
     * @return the ID of the track
     * @throws com.echonest.api.v3.artist.EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Track uploadTrack(File audioFile)
            throws EchoNestException, IOException {

        Track track = getKnownTrack(audioFile);
        if (track != null) {
            return track;
        }

        Params p = new Params();

        p.add("filetype", getFileType(audioFile));
        int tmo = cmd.getTimeout();
        // for track uploads set the timeout to 5 mins
        cmd.setTimeout(300000);
        try {
            Map results = cmd.sendCommand("track/upload", p, true, audioFile);
            Map response = (Map) results.get("response");
            Map trackData = (Map) response.get("track");
            track = new Track(this, trackData);
            return track;
        } finally {
            cmd.setTimeout(tmo);
        }
    }

    /**
     * Determines whether or not the track is known by the echo nest
     *
     * @param md5 of the track
     * @return true if the track is known
     */
    public Track getKnownTrack(String md5) throws EchoNestException {
        try {
            Track track = newTrackByMD5(md5);
            Track.AnalysisStatus status = track.getStatus();
            if (status != AnalysisStatus.UNKNOWN
                    && status != AnalysisStatus.UNAVAILABLE) {
                return track;
            } else {
                return null;
            }
        } catch (EchoNestException e) { // Bug, clean this up
            return null;
        }
    }

    /**
     * Determines whether or not the track is known by the echo nest
     *
     * @param file the file to test
     * @return true if the track is known
     * @throws IOException
     */
    public Track getKnownTrack(File file) throws IOException, EchoNestException {
        return getKnownTrack(Utilities.md5(file));
    }

    private String getFileType(File file) {
        int dot = file.getName().lastIndexOf('.');
        if (dot >= 0 && dot < file.getName().length() - 1) {
            return file.getName().substring(dot + 1).toLowerCase();
        } else {
            return "mp3";
        }
    }

    /**
     * Creates a track from a track ID
     *
     * @param id the ID or MD5 of the track
     * @return a new track
     */
    public Track newTrackByID(String id) throws EchoNestException {
        return Track.createTrack(this, id);
    }

    /**
     * Creates a track from a track ID
     *
     * @param id the ID or MD5 of the track
     * @return a new track
     */
    public Track newTrackByMD5(String md5) throws EchoNestException {
        return new Track(this, md5, "md5");
    }

    /**
     * create a basic playlist
     *
     * @param p the playlist params (see PlaylistParams)
     * @return the playlist
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Playlist createBasicPlaylist(Params p) throws EchoNestException {
        List<Song> songResults = new ArrayList<Song>();
        Map results = cmd.sendCommand("playlist/basic", p);
        Map response = (Map) results.get("response");
        List songList = (List) response.get("songs");
        for (int i = 0; i < songList.size(); i++) {
            Song song = new Song(this, (Map) songList.get(i));
            songResults.add(song);
        }
        Playlist playlist = new Playlist(songResults);
        return playlist;
    }

    /**
     * create a static playlist
     *
     * @param p the playlist params (see PlaylistParams)
     * @return the playlist
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Playlist createStaticPlaylist(Params p) throws EchoNestException {
        List<Song> songResults = new ArrayList<Song>();
        Map results = cmd.sendCommand("playlist/static", p);
        Map response = (Map) results.get("response");
        List songList = (List) response.get("songs");
        for (int i = 0; i < songList.size(); i++) {
            Song song = new Song(this, (Map) songList.get(i));
            songResults.add(song);
        }
        Playlist playlist = new Playlist(songResults);
        return playlist;
    }

    /**
     * Dynamic Playlist 2.0 methods
     */
    /**
     * Creates a new dynamic playlist session. A dynamic playlist is created
     * with an initial set of parameters that define rules for generating the
     * playlist. A session identifier is returned that can be used with other
     * dynamic methods to get new songs, provide feedback or to steer the
     * playlist. Songs in the playlist can be fetched, one at a time, using the
     * dynamic/next method. The playlist is dynamic in that it is adapted
     * dynamically based on the listener's feedback and steering.
     *
     * @param p params dynamic playlist parameters
     * @return the session ID
     * @throws EchoNestException
     */
    public DynamicPlaylistSession createDynamicPlaylist(Params p) throws EchoNestException {
        Map results = cmd.sendCommand("playlist/dynamic/create", p);
        Map response = (Map) results.get("response");
        String sessionID = (String) response.get("session_id");
        return new DynamicPlaylistSession(this, sessionID);
    }

    /**
     * Creates an artist catalog
     *
     * @param name the name of the catalog
     */
    @SuppressWarnings("unchecked")
    public ArtistCatalog createArtistCatalog(String name)
            throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        p.add("type", "artist");
        Map results = cmd.sendCommand("catalog/create", p, true);
        Map response = (Map) results.get("response");
        String id = (String) response.get("id");
        String nname = (String) response.get("name");
        ArtistCatalog catalog = newArtistCatalog(id, nname);
        return catalog;
    }

    /**
     * Gets a previously created artist catalog by ID
     *
     * @param id the catalog ID
     * @return the catalog or null if the catalog is not found or is not an
     * artist catalog
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public ArtistCatalog getArtistCatalogByID(String id) throws EchoNestException {
        Params p = new Params();
        p.add("id", id);
        Map results = cmd.sendCommand("catalog/profile", p, false);
        Map response = (Map) results.get("response");
        Map catalog = (Map) response.get("catalog");

        String type = (String) catalog.get("type");

        if (type.equals("artist")) {
            ArtistCatalog artistCatalog = new ArtistCatalog(this, catalog);
            return artistCatalog;
        } else {
            return null;
        }
    }

    /**
     * Gets a previously created artist catalog by name
     *
     * @param name the catalog name
     * @return the catalog or null if the catalog is not found or is not an
     * artist catalog
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public ArtistCatalog getArtistCatalogByName(String name) throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        Map results = cmd.sendCommand("catalog/profile", p, false);
        Map response = (Map) results.get("response");
        Map catalog = (Map) response.get("catalog");

        String type = (String) catalog.get("type");

        if (type.equals("artist")) {
            ArtistCatalog artistCatalog = new ArtistCatalog(this, catalog);
            return artistCatalog;
        } else {
            return null;
        }
    }

    /**
     * Gets a previously created song catalog by ID
     *
     * @param id the catalog ID
     * @return the catalog or null if the catalog is not found or is not a song
     * catalog
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public SongCatalog getSongCatalogByID(String id) throws EchoNestException {
        Params p = new Params();
        p.add("id", id);
        Map results = cmd.sendCommand("catalog/profile", p, false);
        Map response = (Map) results.get("response");
        Map catalog = (Map) response.get("catalog");

        String type = (String) catalog.get("type");

        if (type.equals("song")) {
            SongCatalog songCatalog = new SongCatalog(this, catalog);
            return songCatalog;
        } else {
            return null;
        }
    }

    /**
     * Gets a previously created song catalog by name
     *
     * @param name the catalog name
     * @return the catalog or null if the catalog is not found or is not a song
     * catalog
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public SongCatalog getSongCatalogByName(String name) throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        Map results = cmd.sendCommand("catalog/profile", p, false);
        Map response = (Map) results.get("response");
        Map catalog = (Map) response.get("catalog");

        String type = (String) catalog.get("type");

        if (type.equals("song")) {
            SongCatalog songCatalog = new SongCatalog(this, catalog);
            return songCatalog;
        } else {
            return null;
        }
    }

    /**
     * List all artist catalogs created with this API key
     *
     * @return a list of artist catalogs
     */
    @SuppressWarnings("unchecked")
    public List<ArtistCatalog> listArtistCatalogs() throws EchoNestException {
        int MAX_CATALOGS_PER_CALL = 30;
        int total = 1;
        int start = 0;

        List<ArtistCatalog> allCatalogs = new ArrayList<ArtistCatalog>();

        while (start < total) {
            Params p = new Params();
            p.add("start", start);
            p.add("results", MAX_CATALOGS_PER_CALL);
            Map results = cmd.sendCommand("catalog/list", p);
            Map response = (Map) results.get("response");

            start = ((Long) response.get("start")).intValue();
            total = ((Long) response.get("total")).intValue();
            List catalogList = (List) response.get("catalogs");
            for (int i = 0; i < catalogList.size(); i++) {
                Map map = (Map) catalogList.get(i);
                if (map.get("type").equals("artist")) {
                    ArtistCatalog catalog = new ArtistCatalog(this, map);
                    allCatalogs.add(catalog);
                }
            }
            start += catalogList.size();
        }
        return allCatalogs;
    }

    private ArtistCatalog newArtistCatalog(String id, String name)
            throws EchoNestException {
        Map<String, String> data = new HashMap<String, String>();
        data.put("id", id);
        data.put("name", name);
        data.put("type", "artist");

        ArtistCatalog catalog = new ArtistCatalog(this, data);
        return catalog;
    }

    /**
     * Creates a song catalog
     *
     * @param name the name of the catalog
     */
    @SuppressWarnings("unchecked")
    public SongCatalog createSongCatalog(String name) throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        p.add("type", "song");
        Map results = cmd.sendCommand("catalog/create", p, true);
        Map response = (Map) results.get("response");
        String id = (String) response.get("id");
        String nname = (String) response.get("name");
        SongCatalog catalog = newSongCatalog(id, nname);
        return catalog;
    }

    /**
     * Creates a song catalog
     *
     * @param name the name of the catalog
     */
    @SuppressWarnings("unchecked")
    public GeneralCatalog createGeneralCatalog(String name) throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        p.add("type", "general");
        Map results = cmd.sendCommand("catalog/create", p, true);
        Map response = (Map) results.get("response");
        String id = (String) response.get("id");
        String nname = (String) response.get("name");
        GeneralCatalog catalog = newGeneralCatalog(id, nname);
        return catalog;
    }

    /**
     * List all song catalogs created with this API key
     *
     * @return a list of artist catalogs
     */
    @SuppressWarnings("unchecked")
    public List<SongCatalog> listSongCatalogs() throws EchoNestException {
        int MAX_CATALOGS_PER_CALL = 30;
        int total = 1;
        int start = 0;

        List<SongCatalog> allCatalogs = new ArrayList<SongCatalog>();

        while (start < total) {
            Params p = new Params();
            p.add("start", start);
            p.add("results", MAX_CATALOGS_PER_CALL);
            Map results = cmd.sendCommand("catalog/list", p);
            Map response = (Map) results.get("response");

            start = ((Long) response.get("start")).intValue();
            total = ((Long) response.get("total")).intValue();
            List catalogList = (List) response.get("catalogs");
            for (int i = 0; i < catalogList.size(); i++) {
                Map map = (Map) catalogList.get(i);
                if (map.get("type").equals("song")) {
                    SongCatalog catalog = new SongCatalog(this, map);
                    allCatalogs.add(catalog);
                }
            }
            start += catalogList.size();
        }
        return allCatalogs;
    }

    /**
     * List all song catalogs created with this API key
     *
     * @return a list of artist catalogs
     */
    @SuppressWarnings("unchecked")
    public List<GeneralCatalog> listGeneralCatalogs() throws EchoNestException {
        int MAX_CATALOGS_PER_CALL = 30;
        int total = 1;
        int start = 0;

        List<GeneralCatalog> allCatalogs = new ArrayList<GeneralCatalog>();

        while (start < total) {
            Params p = new Params();
            p.add("start", start);
            p.add("results", MAX_CATALOGS_PER_CALL);
            Map results = cmd.sendCommand("catalog/list", p);
            Map response = (Map) results.get("response");

            start = ((Long) response.get("start")).intValue();
            total = ((Long) response.get("total")).intValue();
            List catalogList = (List) response.get("catalogs");
            for (int i = 0; i < catalogList.size(); i++) {
                Map map = (Map) catalogList.get(i);
                if (map.get("type").equals("general")) {
                    GeneralCatalog catalog = new GeneralCatalog(this, map);
                    allCatalogs.add(catalog);
                }
            }
            start += catalogList.size();
        }
        return allCatalogs;
    }

    private SongCatalog newSongCatalog(String id, String name)
            throws EchoNestException {
        Map<String, String> data = new HashMap<String, String>();
        data.put("id", id);
        data.put("name", name);
        data.put("type", "song");

        SongCatalog catalog = new SongCatalog(this, data);
        return catalog;
    }

    private GeneralCatalog newGeneralCatalog(String id, String name)
            throws EchoNestException {
        Map<String, String> data = new HashMap<String, String>();
        data.put("id", id);
        data.put("name", name);
        data.put("type", "general");

        GeneralCatalog catalog = new GeneralCatalog(this, data);
        return catalog;
    }

    @SuppressWarnings("unchecked")
    protected PagedListInfo getDocuments(String id, String command,
            String path, int start, int count) throws EchoNestException {
        Params p = new Params();
        p.add("id", id);
        p.add("start", start);
        p.add("results", count);
        Map results = cmd.sendCommand(command, p);
        Map response = (Map) results.get("response");
        return getPagedDocuments(path, response);
    }

    @SuppressWarnings("unchecked")
    protected PagedListInfo getDocuments(String id, String command,
            String path, int start, int count, boolean relevance)
            throws EchoNestException {
        Params p = new Params();
        p.add("id", id);
        p.add("start", start);
        p.add("results", count);
        if (relevance) {
            p.add("high_relevance", relevance);
        }
        Map results = cmd.sendCommand(command, p);
        Map response = (Map) results.get("response");
        return getPagedDocuments(path, response);
    }

    @SuppressWarnings("unchecked")
    protected PagedListInfo getDocuments(String id, String command,
            String path, int start, int count, String license)
            throws EchoNestException {
        Params p = new Params();
        p.add("id", id);
        p.add("start", start);
        p.add("results", count);
        p.add("license", license);
        Map results = cmd.sendCommand(command, p);
        Map response = (Map) results.get("response");
        return getPagedDocuments(path, response);
    }

    @SuppressWarnings("unchecked")
    protected PagedListInfo getDocuments(String id, String command,
            String path, int start, int count, List<String> licenses)
            throws EchoNestException {
        Params p = new Params();
        p.add("id", id);
        p.add("start", start);
        p.add("results", count);
        p.add("license", licenses);
        Map results = cmd.sendCommand(command, p);
        Map response = (Map) results.get("response");
        return getPagedDocuments(path, response);
    }

    @SuppressWarnings("unchecked")
    protected PagedListInfo getPagedDocuments(String path, Map response)
            throws EchoNestException {
        MQuery mq = new MQuery(response);
        Integer start = mq.getInteger("start", 0);
        Integer total = mq.getInteger("total");
        if (total == null) {
            throw new EchoNestException(
                    EchoNestException.CLIENT_SERVER_INCONSISTENCY,
                    "Missing total in doc return");
        }
        List list = (List) mq.getObject(path);
        return new PagedListInfo(start, total, list);
    }

    protected Commander getCmd() {
        return cmd;
    }
}

class PagedListInfo {

    int start;
    int total;
    @SuppressWarnings("unchecked")
    List docs;

    @SuppressWarnings("unchecked")
    public PagedListInfo(int start, int total, List docs) {
        super();
        this.start = start;
        this.total = total;
        this.docs = docs;
    }

    /**
     * @return the start
     */
    public int getStart() {
        return start;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return total;
    }

    /**
     * @return the docs
     */
    @SuppressWarnings("unchecked")
    public List getDocs() {
        return docs;
    }
}
