package com.echonest.api.v4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.echonest.api.v4.util.MQuery;

/**
 * Represents and Echo Nest Artist
 * 
 * @author plamere
 * 
 */
public class Artist extends ENItem {
    private final static String PATH = "artist";
    private final static String TYPE = "artist";

    @SuppressWarnings("unchecked")
    Artist(EchoNestAPI en, Map data) throws EchoNestException {
        super(en, TYPE, PATH, data);
    }

    /**
     * Gets the ID for the artist
     */
    public String getID() {
        if (data.containsKey("id")) {
            return (String) data.get("id");
        } else {
            return (String) data.get("thingID");
        }
    }

    Artist(EchoNestAPI en, String id) throws EchoNestException {
        this(en, id, false);
    }

    Artist(EchoNestAPI en, String idOrName, boolean byName)
            throws EchoNestException {
        super(en, TYPE, PATH, idOrName, byName);
    }

    /**
     * Gets the name of the artist
     * 
     * @return the name of the artist
     * @throws EchoNestException
     */
    public String getName() throws EchoNestException {
        return getString("name");
    }

    /**
     * Gets the hotttnesss for the artist
     * 
     * @return the artist hotttnesss
     * @throws EchoNestException
     */
    public double getHotttnesss() throws EchoNestException {
        fetchBucket("hotttnesss");
        return getDouble("hotttnesss");
    }

    /**
     * Gets the familiarity of the artist
     * 
     * @return the artist familiarity
     * @throws EchoNestException
     */
    public double getFamiliarity() throws EchoNestException {
        fetchBucket("familiarity");
        return getDouble("familiarity");
    }

    /**
     * Gets a foreign id for this artist
     * 
     * @param idspace
     *            the idspace of the artist
     * @return the foreign id
     * @throws EchoNestException
     */
    public String getForeignID(String idspace) throws EchoNestException {
        fetchBucket("id:" + idspace, idspace);
        return getString(idspace);
    }
    
    /**
     * Determines if the artist already has a given idspace
     * @param idspace the idspace
     * @return true if the artist has the idspace
     * @throws EchoNestException
     */
    public boolean hasForeignID(String idspace) throws EchoNestException {
        return hasBucket(idspace);
    }

    /**
     * Gets the most recent audio for this artist
     * 
     * @return a list of audio
     * @throws EchoNestException
     * 
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public List<Audio> getAudio() throws EchoNestException {
        String bucket = "audio";
        fetchBucket(bucket);
        return listToAudio((List) getObject(bucket));
    }

    /**
     * Gets a paged list of audio for tihs artist
     * 
     * @param start
     *            the starting index
     * @param count
     *            the maximum number of results to return
     * @return
     * @throws EchoNestException
     */
    @Deprecated
    public PagedList<Audio> getAudio(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/audio", "audio",
                start, count);
        return listToAudio(plist);
    }

    @SuppressWarnings("unchecked")
    private List<Audio> listToAudio(List docs) {
        List<Audio> results = new ArrayList<Audio>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Audio audio = new Audio(map);
            results.add(audio);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private PagedList<Audio> listToAudio(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<Audio> results = new PagedList<Audio>(plist.getStart(), plist
                .getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Audio audio = new Audio(map);
            results.add(audio);
        }
        return results;
    }
    
    /**
     * Gets the top songs for this artist
     * 
     * @return a list of audio
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Song> getSongs() throws EchoNestException {
        String bucket = "songs";
        fetchBucket(bucket);
        return listToSong((List) getObject(bucket));
    }
    
    @SuppressWarnings("unchecked")
    private List<Song> listToSong(List docs) throws EchoNestException {
        List<Song> results = new ArrayList<Song>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            map.put("artist_name", getName());
            map.put("artist_id", getID());
            Song song = new Song(en, map);
            results.add(song);
        }
        return results;
    }
    
    /**
     * Gets the active years for this artist
     * 
     * @return a list of audio
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public YearsActive getYearsActive() throws EchoNestException {
        String bucket = "years_active";
        fetchBucket(bucket);
        return new YearsActive((List) getObject(bucket));
    }
    
   /**
     * Gets the location for this artist
     * 
     * @return the location of this artist
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public ArtistLocation getArtistLocation() throws EchoNestException {
        String bucket = "artist_location";
        fetchBucket(bucket);
        return new ArtistLocation((Map) getObject(bucket));
    }

    /**
     * Gets a paged list of songs for this artist
     * 
     * @param start
     *            the starting index
     * @param count
     *            the maximum number of results to return
     * @return
     * @throws EchoNestException
     */
    public PagedList<Song> getSongs(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/songs", "songs",
                start, count);
        return listToSong(plist);
    }

    @SuppressWarnings("unchecked")
    private PagedList<Song> listToSong(PagedListInfo plist)
            throws EchoNestException {
        List docs = plist.getDocs();
        PagedList<Song> results = new PagedList<Song>(plist.getStart(), plist
                .getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            map.put("artist_name", getName());
            map.put("artist_id", getID());
            Song song = new Song(en, map);
            results.add(song);
        }
        return results;
    }

    /**
     * Gets the latest bios for this artist
     * 
     * @return the list of latest bios
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Biography> getBiographies() throws EchoNestException {
        String bucket = "biographies";
        fetchBucket(bucket);
        return listToBiography((List) getObject(bucket));
    }

    /**
     * Gets a paged list of bios for this artist
     * 
     * @param start
     *            the starting index
     * @param count
     *            the maximum number of results to return
     * @return
     * @throws EchoNestException
     */
    public PagedList<Biography> getBiographies(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/biographies",
                "biographies", start, count);
        return listToBiography(plist);
    }

    /**
     * Gets a paged list of bios for this artist
     * 
     * @param start
     *            the starting index
     * @param count
     *            the maxiumum number of bios returned
     * @param license
     *            the desired type of license
     * @return
     * @throws EchoNestException
     */
    public PagedList<Biography> getBiographies(int start, int count,
            String license) throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/biographies",
                "biographies", start, count, license);
        return listToBiography(plist);
    }

    /**
     * Gets a paged list of bios for this artist
     * 
     * @param start
     * @param count
     * @param licenses
     * @return
     * @throws EchoNestException
     */
    public PagedList<Biography> getBiographies(int start, int count,
            List<String> licenses) throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/biographies",
                "biographies", start, count, licenses);
        return listToBiography(plist);
    }

    @SuppressWarnings("unchecked")
    private List<Biography> listToBiography(List docs) {
        List<Biography> results = new ArrayList<Biography>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Biography bio = new Biography(map);
            results.add(bio);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private PagedList<Biography> listToBiography(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<Biography> results = new PagedList<Biography>(plist
                .getStart(), plist.getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Biography audio = new Biography(map);
            results.add(audio);
        }
        return results;
    }

    /**
     * Gets a list of the most recent blogs for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Blog> getBlogs() throws EchoNestException {
        String bucket = "blogs";
        fetchBucket(bucket);
        return listToBlog((List) getObject(bucket));
    }

    /**
     * Gets a paged list of recent blogs for this artist
     * 
     * @param start
     * @param count
     * @return
     * @throws EchoNestException
     */
    public PagedList<Blog> getBlogs(int start, int count, boolean highRelevance)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/blogs", "blogs",
                start, count, highRelevance);
        return listToBlog(plist);
    }

    /**
     * Gets a paged list of recent blogs for this artist
     * 
     * @param start
     * @param count
     * @return
     * @throws EchoNestException
     */
    public PagedList<Blog> getBlogs(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/blogs", "blogs",
                start, count);
        return listToBlog(plist);
    }

    @SuppressWarnings("unchecked")
    private List<Blog> listToBlog(List docs) {
        List<Blog> results = new ArrayList<Blog>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Blog bio = new Blog(map);
            results.add(bio);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private PagedList<Blog> listToBlog(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<Blog> results = new PagedList<Blog>(plist.getStart(), plist
                .getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Blog blog = new Blog(map);
            results.add(blog);
        }
        return results;
    }

    /**
     * Gets a list of the most recent images for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Image> getImages() throws EchoNestException {
        String bucket = "images";
        fetchBucket(bucket);
        return listToImage((List) getObject(bucket));
    }

    /**
     * Gets a paged list of recent images for this artist
     * 
     * @param start
     * @param count
     * @return
     * @throws EchoNestException
     */
    public PagedList<Image> getImages(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/images",
                "images", start, count);
        return listToImage(plist);
    }

    /**
     * Gets a paged list of recent images for the artist
     * 
     * @param start
     * @param count
     * @param licenses
     * @return
     * @throws EchoNestException
     */
    public PagedList<Image> getImages(int start, int count,
            List<String> licenses) throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/images",
                "images", start, count, licenses);
        return listToImage(plist);
    }

    /**
     * Gets a paged list of images for this artist
     * 
     * @param start
     * @param count
     * @param license
     * @return
     * @throws EchoNestException
     */
    public PagedList<Image> getImages(int start, int count, String license)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/images",
                "images", start, count, license);
        return listToImage(plist);
    }

    @SuppressWarnings("unchecked")
    private List<Image> listToImage(List docs) {
        List<Image> results = new ArrayList<Image>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Image image = new Image(map);
            results.add(image);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private PagedList<Image> listToImage(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<Image> results = new PagedList<Image>(plist.getStart(), plist
                .getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Image image = new Image(map);
            results.add(image);
        }
        return results;
    }

    /**
     * Gets the most recent news for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<News> getNews() throws EchoNestException {
        String bucket = "news";
        fetchBucket(bucket);
        return listToNews((List) getObject(bucket));
    }

    /**
     * Gets a paged list of news for this artist
     * 
     * @param start
     * @param count
     * @param highRelevance
     *            only return highly relevant news
     * @return
     * @throws EchoNestException
     */
    public PagedList<News> getNews(int start, int count, boolean highRelevance)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/news", "news",
                start, count, highRelevance);
        return listToNews(plist);
    }

    /**
     * Gets a paged list of news for this artist
     * 
     * @param start
     * @param count
     * @return
     * @throws EchoNestException
     */
    public PagedList<News> getNews(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/news", "news",
                start, count);
        return listToNews(plist);
    }

    @SuppressWarnings("unchecked")
    private List<News> listToNews(List docs) {
        List<News> results = new ArrayList<News>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            News news = new News(map);
            results.add(news);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private PagedList<News> listToNews(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<News> results = new PagedList<News>(plist.getStart(), plist
                .getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            News news = new News(map);
            results.add(news);
        }
        return results;
    }

    /**
     * Gets the most recent reviews for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Review> getReviews() throws EchoNestException {
        String bucket = "reviews";
        fetchBucket(bucket);
        return listToReviews((List) getObject(bucket));
    }

    /**
     * Gets a paged list of reviews for this artist
     * 
     * @param start
     * @param count
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public PagedList<Review> getReviews(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/reviews",
                "reviews", start, count);
        return listToReviews(plist);
    }

    @SuppressWarnings("unchecked")
    private List<Review> listToReviews(List docs) {
        List<Review> results = new ArrayList<Review>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Review review = new Review(map);
            results.add(review);
        }
        return results;
    }

    private PagedList<Review> listToReviews(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<Review> results = new PagedList<Review>(plist.getStart(),
                plist.getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Review review = new Review(map);
            results.add(review);
        }
        return results;
    }

    /**
     * Gets a list of videos for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Video> getVideos() throws EchoNestException {
        String bucket = "video";
        fetchBucket(bucket);
        return listToVideos((List) getObject(bucket));
    }

    /**
     * Gets a paged list of videos for this artist
     * 
     * @param start
     * @param count
     * @return
     * @throws EchoNestException
     */
    public PagedList<Video> getVideos(int start, int count)
            throws EchoNestException {
        PagedListInfo plist = en.getDocuments(getID(), "artist/video", "video",
                start, count);
        return listToVideos(plist);
    }

    @SuppressWarnings("unchecked")
    private List<Video> listToVideos(List docs) {
        List<Video> results = new ArrayList<Video>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Video video = new Video(map);
            results.add(video);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    private PagedList<Video> listToVideos(PagedListInfo plist) {
        List docs = plist.getDocs();
        PagedList<Video> results = new PagedList<Video>(plist.getStart(), plist
                .getTotal());
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            Video video = new Video(map);
            results.add(video);
        }
        return results;
    }

    /**
     * Gets a map of URLs for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getUrls() throws EchoNestException {
        fetchBucket("urls");
        Map map = (Map) getObject("urls");
        Map<String, String> results = new HashMap<String, String>();
        for (Object key : map.keySet()) {
            results.put((String) key, (String) map.get(key));
        }
        return results;
    }
    
    /**
     * Gets a map of URLs for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public Map<String, Long> getDocCounts() throws EchoNestException {
        fetchBucket("doc_counts");
        Map map = (Map) getObject("doc_counts");
        Map<String, Long> results = new HashMap<String, Long>();
        for (Object key : map.keySet()) {
            results.put((String) key, (Long) map.get(key));
        }
        return results;
    }

    /**
     * Gets a list of similar artists for this artist
     * 
     * @param count
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Artist> getSimilar(int count) throws EchoNestException {
        List<Artist> similars = null;
        // WORKaround until buckets are added to the artist methods
        if (data.containsKey("similar")) {
            similars = (List<Artist>) data.get("similar");
            if (similars.size() < count) {
                similars = null;
            }
        }
        if (similars == null) {
            Params p = new Params();
            p.add("id", getID());
            p.add("results", count);
            similars = en.getSimilarArtists(p);
            data.put("similar", similars);
        }

        if (similars.size() > count) {
            similars = similars.subList(0, count);
        }
        return similars;
    }

    /**
     * Gets a list of top terms for this artist
     * 
     * @return
     * @throws EchoNestException
     */
    @SuppressWarnings("unchecked")
    public List<Term> getTerms() throws EchoNestException {
        String bucket = "terms";
        fetchBucket(bucket);
        return listToTerms((List) getObject(bucket));
    }

    @SuppressWarnings("unchecked")
    private List<Term> listToTerms(List docs) {
        List<Term> terms = new ArrayList<Term>();
        for (int i = 0; i < docs.size(); i++) {
            Map map = (Map) docs.get(i);
            MQuery mq = new MQuery(map);
            String tname = mq.getString("name");
            double frequency = mq.getDouble("frequency");
            double weight = mq.getDouble("weight");
            Term term = new Term(tname, weight, frequency);
            terms.add(term);
        }
        return terms;
    }
}
