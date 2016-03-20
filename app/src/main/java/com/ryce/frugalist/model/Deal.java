package com.ryce.frugalist.model;

import android.graphics.Color;

import com.ryce.frugalist.network.FrugalistResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Tony on 2016-02-06.
 *
 * Presentation layer deal model
 */
public class Deal extends AbstractListing {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");

    private String price;
    private String unit;
    private String store;
    private Integer rating;
    private Map<String, Boolean> votes;
    private String description;

    public Deal(Long id, String product, String authorId, String imageUrl, String address,
                Float latitude, Float longitude, String price, String unit, String store,
                Integer rating, Date created, String description) {
        super(id, authorId, created, product, address, latitude, longitude, imageUrl);
        this.price = price;
        this.unit = unit;
        this.store = store;
        this.rating = rating;
        this.description = description;
    }

    public Deal(FrugalistResponse.Deal deal) {
        super(deal.id, deal.author, deal.created, deal.product, deal.address,
                deal.location.latitude, deal.location.longitude, deal.imageUrl);
        this.price = deal.price;
        this.unit = deal.unit;
        this.store = deal.store;
        this.rating = deal.rating;
        this.votes = deal.votes;
        this.description = deal.description;
    }

    public Deal(String imageUrl, String price, String product,
                Integer rating, String unit, String store, String address) {
        super(imageUrl, product, address);
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

    public Map<String, Boolean> getVotes() {
        return votes;
    }

    public String getDescription() {
        return description;
    }

    /****************************************
     * CONVENIENCE METHODS
     ****************************************/

    public String getFormattedPrice() {
        return "$" + price + "/" + unit;
    }

    public String getFormattedRating() {
        return (rating >= 0 ? "+" : "") + rating;
    }

    public int getRatingColour() {
        return (rating >= 0 ? Color.rgb(0, 150, 0) : Color.RED);
    }

    public String getFormattedDate() {
        return DATE_FORMAT.format(getCreated());
    }

    /**
     * Convenience method to create a list of view model Deal objects from a list of response deal
     * objects
     * @param resDealList
     * @return
     */
    public static List<AbstractListing> getDealListFromResponseList(FrugalistResponse.DealList resDealList) {
        List<AbstractListing> dealList = new ArrayList<>(resDealList.items.size());
        for (FrugalistResponse.Deal resDeal : resDealList.items) {
            dealList.add(new Deal(resDeal));
        }
        return dealList;
    }

}
