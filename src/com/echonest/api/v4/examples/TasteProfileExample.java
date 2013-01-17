/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.examples;

import com.echonest.api.v4.ArtistCatalog;
import com.echonest.api.v4.ArtistCatalogItem;
import com.echonest.api.v4.CatalogUpdater;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.Playlist;
import com.echonest.api.v4.PlaylistParams;
import com.echonest.api.v4.Song;

/**
 *
 * @author plamere
 */
public class TasteProfileExample {

    private EchoNestAPI en;

    public TasteProfileExample() throws EchoNestException {
        en = new EchoNestAPI();
    }

    public boolean addArtists(ArtistCatalog tp, String[] names) throws EchoNestException {
        CatalogUpdater updater = new CatalogUpdater();
        int id = 1;

        for (String name : names) {
            ArtistCatalogItem item = new ArtistCatalogItem("id-" + id);
            item.setArtistName(name);
            updater.update(item);
            id++;
        }
        String ticket = tp.update(updater);
        return tp.waitForUpdates(ticket, 30000);
    }

    public ArtistCatalog createTasteProfile(String name) throws EchoNestException {
        System.out.println("Creating Taste Profile " + name);
        ArtistCatalog tp = en.createArtistCatalog(name);
        String[] artists = {"weezer", "the beatles", "ben folds", "explosions in the sky",
            "this will destroy you", "muse", "bjork"};
        addArtists(tp, artists);
        return tp;
    }

    public ArtistCatalog findTasteProfile(String name) throws EchoNestException {
        for (ArtistCatalog ac : en.listArtistCatalogs()) {
            if (ac.getName().equals(name)) {
                return ac;
            }
        }
        return null;
    }

    public void createPlaylist(ArtistCatalog tp) throws EchoNestException {
        PlaylistParams p = new PlaylistParams();
        p.setType(PlaylistParams.PlaylistType.CATALOG_RADIO);
        p.addSeedCatalog(tp.getID());
        Playlist playlist = en.createStaticPlaylist(p);

        for (Song song : playlist.getSongs()) {
            System.out.println(song.getArtistName() + " " + song.getTitle());
        }
    }

    public static void main(String[] args) throws EchoNestException {
        String tpName = "Some of my Favorite Artists";
        TasteProfileExample tpe = new TasteProfileExample();

        ArtistCatalog tp = tpe.findTasteProfile(tpName);
        if (tp == null) {
            tp = tpe.createTasteProfile(tpName);
        }
        tpe.createPlaylist(tp);
    }
}
