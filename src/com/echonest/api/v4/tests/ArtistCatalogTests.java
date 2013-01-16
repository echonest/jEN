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

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistCatalog;
import com.echonest.api.v4.ArtistCatalogItem;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.CatalogUpdater;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.SongCatalog;

@RunWith(JUnit4.class)
public class ArtistCatalogTests extends TestCase {

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
    public void listCatalogs() throws EchoNestException {
        for (ArtistCatalog catalog : en.listArtistCatalogs()) {
            System.out
                    .println("  " + catalog.getID() + " " + catalog.getName() + " " + catalog.getType());
        }
        assertTrue("list catalogs", true);
    }

    @Test
    public void listEmptyCatalogs() throws EchoNestException {
        cleanupCatalogs();
        for (ArtistCatalog catalog : en.listArtistCatalogs()) {
            System.out
                    .println("  " + catalog.getID() + " " + catalog.getName() + " " + catalog.getType());
        }
        assertTrue("no catalogs", en.listArtistCatalogs().size() == 0);
    }

    @Test
    public void createCatalogsWithDuplicateNames() throws EchoNestException {
        try {
            ArtistCatalog catalog = en
                    .createArtistCatalog("my duplicate catalog");
            ArtistCatalog dup = en.createArtistCatalog("my duplicate catalog");
            fail("duplicate catalogs");
        } catch (EchoNestException e) {
            System.out.println("dup " + e);
        } finally {
            cleanupCatalogs();
        }
    }

    @Test
    public void catalogRosettaTest() throws EchoNestException {
        deleteCatalogByName("rosetta test catalog");
        ArtistCatalog catalog = createHotCatalog(50, "rosetta test catalog");

        for (ArtistCatalogItem item : catalog.read()) {
            String urn = catalog.getID() + ":artist:" + item.getID();
            Artist artist = en.newArtistByID(urn);
            assertTrue("artist ids match", artist.getID().equals(
                    item.getArtistID()));
        }

    }

    void deleteCatalogByName(String name) throws EchoNestException {
        ArtistCatalog catalog = lookupCatalogByName(name);
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
        System.out.println("create large top hot catalog");
        deleteCatalogByName("top hot catalog");
        ArtistCatalog catalog = createHotCatalog(size, "top hot catalog8");
        verifyCatalog(catalog, size);
    }

    @Test
    public void createLargeTopHotCatalogByIDs() throws EchoNestException {
        int size = 75;
        System.out.println("create large top hot catalog");
        deleteCatalogByName("top hot catalog by ID");
        ArtistCatalog catalog = createHotCatalogByIDs(size,
                "top hot catalog by ID");
        verifyCatalog(catalog, size);
        checkIdMatch(catalog);
    }
    
    @Test
    public void createLargeTopHotCatalogByForeignIDs() throws EchoNestException {
        int size = 500;
        System.out.println("create large top hot catalog");
        deleteCatalogByName("top hot catalog by ID");
        ArtistCatalog catalog = createHotCatalogByForeignIDs(size,
                "top hot catalog by ID", "7digital");
        verifyCatalog(catalog, size);
        checkIdMatch(catalog);
    }

    @Test
    public void simpleBucketTest() throws EchoNestException {
        String name = "small top hot";
        deleteCatalogByName(name);
        ArtistCatalog catalog = createHotCatalog(10, name);
        String[] buckets = { "blogs", "biographies" };
        for (ArtistCatalogItem item : catalog.read(buckets)) {
            List blogs = (List) item.getList("blogs");
            List bios = (List) item.getList("biographies");
            System.out.printf("%s blogs %d bios %d\n", item.getArtistName(), blogs.size(), bios.size());
        }

    }

    private void verifyCatalog(ArtistCatalog catalog, int size)
            throws EchoNestException {
        List<ArtistCatalogItem> items = catalog.read();
        assertTrue("correct size", items.size() == size);
    }

    private ArtistCatalog createHotCatalog(int size, String title)
            throws EchoNestException {
        deleteCatalogByName(title);
        ArtistCatalog catalog = en.createArtistCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        List<Artist> top = en.topHotArtists(size);

        for (Artist artist : top) {
            ArtistCatalogItem item = new ArtistCatalogItem("my-" + artist.getID());
            item.setArtistName(artist.getName());
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        return catalog;
    }

    private ArtistCatalog createHotCatalogByIDs(int size, String title)
            throws EchoNestException {
        deleteCatalogByName(title);
        ArtistCatalog catalog = en.createArtistCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        List<Artist> top = en.topHotArtists(size);

        for (Artist artist : top) {
            ArtistCatalogItem item = new ArtistCatalogItem(artist.getID());
            item.setArtistID(artist.getID());
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        return catalog;
    }

    private ArtistCatalog createHotCatalogByForeignIDs(int size, String title, String idspace)
            throws EchoNestException {
        deleteCatalogByName(title);
        ArtistCatalog catalog = en.createArtistCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        ArtistParams p = new ArtistParams();
        p.setResults(size);
        p.addIDSpace(idspace);
        p.setLimit(true);
        List<Artist> top = en.topHotArtists(p);

        for (Artist artist : top) {
            ArtistCatalogItem item = new ArtistCatalogItem(artist.getID());
            item.setArtistID(artist.getForeignID(idspace));
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        if (!catalog.waitForUpdates(ticket, 300000L)) {
            fail("update took too long");
        }
        return catalog;
    }

    private ArtistCatalog createHotCatalog(int start, int size, String title)
            throws EchoNestException {
        deleteCatalogByName(title);
        ArtistCatalog catalog = en.createArtistCatalog(title);
        CatalogUpdater updater = new CatalogUpdater();

        List<Artist> top = en.topHotArtists(start, size);

        for (Artist artist : top) {
            ArtistCatalogItem item = new ArtistCatalogItem(artist.getID());
            item.setArtistName(artist.getName());
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
        ArtistCatalog catalog = createHotCatalog(size, "read test catalog");
        verifyCatalog(catalog, size);
        List<ArtistCatalogItem> items = catalog.read();
        int mismatchCount = 0;
        for (ArtistCatalogItem item : items) {
            if (!item.getArtistID().equals(item.getID())) {
                Artist seed = en.newArtistByID(item.getArtistID());

                Artist matched = en.newArtistByID(item.getID());

                System.out.println(mismatchCount + " " + "id mismatch "
                        + item.getArtistID() + " " + item.getID() + " "
                        + item.getArtistName() + " " + matched.getName() + " "
                        + seed.getName());

                mismatchCount += 1;
            }
        }
        assertTrue("artist id resolution count is zero", mismatchCount == 0);
    }

    private void checkIdMatch(ArtistCatalog catalog) throws EchoNestException {
        List<ArtistCatalogItem> items = catalog.read();
        int mismatchCount = 0;
        for (ArtistCatalogItem item : items) {
            if (!item.getArtistID().equals(item.getID())) {
                System.out.println(mismatchCount + " " + "id mismatch "
                        + item.getArtistID() + " " + item.getID() + " "
                        + item.getArtistName());

                mismatchCount += 1;
            }
        }
        assertTrue("artist id resolution count is zero", mismatchCount == 0);
    }

    private static ArtistCatalog lookupCatalogByName(String name)
            throws EchoNestException {
        for (ArtistCatalog catalog : en.listArtistCatalogs()) {
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
        assertTrue("catalogs deleted", en.listArtistCatalogs().size() == 0);

        for (int i = 0; i < count; i++) {
            int w = i + WORK_AROUND;
            ArtistCatalog catalog = en.createArtistCatalog("test artist catalog" + w);
            System.out.printf(" %d %s %s\n", i, catalog.getID(), catalog
                    .getName());
        }

        assertTrue("catalogs created", en.listArtistCatalogs().size() == count);
    }

    @Test
    public void createSmallCatalog() throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();
        String[] names = { "lady gaga", "weezer", "led zeppelin",
                "the beatles", "buckethead", "yes", "emerson lake and palmer",
                "deerhoof" };

        deleteCatalogByName("My Small Test Catalog");
        ArtistCatalog catalog = en.createArtistCatalog("My Small Test Catalog");
        int myID = 0;
        for (String name : names) {
            ArtistCatalogItem item = new ArtistCatalogItem("id-" + (myID++));
            item.setArtistName(name);
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        boolean done = catalog.waitForUpdates(ticket, 300000);
        assertTrue("catalog update not done in 120 seconds", done);
        verifyCatalog(catalog, names.length);
    }
    
    @Test
    public void checkUpdateInfo() throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();
        String[] names = { "crappity crap name", "weezer", "led zeppelin",
                "the beatles", "baddity bady name", "yes", "emerson lake and palmer",
                "junkity junk name" };

        deleteCatalogByName("My Small Test Catalog");
        ArtistCatalog catalog = en.createArtistCatalog("My Small Test Catalog");
        int myID = 0;
        for (String name : names) {
            ArtistCatalogItem item = new ArtistCatalogItem("id-" + (myID++));
            item.setArtistName(name);
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        boolean done = catalog.waitForUpdates(ticket, 120000);
        assertTrue("catalog update not done in 120 seconds", done);
        verifyCatalog(catalog, names.length);
    }

    @Test
    public void createSmallCatalogWithHardNames() throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();
        String[] names = { "Sigur Ros", "!!!", "and and and", "bjork",
                "Motley Crue", "emerson lake & palmer" };

        deleteCatalogByName("test catalog with hard names");
        ArtistCatalog catalog = en
                .createArtistCatalog("test artist catalog with hard names");
        int myID = 0;
        for (String name : names) {
            ArtistCatalogItem item = new ArtistCatalogItem("id-" + (myID++));
            item.setArtistName(name);
            updater.update(item);
        }
        String ticket = catalog.update(updater);
        boolean done = catalog.waitForUpdates(ticket, 20000);
        assertTrue("catalog update not done in 20 seconds", done);
        verifyCatalog(catalog, names.length);
    }

    Set<String> getArtistIDSet(ArtistCatalog catalog) throws EchoNestException {
        Set<String> set = new HashSet<String>();

        for (ArtistCatalogItem item : catalog.read()) {
            set.add(item.getArtistID());
        }
        return set;
    }

    @Test
    public void testSimilarityWithSmallCatalog() throws EchoNestException {
        int SIZE = 100;
        ArtistCatalog catalog = createHotCatalog(SIZE, "simtest catalog");
        verifyCatalog(catalog, SIZE);

        ArtistParams p = new ArtistParams();
        p.addIDSpace(catalog.getID());
        p.setLimit(true);
        p.addName("Lady Gaga");
        List<Artist> sims = en.getSimilarArtists(p);
        Set<String> artistIDs = getArtistIDSet(catalog);
        for (Artist sim : sims) {
            assertTrue("sim in catalog " + sim.getName() + " " + sim.getID(), artistIDs.contains(sim.getID()));
        }
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
        en.showStats();
    }
}
