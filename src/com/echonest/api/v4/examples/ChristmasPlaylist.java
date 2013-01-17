/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.examples;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;
import com.echonest.api.v4.Song.SongType;

/**
 *
 * @author plamere
 */
public class ChristmasPlaylist {
    
        public static void main(String[] args) throws EchoNestException {
        EchoNestAPI en = new EchoNestAPI();

        PlaylistParams params = new PlaylistParams();
        params.setType(PlaylistParams.PlaylistType.ARTIST_RADIO);
        params.addArtist("Bing Crosby");
        params.addSongType(SongType.christmas, Song.SongTypeFlag.True);
        Playlist playlist = en.createStaticPlaylist(params);
        for (Song song : playlist.getSongs()) {
            System.out.println(song.getTitle() + " by " + song.getArtistName());
        }
    }
    
}
