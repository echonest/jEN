/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.echonest.api.v4.examples;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.EchoNestException;
import com.echonest.api.v4.TimedEvent;
import com.echonest.api.v4.Track;
import com.echonest.api.v4.TrackAnalysis;
import java.io.File;
import java.io.IOException;

/**
 * This example will demonstrate uploading an MP3, analyzing it and getting the
 * audio summary and the timing info for all of the beats
 *
 * @author plamere
 */
public class TrackAnalysisExample {

    public static void main(String[] args) throws EchoNestException {
        EchoNestAPI en = new EchoNestAPI();

        String path = "music/dizzy.mp3";

        if (args.length > 2) {
            path = args[1];
        }

        File file = new File(path);

        if (!file.exists()) {
            System.err.println("Can't find " + path);
        } else {

            try {
                Track track = en.uploadTrack(file);
                track.waitForAnalysis(30000);
                if (track.getStatus() == Track.AnalysisStatus.COMPLETE) {
                    System.out.println("Tempo: " + track.getTempo());
                    System.out.println("Danceability: " + track.getDanceability());
                    System.out.println("Speechiness: " + track.getSpeechiness());
                    System.out.println("Liveness: " + track.getLiveness());
                    System.out.println("Energy: " + track.getEnergy());
                    System.out.println("Loudness: " + track.getLoudness());
                    System.out.println();
                    System.out.println("Beat start times:");
                    
                    TrackAnalysis analysis = track.getAnalysis();
                    for (TimedEvent beat : analysis.getBeats()) {
                        System.out.println("beat " + beat.getStart());
                    }
                } else {
                    System.err.println("Trouble analysing track " + track.getStatus());
                }
            } catch (IOException e) {
                System.err.println("Trouble uploading file");
            }

        }
    }
}
