package com.ryce.frugalist.model;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Tony on 2016-02-07.
 *
 * Base class for a product listing
 */
public abstract class AbstractListing {

    private UUID uuid;
    private Long id;
    private String authorId;
    private Date created;
    private String product;
    private String imageUrl;
    private String thumbnailUrl;
    private String address;
    private float longitude;
    private float latitude;

    public AbstractListing(Long id, String authorId, Date created, String product, String address, float latitude, float longitude, String imageUrl) {
        this.id = id;
        this.authorId = authorId;
        this.created = created;
        this.product = product;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        // insert 's' after image ID to get Imgur thumbnail
        thumbnailUrl = new StringBuilder(imageUrl).insert(imageUrl.lastIndexOf("."), "s").toString();
    }

    public AbstractListing(String imageUrl, String product, String address) {
        uuid = UUID.randomUUID();
        this.imageUrl = imageUrl;
        this.product = product;
        this.address = address;
        // insert 's' after image ID to get Imgur thumbnail
        thumbnailUrl = new StringBuilder(imageUrl).insert(imageUrl.lastIndexOf("."), "s").toString();
    }

    public String getAddress() {
        return address;
    }

    public Long getId() {
        return id;
    }

    public String getAuthorId() {
        return authorId;
    }

    public Date getCreated() {
        return created;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getProduct() {
        return product;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public UUID getUuid() {
        return uuid;
    }

}
