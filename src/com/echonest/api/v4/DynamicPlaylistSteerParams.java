package com.echonest.api.v4;

public class DynamicPlaylistSteerParams extends Params {

    /**
     * parameters that can be used to as constraints or targets
     */
    public enum SteeringParameter {

        tempo, loudness, danceability, energy, song_hotttnesss,
        artist_hotttnesss, artist_familiarity
    };

    /**
     * Add a minimum constraint
     *
     * @param p the parameter to be constrained
     * @param minValue the minimum allowed value for the parameter
     */
    public void addMinConstraint(SteeringParameter p, float minValue) {
        add("min_" + p.toString(), minValue);
    }

    /**
     * Add a maximum constraint
     *
     * @param p the parameter to be constrained
     * @param maxValue the maximum allowed value for the parameter
     */
    public void addMaxConstraint(SteeringParameter p, float maxValue) {
        add("max_" + p.toString(), maxValue);

    }

    /**
     * Add a target for a parameter
     *
     * @param p the parameter
     * @param targetValue the parameter target value
     */
    public void addTargetValue(SteeringParameter p, float targetValue) {
        add("target_" + p.toString(), targetValue);

    }

    /**
     * Prefer songs that are similar to the given song ID. Song ID can be 'last'
     * to use the more recently returned song.
     *
     * @param id the song ID or 'last'
     */
    public void addNoreLikeThis(String id) {
        add("more_like_this", id);
    }

    /**
     * Prefer songs that are similar to the given song ID. Song ID can be 'last'
     * to use the more recently returned song.
     *
     * @param id the song ID or 'last'
     * @param boost an integer between 0 and 5
     */
    public void addNoreLikeThis(String id, int boost) {
        add("more_like_this", id + "^" + boost);

    }

    /**
     * Prefer songs that are least similar to the given song ID. Song ID can be
     * 'last' to use the more recently returned song.
     *
     * @param id the song ID or 'last'
     */
    public void addLessLikeThis(String id) {
        add("less_like_this", id);

    }

    /**
     * Prefer songs that are least similar to the given song ID. Song ID can be
     * 'last' to use the more recently returned song.
     *
     * @param id the song ID or 'last'
     * @param boost an integer between 0 and 5
     */
    public void addLessLikeThis(String id, int boost) {
        add("less_like_this", id + "^" + boost);

    }

    /**
     * adjust the adventurousness of the session. A value of 0 means no
     * adventurousness, only known and preferred music will be played. A value
     * of 1 means high adventurousness, mostly unknown music will be played.
     * This parameter only applies to catalog and catalog-radio type playlists.
     *
     * @param val the adventurousness - avalue between 0 and 1
     */
    public void setAdventurousness(float val) {
        set("adventurousness", val);
    }

    /**
     * adjust the variety of the session. A higher number will allow for more
     * variety in the artists.
     *
     * @param val the variety - a value between 0 and 1
     */
    public void setVariety(float val) {
        set("variety", val);

    }

    /**
     * Steer towards artists that match the given description
     *
     * @param term the term to boost
     * @param boost the boost
     */
    public void addDescription(EchoNestAPI.DescriptionType type, String term) {
        add(type.toString(), term);
    }

    /**
     * Steer towards artists that match the given description, with a boost
     *
     * @param term the term to boost
     * @param boost the boost
     */
    public void addDescription(EchoNestAPI.DescriptionType type, String term, float boost) {
        String tboost = term + "^" + boost;
        add(type.toString(), tboost);
    }

    /**
     * Bans the given term from appearing on the session
     *
     * @param term the term to ban
     */
    public void banDescription(EchoNestAPI.DescriptionType type, String term) {
        String bannedTerm = "-" + term;
        add(type.toString(), bannedTerm);
    }

    /**
     * Bans the given term from appearing on the session
     *
     * @param term the term to ban
     */
    public void requireDescription(EchoNestAPI.DescriptionType type, String term) {
        String requiredTerm = "+" + term;
        add(type.toString(), requiredTerm);
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
}
