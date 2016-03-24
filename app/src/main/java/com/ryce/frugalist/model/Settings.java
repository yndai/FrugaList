package com.ryce.frugalist.model;

/**
 * Presentation layer user settings
 *
 * Created by Tony on 2016-03-23.
 */
public class Settings {

    public static final Integer RADIUS_DEFAULT = 5;
    public static final Integer RATING_DEFAULT = -5;
    public static final Integer QUALITY_DEFAULT = 50;

    public static final Integer RADIUS_MAX = 20;
    public static final Integer RADIUS_MIN = 1;

    public static final Integer RATING_MAX = 5;
    public static final Integer RATING_MIN = -5;

    public static final Integer QUALITY_MAX = 100;
    public static final Integer QUALITY_MIN = 20;

    private Integer searchRadius;
    private Integer ratingThreshold;
    private Integer uploadQuality;

    public Settings(Integer searchRadius, Integer ratingThreshold, Integer uploadQuality) {
        this.searchRadius = searchRadius;
        this.ratingThreshold = ratingThreshold;
        this.uploadQuality = uploadQuality;
    }

    public static Settings createDefaultSettings() {
        return new Settings(
                RADIUS_DEFAULT,
                RATING_DEFAULT,
                QUALITY_DEFAULT
        );
    }

    public Integer getRatingThreshold() {
        return ratingThreshold;
    }

    public void setRatingThreshold(Integer ratingThreshold) {
        if (ratingThreshold <= RATING_MAX && ratingThreshold >= RATING_MIN) {
            this.ratingThreshold = ratingThreshold;
        } else {
            throw new IllegalArgumentException("Invalid rating threshold: " + ratingThreshold);
        }
    }

    public Integer getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(Integer searchRadius) {
        if (searchRadius <= RADIUS_MAX && searchRadius >= RADIUS_MIN) {
            this.searchRadius = searchRadius;
        } else {
            throw new IllegalArgumentException("Invalid radius: " + ratingThreshold);
        }
    }

    public Integer getUploadQuality() {
        return uploadQuality;
    }

    public void setUploadQuality(Integer uploadQuality) {
        if (uploadQuality <= QUALITY_MAX && uploadQuality >= QUALITY_MIN) {
            this.uploadQuality = uploadQuality;
        } else {
            throw new IllegalArgumentException("Invalid upload quality: " + uploadQuality);
        }
    }

    /****************************************
     * CONVENIENCE METHODS
     ****************************************/

    public String getFormattedSearchRadius() {
        return searchRadius + " KM";
    }

    public String getFormattedRatingThreshold() {
        return (ratingThreshold < 0 ? "" : "+") + ratingThreshold;
    }

    public String getFormattedUploadQuality() {
        return uploadQuality + "%";
    }

}
