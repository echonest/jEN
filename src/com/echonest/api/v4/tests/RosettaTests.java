package com.echonest.api.v4.tests;

import java.util.List;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;

@RunWith(JUnit4.class)
public class RosettaTests extends TestCase {
    static EchoNestAPI en;
    static boolean trace = false;
    static String[] catalogs = { "7digital-US", "7digital-UK", "boxee",
            "playme", "musicbrainz", "mtv", "mtv_numeric", "mtv_music_meter",
            "facebook", "fma", "emusic-US" };

    // these are minimal tests for feed. Better than nothing, but just barely.
    //

    @BeforeClass
    public static void setUpClass() throws EchoNestException {
        en = new EchoNestAPI();
        en.setMinCommandTime(0);
        en.setTraceSends(true);
        en.setTraceRecvs(trace);
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
    }

    // not a test anymore
    public void testArtistCatalogSimilarity() throws EchoNestException {
        for (String catalog : catalogs) {
            testRosettaArtistSimilarity(catalog);
        }
    }

    @Test
    public void test7DigitalUSArtistCatalogSimilarity()
            throws EchoNestException {
        testRosettaArtistSimilarity("7digital-US");
    }

    @Test
    public void test7DigitalUKArtistCatalogSimilarity()
            throws EchoNestException {
        testRosettaArtistSimilarity("7digital-UK");
    }

    @Test
    public void testFacebookArtistCatalogSimilarity() throws EchoNestException {
        testRosettaArtistSimilarity("facebook");
    }

    @Test
    public void testMTVArtistCatalogSimilarity() throws EchoNestException {
        testRosettaArtistSimilarity("mtv");
    }
    
    @Test
    public void testFMACatalogSimilarity()
            throws EchoNestException {
        testRosettaArtistSimilarity("fma");
    }
    
    @Test
    public void testEmusicUSCatalogSimilarity()
            throws EchoNestException {
        testRosettaArtistSimilarity("emusic-US");
    }

    @Test
    public void testMTVMusicMeterArtistCatalogSimilarity()
            throws EchoNestException {
        testRosettaArtistSimilarity("mtv_music_meter");
    }

    @Test
    public void testBoxeeArtistCatalogSimilarity() throws EchoNestException {
        testRosettaArtistSimilarity("boxee-1");
    }

    @Test
    public void testMusicBrainzArtistCatalogSimilarity()
            throws EchoNestException {
        testRosettaArtistSimilarity("musicbrainz");
    }

    @Test
    public void testPlaymeCatalogSimilarity() throws EchoNestException {
        testRosettaArtistSimilarity("playme");
    }

    @Test
    public void testMtvNumericCatalogSimilarity() throws EchoNestException {
        testRosettaArtistSimilarity("mtv_numeric");
    }

    public void testRosettaArtistSimilarity(String catalog)
            throws EchoNestException {
        ArtistParams p = new ArtistParams();
        p.addIDSpace(catalog);
        p.setResults(100);
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        assertTrue("size", artists.size() == 100);

        for (Artist artist : artists) {
            ArtistParams ps = new ArtistParams();
            ps.addIDSpace(catalog);
            ps.setResults(10);
            ps.setLimit(true);
            ps.setID(artist.getForeignID(catalog));
            List<Artist> sartists = en.getSimilarArtists(ps);
            assertTrue("slength", sartists.size() == 10);
            for (Artist sartist : sartists) {
                assertTrue("sim with catalog " + catalog + " "
                        + sartist.getName() 
                        + " seed is " + artist.getName(), sartist.hasBucket(catalog));
            }
        }
    }

    // not a test
    public void testArtistCatalogAccess() throws EchoNestException {
        for (String catalog : catalogs) {
            testRosettaArtistAccess(catalog);
        }
    }

    @Test
    public void test7DigitalUSArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("7digital-US");
    }
    @Test
    public void testFmaArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("fma");
    }
    @Test
    public void testeMusicUSArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("emusic-US");
    }

    @Test
    public void test7DigitalUKArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("7digital-UK");
    }

    @Test
    public void testFacebookArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("facebook");
    }

    @Test
    public void testMusicBrainzArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("musicbrainz");
    }

    @Test
    public void testBoxeeArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("boxee-1");
    }

    @Test
    public void testMTVArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("mtv");
    }

    @Test
    public void testMTVNumericArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("mtv_numeric");
    }

    @Test
    public void testMTVMusicMeterArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("mtv_music_meter");
    }

    @Test
    public void testPlaymeArtistCatalogAccess() throws EchoNestException {
        testRosettaArtistAccess("playme");
    }

    @Test
    public void test7DigitalUSArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("7digital-US");
    }

    @Test
    public void test7DigitalUKArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("7digital-UK");
    }

    @Test
    public void testFacebookArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("facebook");
    }
    
    @Test
    public void testFMAArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("fma");
    }
    
    @Test
    public void testEmusicUSArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("emusic-US");
    }
    
    @Test
    public void testFacebookArtistBigCoverageByFam() throws EchoNestException {
        testRosettaArtistBigCoverage("facebook", 20000, 80, ArtistParams.SORT_FAMILIARITY);
    }
    
    @Test
    public void testFacebookArtistBigCoverageByHot() throws EchoNestException {
        testRosettaArtistBigCoverage("facebook", 20000, 80, ArtistParams.SORT_HOTTTNESSS);
    }


    @Test
    public void testMusicBrainzArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("musicbrainz");
    }

    @Test
    public void testBoxeeArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("boxee-1");
    }

    @Test
    public void testMTVArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("mtv");
    }

    @Test
    public void testMTVNumericArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("mtv_numeric");
    }

    @Test
    public void testMTVMusicMeterArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("mtv_music_meter");
    }

    @Test
    public void testPlaymeArtistCoverage() throws EchoNestException {
        testRosettaArtistCoverage("playme");
    }

    public void testRosettaArtistAccess(String catalog)
            throws EchoNestException {
        int size = 500;
        ArtistParams p = new ArtistParams();
        p.addIDSpace(catalog);
        p.setResults(size);
        p.setLimit(true);
        List<Artist> artists = en.topHotArtists(p);
        int count = 0;
        assertTrue("size", artists.size() == size);

        for (Artist artist : artists) {
            try {
                Artist nArtist = en.newArtistByID(artist.getForeignID(catalog));
                double fam = nArtist.getFamiliarity();
                count++;
            } catch (EchoNestException e) {
                System.out
                        .println("Couldn't get " + artist.getForeignID(catalog)
                                + " " + artist.getName());
            }
        }
        assertTrue("found " + count + " of " + size, count == size);
    }

    public void testRosettaArtistCoverage(String catalog)
            throws EchoNestException {
        int size = 1000;
        int missing = 0;
        ArtistParams p = new ArtistParams();
        p.addIDSpace(catalog);
        p.setResults(size);
        List<Artist> artists = en.topHotArtists(p);
        assertTrue("size", artists.size() == size);

        for (Artist artist : artists) {
            if (!artist.hasForeignID(catalog)) {
                System.out.printf("missing %s artist %s\n", catalog, artist
                        .getName());
                missing += 1;
            }
        }

        p.setResults(100);
        p.sortBy(ArtistParams.SORT_FAMILIARITY, false);
        artists = en.searchArtists(p);
        assertTrue("size", artists.size() == 100);

        for (Artist artist : artists) {
            if (!artist.hasForeignID(catalog)) {
                System.out.printf("missing %s artist %s\n", catalog, artist
                        .getName());
                missing += 1;
            }
        }

        assertTrue("missing " + missing + " of " + size + " " + catalog
                + " ids ", missing == 0);
    }

    public void testRosettaArtistBigCoverage(String catalog, int count,
            int percent, String sort) throws EchoNestException {

        int missing = 0;
        int size = 100;
        int total = 0;
        int target = (int) Math.rint(count * percent / 100.0);
        
        System.out.printf("target is %d\n", target);


        for (int i = 0; i < count; i += size) {
            ArtistParams p = new ArtistParams();
            p.addIDSpace(catalog);
            p.setResults(size);
            p.setStart(i);
            p.sortBy(sort, false);

            List<Artist> artists = en.searchArtists(p);
            assertTrue("size", artists.size() == size);

            for (Artist artist : artists) {
                total += 1;
                if (!artist.hasForeignID(catalog)) {
                    missing += 1;
                    System.out.printf("%.0f%% %d/%d missing %s artist %s\n", missing * 100.0 / total, missing, total,
                            catalog, artist.getName());
                }
            }
        }

        assertTrue("missing " + missing + " of " + count + " " + catalog
                + " ids", total - missing > target);
    }
    
    
}
