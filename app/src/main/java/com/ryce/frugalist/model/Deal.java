package com.ryce.frugalist.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

import com.ryce.frugalist.network.FrugalistResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Tony on 2016-02-06.
 *
 * Presentation layer deal model
 */
public class Deal extends AbstractListing {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy", Locale.CANADA);

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
        return price + "/" + unit;
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
        List<AbstractListing> dealList;

        if (resDealList.items == null) {

            // no list was returned, means no elements found
            dealList = new ArrayList<>();

        } else {

            // convert response deals into view model deals
            dealList = new ArrayList<>(resDealList.items.size());
            for (FrugalistResponse.Deal resDeal : resDealList.items) {
                dealList.add(new Deal(resDeal));
            }

        }

        return dealList;
    }

    /****************************************
     * Parcelable Implementation
     ****************************************/

    private Deal(Parcel in) {
        super(in);
        price = in.readString();
        unit = in.readString();
        store = in.readString();
        rating = in.readInt();
        description = in.readString();

        // handle map
        int mapSize = in.readInt();
        if (mapSize != -1) {
            votes = new HashMap<>();
            for (int i = 0; i < mapSize; i++) {
                String userId = in.readString();
                Boolean vote = in.readInt() != 0;
                votes.put(userId, vote);
            }
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);

        out.writeString(price);
        out.writeString(unit);
        out.writeString(store);
        out.writeInt(rating);
        out.writeString(description);

        // write map in parts
        if (votes != null) {
            out.writeInt(votes.size());
            for (Map.Entry<String, Boolean> vote : votes.entrySet()) {
                out.writeString(vote.getKey());
                // write int as there is no write boolean
                out.writeInt(vote.getValue() ? 1 : 0);
            }
        } else {
            // write -1 as size if map is null
            out.writeInt(-1);
        }
    }

    public static final Parcelable.Creator<Deal> CREATOR = new Parcelable.Creator<Deal>() {

        public Deal createFromParcel(Parcel in) {
            return new Deal(in);
        }

        public Deal[] newArray(int size) {
            return new Deal[size];
        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

}
