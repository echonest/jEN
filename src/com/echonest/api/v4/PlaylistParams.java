package com.echonest.api.v4;

public class PlaylistParams extends Params {

    public enum PlaylistType {

        ARTIST, ARTIST_RADIO, ARTIST_DESCRIPTION, SONG_RADIO, CATALOG, CATALOG_RADIO, GENRE_RADIO
    }

    public enum PlaylistSort {

        TEMPO, DURATION, LOUDNESS, ARTIST_FAMILIARITY, ARTIST_HOTTTNESSS,
        ARTIST_START_YEAR, ARTIST_END_YEAR,
        SONG_HOTTTNESSS, LATITUDE, LONGITUDE, MODE, KEY, DANCEABILITY, ENERGY,
    }

    /**
     * Sets the type of the playlist. Default is ARTIST
     *
     * @param type the type of the playlist.
     */
    public void setType(PlaylistType type) {
        String stype = type.toString().toLowerCase().replace("_", "-");
        set("type", stype);
    }

    /**
     * How songs are to be picked for artist-type playlists. After songs have
     * been filtered by any constraints, artist_pick is used to determined which
     * song is to be picked for an artist. There are two components to the
     * artist_pick, separated by a tilde - a song sort and a value. An
     * artist_pick of hotttnesss-desc~10 means to pick a track at random from
     * the top 10 hotttest tracks.
     *
     * @param songSort the song sort
     * @param ascending if true, ascending sort
     * @param count the number of songs to pick from
     */
    public void setArtistPick(String songSort, boolean ascending, int count) {
        String sort = ascending ? "-asc" : "-desc";
        // String totalSort = songSort + sort + "~" + count;
        String totalSort = songSort + sort;
        set("artist_pick", totalSort);
    }

    /**
     * the variety of artists to be represented in the playlist. A higher number
     * will allow for more variety in the artists. Appropriate for playlists of
     * type artist-radio, artist-path, artist-walk, artist-description
     *
     * @param variety a value between 0 and 1
     */
    public void setVariety(float variety) {
        set("variety", variety);
    }

    /**
     * Controls the distribution of artists in the playlist. A focused
     * distribution yields a playlist of songs that are tightly clustered around
     * the seeds, whereas a wandering distribution yields a playlist from a
     * broader range of artists.
     *
     * @param variety a value between 0 and 1
     */
    public void setDistribution(boolean focused) {
        set("distribution", focused  ? "focused" : "wandering");
    }

    /**
     * Sets the desired adventurousness for the playlist. A value between 0 and
     * 1. A higher value indicates that a more adventurous playlist should be
     * generated.
     *
     * @param adventure a value between 0 and 1
     */
    public void setAdventurousness(float adventure) {
        set("adventurousness", adventure);
    }

    /**
     * Adds a seed catalog
     *
     * @param id the ID of the seed catalog
     */
    public void addSeedCatalog(String id) {
        add("seed_catalog", id);
    }

    /**
     * Adds an ID of a seed artist
     *
     * @param artistID an ID of the seed artist
     */
    public void addArtistID(String artistID) {
        add("artist_id", artistID);
    }

    /**
     * Adds the name of a seed artist
     *
     * @param artist the name of the seed artist
     */
    public void addArtist(String artist) {
        add("artist", artist);
    }

    /**
     * Adds a description of the type of artists that can be included in the
     * playlist
     *
     * @param desc
     */
    public void addDescription(String desc) {
        add("description", desc);
    }

    /**
     * Adds a style of the type of artists that can be included in the playlist
     *
     * @param style the style
     */
    public void addStyle(String style) {
        add("style", style);
    }

    /**
     * Adds a mood of the type of artists that can be included in the playlist
     *
     * @param mood the mood
     */
    public void addMood(String mood) {
        add("mood", mood);
    }

    /**
     * Adds a genre to the type of artists that can be included in the playlist
     * (for genre-radio)
     *
     * @param genre the name of the genre
     */
    public void addGenre(String genre) {
        add("genre", genre);
    }

    /**
     * Include songs of the given type
     *
     * @param type the song type
     * @param flag the state
     */
    public void addSongType(Song.SongType type, Song.SongTypeFlag flag) {
        add("song_type", type.toString() + ":" + flag.toString().toLowerCase());
    }

    /**
     * Set the maximum number of songs to return in the playlist
     *
     * @param results the results
     */
    public void setResults(int results) {
        set("results", results);
    }

    public void setMaxTempo(float tempo) {
        set("max_tempo", tempo);
    }

    public void setMinTempo(float tempo) {
        set("min_tempo", tempo);
    }

    public void setMaxDuration(float val) {
        set("max_duration", val);
    }

    public void setMinDuration(float val) {
        set("min_duration", val);
    }

    public void setMaxLoudness(float val) {
        set("max_loudness", val);
    }

    public void setMinLoudness(float val) {
        set("min_loudness", val);
    }

    public void setMaxFamiliarity(float val) {
        add("max_familiarity", val);
    }

    public void setMaxDanceability(float val) {
        add("max_danceability", val);
    }

    public void setMinDanceability(float val) {
        add("min_danceability", val);
    }

    public void setMaxEnergy(float val) {
        add("max_energy", val);
    }

    public void setMinEnergy(float val) {
        add("min_energy", val);
    }

    public void setArtistMaxFamiliarity(float val) {
        set("artist_max_familiarity", val);
    }

    public void setArtistMinFamiliarity(float val) {
        set("artist_min_familiarity", val);
    }

    public void setArtistMaxHotttnesss(float val) {
        set("artist_max_hotttnesss", val);
    }

    public void setArtistMinHotttnesss(float val) {
        set("aritst_min_hotttnesss", val);
    }

    public void setSongMaxHotttnesss(float val) {
        set("song_max_hotttnesss", val);
    }

    public void setSongMinHotttnesss(float val) {
        set("song_min_hotttnesss", val);
    }

    public void setArtistMaxLongitude(float val) {
        set("aritst_max_longitude", val);
    }

    public void setArtistMinLongitude(float val) {
        set("aritst_min_longitude", val);
    }

    public void setArtistMaxLatitude(float val) {
        set("artist_max_latitude", val);
    }

    public void setArtistMinLatitude(float val) {
        set("artist_min_latitude", val);
    }

    public void setMode(int mode) {
        set("mode", mode);
    }

    public void setKey(int key) {
        set("key", key);
    }

    public void setLimit(boolean limit) {
        set("limit", limit);
    }

    /**
     * Constrains results to artists that have an earliest start year before the
     * given year
     *
     * @param year
     */
    public void setArtistStartYearBefore(int year) {
        set("artist_start_year_before", year);
    }

    /**
     * Constrains results to artists that have an earliest start year after the
     * given year
     *
     * @param year
     */
    public void setArtistStartYearAfter(int year) {
        set("artist_start_year_after", year);
    }

    /**
     * Constrains results to artists that have a latest end year before the
     * given year
     *
     * @param year
     */
    public void setArtistEndYearBefore(int year) {
        set("artist_end_year_before", year);
    }

    /**
     * Constrains results to artists that have a latest end year after the given
     * year
     *
     * @param year
     */
    public void setArtistEndYearAfter(int year) {
        set("artist_end_year_after", year);
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

    public void addIDSpace(String idspace) {
        if (!idspace.startsWith("id:")) {
            idspace = "id:" + idspace;
        }
        add("bucket", idspace);
    }

    public void sortBy(PlaylistSort sortType, boolean ascending) {
        String sort = sortType.toString().toLowerCase();
        if (ascending) {
            sort = sort + "-asc";
        } else {
            sort = sort + "-desc";
        }
        set("sort", sort);
    }

    /*
     * If true the playlist delivered will meet the DMCA rules
     * @param dmca if true, playlist will meet DMCA rules
     */
    public void setDMCA(boolean dmca) {
        set("dmca", dmca);
    }
}
