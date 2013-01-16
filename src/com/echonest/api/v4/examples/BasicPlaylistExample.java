/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.examples;

import com.echonest.api.v4.BasicPlaylistParams;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.Song;

/**
 *
 * @author plamere
 */
public class BasicPlaylistExample {

    public static void main(String[] args) throws EchoNestException {
        EchoNestAPI en = new EchoNestAPI();

        BasicPlaylistParams params = new BasicPlaylistParams();
        params.addArtist("Weezer");
        params.setType(BasicPlaylistParams.PlaylistType.ARTIST_RADIO);
        params.setResults(10);
        Playlist playlist = en.createBasicPlaylist(params);

        for (Song song : playlist.getSongs()) {
            System.out.println(song.toString());
        }
    }
}
