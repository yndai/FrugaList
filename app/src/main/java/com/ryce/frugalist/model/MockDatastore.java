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

    // listeners
    List<ListSectionRecyclerAdapter> listeners = new ArrayList<ListSectionRecyclerAdapter>();

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

        Deal deal = new Deal(test_peach, "2.99", "Peachy", 5, "lb", "Zehr's");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_apple, "0.99", "Apple", 7, "lb", "ValuMart");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_cheese, "3.99", "Cheese", 9, "lb", "Sobey's");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_apple, "0.89", "Apples", 1, "lb", "Zehr's");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_apple, "1.99", "Appless", -6, "lb", "Metro");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_cheese, "6.99", "Cheese", -5, "lb", "Sobey's");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_cheese, "6.99", "Cheese", -1, "lb", "Sobey's");
        deals.put(deal.getId(), deal);

        deal = new Deal(test_cheese, "6.99", "Cheese", -1, "lb", "Sobey's");
        deals.put(deal.getId(), deal);
    }

    public Map<UUID, Deal> getDeals() {
        return deals;
    }

    public void addListener(ListSectionRecyclerAdapter adapter) {
        listeners.add(adapter);
    }
    public void removeListener(ListSectionRecyclerAdapter adapter) {
        listeners.remove(adapter);
    }

    public Deal getDeal(UUID id) {
        return deals.get(id);
    }

    public void addDeal(Deal deal) {
        deals.put(deal.getId(), deal);
        // notify
        for (ListSectionRecyclerAdapter adapter : listeners) {
            adapter.addItem(deal);
        }
    }
}
