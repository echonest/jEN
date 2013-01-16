package com.echonest.api.v4;

import java.util.Date;
import java.util.Map;

public class Video extends WebDocument {
	
	@SuppressWarnings("unchecked")
	Video(Map map) {
		super("video", map);
	}
	
    /**
     * Gets the originating site of the video
     * @return the site
     */
    public String getSite() {
        return getString("site");
    }

    /**
     * Gets the title of the video
     * @return the title
     */
    public String getTitle() {
        return getString("title");
    }

    /**
     * Gets the URL of the video
     * @return the URL
     */
    public String getURL() {
        return getString("url");
    }


    /**
     * Gets a thumbnail image of the video
     * @return a URL to the image
     */
    public String getImageURL() {
        return getString("image_url");
    }

    /**
     * Gets the date the image was found
     * @return the date found
     */
    public Date getDateFound() {
        return getDate("date_found");
    }

}
