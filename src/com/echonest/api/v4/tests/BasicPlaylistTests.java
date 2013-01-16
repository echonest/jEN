/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Before;

/**
 *
 * @author plamere
 */
public class BasicPlaylistTests extends TestCase {
        private static int SKIP_TIME = 10;

    static EchoNestAPI en;
    static boolean trace = true;
    static Random rng = new Random();
    static String WEEZER_ID = "AR633SY1187B9AC3B9";
    static String SCORCHO_ID = "SOCRHFJ12A67021D74";

    static String ELP_ID = "ARMR7HO1187FB462CB";

    @Before
    @Override
    public  void setUp() throws EchoNestException {
        en = new EchoNestAPI();
        en.setMinCommandTime(0);
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);
    }
    
    
    @org.junit.Test
    public void testSimpleBasicArtistPlaylist() throws EchoNestException {
        BasicPlaylistParams p = new BasicPlaylistParams();
        p.addArtist("Weezer");
        p.setResults(10);

        Playlist playlist = en.createBasicPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 10);
        showPlaylist(playlist);
        basicChecks(playlist);
    }
    

    @org.junit.Test
    public void testDuplicateTitles() throws EchoNestException {
        BasicPlaylistParams p = new BasicPlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setResults(10);

        Playlist playlist = en.createBasicPlaylist(p);
        basicChecks(playlist);
        checkForDupSongsByTitle(playlist);
    }

    @org.junit.Test
    public void testSimpleArtistRadio() throws EchoNestException {
        BasicPlaylistParams p = new BasicPlaylistParams();
        p.addArtistID(WEEZER_ID);
        p.setType(BasicPlaylistParams.PlaylistType.ARTIST_RADIO);
        p.setResults(10);

        Playlist playlist = en.createBasicPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 10);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
    }
    
    @org.junit.Test
    public void testSimpleSongRadio() throws EchoNestException {
        BasicPlaylistParams p = new BasicPlaylistParams();
        p.addSongID(SCORCHO_ID);
        p.setType(BasicPlaylistParams.PlaylistType.SONG_RADIO);
        p.setResults(10);

        Playlist playlist = en.createBasicPlaylist(p);
        assertTrue("playlist length", playlist.getSongs().size() == 10);
        showPlaylist(playlist);
        basicArtistRadioChecks(playlist);
    }
    
         void basicArtistRadioChecks(Playlist playlist) {
        basicChecks(playlist);
       // checkForConsecutiveArtists(playlist);
    }

     void basicChecks(Playlist playlist) {
        checkForDupSongs(playlist);
    }

     void checkForDupSongs(Playlist playlist) {
        Set<String> songIdSet = new HashSet<String>();

        for (Song song : playlist.getSongs()) {
            if (songIdSet.contains(song.getID())) {
                fail("duplicate song " + song);
            }
            songIdSet.add(song.getID());
        }
    }

     void checkForConsecutiveArtists(Playlist playlist) {
        String last = "";
        for (Song song : playlist.getSongs()) {
            assertTrue("no consecutive artists", !song.getArtistID().equals(
                    last));
            last = song.getArtistID();
        }
    }

     void checkForDupSongsByTitle(Playlist playlist) {
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
