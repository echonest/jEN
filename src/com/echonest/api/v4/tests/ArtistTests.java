package com.echonest.api.v4.tests;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.Audio;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.Blog;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Image;
import com.echonest.api.v4.News;
import com.echonest.api.v4.PagedList;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Review;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Term;
import com.echonest.api.v4.Video;
import com.echonest.api.v4.YearsActive;

@RunWith(JUnit4.class)
public class ArtistTests extends TestCase {
    private static String BEATLES_MBID = "musicbrainz:artist:b10bbbfc-cf9e-42e0-be17-e2c3e1d2600d";
    private static String WEEZER_MBID = "musicbrainz:artist:6fe07aa5-fec0-4eca-a456-f29bff451b04";
    private static String COLDPLAY_MBID = "musicbrainz:artist:cc197bad-dc9c-440d-a5b5-d52ba2e14234";
    private static String WEEZER_PAULIFY = "paulify:artist:b8db077b79276a64d8f0f197d4986ce0";
    private static String COLDPLAY_PAULIFY = "paulify:artist:aa968850a9d255494612acd0552f8fcd";

    static EchoNestAPI en;
    static Artist beatles;
    static Artist weezer;
    static Artist coldplay;

    static boolean trace = true;
    private final static long THIS_YEAR = 2011L;

    @BeforeClass
    public static void setUpClass() throws EchoNestException {
        en = new EchoNestAPI();
        en.setMinCommandTime(0);
        en.setTraceSends(true);
        en.setTraceRecvs(false);

        beatles = en.searchArtists("The Beatles").get(0);
        weezer = en.searchArtists("Weezer").get(0);
        coldplay = en.searchArtists("Coldplay").get(0);

    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
        en.showStats();
    }

    @Test
    public void testSearch() throws EchoNestException {
        List<Artist> artists = en.searchArtists("The Beatles");
        assertTrue(artists.get(0).getName().equals("The Beatles"));

        artists = en.searchArtists("Weezer");
        assertTrue(artists.get(0).getName().equals("Weezer"));

        artists = en.searchArtists("Guns ANd Roses");
        assertTrue(artists.get(0).getName().equals("Guns N' Roses"));

        artists = en.searchArtists("GNR");
        assertTrue(foundArtist(artists, "Guns N' Roses"));

        artists = en.searchArtists("bjork");
        assertTrue(artists.get(0).getName().equals("Björk"));

        artists = en.searchArtists("bjork");
        assertTrue(artists.get(0).getName().equals("Björk"));

        artists = en.searchArtists("Emerson Lake & Palmer");
        assertTrue(artists.get(0).getName().equals("Emerson, Lake & Palmer"));

        artists = en.searchArtists("ThereIsNoArtistMatchingThisSTupidLongName");
        assertTrue(artists.size() == 0);
    }

    @Test
    public void testTooManySimilars() throws EchoNestException {
        try {
            beatles.getSimilar(500);
            fail();
        } catch (EchoNestException e) {

        }
    }

    @Test
    public void testSimilars() throws EchoNestException {
        List<Artist> artists = beatles.getSimilar(50);
        assertTrue(foundArtist(artists, "The Rolling Stones"));
        assertTrue(foundArtist(artists, "The Beau Brummels"));
    }

    @Test
    public void testSimilarsWithMinResults() throws EchoNestException {

        ArtistParams p = new ArtistParams();

        p.setName("weezer");
        p.setResults(30);
        p.setMinimumResults(20);
        List<Artist> results = en.getSimilarArtists(p);
        assertTrue("minimum results", results.size() >= 20);
    }

    @Test
    public void testHighlyConstrainedSimilarsWithMinResults()
            throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setName("weezer");
        p.setResults(30);
        p.setMinimumResults(20);
        p.setMaxFamiliarity(.6f);
        p.setMinFamiliarity(.59f);
        List<Artist> results = en.getSimilarArtists(p);
        assertTrue("minimum results", results.size() >= 20);
    }

    @Test
    public void testWreckomendation() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setName("The Beatles");
        p.setResults(30);
        p.setReverse(true);

        List<Artist> artists = en.getSimilarArtists(p);
        assertTrue("rolling stones",
                !foundArtist(artists, "The Rolling Stones"));
        assertTrue("the beau brummels", !foundArtist(artists,
                "The Beau Brummels"));
    }

    @Test
    public void testWreckomendationMinFamiliarity() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setName("The Beatles");
        p.setResults(30);
        p.setReverse(true);
        p.setMinFamiliarity(.6f);

        List<Artist> artists = en.getSimilarArtists(p);
        assertTrue("rolling stones",
                !foundArtist(artists, "The Rolling Stones"));
        assertTrue("the beau brummels", !foundArtist(artists,
                "The Beau Brummels"));
    }

    @Test
    public void testHotttnesss() throws EchoNestException {
        assertTrue(getArtist("Lady Gaga").getHotttnesss() > .7);
        assertTrue(getArtist("Beyonc�").getHotttnesss() > .6);
        assertTrue(getArtist("Tiny Tim").getHotttnesss() < .6);
    }

    @Test
    public void testFamiliarity() throws EchoNestException {
        assertTrue(getArtist("Lady Gaga").getFamiliarity() > .5);
        assertTrue(getArtist("The Beatles").getFamiliarity() > .8);
        assertTrue(getArtist("Bonerama").getFamiliarity() < .6);
    }

    @Test
    public void testAudio() throws EchoNestException {
        checkAudio(beatles, beatles.getAudio());
        checkAudio(weezer, weezer.getAudio());
        checkAudio(beatles, beatles.getAudio(20, 30));
        checkAudio(weezer, weezer.getAudio(10, 20));
    }

    @Test
    public void testSongs() throws EchoNestException {
        checkSongs(beatles, beatles.getSongs());
        checkSongs(weezer, weezer.getSongs());
        checkSongs(beatles, beatles.getSongs(20, 30));
        checkSongs(weezer, weezer.getSongs(10, 20));
    }

    @Test
    public void testAccessByName() throws EchoNestException {
        Artist weezer = en.newArtistByName("Weezer");
        checkAudio(weezer, weezer.getAudio());
        checkVideos(weezer, weezer.getVideos());
    }

    @Test
    public void testPagedAudio() throws EchoNestException {
        {
            PagedList<Audio> plist = beatles.getAudio(2, 5);
            assertTrue(plist.getStart() == 2);
            assertTrue(plist.getTotal() > 15);
            assertTrue(plist.size() == 5);
        }
        {
            PagedList<Audio> plist = weezer.getAudio(2, 5);
            assertTrue(plist.getStart() == 2);
            assertTrue(plist.getTotal() > 15);
            assertTrue(plist.size() == 5);
        }

        {
            PagedList<Audio> plist = coldplay.getAudio(0, 10);
            assertTrue(plist.getStart() == 0);
            assertTrue(plist.getTotal() > 15);
            assertTrue(plist.size() == 10);
        }
    }

    @Test
    public void testQueryByMBID() throws EchoNestException {
        Artist weezerFromMBID = en.newArtistByID(WEEZER_MBID);
        // System.out.println("weezmbid " + weezerFromMBID.toString());
        // System.out.println("weezer " + weezer.toString());
        assertTrue(weezer.equals(weezerFromMBID));

        Artist coldplayFromMBID = en.newArtistByID(COLDPLAY_MBID);
        assertTrue(coldplay.equals(coldplayFromMBID));
    }

    @Test
    public void testMusicbranz() throws EchoNestException {
        assertTrue(weezer.getForeignID("musicbrainz").equals(WEEZER_MBID));
        assertTrue(coldplay.getForeignID("musicbrainz").equals(COLDPLAY_MBID));
    }

    @Test
    public void testPaulify() throws EchoNestException {
        assertTrue(weezer.getForeignID("paulify").equals(WEEZER_PAULIFY));
        assertTrue(coldplay.getForeignID("paulify").equals(COLDPLAY_PAULIFY));
    }

    public void checkAudio(Artist artist, List<Audio> audios)
            throws EchoNestException {

        for (Audio audio : audios) {

            // hmmm, how to test. not all fields are defined for all audios

            System.out.println();

            if (trace) {
                audio.dump();
            }

            audio.getArtistName();
            audio.getDate();
            audio.getLength();
            audio.getRelease();
            audio.getTitle();
            audio.getURL();

            // assertNotNull(audio.getArtistName());
            // assertTrue(audio.getArtistName(),
            // audio.getArtistName().equals(artist.getName()));
            // assertNotNull(audio.getArtistName());
            // assertNotNull(audio.getDate());
            // assertTrue(audio.getLength() > 0);
            // assertNotNull(audio.getRelease());
            // assertNotNull(audio.getTitle());
            checkURL(audio.getURL());
            // assertNotNull(audio.getLink());
        }
    }

    public void checkSongs(Artist artist, List<Song> songs)
            throws EchoNestException {

        assertTrue("has songs", songs.size() > 0);
        for (Song song : songs) {
            assertNotNull("has song ID", song.getID());
            assertTrue("has matching artist id", song.getArtistID().equals(artist.getID()));
            verifySong(song);
            
        }
    }
    
    private void verifySong(Song song) throws EchoNestException {
        song.showAll();
    }

    @Test
    public void testBiography() throws EchoNestException {
        checkBio(beatles, beatles.getBiographies());
        checkBio(weezer, weezer.getBiographies());
        checkBio(beatles, beatles.getBiographies(2, 3));
        checkBio(weezer, weezer.getBiographies(1, 5));
    }
    
    @Test
    public void testDocCounts() throws EchoNestException {
        checkDocCounts(beatles, beatles.getDocCounts());
        checkDocCounts(weezer, weezer.getDocCounts());
    }
    
    
    
    @Test
    public void testTopDocCounts() throws EchoNestException {
        for (Artist artist : en.topHotArtists(20)) {
            checkDocCounts(artist, artist.getDocCounts());
        }
    }
    
    @Test
    public void testTopFamiliarYearsActive() throws EchoNestException {
        int missing = 0;
        ArtistParams p = new ArtistParams();
        p.includeYearsActive();
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);
        p.setResults(100);
        for (Artist artist : en.searchArtists(p)) {
            checkYearsActive(artist);
            if (artist.getYearsActive().getRange() == null) {
                missing++;
                System.out.println("  missing years active for " + artist.getName());
            }
        }
        assertTrue("missing years active for familiar artists", missing == 0);
    }
    
    @Test
    public void testTopHotYearsActive() throws EchoNestException {
        int missing = 0;
        ArtistParams p = new ArtistParams();
        p.includeYearsActive();
        p.sortBy(ArtistParams.SORT_HOTTTNESSS, false);
        p.setResults(100);
        for (Artist artist : en.searchArtists(p)) {
            checkYearsActive(artist);
            if (artist.getYearsActive().getRange() == null) {
                missing++;
                System.out.println("  missing years active for " + artist.getName());
            }
        }
        assertTrue("missing years active for familiar artists", missing == 0);
    }
    
    
    
    @Test
    public void testYearsActive() throws EchoNestException {
        YearsActive bya = beatles.getYearsActive();
        assertTrue("beatles start ", bya.getRange()[0] == 1960L);
        assertTrue("beatles end ", bya.getRange()[1] == 1970L);
        
        YearsActive wya = weezer.getYearsActive();
        assertTrue("weezer start ", wya.getRange()[0] == 1992L);
        assertTrue("weezer end ", wya.getRange()[1] == null);
        
        Artist yes = en.searchArtists("Yes").get(0);

        YearsActive yya = yes.getYearsActive();
        assertTrue("Yes ranges", yya.size() == 3);

    }
    
    @Test
    public void testYearsActiveThroughout() throws EchoNestException {
        fail("not implementd yet");
    }
    
    @Test
    public void testYearsActiveDuring() throws EchoNestException {
        fail("not implementd yet");
    }

    @Test
    public void testPagedBiography() throws EchoNestException {
        checkPager(beatles.getBiographies(2, 3), 2, 3, 5);
        checkPager(weezer.getBiographies(1, 5), 1, 5, 5);
        checkPager(coldplay.getBiographies(1, 5), 1, 5, 5);
    }

    private void checkPager(PagedList<?> list, int start, int size, int min) {
        assertTrue("pager start", list.getStart() == start);
        assertTrue("pager total", list.getTotal() > min);
        assertTrue("pager size", list.size() == size);
    }

    @Test
    public void testBadBioLicenses() throws EchoNestException {
        try {
            weezer.getBiographies(0, 100, "ima-bad-license");
            fail();
        } catch (EchoNestException e) {
        }
    }

    @Test
    public void testBioLicenses() throws EchoNestException {
        List<Artist> artists = en.topHotArtists(100);
        int count = 0;
        for (Artist artist : artists) {
            List<Biography> bios = artist.getBiographies(0, 100, "cc-by-sa");
            if (bios.size() > 0) {
                count++;
            } else {
                System.out.println("no cc-by-sa bios for " + artist.getName());
            }
            for (Biography bio : bios) {
                assertTrue(bio.getLicenseType().equals("cc-by-sa"));
            }
        }
        System.out.println("Coverage is " + count);
        assertTrue("good bio coverage", count > 50);
    }
    
    public void checkDocCounts(Artist artist, Map<String, Long> docCounts) throws EchoNestException {
         assertTrue("doc counts audio", artist.getAudio(0,10).getTotal() == docCounts.get("audio"));
         assertTrue("doc counts video", artist.getVideos(0,10).getTotal() == docCounts.get("video"));
         assertTrue("doc counts reviews", artist.getReviews(0,10).getTotal() == docCounts.get("reviews"));
         assertTrue("doc counts images", artist.getImages(0,10).getTotal() == docCounts.get("images"));
         assertTrue("doc counts biographies", artist.getBiographies(0,10).getTotal() == docCounts.get("biographies"));
         assertTrue("doc counts news", artist.getNews(0,10).getTotal() == docCounts.get("news"));
         assertTrue("doc counts blogs", artist.getBlogs(0,10).getTotal() == docCounts.get("blogs"));
         assertTrue("doc counts songs", artist.getSongs(0,10).getTotal() == docCounts.get("songs"));
    }
    
    public void checkYearsActive(Artist artist) throws EchoNestException {
        YearsActive ya = artist.getYearsActive();
        if (ya.size() > 0) {
            Long[] range = ya.getRange();
            System.out.printf("%s %d %d\n", artist.getName(), range[0], range[1]);
            if (range[0] != null && range[1] != null) {
                assertTrue("years active too long", range[1] - range[0] < 75);
                assertTrue("years active too short", range[1] - range[0] >= 1);
            } else if (range[1] == null) {
                assertTrue("years active too long", THIS_YEAR - range[0] < 75);
            }
        }
        
        // perform some range checks
        if (ya.size() > 1) {
            for (int i = 1; i < ya.size(); i++) {
                Long[] prev = ya.getRange(i - 1);
                Long[] cur = ya.getRange(i);
                System.out.printf("%s %d %d\n", artist.getName(), cur[0], cur[1]);

                assertNotNull("should have a start year a", prev[0]);
                assertNotNull("should have a start year b", cur[0]);
                assertTrue("out of order a", year(cur[0]) > year(prev[0]));
                assertTrue("out of order b", year(cur[1]) > year(prev[1]));
                assertTrue("out of order c", year(cur[0]) > year(prev[1]));
            }
        }
    }
    
    int year(Long y) {
        if (y == null) {
            y = THIS_YEAR;
        }
        return y.intValue();
    }

    public void checkBio(Artist artist, List<Biography> bios)
            throws EchoNestException {

        for (Biography bio : bios) {
            System.out.println();

            if (trace) {
                bio.dump();
            }

            bio.getLicenseAttribution();
            bio.getLicenseType();
            bio.getSite();
            bio.getText();
            bio.getURL();

            assertNotNull(bio.getLicenseAttribution());
            assertNotNull(bio.getLicenseType());
            assertNotNull(bio.getSite());
            assertNotNull(bio.getText());
            checkURL(bio.getURL());
        }
    }

    @Test
    public void testBlog() throws EchoNestException {
        checkBlog(beatles, beatles.getBlogs());
        checkBlog(weezer, weezer.getBlogs());
        checkBlog(beatles, beatles.getBlogs(2, 3));
        checkBlog(weezer, weezer.getBlogs(1, 5));

    }

    @Test
    public void testPagedBlog() throws EchoNestException {
        checkPager(beatles.getBlogs(2, 3), 2, 3, 5);
        checkPager(weezer.getBlogs(1, 5), 1, 5, 5);
        checkPager(coldplay.getBlogs(1, 5), 1, 5, 5);
    }

    public void checkBlog(Artist artist, List<Blog> blogs)
            throws EchoNestException {

        for (Blog blog : blogs) {
            System.out.println();

            if (trace) {
                blog.dump();
            }

            blog.getDateFound();
            blog.getDatePosted();
            blog.getName();
            blog.getSummary();
            blog.getURL();

            assertNotNull(blog.getDateFound());
            assertNotNull(blog.getDatePosted());
            assertNotNull(blog.getName());
            assertNotNull(blog.getSummary());
            checkURL(blog.getURL());
        }
    }

    @Test
    public void testImages() throws EchoNestException {
        checkImage(beatles, beatles.getImages());
        checkImage(weezer, weezer.getImages());
        checkImage(beatles, beatles.getImages(2, 3));
        checkImage(weezer, weezer.getImages(1, 5));

    }

    @Test
    public void testPagedImages() throws EchoNestException {
        checkPager(beatles.getImages(2, 3), 2, 3, 5);
        checkPager(weezer.getImages(1, 5), 1, 5, 5);
        checkPager(coldplay.getImages(1, 5), 1, 5, 5);
    }

    @Test
    public void testBadImageLicenses() throws EchoNestException {
        try {
            weezer.getImages(0, 100, "ima-bad-license");
            fail();
        } catch (EchoNestException e) {
        }
    }

    @Test
    public void testImageLicenses() throws EchoNestException {
        List<Image> images = weezer.getImages(0, 100, "cc-by-sa");
        assertTrue(images.size() > 0);
        for (Image image : images) {
            assertTrue(image.getLicenseType().equals("cc-by-sa"));
        }
    }

    public void checkImage(Artist artist, List<Image> images)
            throws EchoNestException {

        for (Image image : images) {
            System.out.println();

            if (trace) {
                image.dump();
            }

            image.getLicenseAttribution();
            image.getLicenseType();
            image.getURL();

            assertNotNull(image.getLicenseAttribution());
            assertNotNull(image.getLicenseType());
            checkURL(image.getURL());
        }
    }

    @Test
    public void testNews() throws EchoNestException {
        checkNews(beatles, beatles.getNews());
        checkNews(weezer, weezer.getNews());
        checkNews(beatles, beatles.getNews(2, 3));
        checkNews(weezer, weezer.getNews(1, 5));

    }

    @Test
    public void testPagedNews() throws EchoNestException {
        checkPager(beatles.getNews(2, 3), 2, 3, 5);
        checkPager(weezer.getNews(1, 5), 1, 5, 5);
        checkPager(coldplay.getNews(1, 5), 1, 5, 5);
    }

    @Test
    public void testRelevantNews() throws EchoNestException {
        // checkNewsTitle(beatles.getNews(0, 15, true), beatles);
        checkNewsTitle(weezer.getNews(0, 15, true), weezer);
        // checkNewsTitle(coldplay.getNews(0, 15, true), coldplay);
    }

    @Test
    public void testRelevantBlogs() throws EchoNestException {
        // checkBlogTitle(beatles.getBlogs(0, 15, true), beatles);
        checkBlogTitle(weezer.getBlogs(0, 15, true), weezer);
        // checkBlogTitle(coldplay.getBlogs(0, 15, true), coldplay);
    }

    private void checkNewsTitle(List<News> newsList, Artist artist)
            throws EchoNestException {
        for (News news : newsList) {
            titleMatch(news.getName(), artist);
        }
    }

    private void checkBlogTitle(List<Blog> blogs, Artist artist)
            throws EchoNestException {
        for (Blog blog : blogs) {
            titleMatch(blog.getName(), artist);
        }
    }

    private void titleMatch(String title, Artist artist)
            throws EchoNestException {
        assertTrue("relevant name in title", title.toLowerCase().contains(
                artist.getName().toLowerCase()));
    }

    @Test
    public void searchEmptyBucketTest() throws EchoNestException {
        Artist gaga = getArtist("Lady Gaga");
        assertTrue("audio", !gaga.hasBucket("audio"));
        assertTrue("biographies", !gaga.hasBucket("biographies"));
        assertTrue("blogs", !gaga.hasBucket("blogs"));
        assertTrue("familiarity", !gaga.hasBucket("familiarity"));
        assertTrue("hotttnesss", !gaga.hasBucket("hotttnesss"));
        assertTrue("images", !gaga.hasBucket("images"));
        assertTrue("news", !gaga.hasBucket("news"));
        assertTrue("reviews", !gaga.hasBucket("reviews"));
        assertTrue("urls", !gaga.hasBucket("urls"));
        assertTrue("video", !gaga.hasBucket("video"));
        assertTrue("songs", !gaga.hasBucket("songs"));

    }

    @Test
    public void searchFullBucketTest() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setName("Lady Gaga");
        p.includeAll();
        List<Artist> results = en.searchArtists(p);
        assertTrue("results", results.size() > 0);
        Artist gaga = results.get(0);
        assertTrue("audio", gaga.hasBucket("audio"));
        assertTrue("biographies", gaga.hasBucket("biographies"));
        assertTrue("blogs", gaga.hasBucket("blogs"));
        assertTrue("familiarity", gaga.hasBucket("familiarity"));
        assertTrue("hotttnesss", gaga.hasBucket("hotttnesss"));
        assertTrue("images", gaga.hasBucket("images"));
        assertTrue("news", gaga.hasBucket("news"));
        assertTrue("reviews", gaga.hasBucket("reviews"));
        assertTrue("urls", gaga.hasBucket("urls"));
        assertTrue("video", gaga.hasBucket("video"));
        assertTrue("songs", gaga.hasBucket("songs"));
        assertTrue("years_active", gaga.hasBucket("years_active"));
    }

    @Test
    public void artistCoverageWithBuckets() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(10);
        p.includeAll();
        List<Artist> artists = en.topHotArtists(p);
        for (Artist artist : artists) {
            assertTrue("audio", artist.hasBucket("audio"));
            assertTrue("biographies", artist.hasBucket("biographies"));
            assertTrue("blogs", artist.hasBucket("blogs"));
            assertTrue("familiarity", artist.hasBucket("familiarity"));
            assertTrue("hotttnesss", artist.hasBucket("hotttnesss"));
            assertTrue("images", artist.hasBucket("images"));
            assertTrue("news", artist.hasBucket("news"));
            assertTrue("reviews", artist.hasBucket("reviews"));
            assertTrue("urls", artist.hasBucket("urls"));
            assertTrue("video", artist.hasBucket("video"));
            assertTrue("songs", artist.hasBucket("songs"));
            assertTrue("years_active", artist.hasBucket("years_active"));
        }
    }

    @Test
    public void artistSearchWithBuckets() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(10);
        p.includeAll();
        p.setName("the");
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            assertTrue("audio", artist.hasBucket("audio"));
            assertTrue("biographies", artist.hasBucket("biographies"));
            assertTrue("blogs", artist.hasBucket("blogs"));
            assertTrue("familiarity", artist.hasBucket("familiarity"));
            assertTrue("hotttnesss", artist.hasBucket("hotttnesss"));
            assertTrue("images", artist.hasBucket("images"));
            assertTrue("news", artist.hasBucket("news"));
            assertTrue("reviews", artist.hasBucket("reviews"));
            assertTrue("urls", artist.hasBucket("urls"));
            assertTrue("video", artist.hasBucket("video"));
            assertTrue("songs", artist.hasBucket("songs"));
            assertTrue("years_active", artist.hasBucket("years_active"));
        }
    }

    @Test
    public void artistSearchWithFamConstraints() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(10);
        p.includeFamiliarity();
        p.setMinFamiliarity(.8f);
        p.setMaxFamiliarity(.9f);
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            assertTrue("min_fam", artist.getFamiliarity() >= .8f);
            assertTrue("max_fam", artist.getFamiliarity() <= .9f);
        }
    }

    @Test
    public void artistSearchWithHotConstraints() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(10);
        p.includeHotttnesss();
        p.setMinHotttnesss(.8f);
        p.setMaxHotttnesss(.9f);
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            assertTrue("min_hot", artist.getHotttnesss() >= .8f);
            assertTrue("max_hot", artist.getHotttnesss() <= .9f);
        }
    }

    @Test
    public void artistSearchByNameWithFamSort() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(10);
        p.includeFamiliarity();
        p.setName("karaoke");
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);

        List<Artist> artists = en.searchArtists(p);
        double last = 1.0;
        for (Artist artist : artists) {
            if (!Double.isNaN(artist.getFamiliarity())) {
                assertTrue("fam_order", artist.getFamiliarity() <= last);
                last = artist.getFamiliarity();
            }
        }
    }

    @Test
    public void artistSearchByNameWithHotConstraints() {
        try {
            ArtistParams p = new ArtistParams();
            p.setResults(10);
            p.includeHotttnesss();
            p.setName("weezer");
            p.setMinHotttnesss(.5f);
            List<Artist> artists = en.searchArtists(p);
            fail("no search by name with constraints");
        } catch (EchoNestException e) {

        }
    }

    @Test
    public void artistTopHotttCount() throws EchoNestException {
        int stepSize = 1000;
        int total = 1000;

        Set<Artist> artists = new HashSet<Artist>();
        for (int i = 0; i < total; i += stepSize) {
            artists.addAll(en.topHotArtists(i, stepSize));
        }
        System.out.println("Found " + artists.size() + " hot artists");
        assertTrue("not enough artists", artists.size() == total);
    }

    @Test
    public void artistTopHotttCountByPage() throws EchoNestException {
        int stepSize = 100;
        int total = 1000;

        Set<Artist> artists = new HashSet<Artist>();
        for (int i = 0; i < total; i += stepSize) {
            artists.addAll(en.topHotArtists(i, stepSize));
        }
        System.out.println("Found " + artists.size() + " hot artists");
        assertTrue("not enough artists", artists.size() == total);
    }

    @Test
    public void artistSearchWithBothConstraints() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(100);
        p.includeHotttnesss();
        p.includeFamiliarity();

        p.setMinFamiliarity(.8f);
        p.setMaxFamiliarity(.9f);
        p.setMinHotttnesss(.8f);
        p.setMaxHotttnesss(.9f);
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            assertTrue("min_hot", artist.getHotttnesss() >= .8f);
            assertTrue("max_hot", artist.getHotttnesss() <= .9f);
            assertTrue("min_fam", artist.getFamiliarity() >= .8f);
            assertTrue("max_fam", artist.getFamiliarity() <= .9f);
        }
    }

    @Test
    public void artistSearchWithMixedConstraints() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.includeHotttnesss();
        p.includeFamiliarity();

        p.setMinFamiliarity(.8f);
        p.setMaxHotttnesss(.9f);
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            assertTrue("max_hot", artist.getHotttnesss() <= .9f);
            assertTrue("min_fam", artist.getFamiliarity() >= .8f);
        }
    }

    @Test
    public void artistSearchWithAmbiguousConstraints() {
        try {
            ArtistParams p = new ArtistParams();
            p.setResults(10);
            p.includeHotttnesss();
            p.includeFamiliarity();

            p.setMinFamiliarity(.8f);
            p.setMinFamiliarity(.89f);
            List<Artist> artists = en.searchArtists(p);
        } catch (EchoNestException e) {

        }
    }

    @Test
    public void artistSearchWithAmbiguousSorts() {
        try {
            ArtistParams p = new ArtistParams();
            p.setResults(10);
            p.includeHotttnesss();
            p.includeFamiliarity();
            p.sortBy(ArtistParams.SORT_FAMILIARITY, true);
            p.sortBy(ArtistParams.SORT_HOTTTNESSS, true);
            List<Artist> artists = en.searchArtists(p);
            fail("ambiguous constraints");
        } catch (EchoNestException e) {

        }
    }

    @Test
    public void artistSearchForTopHot() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(100);
        p.includeHotttnesss();
        p.sortBy(ArtistParams.SORT_HOTTTNESSS, false);
        List<Artist> artists = en.searchArtists(p);
        List<Artist> top = en.topHotArtists(100);

        assertTrue("size match", top.size() == artists.size());

        assertTrue("top hot match", artists.containsAll(top));
        assertTrue("hot top match", top.containsAll(artists));
    }

    @Test
    public void artistSearchForTopFam() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.includeFamiliarity();
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);
        List<Artist> artists = en.searchArtists(p);
        double last = 1;
        for (Artist artist : artists) {
            assertTrue("Bad fam order", artist.getFamiliarity() <= last);
            last = artist.getFamiliarity();
        }
    }

    @Test
    public void artistSearchWithManyResults() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(100);
        p.includeFamiliarity();
        p.includeBlogs();
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);
        List<Artist> artists = en.searchArtists(p);
        double last = 1;
        for (Artist artist : artists) {
            assertTrue("Bad fam order", artist.getFamiliarity() <= last);
            last = artist.getFamiliarity();
        }
    }

    @Test
    public void artistSearchWithAmbiguousQuery() throws EchoNestException {
        try {
            ArtistParams p = new ArtistParams();
            p.setResults(10);
            p.includeHotttnesss();
            p.addDescription("rock");
            p.setName("beatles");
            p.includeFamiliarity();

            p.setMinFamiliarity(.8f);
            p.setMinFamiliarity(.89f);
            List<Artist> artists = en.searchArtists(p);
            fail("ambiguous query");
        } catch (EchoNestException e) {
        }
    }

    @Test
    public void artistSearchByDescription() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(100);
        p.addDescription("british invasion");
        p.addDescription("60s");
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);

        boolean found = false;
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            if (artist.equals(beatles)) {
                found = true;
            }
        }
        assertTrue("Beatles should be familiar 60s British Invasion artist",
                found);
    }

    @Test
    public void artistSearchByDescription2() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(100);
        p.addDescription("merseybeat");
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);

        boolean found = false;
        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            if (artist.equals(beatles)) {
                found = true;
            }
        }
        assertTrue("Beatles should be familiar merseybeat  artist", found);
    }

    @Test
    public void artistRepeatableSearchByDescription() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.addDescription("rock");
        List<Artist> artists1 = en.searchArtists(p);
        List<Artist> artists2 = en.searchArtists(p);

        assertTrue("return length", artists1.size() == artists2.size());

        for (int i = 0; i < artists1.size(); i++) {
            assertTrue("query should match", artists1.get(i).equals(
                    artists2.get(i)));
        }
    }

    @Test
    public void artistSortByFam() throws EchoNestException {
        {
            ArtistParams p = new ArtistParams();
            p.setResults(20);
            p.includeHotttnesss();
            p.sortBy(ArtistParams.SORT_FAMILIARITY, false);
            p.includeFamiliarity();

            double lastFam = 1.0f;
            List<Artist> artists = en.searchArtists(p);
            for (Artist artist : artists) {
                assertTrue("famsort", artist.getFamiliarity() <= lastFam);
                lastFam = artist.getFamiliarity();
            }
        }

        {
            ArtistParams p = new ArtistParams();
            p.setResults(20);
            p.includeHotttnesss();
            p.sortBy(ArtistParams.SORT_FAMILIARITY, true);
            p.includeFamiliarity();

            double lastFam = -1.0f;
            List<Artist> artists = en.searchArtists(p);
            for (Artist artist : artists) {
                if (!Double.isNaN(artist.getFamiliarity())) {
                    assertTrue("famsort", artist.getFamiliarity() >= lastFam);
                }
                lastFam = artist.getFamiliarity();
            }
        }
    }

    @Test
    public void negativeFam() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.includeHotttnesss();
        p.sortBy(ArtistParams.SORT_FAMILIARITY, true);
        p.includeFamiliarity();

        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            if (!Double.isNaN(artist.getFamiliarity())) {
                assertTrue("negative familiarity", artist.getFamiliarity() >= 0);
            }
        }
    }

    @Test
    public void artistSortByHot() throws EchoNestException {
        {
            ArtistParams p = new ArtistParams();
            p.setResults(20);
            p.includeHotttnesss();
            p.sortBy(ArtistParams.SORT_HOTTTNESSS, false);
            p.includeFamiliarity();

            double last = 1.0f;
            List<Artist> artists = en.searchArtists(p);
            for (Artist artist : artists) {
                assertTrue("hot sort", artist.getHotttnesss() <= last);
                last = artist.getHotttnesss();
            }
        }

        {
            ArtistParams p = new ArtistParams();
            p.setResults(20);
            p.includeHotttnesss();
            p.sortBy(ArtistParams.SORT_HOTTTNESSS, true);
            p.includeFamiliarity();

            double last = .0f;
            List<Artist> artists = en.searchArtists(p);
            for (Artist artist : artists) {
                assertTrue("famsort", artist.getHotttnesss() >= last);
                last = artist.getHotttnesss();
            }
        }
    }

    @Test
    public void topTermsTest() throws EchoNestException {
        List<Term> topTerms = en.getTopTerms(1000);
        assertTrue("count 1000", topTerms.size() == 1000);
        assertTrue("rock is top", topTerms.get(0).getName().equals("rock"));
        assertTrue("rock is 1", topTerms.get(0).getFrequency() == 1.0);
    }

    @Test
    public void artistSingleSpace() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.addIDSpace("paulify");
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        for (Artist artist : artists) {
            assertTrue("id for " + artist.getName(), artist.getForeignID(
                    "paulify").length() > 0);
        }
    }

    @Test
    public void artistTermsTest() throws EchoNestException {
        List<Term> terms = beatles.getTerms();
        boolean found = false;

        assertTrue("not enough terms", terms.size() > 20);
        {
            double last = 1.0;

            for (Term term : terms) {
                if (term.getName().equals("british invasion")) {
                    found = true;
                }
                assertTrue("term sort 1", term.getWeight() <= last);
                last = term.getWeight();
            }
            if (!found) {
                fail("beatles must be british invasion");
            }
        }

        {
            double last = 1.0;

            Term.sortByFrequency(terms);
            for (Term term : terms) {
                assertTrue("term sort 1", term.getFrequency() <= last);
                last = term.getFrequency();
            }
        }

        {
            double last = 1.0;

            Term.sortByWeight(terms);
            for (Term term : terms) {
                assertTrue("term sort 1", term.getWeight() <= last);
                last = term.getWeight();
            }
        }
    }

    @Test
    public void artistTermCoverage() throws EchoNestException {
        int max = 100;
        List<Artist> hot = en.topHotArtists(max);
        int sum = 0;
        Artist minArtist = null;
        for (Artist artist : hot) {
            System.out.println("artist is " + artist.getName());
            if (minArtist == null
                    || artist.getTerms().size() < minArtist.getTerms().size()) {
                minArtist = artist;
            }
            sum += artist.getTerms().size();
        }
        int avg = sum / max;
        assertTrue("avg terms is " + avg, avg > 15);
        assertTrue("too few terms for " + minArtist.getName(), minArtist
                .getTerms().size() > 5);
    }

    @Test
    public void artistTermCoverageWithBuckets() throws EchoNestException {
        int avg = 0;
        int lavg = 0;
        int max = 100;

        {
            List<Artist> hot = en.topHotArtists(max);
            int sum = 0;
            Artist minArtist = null;
            for (Artist artist : hot) {
                if (minArtist == null
                        || artist.getTerms().size() < minArtist.getTerms()
                                .size()) {
                    minArtist = artist;
                }
                sum += artist.getTerms().size();
            }
            avg = sum / max;
            assertTrue("avg terms is " + avg, avg > 15);
        }
        {
            ArtistParams p = new ArtistParams();
            p.setResults(max);
            p.includeTerms();

            List<Artist> hot = en.topHotArtists(p);
            int sum = 0;
            Artist minArtist = null;
            for (Artist artist : hot) {
                if (minArtist == null
                        || artist.getTerms().size() < minArtist.getTerms()
                                .size()) {
                    minArtist = artist;
                }
                sum += artist.getTerms().size();
            }
            lavg = sum / max;
            assertTrue("avg terms is " + avg, avg > 15);
        }
        assertTrue("same avg", avg == lavg);
    }

    @Test
    public void musicBrainzArtistTest() throws EchoNestException {
        for (Artist artist : en.topHotArtists(100)) {
            String mbid = artist.getForeignID("musicbrainz");
            if (mbid != null) {
                Artist nArtist = en.newArtistByID(mbid);
                System.out.printf("%s %s %s %s\n", mbid, artist.getName(),
                        nArtist.getForeignID("musicbrainz"), nArtist.getName());
                assertTrue("mbid mismatch for " + artist.getName(), nArtist
                        .equals(artist));
            } else {
                System.out.println("NO MBID for " + artist.getName());
            }
        }
    }

    @Test
    public void artistTermCoverageWithBuckets2() throws EchoNestException {
        int max = 250;
        int minMissing = 10;
        int hasTerms = 0;
        {
            ArtistParams p = new ArtistParams();
            p.setResults(max);
            p.includeTerms();

            List<Artist> hot = en.topHotArtists(p);
            for (Artist artist : hot) {
                if (artist.getTerms().size() > 0) {
                    hasTerms++;
                } else {
                    System.out.println(" no terms for " + artist.getName());
                }
            }
            int missing = hot.size() - hasTerms;
            assertTrue("missing too many terms " + missing + " min "
                    + minMissing, missing < minMissing);
        }
    }

    @Test
    public void artistMultiSpace() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.addIDSpace("paulify");
        p.addIDSpace("musicbrainz");
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        for (Artist artist : artists) {
            String mbid = artist.hasBucket("musicbrainz") ? artist
                    .getForeignID("musicbrainz") : null;
            String paulify = artist.hasBucket("paulify") ? artist
                    .getForeignID("paulify") : null;
            assertTrue("id for " + artist.getName(), mbid != null
                    || paulify != null);
        }
    }

    @Test
    public void artistIdTranslation() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.setResults(20);
        p.addIDSpace("paulify");
        p.setLimit(true);
        int mbCount = 0;
        List<Artist> artists = en.topHotArtists(p);
        for (Artist artist : artists) {
            String paulify = artist.getForeignID("paulify");
            Artist partist = en.newArtistByID(paulify);
            assertTrue("en ID mismatch", partist.getID().equals(artist.getID()));
            String mbid = partist.getForeignID("musicbrainz");
            if (mbid != null) {
                mbCount++;
                Artist martist = en.newArtistByID(mbid);
                assertTrue(martist.getID().equals(artist.getID()));
                assertTrue("paulify from mbid doesn't match", martist
                        .getForeignID("paulify").equals(paulify));
            }
            assertTrue("no mbids found", mbCount > 0);
        }
    }

    @Test
    public void artistProfileWithBuckets() throws EchoNestException {
        List<Artist> artists = en.topHotArtists(20);
        String[] buckets = { "audio", "blogs", "biographies", "familiarity",
                "hotttnesss", "urls", "reviews", "images", "news", "video", "songs" };
        for (Artist artist : artists) {
            artist.fetchBuckets(buckets, true);
            assertTrue("audio", artist.hasBucket("audio"));
            assertTrue("biographies", artist.hasBucket("biographies"));
            assertTrue("blogs", artist.hasBucket("blogs"));
            assertTrue("familiarity", artist.hasBucket("familiarity"));
            assertTrue("hotttnesss", artist.hasBucket("hotttnesss"));
            assertTrue("images", artist.hasBucket("images"));
            assertTrue("news", artist.hasBucket("news"));
            assertTrue("reviews", artist.hasBucket("reviews"));
            assertTrue("urls", artist.hasBucket("urls"));
            assertTrue("video", artist.hasBucket("video"));
            assertTrue("songs", artist.hasBucket("songs"));

        }
    }

    @Test
    public void artistTopHotttWithIDSpaces() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.addIDSpace("paulify");
        p.setResults(100);
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        assertTrue("size", artists.size() == 100);
        for (Artist artist : artists) {
            assertTrue(artist.getName(), artist.hasBucket("paulify"));
        }
    }

    @Test
    public void artistTopHotttFreshness() throws EchoNestException {
        int count = 10;
        ArtistParams p = new ArtistParams();
        p.includeBlogs();
        p.includeNews();
        p.setResults(count);
        List<Artist> artists = en.topHotArtists(p);
        assertTrue("size", artists.size() == count);
        for (Artist artist : artists) {
            System.out.println("=== Artist " + artist.getName());
            assertTrue("blog size", artist.getBlogs().size() > 0);
            assertTrue("blog age", getAgeInDays(artist.getBlogs().get(0)
                    .getDatePosted()) < 30);

            assertTrue("news size", artist.getNews().size() > 0);
            assertTrue("news age", getAgeInDays(artist.getNews().get(0)
                    .getDatePosted()) < 30);
        }
    }

    private int getAgeInDays(Date date) {
        Date now = new Date();
        long deltaSeconds = (now.getTime() - date.getTime()) / 1000L;
        int ageInDays = ((int) deltaSeconds) / (60 * 60 * 24);
        System.out.println("Age in days " + date + " is " + ageInDays);
        return ageInDays;
    }

    @Test
    public void artistSimilarityWithIDSpaces() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.addIDSpace("paulify");
        p.setResults(100);
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        assertTrue("size", artists.size() == 100);
        for (Artist artist : artists) {
            ArtistParams ps = new ArtistParams();
            ps.addIDSpace("paulify");
            ps.setResults(10);
            ps.setLimit(true);
            ps.setID(artist.getForeignID("paulify"));
            List<Artist> sartists = en.getSimilarArtists(ps);
            assertTrue("slength", sartists.size() == 10);
            for (Artist sartist : sartists) {
                assertTrue(sartist.getName(), sartist.hasBucket("paulify"));
            }
        }
    }

    public void artistSimilarityForBoxee() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.addIDSpace("boxee-1");
        p.setResults(100);
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        assertTrue("size", artists.size() == 100);
        for (Artist artist : artists) {
            ArtistParams ps = new ArtistParams();
            ps.addIDSpace("boxee-1");
            ps.setResults(10);
            ps.setLimit(true);
            ps.setID(artist.getForeignID("boxee-1"));
            List<Artist> sartists = en.getSimilarArtists(ps);
            assertTrue("slength", sartists.size() == 10);
            for (Artist sartist : sartists) {
                assertTrue(sartist.getName(), sartist.hasBucket("boxee-1"));
            }
        }
    }

    @Test
    public void artistSimilarityWithLimitButNoBucket() {
        ArtistParams p = new ArtistParams();
        p.setLimit(true);
        p.setID(weezer.getID());
        try {
            en.getSimilarArtists(p);
            fail("limit with no idspace");
        } catch (EchoNestException e) {
        }
    }

    @Test
    public void artistCoverage() throws EchoNestException {
        int size = 20;
        int minScore = 15;

        ArtistParams p = new ArtistParams();
        p.setResults(size);
        List<Artist> artists = en.topHotArtists(p);

        int ac = 0, bc = 0, rc = 0, ic = 0, nc = 0, vc = 0;

        for (Artist artist : artists) {
            if (artist.getAudio().size() > 0) {
                ac++;
            }
            if (artist.getBlogs().size() > 0) {
                bc++;
            }
            if (artist.getReviews().size() > 0) {
                rc++;
            }
            if (artist.getImages().size() > 0) {
                ic++;
            }
            if (artist.getNews().size() > 0) {
                nc++;
            }
            if (artist.getVideos().size() > 0) {
                vc++;
            }
        }

        assertTrue("audio", ac > minScore);
        assertTrue("blogs", bc > minScore);
        assertTrue("reviews", rc > minScore);
        assertTrue("images", ic > minScore);
        assertTrue("news", nc > minScore);
        assertTrue("video", vc > minScore);
    }

    public void checkNews(Artist artist, List<News> newsList)
            throws EchoNestException {

        for (News news : newsList) {
            System.out.println();

            if (trace) {
                news.dump();
            }
            news.getDateFound();
            news.getDatePosted();
            news.getName();
            news.getSummary();
            news.getURL();

            assertNotNull(news.getDateFound());
            assertNotNull(news.getDatePosted());
            assertNotNull(news.getName());
            assertNotNull(news.getSummary());
            checkURL(news.getURL());
        }
    }

    private void checkURL(String urlString) {
        assertNotNull("url is null", urlString);
        try {
            URL url = new URL(urlString);
            assertNotNull("url is null", url);
        } catch (MalformedURLException e) {
            fail("malformed url " + urlString);
        }
    }

    @Test
    public void testReviews() throws EchoNestException {
        checkReviews(beatles, beatles.getReviews());
        checkReviews(weezer, weezer.getReviews());
        checkReviews(beatles, beatles.getReviews(2, 3));
        checkReviews(weezer, weezer.getReviews(1, 5));

    }

    @Test
    public void testPagedReviews() throws EchoNestException {
        checkPager(beatles.getReviews(2, 3), 2, 3, 5);
        checkPager(weezer.getReviews(1, 5), 1, 5, 5);
        checkPager(coldplay.getReviews(1, 5), 1, 5, 5);
    }

    public void testArtistParams() throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.includeBlogs();
        p.includeFamiliarity();
        p.setName("weezer");
        List<Artist> artists = en.searchArtists(p);
    }

    public void checkReviews(Artist artist, List<Review> reviews)
            throws EchoNestException {

        for (Review review : reviews) {
            System.out.println();

            if (trace) {
                review.dump();
            }

            review.getDateFound();
            review.getDateReviewed();
            review.getImageURL();
            review.getName();
            review.getRelease();
            review.getReviewText();
            review.getSummary();
            review.getURL();

            checkURL(review.getURL());
            assertNotNull(review.getDateFound());
            // assertNotNull(review.getDateReviewed());
            // assertNotNull(review.getImageUrl());
            assertNotNull(review.getName());
            assertNotNull(review.getRelease());
            // assertNotNull(review.getReviewText());
            assertNotNull(review.getSummary());

        }
    }

    @Test
    public void testVideos() throws EchoNestException {
        checkVideos(beatles, beatles.getVideos());
        checkVideos(weezer, weezer.getVideos());
        checkVideos(beatles, beatles.getVideos(2, 3));
        checkVideos(weezer, weezer.getVideos(1, 5));

    }

    @Test
    public void testPagedVideos() throws EchoNestException {
        checkPager(beatles.getVideos(2, 3), 2, 3, 5);
        checkPager(weezer.getVideos(1, 5), 1, 5, 5);
        checkPager(coldplay.getVideos(1, 5), 1, 5, 5);
    }

    public void checkVideos(Artist artist, List<Video> videos)
            throws EchoNestException {

        for (Video video : videos) {
            System.out.println();

            if (trace) {
                video.dump();
            }

            video.getDateFound();
            video.getImageURL();
            video.getTitle();
            video.getSite();
            video.getURL();

            checkURL(video.getURL());
            assertNotNull(video.getDateFound());
            assertNotNull(video.getTitle());
            // assertNotNull(video.getImageURL());
            assertNotNull(video.getSite());
        }
    }

    @Test
    public void testHotRange() throws EchoNestException {
        Params p = new Params();
        p.add("results", 1000);
        en.topHotArtists(p);
    }

    @Test
    public void testHotGaga() throws EchoNestException {
        Params p = new Params();
        p.add("results", 100);
        boolean found = false;
        List<Artist> artists = en.topHotArtists(p);
        for (Artist artist : artists) {
            if (artist.getName().equals("Lady Gaga")) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test
    public void testHotSearch() throws EchoNestException {
        Params p = new Params();
        p.add("results", 20);
        List<Artist> artists = en.topHotArtists(p);
        for (Artist artist : artists) {
            List<Artist> sartists = en.searchArtists(artist.getName(), 1);
            assertTrue("has results", sartists.size() == 1);
            assertTrue("name match", sartists.get(0).getName().equals(
                    artist.getName()));
        }
    }

    private Artist getArtist(String name) throws EchoNestException {
        return en.searchArtists(name).get(0);
    }

    private boolean foundArtist(List<Artist> artists, String name)
            throws EchoNestException {
        for (Artist artist : artists) {
            if (artist.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
