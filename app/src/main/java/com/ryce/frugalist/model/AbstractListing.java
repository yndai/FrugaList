package com.ryce.frugalist.model;

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
    private String thumbnailUrl;
    private String address;
    private float longitude;
    private float latitude;

    public AbstractListing(String imageUrl, String product, String address) {
        id = UUID.randomUUID();
        this.imageUrl = imageUrl;
        this.product = product;
        this.address = address;
        // insert 's' after image ID to get Imgur thumbnail
        thumbnailUrl = new StringBuilder(imageUrl).insert(imageUrl.lastIndexOf("."), "s").toString();
    }

    public UUID getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getProduct() {
        return product;
    }



}
