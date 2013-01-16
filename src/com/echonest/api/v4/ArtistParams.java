package com.echonest.api.v4;

/**
 * Manages params for artist search and similarity requests
 * @author plamere
 *
 */
public class ArtistParams extends Params {
    public static String SORT_FAMILIARITY = "familiarity";
    public static String SORT_HOTTTNESSS = "hotttnesss";
    public static String SORT_START_YEAR = "artist_start_year";
    public static String SORT_END_YEAR = "artist_end_year";


    /**
     * Sets the artist name
     * @param query
     */
    public void setName(String query) {
        add("name", query);
    }

    /**
     * Sets the artist ID
     * @param id
     */
    public void setID(String id) {
        add("id", id);
    }
    
    public void addName(String query) {
        add("name", query);
    }

    /**
     * Sets the artist ID
     * @param id
     */
    public void addID(String id) {
        add("id", id);
    }

    /**
     * Adds a description
     * @param description
     */
    public void addDescription(String description) {
        add("description", description);
    }

    /**
     * Sets the desired number of return results
     * @param results
     */
    public void setResults(int results) {
        set("results", results);
    }
    
    /**
     * Sets the minimum number of returned results
     * @param results
     */
    public void setMinimumResults(int results) {
        set("min_results", results);
    }
    
    
    /**
     * Constrains results to artists that have an earliest
     * start year before the given year
     * @param year
     */
    public void setStartYearBefore(int year) {
        set("artist_start_year_before", year);
    }
    
    /**
     * Constrains results to artists that have an earliest
     * start year after the given year
     * @param year
     */
    public void setStartYearAfter(int year) {
        set("artist_start_year_after", year);
    }
    
    /**
     * Constrains results to artists that have a latest
     * end year before the given year
     * @param year
     */
    public void setEndYearBefore(int year) {
        set("artist_end_year_before", year);
    }
    
    /**
     * Constrains results to artists that have a latest
     * end year after the given year
     * @param year
     */
    public void setEndYearAfter(int year) {
        set("artist_end_year_after", year);
    }
    
    /**
     * Constrains results to artists that were active
     * at any time during the given range
     * @param startYear the start year of interest
     * @param endYear the end year of interest
     */
    public void setArtistActiveDuring(int startYear, int endYear) {
        set("artist_active_during", startYear + ":" + endYear);
    }
    
    /**
     * Constrains results to artists that were active
     * throughout all of the the given range
     * @param startYear the start year of interest
     * @param endYear the end year of interest
     */
    public void setArtistActiveThroughout(int startYear, int endYear) {
        set("artist_active_throughout", startYear + ":" + endYear);
    }
    
    /**
     * Sets the starting index 
     * @param start
     */
    public void setStart(int start) {
        set("start", start);
    }

    
    public void setLimit(boolean limit) {
        set("limit", limit);
    }
    
    public void setReverse(boolean reverse) {
        set("reverse", reverse);
    }

    
    public void setSoundsLike(boolean soundsLike) {
        set("sounds_like", soundsLike);
    }

    public void setMinFamiliarity(float min) {
        set("min_familiarity", min);
    }

    public void setMaxFamiliarity(float max) {
        set("max_familiarity", max);
    }
    
    public void setMinHotttnesss(float min) {
        set("min_hotttnesss", min);
    }

    public void setMaxHotttnesss(float max) {
        set("max_hotttnesss", max);
    }

    public void includeAll() {
        includeBiographies();
        includeBlogs();
        includeFamiliarity();
        includeHotttnesss();
        includeImages();
        includeNews();
        includeReviews();
        includeURLs();
        includeVideo();
        includeSongs();
        includeDocCounts();
        includeYearsActive();
        includeArtistLocation();
        includeTerms();
    }

    public void includeBiographies() {
        add("bucket", "biographies");
    }
    
    public void includeDocCounts() {
        add("bucket", "doc_counts");
    }

    public void includeBlogs() {
        add("bucket", "blogs");
    }

    public void includeFamiliarity() {
        add("bucket", "familiarity");
    }

    public void includeTerms() {
        add("bucket", "terms");
    }
    
    public void includeSongs() {
        add("bucket", "songs");
    }

    public void includeHotttnesss() {
        add("bucket", "hotttnesss");
    }

    public void includeImages() {
        add("bucket", "images");
    }

    public void includeNews() {
        add("bucket", "news");
    }

    public void includeReviews() {
        add("bucket", "reviews");
    }

    public void includeURLs() {
        add("bucket", "urls");
    }

    public void includeVideo() {
        add("bucket", "video");
    }
    
    public void includeYearsActive() {
        add("bucket", "years_active");
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
    
    public void sortBy(String sort, boolean ascending) {
        if (ascending) {
            sort = sort + "-asc";
        } else {
            sort = sort + "-desc";
        }
        add("sort", sort);
    }
}
