package com.echonest.api.v4;

import java.util.Date;
import java.util.Map;

public class News extends WebDocument {

	 @SuppressWarnings("unchecked")
	News(Map map) {
		super("news", map);
	}
	 
    /**
     * Gets the news article title 
     * @return title
     */
    public String getName() {
        return getString("name");
    }

    /**
     * Gets the URL of the item
     * @return the URL
     */
    public String getURL() {
        return getString("url");
    }

    /**
     * Gets a summary of the item
     * @return the summary
     */
    public String getSummary() {
        return getString("summary");
    }

    /**
     * Gets the date found
     * @return the date found
     */
    public Date getDateFound() {
        return getDate("date_found");
    }

    /**
     * Gets the date posted
     * @return the date posted
     */
    public Date getDatePosted() {
        return getDate("date_posted");
    }
}
