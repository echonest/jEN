package com.echonest.api.v4;

import java.util.Date;
import java.util.Map;

public class Blog extends WebDocument {

	@SuppressWarnings("unchecked")
	Blog(Map map) {
		super("blog", map);
	}
	
    /**
     * Gets the blog name
     * @return the blog name
     */
    public String getName() {
        return getString("name");
    }

    /**
     * Gets the blog url
     * @return the url
     */
    public String getURL() {
        return getString("url");
    }

    /**
     * Gets the blog summary
     * @return the blog summary
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
