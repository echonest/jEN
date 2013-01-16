/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.tests;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.TrackAnalysis;

/**
 * 
 * @author plamere
 */
public class TestHarness {

    private EchoNestAPI echoNest;
    private Map<String, List<Test>> testSets = new HashMap<String, List<Test>>();
    private boolean autoAdvance = true;
    private Artist curArtist = null;
    private HashSet<Artist> visited = new HashSet<Artist>();
    private List<Artist> artistQueue = new ArrayList<Artist>();
    private boolean runSecondary = true;

    TestHarness(EchoNestAPI en) throws EchoNestException {
        echoNest = en;
        addBasicTests();
        addDetailedTests();

        seedQueue();
        advanceArtist();
    }

    private void addBasicTests() {
        String testSet = "basic";

        add(testSet, new Test() {

            public String getName() {
                return "audio";
            }

            public boolean go() throws Exception {
                curArtist.getAudio();
                return true;
            }
        });

        if (runSecondary) {
            add(testSet, new Test() {

                public String getName() {
                    return "audio2";
                }

                public boolean go() throws Exception {
                    curArtist.getAudio(2, 10);
                    return true;
                }
            });
            
            add(testSet, new Test() {

                public String getName() {
                    return "blogs2";
                }

                public boolean go() throws Exception {
                    curArtist.getBlogs(2, 10);
                    return true;
                }
            });
            
            add(testSet, new Test() {

                public String getName() {
                    return "news2";
                }

                public boolean go() throws Exception {
                    curArtist.getNews(2, 10);
                    return true;
                }
            });
            
            add(testSet, new Test() {

                public String getName() {
                    return "reviews2";
                }

                public boolean go() throws Exception {
                    curArtist.getReviews(2, 10);
                    return true;
                }
            });
            
            add(testSet, new Test() {

                public String getName() {
                    return "bios2";
                }

                public boolean go() throws Exception {
                    curArtist.getBiographies(2, 10);
                    return true;
                }
            });
            
            add(testSet, new Test() {

                public String getName() {
                    return "images2";
                }

                public boolean go() throws Exception {
                    curArtist.getImages(2, 10);
                    return true;
                }
            });
            
            add(testSet, new Test() {

                public String getName() {
                    return "videos2";
                }

                public boolean go() throws Exception {
                    curArtist.getVideos(2, 10);
                    return true;
                }
            });
        }

        add(testSet, new Test() {

            public String getName() {
                return "blogs";
            }

            public boolean go() throws Exception {
                curArtist.getBlogs();
                return true;
            }
        });



        add(testSet, new Test() {

            public String getName() {
                return "familiarity";
            }

            public boolean go() throws Exception {
                return curArtist.getFamiliarity() >= .0
                        && curArtist.getFamiliarity() <= 1.0;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "hotttnesss";
            }

            public boolean go() throws Exception {
                return curArtist.getHotttnesss() >= 0.
                        && curArtist.getHotttnesss() <= 1.0;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "search";
            }

            public boolean go() throws Exception {
                List<Artist> artists = echoNest.searchArtists(getArtistName());
                for (Artist artist : artists) {
                    if (artist.equals(curArtist)) {
                        return true;
                    }
                }
                return false;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "news";
            }

            public boolean go() throws Exception {
                curArtist.getNews();
                return true;
            }
        });



        add(testSet, new Test() {

            public String getName() {
                return "reviews";
            }

            public boolean go() throws Exception {
                curArtist.getReviews();
                return true;
            }
        });



        add(testSet, new Test() {

            public String getName() {
                return "images";
            }

            public boolean go() throws Exception {
                curArtist.getImages();
                return true;
            }
        });



        add(testSet, new Test() {

            public String getName() {
                return "bios";
            }

            public boolean go() throws Exception {
                curArtist.getBiographies();
                return true;
            }
        });



        add(testSet, new Test() {

            public String getName() {
                return "musicbrainz id";
            }

            public boolean go() throws Exception {
                String mbid = curArtist.getForeignID("musicbrainz");
                Artist mbartist = echoNest.newArtistByID(mbid);
                return mbartist.equals(curArtist);
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "similar";
            }

            public boolean go() throws Exception {
                List<Artist> sims = curArtist.getSimilar(15);
                return sims.size() == 15;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "urls";
            }

            public boolean go() throws Exception {
                Map<String, String> urlMap = curArtist.getUrls();
                return urlMap.get("itunes_url") != null
                        && urlMap.get("amazon_url") != null;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "videos";
            }

            public boolean go() throws Exception {
                curArtist.getVideos();
                return true;
            }
        });


        add(testSet, new Test() {

            public String getName() {
                return "top_hottt_artists";
            }

            public boolean go() throws Exception {
                return echoNest.topHotArtists(100).size() == 100;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "search_songs";
            }

            public boolean go() throws Exception {
                SongParams p = new SongParams();
                p.setArtist("weezer");
                p.setMinTempo(100);
                p.addIDSpace("7digital");
                return echoNest.searchSongs(p).size() > 1;
            }
        });

        add(testSet, new Test() {

            public String getName() {
                return "get_track_analysis";
            }

            public boolean go() throws Exception {
                SongParams p = new SongParams();
                p.setArtistID(curArtist.getID());
                p.setResults(1);
                p.addIDSpace("7digital");
                p.includeTracks();
                p.setLimit(true);
                List<Song> songs = echoNest.searchSongs(p);
                if (songs.size() > 0) {
                    Song song = songs.get(0);
                    Track track = song.getTrack("7digital");
                    TrackAnalysis analysis = track.getAnalysis();
                    return analysis.getDuration() > 0;
                } else {
                    System.out.println(" no songs for " + curArtist.getName());
                }
                return true;
            }
        });
    }

    void addDetailedTests() {
        String testSet = "details";

        add(testSet, new Test() {

            public String getName() {
                return "get_audio (all)";
            }

            public boolean go() throws Exception {
                return true;
            }
        });
    }

    public void runTests(String testName, int count) {
        int pass = 0;
        int fail = 0;
        List<Test> tests = testSets.get(testName);
        if (tests != null) {
            for (int i = 0; i < count; i++) {
                for (Test test : tests) {
                    try {
                        System.out.print("    " + test.getName() + ": ");
                        if (test.go()) {
                            pass++;
                            System.out.println("OK");
                        } else {
                            fail++;
                            System.out.println("FAIL");
                        }
                    } catch (Exception e) {
                        // System.out.println("ERROR: " + e.getMessage());
                        System.out.println("ERROR");
                    }
                }
                if (autoAdvance) {
                    advanceArtist();
                }
                if (i % 10 == 1) {
                    showStats();
                    System.out.printf(
                            "Test status:  Passed: %d,  Failed: %d\n", pass,
                            fail);
                }
            }
        }
    }

    public void showStats() {
        echoNest.showStats();
    }

    private void add(String testSetName, Test test) {
        List<Test> testSet = testSets.get(testSetName);

        if (testSet == null) {
            testSet = new ArrayList<Test>();
            testSets.put(testSetName, testSet);
        }
        testSet.add(test);
    }

    private String getArtistName() throws EchoNestException {
        return curArtist.getName();
    }

    void seedQueue() throws EchoNestException {
        String[] seeds = { "Lady Gaga", "The Beatles", "Miles Davis",
                "Michael Jackson", "Led Zeppelin" };
        for (String name : seeds) {
            List<Artist> artists = echoNest.searchArtists(name);
            artistQueue.addAll(artists);
        }
        Collections.shuffle(artistQueue);
    }

    void advanceArtist() {
        try {
            do {
                curArtist = artistQueue.remove(0);
                log("Current artist is: " + curArtist.getName());
            } while (visited.contains(curArtist));

            visited.add(curArtist);

            List<Artist> similarArtists = curArtist.getSimilar(15);
            for (Artist sartist : similarArtists) {
                if (!visited.contains(sartist)) {
                    artistQueue.add(sartist);
                }
            }
        } catch (EchoNestException e) {
            log("Advance Artist Error " + e.getMessage());
        }
    }

    void assertTrue(boolean condition, String message) throws TestException {
        if (!condition) {
            throw new TestException(message);
        }
    }

    void fail(String message) throws TestException {
        throw new TestException(message);
    }

    void log(String message) {
        System.out.println(message);
    }
}

interface Test {

    String getName();

    boolean go() throws Exception;
}

@SuppressWarnings("serial")
class TestException extends Exception {

    TestException(String message) {
        super(message);
    }
}
