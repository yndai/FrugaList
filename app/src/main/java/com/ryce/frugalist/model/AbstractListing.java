package com.ryce.frugalist.model;

import android.graphics.Bitmap;

import java.util.UUID;

/**
 * Created by Tony on 2016-02-07.
 *
 * Base class for a product listing
 */
public abstract class AbstractListing {

    private UUID id;
    private String product;
    private String imageUrl;
    private String address;
    private double longitude;
    private double latitude;

    // TODO: these are currently unused!
    // cached image
    private Bitmap image;
    // cached thumbnail
    private Bitmap scaledImage;

    public AbstractListing(String imageUrl, String product, String address) {
        id = UUID.randomUUID();
        this.imageUrl = imageUrl;
        this.product = product;
        this.address = address;
    }

    public void setImage(Bitmap image, int width, int height) {
        this.image = image;
        // cache a thumbnail
        scaledImage = Bitmap.createScaledBitmap(image, width, height, false);
    }

    public UUID getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getProduct() {
        return product;
    }

    public Bitmap getThumbnail() {
        return scaledImage;
    }

}
