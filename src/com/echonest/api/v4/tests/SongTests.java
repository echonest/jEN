package com.echonest.api.v4.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.IdentifySongParams;
import com.echonest.api.v4.Segment;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.TimedEvent;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.TrackAnalysis;
import com.echonest.api.v4.YearsActive;


@RunWith(JUnit4.class)
public class SongTests extends TestCase {
    static EchoNestAPI en;
    static boolean trace = true;
    static Song hey_jude = null;
    static double fudge = .01;

    @BeforeClass
    public static void setUpClass() throws EchoNestException {
        en = new EchoNestAPI();
        en.setMinCommandTime(0);
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);

        SongParams p = new SongParams();
        p.setArtist("The Beatles");
        p.setTitle("Hey Jude");
        hey_jude = en.searchSongs(p).get(0);
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
        en.showStats();
    }

    @Test
    public void songSearchTitleTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setTitle("tarkus");
        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            verifySong(song);
        }
        assertTrue(songs.size() > 0);
        assertTrue(songs.get(0).getTitle().equals("Tarkus"));
    }

    @Test
    public void songSearchArtistTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setArtist("The Beatles");
        List<Song> songs = en.searchSongs(p);

        for (Song song : songs) {
            verifySong(song);
        }
        assertTrue(songs.size() > 0);
        assertTrue(songs.get(0).getArtistName().equals("The Beatles"));
    }

    @Test
    public void songCombinedSearchTest() throws EchoNestException {
        SongParams p = new SongParams();
        String beatlesID = "AR6XZ861187FB4CECD";
        p.setCombined("The Beatles Hey Jude");
        List<Song> songs = en.searchSongs(p);

        assertTrue(songs.size() > 0);
        assertTrue(songs.get(0).getArtistID().equals(beatlesID));
        assertTrue(songs.get(0).getTitle().trim().toLowerCase().contains("hey jude"));
        for (Song song : songs) {
            verifySong(song);
        }
    }

    @Test
    public void songSearchTempoTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(100);
        p.setMaxTempo(130);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_TEMPO, true);
        List<Song> songs = en.searchSongs(p);
        double lastTempo = 0;

        for (Song song : songs) {
            double tempo = song.getTempo();
            assertTrue(tempo >= 100);
            assertTrue(tempo <= 130);
            assertTrue(tempo >= lastTempo);
            lastTempo = tempo;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchHighDanceabiityTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinDanceability(.80f);
        p.setMaxDanceability(1);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_DANCEABILITY, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;

        for (Song song : songs) {
            double cur = song.getDanceability();
            assertTrue(cur >= .8 - fudge);
            assertTrue(cur <= 1 + fudge);
            assertTrue(cur + fudge >= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchFullDanceabiityTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinDanceability(0f);
        p.setMaxDanceability(1);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_DANCEABILITY, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;

        for (Song song : songs) {
            double cur = song.getDanceability();
            assertTrue(cur >= 0f);
            assertTrue(cur <= 1);
            assertTrue(cur >= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchLowDanceabiityTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinDanceability(.0f);
        p.setMaxDanceability(.2f);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_DANCEABILITY, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;

        for (Song song : songs) {
            double cur = song.getDanceability();
            assertTrue(cur >= .0);
            assertTrue(cur <= .2f + fudge);
            assertTrue(cur >= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchLowEnergyTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinEnergy(.0f);
        p.setMaxEnergy(.2f);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_ENERGY, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;

        for (Song song : songs) {
            double cur = song.getEnergy();
            assertTrue(cur >= .0);
            assertTrue(cur <= .2f + fudge);
            assertTrue(cur >= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchFullEnergyTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinEnergy(.0f);
        p.setMaxEnergy(1f);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_ENERGY, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;

        for (Song song : songs) {
            double cur = song.getEnergy();
            assertTrue(cur >= .0);
            assertTrue(cur <= 1);
            assertTrue(cur >= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchHighEnergyTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinEnergy(.8f);
        p.setMaxEnergy(1f);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_ENERGY, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;

        for (Song song : songs) {
            double cur = song.getEnergy();
            assertTrue(cur >= .8f - fudge);
            assertTrue(cur <= 1f);
            System.out.printf("%f %f\n", cur, last);
            assertTrue(cur >= last - fudge);
            last = cur;
            verifySong(song);
        }
    }


    @Test
    public void songSearchModeTest() throws EchoNestException {
        {
            SongParams p = new SongParams();
            p.setMode(1);
            p.includeAudioSummary();
            List<Song> songs = en.searchSongs(p);

            for (Song song : songs) {
                assertTrue(song.getMode() == 1);
                verifySong(song);
            }
        }

        {
            SongParams p = new SongParams();
            p.setMode(0);
            p.includeAudioSummary();
            List<Song> songs = en.searchSongs(p);

            for (Song song : songs) {
                assertTrue(song.getMode() == 0);
                verifySong(song);
            }
        }
    }

    @Test
    public void songCompareAudioSummaryToAnalysis() throws EchoNestException {
        double delta = .5;
        SongParams p = new SongParams();
        p.setMinTempo(119);
        p.setMaxTempo(120);
        p.addIDSpace("paulify");
        p.includeTracks();
        p.setResults(10);
        p.setLimit(true);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            Track track = song.getTrack("paulify");
            verifyTrack(track);
            TrackAnalysis analysis = track.getAnalysis();

            System.out.printf("mode %d %d\n", analysis.getMode(), song
                    .getMode());
            System.out.printf("key %d %d\n", analysis.getKey(), song.getKey());
            System.out.printf("tempo %.2f %.2f\n", analysis.getTempo(), song
                    .getTempo());
            System.out.printf("duration %.2f %.2f\n", analysis.getDuration(),
                    song.getDuration());
            System.out.printf("loudness %.2f %.2f\n", analysis.getLoudness(),
                    song.getLoudness());
            System.out.printf("TS %d %d\n", analysis.getTimeSignature(), song
                    .getTimeSignature());

            assertTrue("mode", analysis.getMode() == song.getMode());
            // assertTrue("key", analysis.getKey() == song.getKey());
            assertTrue("tempo",
                    Math.abs(analysis.getTempo() - song.getTempo()) < delta);
            assertTrue("duration", Math.abs(analysis.getDuration()
                    - song.getDuration()) < delta);
            assertTrue("loudness", Math.abs(analysis.getLoudness()
                    - song.getLoudness()) < delta);
            assertTrue("TS", analysis.getTimeSignature() == song
                    .getTimeSignature());
        }
    }

    @Test
    public void songSearchLoudnessTempoTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinLoudness(-30);
        p.setMaxLoudness(-15);
        p.setMinTempo(100);
        p.setMaxTempo(120);
        p.includeAudioSummary();
        p.sortBy(SongParams.SORT_DURATION, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;
        assertTrue(songs.size() > 0);

        for (Song song : songs) {
            double duration = song.getDuration();
            double loudness = song.getLoudness();
            double tempo = song.getTempo();

            assertTrue(tempo >= 100);
            assertTrue(tempo <= 120);
            assertTrue(loudness >= -30);
            assertTrue(loudness <= -15);
            assertTrue(duration >= last);
            last = duration;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchArtistStartYear() throws EchoNestException {
        int after =  1999;
        SongParams p = new SongParams();
        p.setArtistStartYearAfter(after);
        p.addDescription("pop");

        p.sortBy(SongParams.SORT_ARTIST_START_YEAR, true);
        List<Song> songs = en.searchSongs(p);
        int last = 0;
        assertTrue(songs.size() > 0);

        for (Song song : songs) {
            Artist artist = en.newArtistByID(song.getArtistID());
            YearsActive ya = artist.getYearsActive();
            int cur = ya.getRange(0)[0].intValue();
            assertTrue("ya range", cur > after);
            assertTrue("ya sort", cur >= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchArtistEndYear() throws EchoNestException {
        int before =  1999;
        SongParams p = new SongParams();
        p.addDescription("pop");
        p.setArtistEndYearBefore(before);

        p.sortBy(SongParams.SORT_ARTIST_END_YEAR, false);
        List<Song> songs = en.searchSongs(p);
        int last = 2012;
        assertTrue(songs.size() > 0);

        for (Song song : songs) {
            Artist artist = en.newArtistByID(song.getArtistID());
            YearsActive ya = artist.getYearsActive();
            int cur = ya.getRange(0)[1].intValue();
            assertTrue("ya range", cur < before);
            assertTrue("ya sort", cur <= last);
            last = cur;
            verifySong(song);
        }
    }
    
    @Test
    public void songSearchArtistStartEndYear() throws EchoNestException {
        int after =  1999;
        int before =  2010;

        SongParams p = new SongParams();
        p.setArtistStartYearAfter(after);
        p.setArtistStartYearBefore(before);
        p.setResults(100);

        p.addDescription("pop");

        p.sortBy(SongParams.SORT_ARTIST_START_YEAR, true);
        List<Song> songs = en.searchSongs(p);
        int last = 0;
        assertTrue(songs.size() > 0);

        for (Song song : songs) {
            Artist artist = en.newArtistByID(song.getArtistID());
            YearsActive ya = artist.getYearsActive();
            int cur = ya.getRange(0)[0].intValue();
            assertTrue("ya range", cur > after);
            assertTrue("ya sort", cur >= last);
            last = cur;
            verifySong(song);
        }
    }

    @Test
    public void songSearchArtistLoudnessTempoTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinLoudness(-30);
        p.setMaxLoudness(-15);
        p.setMinTempo(100);
        p.setMaxTempo(120);
        p.includeAudioSummary();
        p.setArtist("Elton John");
        p.setResults(100);
        p.sortBy(SongParams.SORT_DURATION, true);
        List<Song> songs = en.searchSongs(p);
        double last = 0;
        assertTrue(songs.size() > 0);

        for (Song song : songs) {
            double duration = song.getDuration();
            double loudness = song.getLoudness();
            double tempo = song.getTempo();

            assertTrue(tempo >= 100);
            assertTrue(tempo <= 120);
            assertTrue(loudness >= -30);
            assertTrue(loudness <= -15);
            assertTrue(duration >= last);
            last = duration;
            verifySong(song);
        }
    }

    @Test
    public void songSearchForHotArtists() throws EchoNestException {
        List<Artist> hotArtists = en.topHotArtists(20);
        int count = 0;
        for (Artist artist : hotArtists) {
            SongParams p = new SongParams();
            p.setArtistID(artist.getID());
            p.setResults(10);
            List<Song> songs = en.searchSongs(p);
            for (Song song : songs) {
                assertTrue("mismatched artist ID", song.getArtistID().equals(
                        artist.getID()));
            }
            if (songs.size() > 0) {
                count++;
            }
        }
        assertTrue("missing too many songs", count > 10);

    }

    @Test
    public void songSearchConstraintTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setKey(0);
        p.setMaxLoudness(-20);
        p.setMode(1);
        p.setMinTempo(130);
        p.includeAudioSummary();
        List<Song> songs = en.searchSongs(p);

        assertTrue(songs.size() > 0);
        for (Song song : songs) {
            assertTrue(song.getKey() == 0);
            assertTrue(song.getLoudness() <= -20);
            assertTrue(song.getMode() == 1);
            assertTrue(song.getTempo() >= 130);
            verifySong(song);
        }
    }

    @Test
    public void songSearchByDescription() throws EchoNestException {
        SongParams p = new SongParams();
        p.setResults(100);
        p.addDescription("heavy metal");
        p.setMinArtistFamiliarity(.9f);
        p.sortBy(SongParams.SORT_ARTIST_FAMILIARITY, false);
        p.includeArtistFamiliarity();
        List<Song> songs = en.searchSongs(p);

        assertTrue(songs.size() > 0);
        // we better find some SoaD
        boolean found = false;
        for (Song song : songs) {
            if (song.getArtistName().equalsIgnoreCase("System Of A Down")) {
                found = true;
                verifySong(song);
            }
        }

        if (!found) {
            fail("Can't find System of a Down");
        }
    }

    @Test
    public void songSearchHotPop() throws EchoNestException {
        SongParams p = new SongParams();
        p.setResults(100);
        p.addDescription("pop");
        p.addDescription("experimental");
        p.sortBy(SongParams.SORT_SONG_HOTTTNESSS, false);
        p.includeArtistFamiliarity();
        p.setMinArtistHotttnesss(.8f);
        p.includeSongHotttnesss();
        List<Song> songs = en.searchSongs(p);

        assertTrue(songs.size() > 0);
        boolean found = false;
        double last = 1.0;
        for (Song song : songs) {
            verifySong(song);
            if (song.getArtistName().equalsIgnoreCase("Lady Gaga")) {
                found = true;
            }

            if (!Double.isNaN(song.getSongHotttnesss())) {
                assertTrue("Out of order song hotttnesss", song
                        .getSongHotttnesss() <= last);
                last = song.getSongHotttnesss();
            }
        }

        if (!found) {
            fail("Can't find Lady Gaga");
        }
    }

    @Test
    public void songSearchColdPop() throws EchoNestException {
        SongParams p = new SongParams();
        p.setResults(100);
        p.addDescription("pop");
        p.sortBy(SongParams.SORT_SONG_HOTTTNESSS, true);
        p.includeArtistFamiliarity();
        p.includeSongHotttnesss();
        List<Song> songs = en.searchSongs(p);

        assertTrue(songs.size() > 0);
        double last = 0.0;
        for (Song song : songs) {
            verifySong(song);
            if (!Double.isNaN(song.getSongHotttnesss())) {
                assertTrue("Out of order song hotttnesss", song
                        .getSongHotttnesss() >= last);
                last = song.getSongHotttnesss();

            }
        }
    }

    //private final static String badRomanceCode = "eJxVlVuSZEkKQ7dyl4DzZv8b66PotLHpn6rgOg5CEp4Rr_LC6rvb_Ny8by_7e_Yy-ddnzdOXn-18iPYxe0N8z7-I3inj-HXu5zuRPv6Iz0jf15te1PPX9nn5EqcT9_jnYUeJoZxv57cLlAPNiwecoN2z5OKLmP6me1_Fa-Eg_fr1ed0HVrNvY69yHtezV5D9cjyS8uUkhM0jJbhfA_rXj_H6gvj4mtRmVjednFMQaiJcmcl4F_XMll-vh_H2Om5MrfpAuXPF7QHACP9VWQejEoNQ51wAOnELsAMnLeg3K5IsN8KsYG4OGLs2c-YAXE2YMQZbBb-rCetejg0EUCe_zrq1ginQh39n1EYH8AEsP5TzORV6p_l73QEsPHeNfN4P4W0wguRasDJNI5RpfqXC1CMc6Nq7KXJ0rPFpV4UfKPQ0fnUbPD58BUyXp256m37-Bk_d1ouMIrwfvN5omc3dTceg2fUjztb54M6IH1DoysCLmbCDj5SP2aBrKR-Seyo4la0jIQujSP_ChrgWuLgROAwbv-E53kWuz3-OvcNMJVqd2amGYwtpPbkENvpQW9knq8Dn5uBsL8z2JSV90J5YhluDqHFxgz4oCS-AMe43XDNLy8zNLGyYfxVnMVibSV1Cz-SPZKGgKMjK6PRpHX_n54zG6ODEmYNQg3F9inS6FwtUdKcOxlytQnF7DflUfWGT5jgC4RLdXtJ8NXmy4TW_aivdD2jvumEOb-qVyKFfcP0cmwxqoSSZ_ls2TAnDeJFYs2uHGAs12Ei5k63jDVngsW7sFboEC6njIj84t6dhQe1SAjmvETLeT3cYAq2pkIYvdo19OkIN77wDCYuEwITZhCz1ds0OQQg1_Aq5BnlstXI61yYxLE0mg3h_-Y3KscRCp-H5cPzHh5KPSIBPUwKiMDNPDGvCeRpscm6NfWmQ4ujfApupBI37fwUSWf7XQfSU-viw9KzaIw5tizrE8mpHac1h7_ryUYDQ_s6fRmw8JD47eb857xAvA_fkAICNXZ0PD4IyW6-yKODlWgjF5fZHgZ7l4LnP_1Aw_cfR3wSz8hN_VY5d_oKN_qO8T_jZ99W7bbzr7G2s9prrqH-SYPXY_ssgFz6Wgt1g6wb3AkdZ_wDKjlEb";
    private final static String badRomanceCode = "eJxdVAmOXSEMuxIhZOE4EOD-Rxj7taNK1Yys2IRs5P3Wo7XWYwFmAhatQ-sSHkCF0AdAaQ1a5oCkNWltwiFcag8wGkEIvDu0A8YHBTACLrQRFzBpLcKmSyHROKT3Azo_mCaNQKtDM6VFHzOCUwtCIoBNauzINjUGtUPKePZgeYPlQotFupKyDjeCEzgcZzznhHwRNrVC-c5ZOZt2Nh1sOgQHwXihtDiw4MDCCUHArRYTrcYibLoUrUNgvGC8ZLwUaMn5ceAtjZYT-HjJ-SVDJeeXm1AEzi85v3wATrhN-QDiVFrsd3J-k_ObjDc5v8lYE_MTLW0rZisZr1XXMxRjLxT1DGLr-IszBDUmxLbOGIlinvRtEE9pjXXwShoQoYpB7GePVTbwNNtP3-hO_J6th89kW_p4Fogz15gQZbfus3OL8MhzaexKiFjEc1_HVvqxHVvzHtnm4Ff3cG0PPK_eKKwl9HjzoZDVjrwbWGJPiN2zXYhqawKzz318efZcyx-W4eCh1tyKPuxiOuMe9BHwF90U1_EctwvubVtald89O08h9tUlokpXrEw74HdeiLNW7oVFWR4Q8TmcNWe1FyIP-2epp0aQO0A3plH2Ld0d5K3Jhqj3fqt8Zr2NuTQND72Sr9o5E625PsrqCbHrasVv4a68qQquC_94W_C74Cjjkp9qZTxH_d99nNuRwuvjHPlDhiqi34KIl0R0PBaiVblqCaJB7PvBZ_GHZL0S7FWr2Tf8sVcIn7XqQEQjCrHhh6NekjcDCD6twg62-4aUKvLqPsiNj6Ug6vUhyAv-5oQuBX4dfNDpO0fVv-d__Hlu__zj4_S3jxv8sb0f9zmwveS2sP_ID-7zy8-P5L_87PNvfvSJfPLV3cErzm998tDcV98PwMpc3Q==";
    @Test
    public void songIdentifyTestSimple() throws EchoNestException {
        Set<String> songIDs = new HashSet<String>();
        songIDs.add("SOOGHIF12AB0181A2C");
        songIDs.add("SOTKKEA127D9789740");

        IdentifySongParams p = new IdentifySongParams();
        p.includeArtistFamiliarity();
        p.includeSongHotttnesss();
        p.setCode(badRomanceCode);
        List<Song> songs = en.identifySongs(p);

        assertTrue(songs.size() > 0);

        assertTrue("song identify mismatch", songIDs.contains(songs.get(0).getID()));
        for (Song song : songs) {
            verifySong(song);
        }
    }

    private final static String badRomanceBadCode = "BADeJxVlVuSZEkKQ7dyl4DzZv8b66PotLHpn6rgOg5CEp4Rr_LC6rvb_Ny8by_7e_Yy-ddnzdOXn-18iPYxe0N8z7-I3inj-HXu5zuRPv6Iz0jf15te1PPX9nn5EqcT9_jnYUeJoZxv57cLlAPNiwecoN2z5OKLmP6me1_Fa-Eg_fr1ed0HVrNvY69yHtezV5D9cjyS8uUkhM0jJbhfA_rXj_H6gvj4mtRmVjednFMQaiJcmcl4F_XMll-vh_H2Om5MrfpAuXPF7QHACP9VWQejEoNQ51wAOnELsAMnLeg3K5IsN8KsYG4OGLs2c-YAXE2YMQZbBb-rCetejg0EUCe_zrq1ginQh39n1EYH8AEsP5TzORV6p_l73QEsPHeNfN4P4W0wguRasDJNI5RpfqXC1CMc6Nq7KXJ0rPFpV4UfKPQ0fnUbPD58BUyXp256m37-Bk_d1ouMIrwfvN5omc3dTceg2fUjztb54M6IH1DoysCLmbCDj5SP2aBrKR-Seyo4la0jIQujSP_ChrgWuLgROAwbv-E53kWuz3-OvcNMJVqd2amGYwtpPbkENvpQW9knq8Dn5uBsL8z2JSV90J5YhluDqHFxgz4oCS-AMe43XDNLy8zNLGyYfxVnMVibSV1Cz-SPZKGgKMjK6PRpHX_n54zG6ODEmYNQg3F9inS6FwtUdKcOxlytQnF7DflUfWGT5jgC4RLdXtJ8NXmy4TW_aivdD2jvumEOb-qVyKFfcP0cmwxqoSSZ_ls2TAnDeJFYs2uHGAs12Ei5k63jDVngsW7sFboEC6njIj84t6dhQe1SAjmvETLeT3cYAq2pkIYvdo19OkIN77wDCYuEwITZhCz1ds0OQQg1_Aq5BnlstXI61yYxLE0mg3h_-Y3KscRCp-H5cPzHh5KPSIBPUwKiMDNPDGvCeRpscm6NfWmQ4ujfApupBI37fwUSWf7XQfSU-viw9KzaIw5tizrE8mpHac1h7_ryUYDQ_s6fRmw8JD47eb857xAvA_fkAICNXZ0PD4IyW6-yKODlWgjF5fZHgZ7l4LnP_1Aw_cfR3wSz8hN_VY5d_oKN_qO8T_jZ99W7bbzr7G2s9prrqH-SYPXY_ssgFz6Wgt1g6wb3AkdZ_wDKjlEb";

    @Test
    public void songIdentifyBadCode() throws EchoNestException {
        IdentifySongParams p = new IdentifySongParams();
        p.includeArtistFamiliarity();
        p.includeSongHotttnesss();
        p.setCode(badRomanceBadCode);
        List<Song> songs = en.identifySongs(p);
        assertTrue(songs.size() == 0);
    }

    @Test
    public void songSimilarSimple() throws EchoNestException {
        SongParams p = new SongParams();
        p.setID(hey_jude.getID());

        List<Song> sims = en.similarSongs(p);
        assertTrue(sims.size() > 0);
        for (Song song : sims) {
            verifySong(song);
        }
    }

    @Test
    public void songTrackTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(100);
        p.setMaxTempo(130);
        p.addIDSpace("paulify");
        p.includeTracks();
        p.setResults(100);
        p.setLimit(true);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            Track track = song.getTrack("paulify");
            System.out.println("track " + track.getID() + " status "
                    + track.getStatus());
            // verifyTrack(track);
        }
    }

    @Test
    public void songTrackStatusTestPlayme() throws EchoNestException {
        songTrackStatusTestCatalog("playme");
    }

    @Test
    public void songTrackStatusTestPaulify() throws EchoNestException {
        songTrackStatusTestCatalog("paulify");
    }

    @Test
    public void songTrackStatusTest7Digital() throws EchoNestException {
        songTrackStatusTestCatalog("7digital");
    }

    void songTrackStatusTestCatalog(String catalog) throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(100);
        p.setMaxTempo(130);
        p.addIDSpace(catalog);
        p.includeTracks();
        p.setResults(20);
        p.setLimit(true);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            Track track = song.getTrack(catalog);
            verifyTrack(track);
        }
    }

    void songTrackStatusTestCatalogIsReproducable(String catalog)
            throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(100);
        p.setMaxTempo(130);
        p.addIDSpace(catalog);
        p.includeTracks();
        p.setResults(20);
        p.setLimit(true);

        List<Song> songs1 = en.searchSongs(p);
        List<Song> songs2 = en.searchSongs(p);

        assertTrue("same length results", songs1.size() == songs2.size());

        for (int i = 0; i < songs1.size(); i++) {
            assertTrue("results match", songs1.get(i).getID().equals(
                    songs2.get(i).getID()));
        }
    }

    @Test
    public void songTrackTestWithEmptyTracks() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(119);
        p.setMaxTempo(120);
        p.addIDSpace("paulify");
        p.includeTracks();
        p.setResults(100);
        // p.setLimit(true);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            Track track = song.getTrack("paulify");
            if (track != null) {
                verifyTrack(track);
            }
        }
    }

    @Test
    public void songTrackAnalysisTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(100);
        p.setMaxTempo(110);
        p.addIDSpace("paulify");
        p.includeTracks();
        p.setResults(10);
        p.setLimit(true);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            Track track = song.getTrack("paulify");
            verifyTrack(track);
            verifyAnalysis(track.getAnalysis());
        }
    }

    @Test
    public void songAnalysisTest() throws EchoNestException {
        SongParams p = new SongParams();
        p.setMinTempo(100);
        p.setMaxTempo(110);
        p.includeAudioSummary();
        p.setResults(10);

        List<Song> songs = en.searchSongs(p);
        for (Song song : songs) {
            verifyAnalysis(song.getAnalysis());
        }
    }

    private void verifyTrack(Track track) throws EchoNestException {
        assertTrue("status check", track.getStatus().equals(
                Track.AnalysisStatus.COMPLETE));
        track.showAll();
    }

    private void verifyAnalysis(TrackAnalysis analysis) {
        System.out.println("num samples : " + analysis.getNumSamples());
        System.out.println("sample md5  : " + analysis.getMD5());
        System.out.println("num channels: " + analysis.getNumChannels());
        System.out.println("duration    : " + analysis.getDuration());

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

    private void verifySong(Song song) throws EchoNestException {
        song.showAll();
    }
}
