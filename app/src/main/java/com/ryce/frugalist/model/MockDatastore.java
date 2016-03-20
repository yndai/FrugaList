package com.ryce.frugalist.model;

import com.ryce.frugalist.view.list.ListSectionRecyclerAdapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Mock data store
 * Created by Tony on 2016-02-18.
 */
public class MockDatastore {
    private static MockDatastore ourInstance = new MockDatastore();

    public static MockDatastore getInstance() {
        return ourInstance;
    }

    // deal store
    Map<UUID, Deal> deals = new LinkedHashMap<UUID, Deal>();

    // deals Listeners
    List<ListSectionRecyclerAdapter> dealsListeners = new ArrayList<ListSectionRecyclerAdapter>();

    // mock user bookmarks
    Map<UUID, Deal> bookmarks = new LinkedHashMap<UUID, Deal>();

    // deals Listeners
    List<ListSectionRecyclerAdapter> bookmarkListeners = new ArrayList<ListSectionRecyclerAdapter>();

    /**
     * disallow instantiation
     */
    private MockDatastore() {
        init();
    }

    private void init() {
        String test_apple = "http://imgur.com/2dozFb1.jpg";
        String test_cheese = "http://imgur.com/G0f4Lbb.jpg";
        String test_peach = "http://imgur.com/M5b16xH.jpg";

        Deal deal = new Deal(test_peach, "2.99", "Peachy", 5, "lb", "Zehr's", "450 Erb St W, Waterloo");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_apple, "0.99", "Apple", 7, "lb", "ValuMart", "75 King St N, Waterloo");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_cheese, "3.99", "Cheese", 9, "lb", "Sobey's", "450 Columbia St W, Waterloo");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_apple, "0.89", "Apples", 1, "lb", "Zehr's", "450 Erb St W, Waterloo");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_apple, "1.99", "Appless", -6, "lb", "Food Basics", "851 Fischer Hallman Rd, Kitchener");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_cheese, "6.99", "Cheese", -5, "lb", "Sobey's", "450 Columbia St W, Waterloo");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_cheese, "6.99", "Cheese", -1, "lb", "Sobey's", "450 Columbia St W, Waterloo");
        deals.put(deal.getUuid(), deal);

        deal = new Deal(test_cheese, "6.99", "Cheese", -1, "lb", "Sobey's", "450 Columbia St W, Waterloo");
        deals.put(deal.getUuid(), deal);
    }

    // DEAL methods

    public Map<UUID, Deal> getDeals() {
        return deals;
    }

    public void addDealsListener(ListSectionRecyclerAdapter adapter) {
        dealsListeners.add(adapter);
    }
    public void removeDealsListener(ListSectionRecyclerAdapter adapter) {
        dealsListeners.remove(adapter);
    }

    public Deal getDeal(UUID id) {
        return deals.get(id);
    }

    public void addDeal(Deal deal) {
        if (!deals.containsKey(deal.getUuid())) {
            deals.put(deal.getUuid(), deal);
            // notify
            for (ListSectionRecyclerAdapter adapter : dealsListeners) {
                adapter.addItem(deal);
            }
        }
    }

    // BOOKMARK methods

    public Map<UUID, Deal> getBookmarks() {
        return bookmarks;
    }

    public void addBookmarksListener(ListSectionRecyclerAdapter adapter) {
        bookmarkListeners.add(adapter);
    }
    public void removeBookmarksListener(ListSectionRecyclerAdapter adapter) {
        bookmarkListeners.remove(adapter);
    }

    public void removeBookmark(Deal deal) {
        if (bookmarks.containsKey(deal.getUuid())) {
            bookmarks.remove(deal.getUuid());
            // notify
            for (ListSectionRecyclerAdapter adapter : bookmarkListeners) {
                adapter.removeItem(deal);
            }
        }
    }

    public void addBookmark(Deal deal) {
        if (!bookmarks.containsKey(deal.getUuid())) {
            bookmarks.put(deal.getUuid(), deal);
            // notify
            for (ListSectionRecyclerAdapter adapter : bookmarkListeners) {
                adapter.addItem(deal);
            }
        }
    }



}
