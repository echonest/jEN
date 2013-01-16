# jEN 

jEN is an open source Java library for the Echo Nest API. With jEN you have Java
access to the entire set of API methods including:

 **artist** - search for artists by name, description, or attribute, and get
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


This is a Java client API and assorted tools and helpers for the Echo Nest API (at developer.echonest.com). This
client works with Version 4 of the Echo Nest API.

Note - if you want to work with Version 3.0 of the Echo Nest API, you should use Version 3 of the Echo Nest Java
API


## Artist Methods
Some of the Echo Nest artist capabilities:

 * get a list of the hottest artists
 * search for artists
 * get news about an artist
 * get reviews about an artist
 * get blogs about an artist
 * get urls for an artist
 * get video for an artist
 * find audio for an artist
 * find familiarity and hotttness of an artist

## Song methods
Some of the Echo Nest track capabilities:

Search for songs
Get info about a song including:
audio summary
audio
a detailed audio analysis
song hotttnesss
artist hotttnesss
artist familiarity
artist location
Find similar songs

# Getting Started
 * Download the latest release
 * Unzip the archive
 * Add the jEN.jar file to your CLASSPATH
 * Browse some of the code samples
 * Get an API key from developer.echonest.com
 * Browse the Javadocs included with the distribution

# Code Examples
Browse some of the code samples

# Building the jar file

  % ant jar

# Running a test shell

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
