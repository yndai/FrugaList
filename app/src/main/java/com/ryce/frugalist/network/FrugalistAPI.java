package com.ryce.frugalist.network;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Tony on 2016-03-13.
 */
public interface FrugalistAPI {

    // TODO: just a local test server for now
    String server = "http://192.168.1.102:8888/_ah/api/frugalist/v1/";


    /****************************************
     * Deal API GETs
     ****************************************/

    /**
     * Get a deal by Id
     *
     * @param id
     * @return
     */
    @GET("deal")
    Call<FrugalistResponse.Deal> getDealById(@Query("id") Long id);

    /**
     * List all deals
     */
    @GET("deal/list")
    Call<FrugalistResponse.DealList> listDeals();

    /**
     * List nearest deals to a center point with given radius
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    @GET("deal/list/near")
    Call<FrugalistResponse.DealList> listNearestDeals(
            @Query("latitude") Float latitude,
            @Query("longitude") Float longitude,
            @Query("radius") Integer radius
    );

    /**
     * List deals matching a product substring
     *
     * @param product
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    @GET("deal/search/product")
    Call<FrugalistResponse.DealList> listByProduct(
            @Query("product") String product,
            @Query("latitude") Float latitude,
            @Query("longitude") Float longitude,
            @Query("radius") Integer radius
    );

    /**
     * List deals matching a store name substring
     *
     * @param store
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    @GET("deal/search/store")
    Call<FrugalistResponse.DealList> listByStore(
            @Query("product") String store,
            @Query("latitude") Float latitude,
            @Query("longitude") Float longitude,
            @Query("radius") Integer radius
    );

    /**
     * List deals created by a user
     *
     * @param authorId
     * @return
     */
    @GET("deal/list/byauthor")
    Call<FrugalistResponse.DealList> listByAuthor(
            @Query("authorId") String authorId
    );

    /**
     * List deals bookmarked by a given user
     *
     * @param userId
     * @return
     */
    @GET("deal/list/bookmarks")
    Call<FrugalistResponse.DealList> listBookmarks(
            @Query("userId") String userId
    );

    /****************************************
     * Deal API POSTs
     ****************************************/

    /**
     *  Post a deal
     *
     * @param authorId
     * @param product
     * @param imageUrl
     * @param address
     * @param latitude
     * @param longitude
     * @param price
     * @param unit
     * @param store
     * @param description
     * @return
     */
    @POST("deal/add")
    Call<FrugalistResponse.Deal> addDeal(
            @Query("authorId") String authorId,
            @Query("product") String product,
            @Query("imageUrl") String imageUrl,
            @Query("address") String address,
            @Query("latitude") Float latitude,
            @Query("longitude") Float longitude,
            @Query("price") String price,
            @Query("unit") String unit,
            @Query("store") String store,
            @Query("description") String description
    );

    /****************************************
     * Deal API PUTs
     ****************************************/

    /**
     * Update a deal with a rating (true for upvote)
     *
     * @param id
     * @param userId
     * @param upvote
     * @return
     */
    @PUT("deal/update/rating")
    Call<FrugalistResponse.Deal> updateDealRating(
            @Query("id") Long id,
            @Query("userId") String userId,
            @Query("upvote") Boolean upvote
    );

    /****************************************
     * Deal API DELETEs
     ****************************************/

    /**
     * Delete a deal
     *
     * @param id
     * @return
     */
    @DELETE("deal/delete")
    Call<FrugalistResponse.ResponseMsg> updateDealRating(
            @Query("id") Long id
    );

    /****************************************
     * User API GETs
     ****************************************/

    /**
     * Get a user by id
     *
     * @param id
     * @return
     */
    @GET("user")
    Call<FrugalistResponse.User> getUserById(
            @Query("id") String id
    );

    /****************************************
     * User API POSTs
     ****************************************/

    /**
     * Add a user
     *
     * @param id
     * @param name
     * @return
     */
    @POST("user/add")
    Call<FrugalistResponse.User> addUser(
            @Query("id") String id,
            @Query("name") String name
    );

    /****************************************
     * User API PUTs
     ****************************************/

    /**
     * Add or delete a bookmark (add is true for adding a bookmark)
     *
     * @param id
     * @param dealId
     * @param add
     * @return
     */
    @PUT("user/update/bookmark")
    Call<FrugalistResponse.User> addOrDeleteUserBookmark(
            @Query("id") String id,
            @Query("dealId") Long dealId,
            @Query("add") Boolean add
    );

}
