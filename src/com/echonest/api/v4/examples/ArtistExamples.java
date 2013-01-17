package com.echonest.api.v4.examples;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.Biography;
import com.echonest.api.v4.Blog;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Image;
import com.echonest.api.v4.News;
import com.echonest.api.v4.Params;
import com.echonest.api.v4.Review;
import com.echonest.api.v4.Video;
import java.util.Collections;
import java.util.List;

public class ArtistExamples {

    private EchoNestAPI en;
    private static boolean trace = false;

    public ArtistExamples() throws EchoNestException {
        en = new EchoNestAPI();
        en.setTraceSends(trace);
        en.setTraceRecvs(trace);
    }

    public void dumpArtist(Artist artist) throws EchoNestException {
        System.out.printf("%s\n", artist.getName());
        System.out.printf("   hottt %.3f\n", artist.getHotttnesss());
        System.out.printf("   fam   %.3f\n", artist.getFamiliarity());

        System.out.println(" =========  urls ======== ");
        for (String key : artist.getUrls().keySet()) {
            System.out.printf("   %10s %s\n", key, artist.getUrls().get(key));
        }


        System.out.println(" =========  bios ======== ");
        List<Biography> bios = artist.getBiographies();
        for (int i = 0; i < bios.size(); i++) {
            Biography bio = bios.get(i);
            bio.dump();
        }

        System.out.println(" =========  blogs ======== ");
        List<Blog> blogs = artist.getBlogs();
        for (int i = 0; i < blogs.size(); i++) {
            Blog blog = blogs.get(i);
            blog.dump();
        }

        System.out.println(" =========  images ======== ");
        List<Image> images = artist.getImages();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            image.dump();
        }

        System.out.println(" =========  news ======== ");
        List<News> newsList = artist.getNews();
        for (int i = 0; i < newsList.size(); i++) {
            News news = newsList.get(i);
            news.dump();
        }

        System.out.println(" =========  reviews ======== ");
        List<Review> reviews = artist.getReviews();
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            review.dump();
        }

        System.out.println(" =========  videos ======== ");
        List<Video> videos = artist.getVideos();
        for (int i = 0; i < videos.size(); i++) {
            Video video = videos.get(i);
            video.dump();
        }
    }

    public void searchArtistByName(String name, int results)
            throws EchoNestException {
        Params p = new Params();
        p.add("name", name);
        p.add("results", results);

        List<Artist> artists = en.searchArtists(p);
        for (Artist artist : artists) {
            dumpArtist(artist);
            System.out.println();
        }
    }

    public void randomWalk(String seedName, int count) throws EchoNestException {
        List<Artist> artists = en.searchArtists(seedName);
        if (artists.size() > 0) {
            Artist seed = artists.get(0);
            for (int i = 0; i < count; i++) {
                dumpArtist(seed);
                List<Artist> sims = seed.getSimilar(10);
                if (sims.size() > 0) {
                    Collections.shuffle(sims);
                    seed = sims.get(0);
                } else {
                    break;
                }
            }
        }
    }

    public void stats() {
        en.showStats();
    }

    public static void main(String[] args) throws EchoNestException {
        ArtistExamples sse = new ArtistExamples();
        long start = System.currentTimeMillis();
        try {
            sse.searchArtistByName("weezer", 10);
            System.out.println("Random walk");
            sse.randomWalk("weezer", 10);
        } finally {
            System.out.println("Runtime " + (System.currentTimeMillis() - start));
            sse.stats();
        }
    }
}
