/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.examples;

import com.echonest.api.v4.Artist;
import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.YearsActive;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This example shows how to use the Echo Nest API to sort the top 100
 * artists in order of increasing start year.
 *
 * @author plamere
 */
public class OldestTopArtist {

    public static void main(String[] args) throws EchoNestException {
        EchoNestAPI en = new EchoNestAPI();

        List<Artist> artists = en.topHotArtists(100);

        Collections.sort(artists,
                new Comparator<Artist>() {
                    @Override
                    public int compare(Artist t1, Artist t2) {
                        try {
                            Long y1 = t1.getYearsActive().getRange()[0];
                            Long y2 = t2.getYearsActive().getRange()[0];
                            int i1 = y1 != null ? y1.intValue() : 0;
                            int i2 = y2 != null ? y2.intValue() : 0;
                            return i1 - i2;
                        } catch (EchoNestException e) {
                            return 0;
                        }
                    }
                });

        for (Artist artist : artists) {
            YearsActive ya = artist.getYearsActive();
            Long earliest = ya.getRange()[0];
            System.out.println(earliest + " " + artist.getName());
        }

    }
}
