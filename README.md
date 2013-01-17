# jEN 

jEN is an open source Java library for the [Echo Nest API](http://developer.echonest.com/docs/v4/). 

# Quick Start

  * Download the [latest distribution](http://static.echonest.com.s3.amazonaws.com/jEN/files/jEN-latest.zip)
  * Read the extensive [Javadocs for jEN](http://static.echonest.com.s3.amazonaws.com/jEN/javadoc/index.html)
  * Browse the [examples](https://github.com/echonest/jEN/tree/master/src/com/echonest/api/v4/examples)

# What is it?

* jEN is an open source Java library for the Echo Nest API. With jEN you have Java
access to the entire set of API methods including:

* **artist** - search for artists by name, description, or attribute, and get
  back detailed information about any artist including audio, similar artists,
  blogs, familiarity, hotttnesss, news, reviews, urls and video.
* **song** - search songs by artist, title, description, or attribute (tempo,
  duration, etc) and get detailed information back about each song, such as
  hotttnesss, audio summary, or tracks.
* **track** - upload a track to the Echo Nest and receive summary information
  about the track including key, duration, mode, tempo, time signature along
  with detailed track info including timbre, pitch, rhythm and loudness
  information.
* **playlists** - create personalized playlists based on a wide range of parameters
* **taste profiles** - provide for personalized recommendation and playlisting

This is a Java client API and assorted tools and helpers for the Echo Nest API (at developer.echonest.com). This
client works with Version 4 of the Echo Nest API.

## Artist Methods
Some of the Echo Nest artist capabilities:

 * search for artists
 * get familiarity and hotttness of an artist
 * get a list of the hottest artists
 * get news about an artist
 * get bios for an artist
 * get reviews about an artist
 * get blogs about an artist
 * get terms about an artist
 * get similar artists
 * get genre info about an artist
 * get artist location info about an artist
 * get years active info about an artist
 * get urls for an artist
 * get video for an artist
 * get images for an artist
 * get artist ids for other services such as Facebook, Twitter, Rdio, Spotify, Rhapsody and many more

## Song methods
Some of the Echo Nest track capabilities:

 * search for songs
 * get info about a song including:
    * tempo
    * energy
    * danceability
    * loudness
    * liveness
    * speechiness
 * detailed audio analysis
 * song hotttnesss
 * artist hotttnesss
 * artist familiarity
 * artist location

## Playlist Methods
 * create Pandora-style artist radio playlists
 * create Genre Radio Playlists
 * create dynamic playlists that respond to the users explicit and implicit feedback

## Taste Profile Methods
 * create, read, update and delete taste info for a particular user
 * use a taste profile to seed playlists to give peronalized playlists

# Getting Started
 * download the latest release
 * unzip the archive
 * add the jEN.jar file to your CLASSPATH
 * get an API key from [developer.echonest.com](http://developer.echonest.com)
 * Browse the Javadocs included with the distribution

## Quick Start
Here's some sample code that shows how to get similar artists with the API:

    public static void main(String[] args) throws EchoNestException {
        EchoNestAPI echoNest = new EchoNestAPI(API_KEY);
        List<Artist> artists = echoNest.searchArtists("Weezer");

        if (artists.size() > 0) {
            Artist weezer = artists.get(0);
            System.out.println("Similar artists for " + weezer.getName());
            for (Artist simArtist : weezer.getSimilar(10)) {
                System.out.println("   " + simArtist.getName());
            }
        }
    }



# Code Examples
Browse some of the code [examples](https://github.com/echonest/jEN/tree/master/src/com/echonest/api/v4/examples)

# Building the jar file
Grab the source, cd to the top leve directory and issue:

    % ant jar

# Running a test shell

Included with the API is a command line interface to the Echo Nest
API. You can use this shell to interact with the Echo Nest.  To run
the shell invoke the 'enshell' script, or run the command:

java -DECHO_NEST_API_KEY=$MY_ECHO_NEST_KEY -jar jEN.jar 

Type 'help' to show the list of available commands.  Try some:

    % scripts/enshell
      Welcome to The Echo Nest API Shell
      type 'help' 

    nest% get_similar weezer

    Similarity for Weezer
      Rivers Cuomo
      The Rentals
      Fountains of Wayne
      Jimmy Eat World
      Phantom Planet
      The Presidents of the United States of America
      Cake
      Harvey Danger
      The All-American Rejects
      Ben Folds Five
      Nerf Herder
      Motion City Soundtrack
      New Found Glory
      Superdrag
      Foo Fighters

    nest% splaylist Muse

        Muse Time = Is Running Out
        Coldplay = Viva La Vida
        Travis = Sing
        The Killers = Here With Me
        The Subways = I Want To Hear What You Have Got To Say
        Bloc Party = Banquet
        Keane = Is It Any Wonder?
        The Cooper Temple Clause = Talking To A Brick Wall
        Richard Ashcroft = Check The Meaning
        Athlete Wires = (Album Version)

    nest% 

# Release History

  * Jan 18, 2013 [jEN-4.x.t.zip](http://static.echonest.com.s3.amazonaws.com/jEN/files/jEN-4.x.t.zip)
      * Added more examples
      * Fixed throttle code on API error
      * Improved deploy
  * Jan 16, 2013 [jEN-4.x.s.zip](http://static.echonest.com.s3.amazonaws.com/jEN/files/jEN-4.x.s.zip)
