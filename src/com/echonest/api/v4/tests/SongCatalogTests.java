package com.echonest.api.v4.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.echonest.api.v4.ArtistCatalog;
import com.echonest.api.v4.CatalogUpdater;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongCatalog;
import com.echonest.api.v4.SongCatalogItem;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.Track;

@RunWith(JUnit4.class)
public class SongCatalogTests extends TestCase {

    static EchoNestAPI en;
    static boolean trace = true;
    static Random rng = new Random();
    static String WEEZER_ID = "AR633SY1187B9AC3B9";
    static String ELP_ID = "ARMR7HO1187FB462CB";

    @BeforeClass
    public static void setUpClass() {
        try {
            en = new EchoNestAPI();
            en.setMinCommandTime(0);
            en.setTraceSends(trace);
            en.setTraceRecvs(trace);
        } catch (EchoNestException e) {
            System.out.println("EN Exception " + e);
            fail("trouble with the Echo Nest " + e);
            e.printStackTrace();
        }
    }

    @Test
    public void listEmptyCatalogs() throws EchoNestException {
        cleanupCatalogs();
        for (SongCatalog catalog : en.listSongCatalogs()) {
            System.out
                    .println("  " + catalog.getID() + " " + catalog.getName());
        }
        assertTrue("no catalogs", en.listSongCatalogs().size() == 0);
    }

    @Test
    public void createCatalogsWithDuplicateNames() throws EchoNestException {
        try {
            SongCatalog catalog = en
                    .createSongCatalog("my duplicate song catalog");
            SongCatalog dup = en.createSongCatalog("my duplicate song catalog");
            fail("duplicate catalogs");
        } catch (EchoNestException e) {
            System.out.println("dup " + e);
        } finally {
            cleanupCatalogs();
        }
    }

    void deleteCatalogByName(String name) throws EchoNestException {
        SongCatalog catalog = lookupCatalogByName(name);
        if (catalog != null) {
            try {
                catalog.delete();
            } catch (EchoNestException e) {
                System.out.println("erroneous delete error");
            }
        }
    }

    @Test
    public void createLargeTopHotCatalog() throws EchoNestException {
        int size = 75;
        System.out.println("create large top hot song catalog");
        deleteCatalogByName("top hot song catalog");
        SongCatalog catalog = createHotCatalog(size, "top hot song catalog");
        verifyCatalog(catalog, size);
    }

    @Test
    public void createLargeTopHotCatalogByIDs() throws EchoNestException {
        int size = 75;
        System.out.println("create large top hot catalog");
        deleteCatalogByName("top hot song catalog by ID");
        SongCatalog catalog = createHotCatalogByIDs(size,
                "top hot song catalog by ID");
        verifyCatalog(catalog, size);
        checkIdMatch(catalog);
    }

    // @Test
    public void createLargeTopHotCatalogByForeignIDs() throws EchoNestException {
        int size = 50;
        System.out.println("create large top hot catalog");
        deleteCatalogByName("top hot song catalog with fid");
        SongCatalog catalog = createHotCatalogByForeignIDs(size,
                "top hot song catalog with fid", "7digital");
        verifyCatalog(catalog, size);
        checkIdMatch(catalog);
    }

    @Test
    public void simpleBucketTest() throws EchoNestException {
        String name = "small top hot song buckets";
        deleteCatalogByName(name);
        SongCatalog catalog = createHotCatalog(10, name);
        String[] buckets = { "song_hotttnesss" };
        for (SongCatalogItem item : catalog.read(buckets)) {
            Double hotttnesss = item.getDouble("song_hotttnesss");
            System.out.printf("%s hotttnesss %f\n", item.getSongName(),
                    hotttnesss);
        }

    }
    
    
    @Test
    public void customerTest1() throws EchoNestException {
        SongCatalog catalog = en.createSongCatalog("ctest1");
        CatalogUpdater updater = new CatalogUpdater();

        SongCatalogItem item = new SongCatalogItem("1");
        item.setArtistName("Arctic Monkeys");
        item.setSongName("You Probably Couldn't See for the Lights but You Were Staring Straight at Me");
        updater.update(item);
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        verifyCatalog(catalog, 1);
    }

    private void verifyCatalog(SongCatalog catalog, int size)
            throws EchoNestException {
        List<SongCatalogItem> items = catalog.read();
        assertTrue("correct size", items.size() == size);
    }

    private SongCatalog createHotCatalog(int size, String title)
            throws EchoNestException {
        deleteCatalogByName(title);
        SongCatalog catalog = en.createSongCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        List<Song> top = getTopHotSongs(size);

        for (Song song : top) {
            SongCatalogItem item = new SongCatalogItem(song.getID());
            item.setArtistName(song.getArtistName());
            item.setSongName(song.getTitle());
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        return catalog;
    }

    private List<Song> getTopHotSongs(int size) throws EchoNestException {
        SongParams p = new SongParams();
        p.setResults(size);
        p.addIDSpace("7digital");
        p.setLimit(true);
        p.includeTracks();
        p.sortBy(SongParams.SORT_SONG_HOTTTNESSS, false);
        return en.searchSongs(p);
    }

    private SongCatalog createHotCatalogByIDs(int size, String title)
            throws EchoNestException {
        deleteCatalogByName(title);
        SongCatalog catalog = en.createSongCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        List<Song> top = getTopHotSongs(size);

        assertTrue("proper size of top hot songs", top.size() == size);
        for (Song song : top) {
            SongCatalogItem item = new SongCatalogItem(song.getID());
            item.setSongID(song.getID());
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        return catalog;
    }

    private SongCatalog createHotCatalogByForeignIDs(int size, String title,
            String idspace) throws EchoNestException {
        deleteCatalogByName(title);
        SongCatalog catalog = en.createSongCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        List<Song> top = getTopHotSongs(size);

        for (Song song : top) {
            Track track = song.getTrack("7digital");
            SongCatalogItem item = new SongCatalogItem(song.getID());
            item.setSongID(track.getForeignID());
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        return catalog;
    }

    @Test
    public void readCatalogTest() throws EchoNestException {
        int size = 50;
        deleteCatalogByName("read test song catalog");
        SongCatalog catalog = createHotCatalog(size, "read test song catalog");
        verifyCatalog(catalog, size);
        List<SongCatalogItem> items = catalog.read();
        int mismatchCount = 0;
        for (SongCatalogItem item : items) {

            if (!item.getSongID().equals(item.getID())) {

                // if they don't match, check to make sure the titles match

                Song s1 = new Song(en, item.getSongID());
                Song s2 = new Song(en, item.getID());

                if (!s1.getTitle().equals(s2.getTitle())) {
                    System.out.println(mismatchCount + " " + "id mismatch "
                            + item.getSongID() + " " + item.getID() + " "
                            + item.getSongName());
                    mismatchCount += 1;
                }
            }
        }
        assertTrue("song id resolution count is zero. mismatch "
                + mismatchCount + " of " + size, mismatchCount == 0);
    }

    private void checkIdMatch(SongCatalog catalog) throws EchoNestException {
        List<SongCatalogItem> items = catalog.read();
        int mismatchCount = 0;
        for (SongCatalogItem item : items) {
            if (!item.getSongID().equals(item.getID())) {
                System.out.println(mismatchCount + " " + "id mismatch "
                        + item.getSongID() + " " + item.getID() + " "
                        + item.getSongName());

                mismatchCount += 1;
            }
        }
        assertTrue("song id resolution count is zero", mismatchCount == 0);
    }

    private static SongCatalog lookupCatalogByName(String name)
            throws EchoNestException {
        for (SongCatalog catalog : en.listSongCatalogs()) {
            if (name.equals(catalog.getName())) {
                return catalog;
            }
        }
        return null;
    }

    private void cleanupCatalogs() throws EchoNestException {
            // don't delete catalogs
    }

    @Test
    public void createSomeCatalogs() throws EchoNestException {
        int count = 20;
        int WORK_AROUND = 100;

        cleanupCatalogs();
        assertTrue("catalogs deleted", en.listSongCatalogs().size() == 0);

        for (int i = 0; i < count; i++) {
            int w = i + WORK_AROUND;
            SongCatalog catalog = en.createSongCatalog("test song Catalog" + w);
            System.out.printf(" %d %s %s\n", i, catalog.getID(), catalog
                    .getName());
        }

        assertTrue("catalogs created", en.listSongCatalogs().size() == count);
    }

    @Test
    public void createSmallCatalog() throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();
        String[] artistNames = { "lady gaga", "weezer", "led zeppelin",
                "the beatles", "buckethead", "yes", "emerson lake and palmer",
                "deerhoof" };

        String[] songNames = { "bad romance", "my name is jonas",
                "heartbreaker", "blackbird", "colma", "siberian khartru",
                "tarkus", "friend opportunity" };

        deleteCatalogByName("My Song Test Catalog");
        SongCatalog catalog = en.createSongCatalog("My Song Test Catalog");
        int myID = 0;
        for (int i = 0; i < artistNames.length; i++) {
            SongCatalogItem item = new SongCatalogItem("id-" + (myID++));
            item.setArtistName(artistNames[i]);
            item.setSongName(songNames[i]);
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        boolean done = catalog.waitForUpdates(ticket, 120000);
        assertTrue("catalog update not done in 120 seconds", done);
        verifyCatalog(catalog, artistNames.length);
    }

    Set<String> getSongIDSet(SongCatalog catalog) throws EchoNestException {
        Set<String> set = new HashSet<String>();

        for (SongCatalogItem item : catalog.read()) {
            set.add(item.getSongID());
        }
        return set;
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
        en.showStats();
    }
}
