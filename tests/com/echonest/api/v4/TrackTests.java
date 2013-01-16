package com.echonest.api.v4;

import com.echonest.api.v4.util.Utilities;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class TrackTests {
    static EchoNestAPI en;
    static boolean trace = false;
    static Song hey_jude = null;
    static Random rng = new Random();
    static String DIZZY_SONG_ID = "SOZZJMR1280EC73B23";

    @BeforeClass
    public static void setUpClass() throws EchoNestException {
        en = new EchoNestAPI();
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
        en.showStats();
    }

    @Test
    public void trackFromID() throws EchoNestException {
        String id = "TRWXVPA1296187FC15";
        Track track = en.newTrackByID(id);
        assertTrue("track status",
                track.getStatus() == Track.AnalysisStatus.COMPLETE);
        assertTrue("track id", track.getID().equals(id));
        verifyTrack(track);
    }

    @Test
    public void trackFromMD5() throws EchoNestException {
        String md5 = "07a096fd8880931695723d19b1a11611";
        String id = "TRWXVPA1296187FC15";

        Track track = en.newTrackByMD5(md5);
        assertTrue("track status",
                track.getStatus() == Track.AnalysisStatus.COMPLETE);
        assertTrue("track id", track.getID().equals(id));
        verifyTrack(track);
    }

    private void verifyTrack(Track track) throws EchoNestException {
        track.showAll();
        assertTrue("has analysis url", track.getAnalysisURL() != null);
        verifyAnalysis(track.getAnalysis(), false);
    }

    private void verifyAnalysis(TrackAnalysis analysis, boolean full) {
        System.out.println("num samples : " + analysis.getNumSamples());
        System.out.println("sample md5  : " + analysis.getMD5());
        System.out.println("num channels: " + analysis.getNumChannels());
        System.out.println("duration    : " + analysis.getDuration());

        if (full) {

            System.out.println(" Sections ");
            List<TimedEvent> sections = analysis.getSections();
            for (TimedEvent e : sections) {
                System.out.println(e);
            }

            System.out.println(" Bars ");
            List<TimedEvent> bars = analysis.getBars();
            for (TimedEvent e : bars) {
                System.out.println(e);
            }

            System.out.println(" Beats ");
            List<TimedEvent> beats = analysis.getBeats();
            for (TimedEvent e : beats) {
                System.out.println(e);
            }

            System.out.println(" Tatums ");
            List<TimedEvent> tatums = analysis.getTatums();
            for (TimedEvent e : tatums) {
                System.out.println(e);
            }

            System.out.println(" Segments ");
            List<Segment> segments = analysis.getSegments();
            for (Segment e : segments) {
                System.out.println(e);
            }
        }
    }

    @Test
    public void trackAnalysisFromSongTrack() throws EchoNestException {
        SongParams p = new SongParams();
        p.sortBy(SongParams.SORT_ARTIST_HOTTTNESSS, false);
        p.addIDSpace("paulify");
        p.setLimit(true);
        p.includeTracks();
        p.setResults(1);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            Track track = song.getTrack("paulify");
            verifyTrack(track);

            // create a new track from the ID and see if we can get the analysis
            String tid = track.getID();
            Track newTrack = en.newTrackByID(tid);
            verifyTrack(newTrack);
        }
    }

    @Test
    public void trackUploadFromURLTest() throws EchoNestException, IOException {
        List<Artist> artists = en.topHotArtists(100);
        Collections.shuffle(artists);
        Artist artist = artists.get(0);
        List<Audio> audio = artist.getAudio();
        Collections.shuffle(audio);
        if (audio.size() > 0) {
            Audio a = audio.get(0);
            URL url = new URL(a.getURL());
            Track track = en.uploadTrack(url, true);
            track.waitForAnalysis(30000);
            verifyTrack(track);
        }
    }

    @Test
    public void trackUploadFromFileTest() throws EchoNestException, IOException {
        File audioFile = Utilities.createNewAudioFile("au");

        assertNotNull("can't create audio file for upload", audioFile);
        Track track = en.uploadTrack(audioFile);
        track.waitForAnalysis(30000);
        verifyTrack(track);
        
        testLizziness(track);
        TrackAnalysis analysis = track.getAnalysis();
        double tempo = analysis.getTempo();
        int key = analysis.getKey();
        int mode = analysis.getMode();

        assertTrue("track upload tempo", tempo > 130 && tempo < 140);
        assertTrue("track upload key", key == 9);
        assertTrue("track upload mode", mode == 1);
        audioFile.delete();
    }
    
    private void testLizziness(Track track) throws EchoNestException {
        String songID = track.getSongID();
        if (songID != null) {
            assertTrue("song is lizzy", songID.equals(DIZZY_SONG_ID));
        }
    }
    
    @Test
    public void trackUploadWav() throws EchoNestException, IOException {
        File audioFile = Utilities.createNewAudioFile("wav");

        assertNotNull("can't create audio file for upload", audioFile);
        Track track = en.uploadTrack(audioFile);
        track.waitForAnalysis(30000);
        testLizziness(track);

        verifyTrack(track);
        TrackAnalysis analysis = track.getAnalysis();
        double tempo = analysis.getTempo();
        int key = analysis.getKey();
        int mode = analysis.getMode();

        assertTrue("track upload tempo", tempo > 130 && tempo < 140);
        assertTrue("track upload key", key == 9);
        assertTrue("track upload mode", mode == 1);
        audioFile.delete();
    }
    
    @Test
    public void trackUploadOgg() throws EchoNestException, IOException {
        File audioFile = Utilities.createNewAudioFile("ogg");

        assertNotNull("can't create audio file for upload", audioFile);
        Track track = en.uploadTrack(audioFile);
        track.waitForAnalysis(30000);
        testLizziness(track);

        verifyTrack(track);
        TrackAnalysis analysis = track.getAnalysis();
        double tempo = analysis.getTempo();
        int key = analysis.getKey();
        int mode = analysis.getMode();

        assertTrue("track upload tempo", tempo > 130 && tempo < 140);
        assertTrue("track upload key", key == 9);
        assertTrue("track upload mode", mode == 1);
        audioFile.delete();
    }

    @Test
    public void trackUploadNoWaitFileTest() throws EchoNestException,
            IOException {
        File audioFile = Utilities.createNewAudioFile("au");

        assertNotNull("can't create audio file for upload", audioFile);
        Track track = en.uploadTrack(audioFile);
        track.waitForAnalysis(30000);
        verifyTrack(track);
        testLizziness(track);

        TrackAnalysis analysis = track.getAnalysis();
        double tempo = analysis.getTempo();
        int key = analysis.getKey();
        int mode = analysis.getMode();

        assertTrue("track upload tempo", tempo > 130 && tempo < 140);
        assertTrue("track upload key", key == 9);
        assertTrue("track upload mode", mode == 1);
        audioFile.delete();
    }
    
    @Test
    public void trackUploadExistingFile() throws EchoNestException,
            IOException {
        File audioFile = new File("music/dizzy.mp3");
        Track track = en.uploadTrack(audioFile);
        track.waitForAnalysis(30000);
        verifyTrack(track);
        testLizziness(track);

        TrackAnalysis analysis = track.getAnalysis();
        double tempo = analysis.getTempo();
        int key = analysis.getKey();
        int mode = analysis.getMode();

        assertTrue("track upload tempo", tempo > 130 && tempo < 140);
        assertTrue("track upload key", key == 9);
        assertTrue("track upload mode", mode == 1);
    }



    public static void main(String[] args) throws Exception {
        File f = Utilities.createNewAudioFile("au");
        System.out.println("New audio file is " + f);
    }
}
