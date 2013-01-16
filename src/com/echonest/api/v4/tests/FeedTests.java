package com.echonest.api.v4.tests;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.util.Commander;

@RunWith(JUnit4.class)
public class FeedTests extends TestCase {

    // these are minimal tests for feed.  Better than nothing, but just barely.
    //
    
    private static Commander cmd;
    private static String[] artists = { "weezer", "muse", "the+beatles", "modest+mouse", "bjork", "bonerama",
                    "lady+gaga","bj%C3%B6rk", "led+zeppelin", "taylor+swift"};
    
    @BeforeClass
    public static void setUpClass()  {
        cmd = new Commander("test");
        cmd.setTraceSends(false);
        cmd.setTraceRecvs(false);
        
    }

    @AfterClass
    public static void tearDownClass() throws EchoNestException {
    }
    
    @Test
    public void testAudioFeed() throws EchoNestException, IOException {
        for (String artist : artists) {
            testAudioFeed(artist);
        }
    }
    

    public void testAudioFeed(String artist) throws EchoNestException, IOException {
        String command = "/artist/" + norm(artist) + "/audio.rss";
        String url = cmd.buildFeedUrl(command);
        String results = cmd.getStringResults(url, false, null);
        testRSS(results);
        
        String atomCommand = "/artist/" + norm(artist) + "/audio.atom";
        String atomurl = cmd.buildFeedUrl(atomCommand);
        String atomresults = cmd.getStringResults(atomurl, false, null);
        testAtom(atomresults);
        
        String xspfCommand = "/artist/" + norm(artist) + "/audio.xspf";
        String xspfurl = cmd.buildFeedUrl(xspfCommand);
        String xspfresults = cmd.getStringResults(xspfurl, false, null);
        testXspf(xspfresults);
    }
    
    
    @Test
    public void testVideoFeed() throws EchoNestException, IOException {
        for (String artist : artists) {
            testVideoFeed(artist);
        }
    }
    
    
    public void testVideoFeed(String artist) throws EchoNestException, IOException {
        String command = "/artist/" + norm(artist) + "/video.rss";

        String url = cmd.buildFeedUrl(command);
        String results = cmd.getStringResults(url, false, null);
        testRSS(results);
        
        String atomCommand = "/artist/" + norm(artist) + "/video.atom";
        String atomurl = cmd.buildFeedUrl(atomCommand);
        String atomresults = cmd.getStringResults(atomurl, false, null);
        testAtom(atomresults);
    }
    
    @Test
    public void testNewsFeed() throws EchoNestException, IOException {
        for (String artist : artists) {
            testNewsFeed(artist);
        }
    }
    
    
    public void testNewsFeed(String artist) throws EchoNestException, IOException {
        String command = "/artist/" + norm(artist) + "/news.rss";

        String url = cmd.buildFeedUrl(command);
        String results = cmd.getStringResults(url, false, null);
        testRSS(results);
        
        String atomCommand = "/artist/" + norm(artist) + "/news.atom";
        String atomurl = cmd.buildFeedUrl(atomCommand);
        String atomresults = cmd.getStringResults(atomurl, false, null);
        testAtom(atomresults);
    }
    
    @Test
    public void testReviewsFeed() throws EchoNestException, IOException {
        for (String artist : artists) {
            testReviewsFeed(artist);
        }
    }
    
    @Test
    public void bigFeedTest() throws EchoNestException, IOException {
        EchoNestAPI en = new EchoNestAPI();

        List<Artist> artists = en.topHotArtists(50);
        
        for (Artist artist : artists) {
            String nname = URLEncoder.encode(artist.getName(), "UTF-8");
            testNewsFeed(nname);
            testReviewsFeed(nname);
            testBlogFeed(nname);
            testAudioFeed(nname);
        }
    }
    
    
    public void testReviewsFeed(String artist) throws EchoNestException, IOException {
        String command = "/artist/" + norm(artist) + "/reviews.rss";

        String url = cmd.buildFeedUrl(command);
        String results = cmd.getStringResults(url, false, null);
        testRSS(results);
        
        String atomCommand = "/artist/" + norm(artist) + "/reviews.atom";
        String atomurl = cmd.buildFeedUrl(atomCommand);
        String atomresults = cmd.getStringResults(atomurl, false, null);
        testAtom(atomresults);
    }
    
    @Test
    public void testBlogFeed() throws EchoNestException, IOException {
        for (String artist : artists) {
            testBlogFeed(artist);
        }
    }
    
    
    public void testBlogFeed(String artist) throws EchoNestException, IOException {
        String command = "/artist/" + norm(artist) + "/blogs.rss";

        String url = cmd.buildFeedUrl(command);
        String results = cmd.getStringResults(url, false, null);
        testRSS(results);
        
        String atomCommand = "/artist/" + norm(artist) + "/blogs.atom";
        String atomurl = cmd.buildFeedUrl(atomCommand);
        String atomresults = cmd.getStringResults(atomurl, false, null);
        testAtom(atomresults);
    }
    
    
    private void  testRSS(String results) {
        assertTrue("got back results", results.length() >  100);
        assertTrue("is xml", results.startsWith("<?xml version="));
        assertTrue("is rss", results.contains("<rss version"));
    }
    
    private void  testAtom(String results) {
        assertTrue("got back results", results.length() >  100);
        assertTrue("is xml", results.startsWith("<?xml version="));
        assertTrue("is atom", results.contains("http://www.w3.org/2005/Atom"));
    }
    
    private void  testXspf(String results) {
        assertTrue("got back results", results.length() >  10);
        assertTrue("is XSPF", results.contains("<playlist version=\"1\">"));
    }
    
    private String norm(String name) {
        return name;
    }


    public static void main(String[] args) throws Exception {
    }
}