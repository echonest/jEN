package com.echonest.api.v4;

import java.util.Date;
import java.util.Map;

public class Review extends WebDocument {
	
	Review(Map map) {
		super("review", map);
	}
	/**
	 * Gets the title of the review
	 * 
	 * @return the title
	 */
	public String getName() {
		return getString("name");
	}

	/**
	 * Gets the review text
	 * 
	 * @return the review text
	 */
	public String getReviewText() {
		return getString("review_text");
	}

	/**
	 * Gets the name of the release
	 * 
	 * @return the name of the release
	 */
	public String getRelease() {
		return getString("release");
	}

	/**
	 * Gets the URL of the review
	 * 
	 * @return the URL
	 */
	public String getURL() {
		return getString("url");
	}

	/**
	 * Gets a summary of the review
	 * 
	 * @return a summary
	 */
	public String getSummary() {
		return getString("summary");
	}

	/**
	 * Gets an image associated with the review
	 * 
	 * @return the image url
	 */
	public String getImageURL() {
		return getString("image_url");
	}

	/**
	 * Get the date the item was found
	 * 
	 * @return the date
	 */
	public Date getDateFound() {
		return getDate("date_found");
	}

	/**
	 * Get the date the item was reviewed
	 * 
	 * @return the date
	 */
	public Date getDateReviewed() {
		return getDate("date_reviewed");
	}

}
