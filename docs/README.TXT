The Echo Nest Java API
----------------------
For Echo Nest Version 4

This is a Java client for the Echo Nest Java API.  To use the API 
get a developer key from developer.echonest.com.

    http://developer.echonest.com/

License:

    New BSD License - http://www.opensource.org/licenses/bsd-license.php


Quick Start
-------------
Here's some sample code that shows how to get similar artists
with the API:

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


The Echo Nest Shell
------------------
Included with the API is a command line interface to the Echo Nest
API. You can use this shell to interact with the Echo Nest.  To run
the shell invoke the 'enshell' script, or run the command:

java -DECHO_NEST_API_KEY=$MY_ECHO_NEST_KEY -jar lib/jEN-xx.jar 

Type 'help' to show the list of available commands.  Try some:

    nest%  get_similar weezer
    nest%  get_fam led zeppelin


Revision History
----------------
Version 0.9 - April 21, 2010
Version 1.1 - May 13, 2010
   - support for Echo Nest API version 4.1
   - Added support for artist search with constraints
   - Addes support for top terms
