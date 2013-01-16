/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4;

/**
 *
 * @author plamere
 */
public class DynamicPlaylistParams extends PlaylistParams {

    /**
     * Set the maximum number of songs to return in the playlist
     *
     * @param results the results
     */
    @Override
    public void setResults(int results) {
        throw new UnsupportedOperationException("setResults not supported on dynamic playlist create");
    }

    /**
     * The IDs of catalogs that should be updated with session information
     * (plays, skips, ratings,bans, favorites, etc). Multiple session catalogs
     * (up to 5) can be listed and all will be updated with the same
     * information. The session catalogs must have been previously created using
     * the same API key as used in this call.
     *
     * @param id the session catalog to add
     */
    public void addSessionCatalog(String id) {
        super.add("session_id", id);
    }
}
