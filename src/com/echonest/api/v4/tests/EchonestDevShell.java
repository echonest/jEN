/*


 * 
 * 
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.ArtistCatalog;
import com.echonest.api.v4.ArtistLocation;
import com.echonest.api.v4.ArtistParams;
import com.echonest.api.v4.BasicPlaylistParams;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.SongCatalog;
import com.echonest.api.v4.SongCatalogItem;
import com.echonest.api.v4.SongParams;
import com.echonest.api.v4.Term;
import com.echonest.api.v4.TimedEvent;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.TrackAnalysis;
import com.echonest.api.v4.WebDocument;
import com.echonest.api.v4.YearsActive;
import com.echonest.api.v4.PlaylistParams.PlaylistType;
import com.echonest.api.v4.util.Shell;
import com.echonest.api.v4.util.ShellCommand;
import com.echonest.api.v4.util.Utilities;

/**
 *
 * @author plamere
 */
public class EchonestDevShell {

    private Shell shell;
    private EchoNestAPI en;
    private TestHarness testHarness;
    private Map<String, Artist> artistCache = new HashMap<String, Artist>();
    private int displayCount = 15;
    private Set<Artist> done = new HashSet<Artist>();
    private List<Artist> todo = new ArrayList<Artist>();
    private Track currentTrack;

    public EchonestDevShell() throws EchoNestException {
        en = new EchoNestAPI();
        shell = new Shell();
        shell.setPrompt("nest% ");
        addEchoNestCommands();
    }

    public void go() {
        System.out.println("Welcome to The Echo Nest API Shell");
        System.out.println("   type 'help' ");
        shell.run();
    }

    public Shell getShell() {
        return shell;
    }

    private void addEchoNestCommands() {
        shell.add("enid", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    System.out.println(artist.getName() + " " + artist.getID());
                }
                return "";
            }

            public String getHelp() {
                return "gets the ENID for an arist";
            }
        });

        shell.add("search_artist", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> artists = en.searchArtists(Shell.mash(args, 1));
                for (Artist artist : artists) {
                    System.out.println(artist.getID() + " " + artist.getName());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists exact match  ";
            }
        });

        shell.add("suggest_artist", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> artists = en.suggestArtists(Shell.mash(args, 1));
                for (Artist artist : artists) {
                    System.out.println(artist.getID() + " " + artist.getName());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists exact match  ";
            }
        });

        shell.add("top_hottt", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Params p = new Params();
                p.add("results", 100);

                List<Artist> artists = en.topHotArtists(p);
                for (Artist artist : artists) {
                    System.out.println(artist.getID() + " " + artist.getName());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists exact match  ";
            }
        });


        shell.add("list_terms", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<String> terms = en.listTerms(EchoNestAPI.TermType.ANY);
                for (String term : terms) {
                    System.out.println(term);
                }
                return "";
            }

            public String getHelp() {
                return "list available terms";
            }
        });

        shell.add("list_genres", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<String> genres = en.listGenres();
                for (String genre : genres) {
                    System.out.println(genre);
                }
                return "";
            }

            public String getHelp() {
                return "list available genres";
            }
        });


        shell.add("random_walk", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                int count = 10;
                String artistName = "justin bieber";

                if (args.length > 1) {
                    count = Integer.parseInt(args[1]);
                }

                if (args.length > 2) {
                    artistName = Shell.mash(args, 2);
                }

                Artist curSeed = getArtist(artistName);

                for (int i = 0; i < count; i++) {
                    System.out.println("Similars for " + curSeed.getName());
                    List<Artist> similars = curSeed.getSimilar(15);
                    for (Artist sim : similars) {
                        System.out.println("  " + sim.getName());
                    }
                    Collections.shuffle(similars);
                    curSeed = similars.get(0);
                }
                return "";
            }

            public String getHelp() {
                return "random similarity walk. Usage: random_walk [count] [initial seed] ";
            }
        });

        shell.add("top_terms", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                int count = 100;
                if (args.length > 1) {
                    count = Integer.parseInt(args[1]);
                }
                List<Term> terms = en.getTopTerms(count);
                for (Term term : terms) {
                    System.out.println(term.getFrequency() + " "
                            + term.getName());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists exact match  ";
            }
        });

        shell.add("pp_songs", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Params p = new Params();
                populateParams(p, args);

                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.println(song.toString());
                }
                return "";
            }

            public String getHelp() {
                return "searches for songs ";
            }
        });

        shell.add("track", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 1 && currentTrack != null) {
                    dumpTrack(currentTrack, false);
                } else {
                    for (int i = 1; i < args.length; i++) {
                        String id = args[i];
                        Track track = null;
                        if (id.startsWith("TR")) {
                            track = en.newTrackByID(id);
                        } else {
                            track = en.newTrackByMD5(id);
                        }
                        dumpTrack(track, false);
                    }
                }
                return "";
            }

            public String getHelp() {
                return "dumps tracks by ID or MD5";
            }
        });

        shell.add("trackDetails", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 1 && currentTrack != null) {
                    dumpTrack(currentTrack, true);
                } else {
                    for (int i = 1; i < args.length; i++) {
                        String id = args[i];
                        Track track = null;
                        if (id.startsWith("TR")) {
                            track = en.newTrackByID(id);
                        } else {
                            track = en.newTrackByMD5(id);
                        }
                        dumpTrack(track, true);
                    }
                }
                return "";
            }

            public String getHelp() {
                return "dumps tracks by ID or MD5";
            }
        });

        shell.add("psearch_artists", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Params p = new Params();
                populateParams(p, args);
                List<Artist> artists = en.searchArtists(p);
                for (Artist artist : artists) {
                    System.out.println(artist.toString());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists (using params) ";
            }
        });

        shell.add("qbd", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                ArtistParams p = new ArtistParams();
                p.setResults(displayCount);
                p.sortBy(ArtistParams.SORT_HOTTTNESSS, false);
                // p.sortBy(ArtistParams.SORT_FAMILIARITY, false);

                String description = Shell.mash(args, 1);
                p.addDescription(description);
                List<Artist> artists = en.searchArtists(p);
                for (Artist artist : artists) {
                    System.out.println(artist.getName());
                }
                return "";
            }

            public String getHelp() {

                return "query by description";
            }
        });
        shell.add("psimilar_songs", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Params p = new Params();
                populateParams(p, args);

                List<Song> songs = en.similarSongs(p);
                for (Song song : songs) {
                    System.out.println(song.toString());
                }
                return "";
            }

            public String getHelp() {
                return "finds similar songs to the seed song ";
            }
        });

        shell.add("most_danceable", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongParams p = new SongParams();
                p.setArtistID(getArtist(Shell.mash(args, 1)).getID());
                p.sortBy(SongParams.SORT_DANCEABILITY, false);
                p.includeAudioSummary();

                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.printf("%.2f %s\n", song.getDanceability(), song
                            .getTitle());
                }
                return "";
            }

            public String getHelp() {
                return "finds most danceable songs by an artist ";
            }
        });

        shell.add("least_danceable", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongParams p = new SongParams();
                p.setArtistID(getArtist(Shell.mash(args, 1)).getID());
                p.sortBy(SongParams.SORT_DANCEABILITY, true);
                p.includeAudioSummary();

                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.printf("%.2f %s\n", song.getDanceability(), song
                            .getTitle());
                }
                return "";
            }

            public String getHelp() {
                return "finds least danceable songs by an artist ";
            }
        });

        shell.add("most_energy", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongParams p = new SongParams();
                p.setArtistID(getArtist(Shell.mash(args, 1)).getID());
                p.sortBy(SongParams.SORT_ENERGY, false);
                p.includeAudioSummary();

                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.printf("%.2f %s\n", song.getEnergy(), song
                            .getTitle());
                }
                return "";
            }

            public String getHelp() {
                return "finds most energetic songs by an artist ";
            }
        });

        shell.add("least_energy", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongParams p = new SongParams();
                p.setArtistID(getArtist(Shell.mash(args, 1)).getID());
                p.sortBy(SongParams.SORT_ENERGY, true);
                p.includeAudioSummary();

                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.printf("%.2f %s\n", song.getEnergy(), song
                            .getTitle());
                }
                return "";
            }

            public String getHelp() {
                return "finds least danceable songs by an artist ";
            }
        });

        shell.add("psimilar_artists", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Params p = new Params();

                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    String[] pv = arg.split("=");
                    if (pv.length == 2) {
                        p.add(pv[0], pv[1]);
                    } else {
                        System.out.println("Don't understand " + arg);
                    }
                }
                List<Artist> artists = en.getSimilarArtists(p);
                for (Artist artist : artists) {
                    System.out.println(artist.toString());
                }
                return "";
            }

            public String getHelp() {
                return "searches for artists (using params) ";
            }
        });

        shell.add("get_songs", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Params p = new Params();

                for (int i = 1; i < args.length; i++) {
                    String arg = args[i];
                    String[] pv = arg.split("=");
                    if (pv.length == 2) {
                        p.add(pv[0], pv[1]);
                    } else {
                        System.out.println("Don't understand " + arg);
                    }
                }
                List<Song> songs = en.getSongs(p);
                for (Song song : songs) {
                    System.out.println(song.toString());
                }
                return "";
            }

            public String getHelp() {
                return "gets info for a song ";
            }
        });

        shell.add("hot_songs", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                boolean old_way = true;
                SongParams p = new SongParams();
                p.sortBy(SongParams.SORT_SONG_HOTTTNESSS, false);

                String description = Shell.mash(args, 1);

                if (old_way) {
                    p.addDescription(description);
                } else {
                    if (description.length() > 0) {
                        String[] fields = description.split(",");
                        for (String f : fields) {
                            p.addDescription(f.trim());
                        }
                    }
                }
                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.println(song.toString());
                }
                return "";
            }

            public String getHelp() {
                return "show hot songs";
            }
        });

        shell.add("display_count", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    displayCount = Integer.parseInt(args[1]);
                } else {
                    System.out.println("Display count: " + displayCount);
                }
                return "";
            }

            public String getHelp() {
                return "sets/gets the number of items to display";
            }
        });

        shell.add("get_similar", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    System.out.println("Similarity for " + artist.getName());
                    List<Artist> artists = artist.getSimilar(displayCount);
                    for (Artist sartist : artists) {
                        System.out.printf("  %s\n", sartist.getName());
                    }

                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "finds similar artists";
            }
        });

        shell.add("get_blogs", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("blogs", artist.getBlogs());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets blogs for an artist";
            }
        });

        shell.add("get_images", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("images", artist.getImages());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets images for an artist";
            }
        });

        shell.add("get_audio", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("audio", artist.getAudio());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets audio for an artist";
            }
        });

        shell.add("get_bio", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("audio", artist.getBiographies());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets bios for an artist";
            }
        });

        shell.add("get_album_art", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongParams p = new SongParams();
                p.setArtist(args[1]);
                p.setTitle(args[2]);
                p.includeTracks();
                p.setLimit(true);
                p.addIDSpace("7digital-US");
                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.println(song.toString());
                    String url = song.getString("tracks[0].release_image");
                    System.out.println("release image" + url);
                }
                return "";
            }

            public String getHelp() {
                return "shows cover art for a song";
            }
        });


        shell.add("get_song_foreign_ids", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongParams p = new SongParams();
                p.setArtist(args[1]);
                p.setTitle(args[2]);
                p.includeTracks();
                p.setLimit(true);
                p.addIDSpace("rdio-us-streaming");
                List<Song> songs = en.searchSongs(p);
                for (Song song : songs) {
                    System.out.println(song.toString());
                    String fid = song.getString("foreign_ids[0].foreign_id");
                    System.out.println("fid is " + fid);
                }
                return "";
            }

            public String getHelp() {
                return "demonstrate how to extract song foreign ids from a song";
            }
        });

        shell.add("get_urls", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    System.out.println("URLS for " + artist.getName());
                    Map<String, String> urlMap = artist.getUrls();
                    List<String> keys = new ArrayList<String>(urlMap.keySet());
                    Collections.sort(keys);
                    for (String key : keys) {
                        System.out.printf("%20s : %s\n", key, urlMap.get(key));
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets urls for an artist";
            }
        });

        shell.add("get_video", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("video", artist.getVideos());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets videos for an artist";
            }
        });

        shell.add("get_news", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("news", artist.getNews());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets news for an artist";
            }
        });

        shell.add("get_reviews", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    dumpDocs("reviews", artist.getReviews());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets reviews for an artist";
            }
        });

        shell.add("get_artist_location", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    ArtistLocation location = artist.getArtistLocation();
                    System.out.println("Location is " + location.getLocation());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets location for an artist";
            }
        });

        shell.add("get_terms", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    System.out.println("Terms for " + artist.getName());
                    List<Term> terms = artist.getTerms();
                    for (Term term : terms) {
                        System.out.printf("%.2f %s\n", term.getWeight(), term
                                .getName());
                    }
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets terms for an artist";
            }
        });

        shell.add("get_fam", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    System.out.println("Familiarity for " + artist.getName()
                            + " " + artist.getFamiliarity());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets familiarity for an artist";
            }
        });

        shell.add("get_hot", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                Artist artist = getArtist(Shell.mash(args, 1));
                if (artist != null) {
                    System.out.println("Hotttnesss for " + artist.getName()
                            + " " + artist.getHotttnesss());
                } else {
                    System.out.println("Can't find artist");
                }
                return "";
            }

            public String getHelp() {
                return "gets hotttnesss for an artist";
            }
        });

        shell.add("trackUpload", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                String arg = null;
                if (args.length >= 2) {
                    arg = Shell.mash(args, 1);
                } else {
                    arg = Utilities.createNewAudioFile("au").getAbsolutePath();
                }
                try {
                    Track track;
                    if (arg.startsWith("http:")) {
                        track = en.uploadTrack(new URL(arg), true);
                    } else {
                        System.out.println("md5 is " + Utilities.md5(arg));
                        track = en.uploadTrack(new File(arg), true);
                    }
                    track.waitForAnalysis(30000);
                    System.out.println("ID: " + track.getID() + " status "
                            + track.getStatus());

                    currentTrack = track;
                    System.out.println("Tempo is "
                            + track.getAnalysis().getTempo());
                } catch (IOException e) {
                    System.out.println("Trouble uploading");
                }
                return "";
            }

            public String getHelp() {
                return "uploads a track";
            }
        });

        shell.add("trackBeats", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                TrackAnalysis a = currentTrack.getAnalysis();
                List<TimedEvent> beats = a.getBeats();
                for (TimedEvent beat : beats) {
                    System.out.printf("%.6f, %.6f\n", beat.getStart(), beat
                            .getDuration());
                }
                return "";
            }

            public String getHelp() {
                return "uploads a track";
            }
        });

        shell.add("trackReanalyze", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    Track track = en.newTrackByID(args[1]);
                    Track.AnalysisStatus status = track.reanalyze(true);
                    System.out.println("Analysis status " + status);
                } else {
                    System.out.println("trackReanalyze TRID");
                }
                return "";
            }

            public String getHelp() {
                return "reanalyzes a trak";
            }
        });

        shell.add("stats", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                en.showStats();
                return "";
            }

            public String getHelp() {
                return "shows stats";
            }
        });

        shell.add("acov" + "", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {

                int count = 100;
                String[] buckets = {"audio", "biographies", "blogs",
                    "familiarity", "hotttnesss", "images", "news",
                    "reviews", "urls", "video"};

                if (args.length >= 2) {
                    count = Integer.parseInt(args[1]);
                }

                if (todo.size() == 0) {
                    Artist seed = getArtist("The Beatles");
                    todo.add(seed);
                }

                for (int i = 0; i < count; i++) {
                    Artist next = null;

                    while (todo.size() > 0 && next == null) {
                        next = todo.remove(0);
                        if (!done.contains(next)) {
                            break;
                        }
                    }

                    if (next != null) {
                        done.add(next);

                        next.fetchBuckets(buckets);
                        float score = scoreArtist(next);
                        if (i % 20 == 0) {
                            System.out
                                    .printf(
                                    "%5s %3s %3s %3s %3s %3s %3s %3s %3s %5s %5s %s\n",
                                    "score", "aud", "bio", "blg",
                                    "img", "nws", "rvs", "vid", "url",
                                    "hot", "fam", "");
                        }

                        System.out
                                .printf(
                                "%5.1f %3d %3d %3d %3d %3d %3d %3d %3d %.3f %.3f %s\n",
                                score, next.getAudio().size(), next
                                .getBiographies().size(), next
                                .getBlogs().size(), next
                                .getImages().size(), next
                                .getNews().size(), next
                                .getReviews().size(), next
                                .getVideos().size(), next
                                .getUrls().size(), next
                                .getHotttnesss(), next
                                .getFamiliarity(), next
                                .getName());
                        todo.addAll(next.getSimilar(20));
                        Collections.shuffle(todo);
                    }
                }
                return "";
            }

            public String getHelp() {
                return "shows artist coverage";
            }
        });

        shell.add("mbidtest", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                int count = 100;
                if (args.length >= 2) {
                    count = Integer.parseInt(args[1]);
                }
                for (Artist artist : en.topHotArtists(count)) {
                    String mbid = artist.getForeignID("musicbrainz");
                    if (mbid != null && mbid.length() > 0) {
                        Artist nArtist = en.newArtistByID(mbid);
                        String status = "FAIL";
                        if (artist.equals(nArtist)) {
                            status = "GOOD";
                        }
                        System.out.printf("   %s %s %s %s %s\n", status, mbid,
                                artist.getName(), nArtist
                                .getForeignID("musicbrainz"), nArtist
                                .getName());
                    } else {
                        System.out.printf("MISSING mbid for %s\n", artist
                                .getName());
                    }
                }
                return "";
            }

            public String getHelp() {
                return "test mbids for popular artists";
            }
        });

        shell.add("scov", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                int count = 100;
                boolean showSims = false;
                if (args.length >= 2) {
                    count = Integer.parseInt(args[1]);
                }

                if (args.length >= 3) {
                    showSims = true;
                }

                if (todo.size() == 0) {
                    Artist seed = getArtist("The Beatles");
                    todo.add(seed);
                }

                for (int i = 0; i < count; i++) {
                    Artist next = null;

                    while (todo.size() > 0 && next == null) {
                        next = todo.remove(0);
                        if (!done.contains(next)) {
                            break;
                        }
                    }

                    if (next != null) {
                        done.add(next);
                        System.out.printf("%s %s\n", next.getID(), next
                                .getName());
                        try {
                            List<Artist> sims = next.getSimilar(15);
                            if (showSims) {
                                for (Artist sim : sims) {
                                    System.out
                                            .printf("    %s\n", sim.getName());
                                }
                            }
                            todo.addAll(sims);
                        } catch (EchoNestException e) {
                            System.out.printf("WARNING, no sims for %s %s\n",
                                    next.getID(), next.getName());
                        }
                        Collections.shuffle(todo);
                    }
                }
                return "";
            }

            public String getHelp() {
                return "shows artist similarity coverage";
            }
        });

        shell.add("splaylist", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                PlaylistParams p = new PlaylistParams();
                p.setType(PlaylistType.ARTIST_RADIO);
                p.addArtist(Shell.mash(args, 1));
                p.setResults(10);
                p.setVariety(.5f);
                Playlist playlist = en.createStaticPlaylist(p);
                for (Song song : playlist.getSongs()) {
                    System.out.printf("%40.40s %s\n", song.getArtistName(),
                            song.getTitle());
                }
                return "";
            }

            public String getHelp() {
                return "generates an artist radio playlist";
            }
        });


        shell.add("bplaylist", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                BasicPlaylistParams p = new BasicPlaylistParams();
                p.setType(BasicPlaylistParams.PlaylistType.ARTIST_RADIO);
                p.addArtist(Shell.mash(args, 1));
                p.setResults(10);
                Playlist playlist = en.createBasicPlaylist(p);
                for (Song song : playlist.getSongs()) {
                    System.out.printf("%40.40s %s\n", song.getArtistName(),
                            song.getTitle());
                }
                return "";
            }

            public String getHelp() {
                return "generates a basic artist radio playlist";
            }
        });


        shell.add("wiki_bio_cov", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<Artist> artists = en.topHotArtists(1000);
                int count = 0;
                int which = 0;
                for (Artist artist : artists) {
                    which++;
                    List<Biography> bios = artist.getBiographies(0, 100,
                            "cc-by-sa");
                    if (bios.size() == 0) {
                        count++;
                        System.out.printf("%d %d %s\n", which, count, artist
                                .getName());
                    }
                }
                System.out.println("Missing coverage count " + count);
                return "";
            }

            public String getHelp() {
                return "shows artist wiki bio coverage";
            }
        });

        shell.add("trace", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    en.setTraceSends(args[1].equals("true"));
                    en.setTraceRecvs(args[1].equals("true"));

                } else {
                    System.out.println("Usage: trace true|false");
                }
                return "";
            }

            public String getHelp() {
                return "enables/disables trace";
            }
        });

        shell.add("traceSends", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    en.setTraceSends(args[1].equals("true"));
                } else {
                    System.out.println("Usage: traceSends true|false");
                }
                return "";
            }

            public String getHelp() {
                return "enables/disables trace sends";
            }
        });

        shell.add("traceRecvs", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                if (args.length == 2) {
                    en.setTraceRecvs(args[1].equals("true"));
                } else {
                    System.out.println("Usage: traceRecvs true|false");
                }
                return "";
            }

            public String getHelp() {
                return "enables/disables trace recvs";
            }
        });

        shell.add("ya_check", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                int count = 10;
                String artistName = "justin bieber";

                if (args.length > 1) {
                    count = Integer.parseInt(args[1]);
                }

                if (args.length > 2) {
                    artistName = Shell.mash(args, 2);
                }

                Artist curSeed = getArtist(artistName);

                List<Artist> queue = new ArrayList<Artist>();
                Set<String> visited = new HashSet<String>();

                queue.add(curSeed);

                while (queue.size() > 0) {
                    Artist artist = queue.remove(0);
                    if (!visited.contains(artist.getID())) {
                        visited.add(artist.getID());
                        if (visited.size() > count) {
                            break;
                        }
                        ArtistParams p = new ArtistParams();
                        p.includeYearsActive();
                        p.setID(artist.getID());
                        for (Artist sim : en.getSimilarArtists(p)) {
                            yaCheck(sim);
                            queue.add(sim);
                        }
                    }
                }
                return "";
            }

            public String getHelp() {
                return "check years active";
            }
        });

        shell.add("aCatList", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<ArtistCatalog> lac = en.listArtistCatalogs();
                int which = 0;
                for (ArtistCatalog ac : lac) {
                    System.out.printf("%d %s %s\n", ++which, ac.getID(), ac.getName());
                }
                return "";
            }

            public String getHelp() {
                return "lists artists catalogs";
            }
        });

        shell.add("aCatName", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                ArtistCatalog ac = en.getArtistCatalogByName(args[1]);
                if (ac != null) {
                    System.out.printf("%s %s\n", ac.getID(), ac.getName());
                }
                return "";
            }

            public String getHelp() {
                return "get an artist catalog by name";
            }
        });

        shell.add("aCatID", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                ArtistCatalog ac = en.getArtistCatalogByID(args[1]);
                if (ac != null) {
                    System.out.printf("%s %s\n", ac.getID(), ac.getName());
                }
                return "";
            }

            public String getHelp() {
                return "get an artist catalog by id";
            }
        });


        shell.add("sCatName", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongCatalog ac = en.getSongCatalogByName(args[1]);
                if (ac != null) {
                    System.out.printf("%s %s\n", ac.getID(), ac.getName());
                }
                return "";
            }

            public String getHelp() {
                return "get an song catalog by name";
            }
        });

        shell.add("sCatID", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                SongCatalog ac = en.getSongCatalogByID(args[1]);
                if (ac != null) {
                    System.out.printf("%s %s\n", ac.getID(), ac.getName());
                    for (SongCatalogItem sci : ac.read(0, 30)) {
                        System.out.println(sci);
                    }
                }
                return "";
            }

            public String getHelp() {
                return "get an song catalog by id";
            }
        });



        shell.add("aCatCreate", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                en.createArtistCatalog(args[1]);
                return "";
            }

            public String getHelp() {
                return "creates an artist catalog";
            }
        });

        shell.add("sCatList", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                List<SongCatalog> lsc = en.listSongCatalogs();
                for (SongCatalog sc : lsc) {
                    System.out.printf("%s %s\n", sc.getID(), sc.getName());
                }
                return "";
            }

            public String getHelp() {
                return "lists song catalogs";
            }
        });

        shell.add("runTests", new ShellCommand() {
            public String execute(Shell ci, String[] args) throws Exception {
                String tests = "basic";
                int count = 100;

                if (args.length >= 2) {
                    count = Integer.parseInt(args[1]);
                }

                if (args.length >= 3) {
                    tests = args[2];
                }

                if (testHarness == null) {
                    testHarness = new TestHarness(en);
                }

                testHarness.runTests(tests, count);
                return "";
            }

            public String getHelp() {
                return "Usage: runTests  [count] [testset]";
            }
        });

    }

    private Artist getArtist(String name) throws EchoNestException {
        Artist artist = null;

        artist = artistCache.get(name);
        if (artist == null) {
            List<Artist> artists = en.searchArtists(name);
            if (artists.size() > 0) {
                artist = artists.get(0);
                artistCache.put(name, artist);
            }
        }
        return artist;
    }

    private void dumpDocs(String title, List<? extends WebDocument> list) {
        System.out.println(title);
        for (WebDocument d : list) {
            d.dump();
        }
        System.out.println("Total: " + list.size());
        System.out.println();
    }

    private void dumpTrack(Track track, boolean full) throws EchoNestException {
        if (track != null) {
            System.out.println(" ---- track ----");
            System.out.println("id      : " + track.getID());
            System.out.println("status  : " + track.getStatus());
            System.out.println("analysis: " + track.getAnalysisURL());

            TrackAnalysis analysis = track.getAnalysis();
            if (full && analysis != null) {
                analysis.dump();
            }
        }
    }
    private int yaTries;
    private int yaFound;
    private int yaErrors;
    private Set<String> checked = new HashSet<String>();

    private void yaCheck(Artist artist) throws EchoNestException {

        if (checked.contains(artist.getID())) {
            return;
        }

        checked.add(artist.getID());

        YearsActive ya = artist.getYearsActive();
        String msg = "";

        yaTries++;

        if (ya.size() > 0) {
            yaFound++;
            int start = ya.getRange(0)[0].intValue();
            int end = ya.getRange(ya.size() - 1)[1] == null ? 2011 : ya
                    .getRange(ya.size() - 1)[1].intValue();

            if (start < 1880L) {
                msg += " too early";
            }

            if (start > 2011L) {
                msg += " too late";
            }

            if (end > 2011) {
                msg += " end too late";
            }

            if (end - start > 70) {
                msg += " probably too long";
            }

            if (start > end) {
                msg += " memento artist";
            }

            if (ya.size() > 5) {
                msg += " many splits";
            }

            for (int i = 0; i < ya.size() - 1; i++) {
                int tstart = ya.getRange(i)[0].intValue();
                int tend = ya.getRange(i)[1] == null ? 2011 : ya.getRange(i)[1]
                        .intValue();
                int nstart = ya.getRange(i + 1)[0].intValue();

                if (tstart >= nstart) {
                    msg += " range overlap";
                }

                if (tend >= nstart) {
                    msg += " range overlap";
                }

                if (tend < start) {
                    msg += " bad single range";
                }
            }

        } else {
            msg += " missing years active";
        }

        if (msg.length() > 0) {
            System.out.printf(" %d %d %d %s %s\n", yaTries, yaFound, yaErrors,
                    artist.getName(), msg);
        }
        if (msg.length() > 0) {
            ya.dump();
        }
    }

    private float scoreArtist(Artist artist) throws EchoNestException {
        int sum = artist.getAudio().size() + artist.getBiographies().size()
                + artist.getBlogs().size() + artist.getNews().size()
                + artist.getReviews().size() + artist.getImages().size()
                + artist.getVideos().size() + artist.getUrls().size();

        return sum * 100f / 120f;
    }

    private void populateParams(Params p, String[] args) {
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            String[] pv = arg.split("=");
            if (pv.length == 2) {
                String value = pv[1].replace('+', ' ');
                p.add(pv[0], value);
            } else {
                System.out.println("Don't understand " + arg);
            }
        }
    }

    public static void main(String[] args) {
        try {
            EchonestDevShell shell = new EchonestDevShell();
            shell.go();
        } catch (EchoNestException e) {
            System.err.println("Can't connect to the echonest");
        }
    }
}
