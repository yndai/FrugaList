package com.ryce.frugalist.network;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Models JSON responses from Frugalist API
 *
 * Created by Tony on 2016-03-13.
 */
public class FrugalistResponse {

    public static class Deal {
        public Long id;
        public String product;
        public String price;
        public String unit;
        public Integer rating;
        public Map<String, Boolean> votes;
        public Date created;
        public String author;
        public String imageUrl;
        public String store;
        public String address;
        public GeoPt location;
        public String description;
    }

    public static class DealList {
        public List<Deal> items;
    }

    public static class ResponseMsg {
        public String msg;
    }

    public static class GeoPt {
        public Float latitude;
        public Float longitude;
    }

    public static class User {
        public String id;
        public String name;
        public Set<Long> bookmarks;
    }
}
