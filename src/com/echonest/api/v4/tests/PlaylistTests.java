package com.echonest.api.v4.tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.echonest.api.v4.DynamicPlaylistParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.PlaylistParams.PlaylistSort;
import com.echonest.api.v4.PlaylistParams.PlaylistType;
import com.echonest.api.v4.util.Utilities;

@RunWith(JUnit4.class)
public class PlaylistTests extends TestCase {
    private static int SKIP_TIME = 10;

    static EchoNestAPI en;
    static boolean trace = true;
    static Random rng = new Random();
    static String WEEZER_ID = "AR633SY1187B9AC3B9";
    static String ELP_ID = "ARMR7HO1187FB462CB";

    @BeforeClass
    public static void setUpClass() throws EchoNestException {
        en = new EchoNestAPI();
        en.setMinCommandTime(0);
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
        en.showStats();
    }

    @Test
    public void simpleArtistPlaylist() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addArtist("Weezer");
        p.setResults(10);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 10);
        showPlaylist(playlist);
        basicChecks(playlist);
    }

    @Test
    public void simpleArtistCheck() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setResults(10);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 10);
        showPlaylist(playlist);
        basicChecks(playlist);

        for (Song song : playlist.getSongs()) {
            assertTrue("artist ID must be weezer", song.getArtistID().equals(
                    WEEZER_ID));
        }
    }

    @Test
    public void duplicateTitles() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setResults(10);

        Playlist playlist = en.createStaticPlaylist(p);
        basicChecks(playlist);
        checkForDupSongsByTitle(playlist);
    }

    @Test
    public void simpleArtistRadio() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setResults(10);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 10);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
    }

    @Test
    public void multiArtistRadio() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.addArtist("radiohead");
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setResults(20);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
    }

    @Test
    public void qbdPlaylists() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("female^5");
        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
    }

    @Test
    public void qbdFastMetal() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(160);
        p.setMaxTempo(180);
        p.includeAudioSummary();
        p.sortBy(PlaylistSort.TEMPO, true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 160);
            assertTrue("tempo too fast", song.getTempo() <= 180);
            assertTrue("Not in order", song.getTempo() >= last);
            last = song.getTempo();
        }
    }

    @Test
    public void qbdFastesttMetal() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("fast");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(160);
        p.includeAudioSummary();
        p.sortBy(PlaylistSort.TEMPO, true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 160);
            assertTrue("Not in order", song.getTempo() >= last);
            last = song.getTempo();
        }
    }
    
    @Test
    public void qbdMostDanceableMetal() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("fast");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(120);
        p.includeAudioSummary();
        p.sortBy(PlaylistSort.DANCEABILITY, false);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 1;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 120);
            assertTrue("Not in order", song.getDanceability() <= last);
            last = song.getDanceability();
        }
    }
    
    @Test
    public void qbdLeastDanceableMetal() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("fast");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(120);
        p.includeAudioSummary();
        p.sortBy(PlaylistSort.DANCEABILITY, true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 120);
            assertTrue("Not in order", song.getDanceability() >= last);
            last = song.getDanceability();
        }
    }
    
    @Test
    public void qbdMostEnergeticPop() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("pop");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(120);
        p.includeAudioSummary();
        p.sortBy(PlaylistSort.ENERGY, false);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 1;
        for (Song song : playlist.getSongs()) {
            System.out.println("energy " + song.getEnergy());
            assertTrue("tempo too slow", song.getTempo() >= 120);
            assertTrue("Not in order", song.getEnergy() <= last);
            last = song.getEnergy();
        }
    }
    
    @Test
    public void qbdLeastEnergeticPop() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("pop");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(120);
        p.includeAudioSummary();
        p.sortBy(PlaylistSort.ENERGY, true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 120);
            assertTrue("Not in order", song.getEnergy() >= last);
            last = song.getEnergy();
        }
    }

    @Test
    public void qbdFastesttMetalInPaulify() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("fast");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(160);
        p.includeAudioSummary();
        p.includeTracks();
        p.sortBy(PlaylistSort.TEMPO, true);
        p.addIDSpace("paulify");
        p.setLimit(true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 160);
            assertTrue("Not in order", song.getTempo() >= last);
            last = song.getTempo();
            Track track = song.getTrack("paulify");
            assertNotNull("found a track", track);
            String url = track.getAudioUrl();
            System.out.println("Url: " + url);
        }
    }

    @Test
    public void qbdFastestMetalInPaulifyAdaptive() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("fast");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.setMinTempo(160);
        p.includeAudioSummary();
        p.includeTracks();
        p.sortBy(PlaylistSort.TEMPO, true);
        p.addIDSpace("paulify");
       // p.add("algorithm", "adaptive");
        p.setLimit(true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("tempo too slow", song.getTempo() >= 160);
            assertTrue("Not in order", song.getTempo() >= last);
            last = song.getTempo();
            Track track = song.getTrack("paulify");
            assertNotNull("found a track", track);
            String url = track.getAudioUrl();
            System.out.println("Url: " + url);
        }
    }

    @Test
    public void qbdSimpleSortedMetal() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.includeAudioSummary();
        p.includeTracks();
        //p.setAudio(true);
        p.sortBy(PlaylistSort.TEMPO, true);
        p.addIDSpace("7digital");
        // p.add("algorithm", "adaptive");
        p.setLimit(true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;
        for (Song song : playlist.getSongs()) {
            assertTrue("Not in order", song.getTempo() >= last);
            last = song.getTempo();
            Track track = song.getTrack("7digital");
            assertNotNull("found audio", track.getPreviewUrl());
            assertNotNull("found cover art", song.getCoverArt());

            assertNotNull("found a track", track);
            String url = track.getAudioUrl();
            System.out.println("Url: " + url);
        }
    }

    @Test
    public void majorModeMetal() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.includeAudioSummary();
        p.setMode(0);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        for (Song song : playlist.getSongs()) {
            assertTrue("mode check", song.getMode() == 0);
        }
    }

    @Test
    public void keyMetalTest() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.includeAudioSummary();
        p.setKey(0);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        for (Song song : playlist.getSongs()) {
            assertTrue("key check", song.getKey() == 0);
        }
    }

    @Test
    public void metalPlaylistSortedByArtistHotttnesss()
            throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.includeArtistHotttnesss();
        p.sortBy(PlaylistSort.ARTIST_HOTTTNESSS, true);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;

        for (Song song : playlist.getSongs()) {
            assertTrue("artist hotttnesss check",
                    song.getArtistHotttnesss() >= last);
            last = song.getArtistHotttnesss();
        }
    }

    @Test
    public void wellConstrainedPlaylist() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("heavy metal");
        p.addDescription("violin");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);
        p.includeAudioSummary();
        p.setMinLoudness(-20);
        p.setMaxLoudness(-10);
        p.setMaxTempo(100);
        p.setMinTempo(90);
        p.setArtistMaxHotttnesss(.5f);
        p.setMaxDuration(300f);
        p.includeArtistHotttnesss();

        p.sortBy(PlaylistSort.ARTIST_HOTTTNESSS, true);

        Playlist playlist = en.createStaticPlaylist(p);
        // assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        double last = 0;

        for (Song song : playlist.getSongs()) {
            System.out.println("artist hotttnesss " + song.getArtistHotttnesss());
            assertTrue("artist hotttnesss check",
                    song.getArtistHotttnesss() >= last);
            last = song.getArtistHotttnesss();
        }
    }

    @Test
    public void qbdPlaylistsForProgRock() throws EchoNestException {
        boolean found = false;
        PlaylistParams p = new PlaylistParams();
        p.addDescription("progressive rock");
        p.addDescription("organ");
        p.addDescription("keith emerson");
        p.addDescription("moog");
        p.addDescription("isle of wight");

        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(20);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 20);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
        for (Song song : playlist.getSongs()) {
            if (song.getArtistID().equals(ELP_ID)) {
                found = true;
            }
        }
        if (!found) {
            fail("no ELP found");
        }
    }

    @Test
    public void longPlaylist() throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.addDescription("progressive rock");
        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setVariety(1);
        p.setResults(30);
        p.sortBy(PlaylistSort.ARTIST_FAMILIARITY, false);
        p.includeArtistFamiliarity();
        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 30);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
        double last = 1.0;
        for (Song song : playlist.getSongs()) {
            assertTrue("familiarity is in order",
                    song.getArtistFamiliarity() <= last);
            last = song.getArtistFamiliarity();
        }
    }

    @Test
    public void varyVariety() throws EchoNestException {

        int artists_full = countArtists(getPlaylistWithVariety(1));
        int artists_5 = countArtists(getPlaylistWithVariety(.5f));
        int artists_1 = countArtists(getPlaylistWithVariety(.1f));
        int artists_0 = countArtists(getPlaylistWithVariety(.0f));

        System.out.printf("f:%d .5:%d .1:%d 0:%d\n", artists_full, artists_5,
                artists_1, artists_0);

        assertTrue("full variety", artists_full >= artists_5);
        assertTrue(".5 variety", artists_5 >= artists_1);
        assertTrue(".1 variety", artists_1 >= artists_0);
        assertTrue("0 variety", artists_0 > 1);
    }

    @Test
    public void artistPickTestTop25Test() throws EchoNestException {
        // Collect top 25 songs by weezer
        SongParams sp = new SongParams();
        sp.setResults(25);
        sp.setArtistID(WEEZER_ID);
        sp.sortBy(SongParams.SORT_SONG_HOTTTNESSS, false);
        List<Song> songs = en.searchSongs(sp);

        Set<String> ids = new HashSet<String>();
        for (Song song : songs) {
            ids.add(song.getID());
            ids.add(song.getTitle());
        }

        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.ARTIST);
        p.setVariety(0);
        p.setResults(25);
        p.setArtistPick("song_hotttnesss", false, 100);

        Playlist playlist = en.createStaticPlaylist(p);
        basicChecks(playlist);

        for (Song song : playlist.getSongs()) {
            if (song.getArtistID().equals(WEEZER_ID)) {
                assertTrue("song in top 25 hotttest songs", ids.contains(song
                        .getID()) || ids.contains(song.getTitle()));
            }
        }
    }

    @Test
    public void artistPickTestBottom25Test() throws EchoNestException {
        // Collect bottom 25 songs by weezer
        SongParams sp = new SongParams();
        sp.setResults(25);
        sp.setArtistID(WEEZER_ID);
        sp.sortBy(SongParams.SORT_SONG_HOTTTNESSS, true);
        List<Song> songs = en.searchSongs(sp);

        Set<String> ids = new HashSet<String>();
        for (Song song : songs) {
            ids.add(song.getID());
        }

        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setVariety(0);
        p.setResults(25);
        p.setArtistPick("song_hotttnesss", true, 25);

        Playlist playlist = en.createStaticPlaylist(p);
        basicChecks(playlist);

        for (Song song : playlist.getSongs()) {
            if (song.getArtistID().equals(WEEZER_ID)) {
                assertTrue("song in bottom 25 hotttest songs", ids
                        .contains(song.getID()));
            }
        }
    }

    private Playlist getPlaylistWithVariety(float variety)
            throws EchoNestException {
        int size = 25;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setVariety(variety);
        p.setResults(size);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        basicChecks(playlist);
        return playlist;
    }

    private int countArtists(Playlist playlist) {
        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
        return artistIDs.size();
    }
    @Test
    public void catalogPlaylistWithSongCatalog() throws EchoNestException {
        String catalog = "CAKSMUX1321A708AA4";
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.CATALOG);
        p.setResults(size);
        p.addSeedCatalog(catalog);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
        System.out.println("Unique artists " + artistIDs.size());
    }

    @Test
    public void catalogRadioPlaylistWithSongCatalog() throws EchoNestException {
        String catalog = "CAKSMUX1321A708AA4";
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.CATALOG_RADIO);
        p.setResults(size);
        p.addSeedCatalog(catalog);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
    }
    
    @Test
    public void catalogPlaylistWithArtistCatalog() throws EchoNestException {
        String catalog = "CAABOUD13216257FC7";
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.CATALOG);
        p.setResults(size);
        p.addSeedCatalog(catalog);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
        System.out.println("Unique artists " + artistIDs.size());
    }
    
    @Test
    public void catalogPlaylistWithArtistCatalogAndHighAdventurousness() throws EchoNestException {
        String catalog = "CAABOUD13216257FC7";
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.CATALOG);
        p.setResults(size);
        p.setAdventurousness(1);
        p.addSeedCatalog(catalog);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
        System.out.println("Unique artists " + artistIDs.size());
    }

    @Test
    public void catalogRadioPlaylistWithArtistCatalog() throws EchoNestException {
        String catalog = "CAABOUD13216257FC7";
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.CATALOG_RADIO);
        p.setResults(size);
        p.addSeedCatalog(catalog);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
    }
    
    @Test
    public void minVarietyPlaylist() throws EchoNestException {
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setVariety(0);
        p.setResults(size);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
        System.out.println("Unique artists " + artistIDs.size());
        assertTrue("all artists should be different", artistIDs.size() == 2);
    }
    
    @Test
    public void multiSeedArtistPlaylist() throws EchoNestException {
        int size = 10;
        PlaylistParams p = new PlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.addArtistID(ELP_ID);
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setResults(size);

        Playlist playlist = en.createStaticPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == size);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);

        Set<String> artistIDs = new HashSet<String>();
        for (Song song : playlist.getSongs()) {
            String id = song.getArtistID();
            artistIDs.add(id);
        }
        System.out.println("Unique artists " + artistIDs.size());
        assertTrue("all artists should be different", artistIDs.size() == 2);
    }

    // ---------------------------------
    // Dynamic Playlist tests are here
    // ---------------------------------

    @Test
    public void badDynamicPlaylistSession() throws EchoNestException {
        try {
            en.getNextInDynamicPlaylist("BADSESSIONID", 0);
            fail("bad session accepted");
        } catch (EchoNestException e) {
            assertTrue("proper error code", e.getCode() == 5);
        }
    }

    @Test
    public void simpleDynamicPlaylistSession() throws EchoNestException {
        PlaylistParams p = new DynamicPlaylistParams();
        p.addArtist("weezer");
        p.setVariety(.3f);
        p.setType(PlaylistType.ARTIST_RADIO);
        Playlist playlist = en.createDynamicPlaylist(p);
        basicArtistRadioChecks(playlist);
        showPlaylist(playlist);

        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < 10; i++) {
            playlist = en.getNextInDynamicPlaylist(playlist.getSession());
            basicArtistRadioChecks(playlist);
            showPlaylist(playlist);

            songs.addAll(playlist.getSongs());
        }

        Playlist fullPlaylist = new Playlist(songs);
        basicArtistRadioChecks(fullPlaylist);
        showPlaylist(fullPlaylist);
    }

    @Test
    public void veryLongDynamicPlaylistSession() throws EchoNestException {
        int LENGTH = 200;
        PlaylistParams p = new DynamicPlaylistParams();
        p.addArtist("weezer");
        p.setVariety(.8f);
        p.setType(PlaylistType.ARTIST_RADIO);
        Playlist playlist = en.createDynamicPlaylist(p);
        basicArtistRadioChecks(playlist);
        showPlaylist(playlist);

        List<Song> songs = new ArrayList<Song>();
        for (int i = 0; i < LENGTH; i++) {
            playlist = en.getNextInDynamicPlaylist(playlist.getSession());
            if (playlist.getSongs().size() == 0) {
                break;
            }
            basicArtistRadioChecks(playlist);
            showPlaylist(playlist);
            songs.addAll(playlist.getSongs());
        }

        Playlist fullPlaylist = new Playlist(songs);
        basicArtistRadioChecks(fullPlaylist);
        showPlaylist(fullPlaylist);
        System.out
                .println("Playlist size is " + fullPlaylist.getSongs().size());
        assertTrue("length is OK", fullPlaylist.getSongs().size() == LENGTH);
    }

    @Test
    public void dmcaDynamicPlaylistSession() throws EchoNestException {
        PlaylistParams p = new DynamicPlaylistParams();
        p.addArtist("weezer");
        p.setVariety(.8f);
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setDMCA(true);
        p.includeAudioSummary();
        dmcaDynamicPlaylistCheck(p, 30);
    }
    
    @Test
    public void dmcaDynamicPlaylistSession2() throws EchoNestException {
        PlaylistParams p = new DynamicPlaylistParams();
        p.addDescription("pop");
        p.setArtistPick("song_hotttnesss", false, 25);
        p.sortBy(PlaylistSort.SONG_HOTTTNESSS, false);
        p.setVariety(.8f);
        p.setType(PlaylistType.ARTIST_DESCRIPTION);
        p.setDMCA(true);
        p.includeAudioSummary();
        p.includeSongHotttnesss();
        p.setSongMinHotttnesss(.5f);
        Playlist playlist = dmcaDynamicPlaylistCheck(p, 100);
        
        double lastHotttnesss = 1.0;
        for (Song song : playlist.getSongs()) {
            assertTrue("song hotttnesss in range", song.getSongHotttnesss() >= .5f);
            assertTrue("hottttnesss sort", song.getSongHotttnesss() <= lastHotttnesss);
            lastHotttnesss = song.getSongHotttnesss();
        }
    }
    
    @Test
    public void dmcaDynamicPlaylistSession3() throws EchoNestException {
        PlaylistParams p = new DynamicPlaylistParams();
        p.setVariety(.8f);
        p.addArtist("Lady Gaga");
        p.addArtist("Katy Perry");
        p.addArtist("Beyoncé");
        p.setType(PlaylistType.ARTIST_RADIO);
        p.setDMCA(true);
        p.includeAudioSummary();
        dmcaDynamicPlaylistCheck(p, 100);
    }
    
    public Playlist dmcaDynamicPlaylistCheck(PlaylistParams p, int length) throws EchoNestException {
        Playlist playlist = en.createDynamicPlaylist(p);
        List<Song> songs = new ArrayList<Song>();
        List<Double> startOffsets = new ArrayList<Double>();
        double startTime = 0;
        int userOffset = -1;
        while (songs.size() < length) {
            assertTrue("just a single song", playlist.getSongs().size() == 1);

            if (userOffset < 0 || userOffset > SKIP_TIME) {
                startOffsets.add(startTime);
                songs.add(playlist.getSongs().get(0));
            }

            double lastLength = playlist.getSongs().get(0).getDuration();

            userOffset = simulate_user_behavior();
            playlist = en.getNextInDynamicPlaylist(playlist.getSession(), 0,
                    userOffset);

            if (userOffset == -1) {
                startTime += lastLength;
            } else {
                startTime += userOffset;
            }

            if (playlist.getSongs().size() == 0) {
                break;
            }
        }

        Playlist fullPlaylist = new Playlist(songs);
        showPlaylist(fullPlaylist);
        basicArtistRadioChecks(fullPlaylist);
        dmcaCheck(fullPlaylist, startOffsets);
        assertTrue("playlist length", length == fullPlaylist.getSongs().size());
        return fullPlaylist;
    }

    // Checks to make sure that the time-stamped playlist conforms to DMCA rules

    private void dmcaCheck(Playlist playlist, List<Double> startTimes) {
        assertTrue("mismatched lengths",
                playlist.getSongs().size() == startTimes.size());
        for (int i = 0; i < startTimes.size(); i++) {
            Song song = playlist.getSongs().get(i);
            System.out.printf("%.2f %s // %s\n", startTimes.get(i), song
                    .getTitle(), song.getArtistName());
        }

        List<Song> songs = playlist.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            double startTime = startTimes.get(i);

            // get last 3 hours of songs
            List<Song> history = new ArrayList<Song>();
            for (int j = 0; j < i; j++) {
                if (startTimes.get(j) >= startTime - 3 * 60 * 60) {
                    history.add(songs.get(j));
                }
            }

            
            System.out.printf("Checking %d, history has %d songs\n", i, history.size());
            Song song = songs.get(i);
            int artistCount = 1;
            int releaseCount = 1;
            int consecutiveArtistCount = 1;
            int consecutiveReleaseCount = 1;
            for (Song prevSong : history) {
                if (song.getArtistID().equals(prevSong.getArtistID())) {
                    artistCount++;
                    if (artistCount > 4) {
                        System.out.println("Too may of artist " + song.getArtistName() +  " at index " + i);
                    }
                }

                if (song.getReleaseName().equals(prevSong.getReleaseName())) {
                    releaseCount++;
         
                }
            }

            for (Song prevSong : history) {
                if (song.getArtistID().equals(prevSong.getArtistID())) {
                    consecutiveArtistCount++;
                } else {
                    break;
                }
            }
            for (Song prevSong : history) {
                if (song.getReleaseName().equals(prevSong.getReleaseName())) {
                    consecutiveReleaseCount++;
                } else {
                    break;
                }
            }
            assertTrue("No more than three songs from the same recording",
                    releaseCount <= 3);
            assertTrue(
                    "No more than two songs in a row, from the same recording",
                    consecutiveReleaseCount <= 2);
            assertTrue(
                    "No more than four songs from the same artist or anthology",
                    artistCount <= 4);
            assertTrue(
                    "No more than three songs in a row from the same artist or anthology",
                    consecutiveArtistCount <= 3);
        }
    }

    private int simulate_user_behavior() {
        boolean trace = true;
        int choice = -1;

        double PERCENT_PAUSE = .03;
        double PERCENT_SKIP = PERCENT_PAUSE + .10;
        double PERCENT_ADVANCE = PERCENT_SKIP + .05;
        double PERCENT_FULL = 1.0;

        double chance = rng.nextFloat();
        if (chance <= PERCENT_PAUSE) {
            choice = rng.nextInt(300) + 200;
            if (trace) {
                System.out.printf("Pausing for %d seconds\n", choice);
            }
        } else if (chance <= PERCENT_SKIP) {
            choice = rng.nextInt(SKIP_TIME);
            if (trace) {
                System.out.printf("Skipping after %d seconds\n", choice);
            }
        } else if (chance <= PERCENT_ADVANCE) {
            choice = rng.nextInt(60) + 2 * SKIP_TIME;
            if (trace) {
                System.out.printf("Advancing after %d seconds\n", choice);
            }
        } else {
            choice = -1;
            if (trace) {
                System.out.printf("Playing the full song\n");
            }
        }
        return choice;
    }

    private void basicArtistRadioChecks(Playlist playlist) {
        basicChecks(playlist);
       // checkForConsecutiveArtists(playlist);
    }

    private void basicChecks(Playlist playlist) {
        checkForDupSongs(playlist);
    }

    private void checkForDupSongs(Playlist playlist) {
        Set<String> songIdSet = new HashSet<String>();

        for (Song song : playlist.getSongs()) {
            if (songIdSet.contains(song.getID())) {
                fail("duplicate song " + song);
            }
            songIdSet.add(song.getID());
        }
    }

    private void checkForConsecutiveArtists(Playlist playlist) {
        String last = "";
        for (Song song : playlist.getSongs()) {
            assertTrue("no consecutive artists", !song.getArtistID().equals(
                    last));
            last = song.getArtistID();
        }
    }

    private void checkForDupSongsByTitle(Playlist playlist) {
        Set<String> songTitleSet = new HashSet<String>();

        for (Song song : playlist.getSongs()) {
            if (songTitleSet.contains(song.getTitle())) {
                fail("duplicate song (by title)" + song);
            }
            songTitleSet.add(song.getTitle());
        }
    }

    void showPlaylist(Playlist playlist) {
        if (playlist.getSession() != null) {
            System.out.println("Session: " + playlist.getSession());
        }
        for (Song song : playlist.getSongs()) {
            showSong(song);
        }
    }

    void showSong(Song song) {
        System.out.println("   " + song.toString());
    }

}
