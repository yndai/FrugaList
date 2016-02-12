package com.ryce.frugalist.model;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Created by Tony on 2016-02-06.
 *
 * Presentation layer deal model
 */
public class Deal extends AbstractListing {

    private String price;
    private String unit;
    private String store;
    private Integer rating;

    public Deal(Bitmap image, String imageUrl, String price, String product, Integer rating, String unit, String store) {
        super(image, imageUrl, product);
        this.price = price;
        this.rating = rating;
        this.unit = unit;
        this.store = store;
    }

    public String getPrice() {
        return price;
    }

    public Integer getRating() {
        return rating;
    }

    public String getUnit() {
        return unit;
    }

    public String getStore() {
        return store;
    }

    // convenience methods

    public String getFormattedPrice() {
        return "$" + price + "/" + unit;
    }

    public String getFormattedRating() {
        return (rating >= 0 ? "+" : "-") + rating;
    }

    public int getRatingColour() {
        return (rating >= 0 ? Color.GREEN : Color.RED);
    }

}
