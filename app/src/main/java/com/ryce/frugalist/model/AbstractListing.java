package com.ryce.frugalist.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Tony on 2016-02-07.
 *
 * Base class for a product listing
 */
public abstract class AbstractListing implements Parcelable {

    private Long id;
    private String authorId;
    private Date created;
    private String product;
    private String imageUrl;
    private String thumbnailUrl;
    private String address;
    private float latitude;
    private float longitude;

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

    /****************************************
     * Parcelable Implementation
     ****************************************/

    protected AbstractListing(Parcel in) {
        id = in.readLong();
        authorId = in.readString();
        created = new Date(in.readLong());
        product = in.readString();
        imageUrl = in.readString();
        thumbnailUrl = in.readString();
        address = in.readString();
        latitude = in.readFloat();
        longitude = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(authorId);
        out.writeLong(created.getTime());
        out.writeString(product);
        out.writeString(imageUrl);
        out.writeString(thumbnailUrl);
        out.writeString(address);
        out.writeFloat(latitude);
        out.writeFloat(longitude);
    }

}
