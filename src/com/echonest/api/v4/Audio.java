package com.echonest.api.v4;

import java.util.Date;
import java.util.Map;

public class Audio extends WebDocument {

	@SuppressWarnings("unchecked")
	Audio(Map map) {
		super("audio", map);
	}
	
	   /**
     * Gets the artist name
     * @return the artist name
     */
    public String getArtistName() {
        return getString("artist");
    }
        

    /**
     * Gets release (album) information
     * @return the release name
     */
    public String getRelease() {
        return getString("release");
    }

    /**
     * Gets the audio title 
     * @return the title
     */
    public String getTitle() {
        return getString("title");
    }

    /**
     * Gets the audio link
     * @return the link
     */
    public String getLink() {
        return getString("link");
    }

    /**
     * Gets the date of the audio
     * @return the date
     */
    public Date getDate() {
        return getDate("date");
    }
    
    /**
     * Gets the URL of the audio
     * @return the URL
     */
    public String getURL() {
        return getString("url");
    }

    /**
     * Gets the length (in seconds) of the audio
     * @return the length of the audio
     */
    public double getLength() {
        return getDouble("length");
    }


}
