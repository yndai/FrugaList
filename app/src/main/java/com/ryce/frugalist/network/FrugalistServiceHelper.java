package com.ryce.frugalist.network;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Handles JSON marshalling and unmarshalling for Frugalist API
 *
 * Created by Tony on 2016-03-13.
 */
public class FrugalistServiceHelper {

    /** NO ONE expects this secret key! */
    private static final String FRUGALIST_CLIENT_ID = "Client-ID thespanishinquisition";

    // disallow instantiation
    private FrugalistServiceHelper() {
    }

    // service object
    private static FrugalistAPI mFrugalistAPI;

    /****************************************************************************
     * DEAL SERVICES
     ****************************************************************************/

    /**
     * GET deal by id
     * @param callback
     * @param id
     */
    public static void doGetDealById(Callback<FrugalistResponse.Deal> callback, Long id) {

        Call<FrugalistResponse.Deal> dealCall = getService().getDealById(id);

        dealCall.enqueue(callback);
    }

    /**
     * GET list of deals
     * @param callback
     */
    public static void doGetDealList(Callback<FrugalistResponse.DealList> callback) {

        Call<FrugalistResponse.DealList> dealListCall = getService().listDeals();

        dealListCall.enqueue(callback);
    }

    /**
     * GET list of nearby deals
     * @param callback
     * @param latitude
     * @param longitude
     * @param radius
     */
    public static void doGetNearbyDealList(Callback<FrugalistResponse.DealList> callback,
                                    Float latitude,
                                    Float longitude,
                                    Integer radius
    ) {
        Call<FrugalistResponse.DealList> dealListCall =
                getService().listNearestDeals(latitude, longitude, radius);

        dealListCall.enqueue(callback);
    }

    /**
     * GET list of nearby deals by product name
     * @param callback
     * @param product
     * @param latitude
     * @param longitude
     * @param radius
     */
    public static void doGetListByProduct(Callback<FrugalistResponse.DealList> callback,
                                          String product,
                                          Float latitude,
                                          Float longitude,
                                          Integer radius
    ) {
        Call<FrugalistResponse.DealList> dealListCall =
                getService().listByProduct(product, latitude, longitude, radius);

        dealListCall.enqueue(callback);
    }

    /**
     * GET list of nearby deals by store name
     * @param callback
     * @param store
     * @param latitude
     * @param longitude
     * @param radius
     */
    public static void doGetListByStore(Callback<FrugalistResponse.DealList> callback,
                                        String store,
                                        Float latitude,
                                        Float longitude,
                                        Integer radius
    ) {
        Call<FrugalistResponse.DealList> dealListCall =
                getService().listByStore(store, latitude, longitude, radius);

        dealListCall.enqueue(callback);
    }

    /**
     * GET list of nearby deals created by a given user
     * @param callback
     * @param authorId
     */
    public static void doGetListByAuthor(Callback<FrugalistResponse.DealList> callback, String authorId) {

        Call<FrugalistResponse.DealList> dealListCall = getService().listByAuthor(authorId);

        dealListCall.enqueue(callback);
    }

    /**
     * GET list of bookmarks saved by a given user
     * @param callback
     * @param userId
     */
    public static void doGetBookmarksList(Callback<FrugalistResponse.DealList> callback, String userId) {

        Call<FrugalistResponse.DealList> dealListCall = getService().listBookmarks(userId);

        dealListCall.enqueue(callback);
    }

    /**
     * POST a new deal
     * @param callback
     * @param deal
     */
    public static void doPostDeal(Callback<FrugalistResponse.Deal> callback,
                                  FrugalistRequest.Deal deal
    ) {
        Call<FrugalistResponse.Deal> dealCall = getService().addDeal(
                deal.authorId, deal.product, deal.imageUrl, deal.address, deal.latitude,
                deal.longitude, deal.price, deal.unit, deal.store, deal.description);

        dealCall.enqueue(callback);
    }

    /**
     * PUT a new deal with updated rating (upvote is true for upvote)
     * @param callback
     * @param id
     * @param userId
     * @param upvote
     */
    public static void doUpdateDealRating(Callback<FrugalistResponse.Deal> callback,
                                          Long id,
                                          String userId,
                                          Boolean upvote
    ) {
        Call<FrugalistResponse.Deal> dealCall = getService().updateDealRating(id, userId, upvote);

        dealCall.enqueue(callback);
    }

    /**
     * DELETE a deal
     * @param callback
     * @param id
     */
    public static void doDeleteDeal(Callback<FrugalistResponse.ResponseMsg> callback, Long id) {

        Call<FrugalistResponse.ResponseMsg> dealCall = getService().deleteDeal(id);

        dealCall.enqueue(callback);
    }

    /****************************************************************************
     * USER SERVICES
     ****************************************************************************/

    /**
     * GET a user by id (a new user is created if the id does not exist)
     * @param callback
     * @param id
     * @param name
     */
    public static void doGetOrCreateUser(Callback<FrugalistResponse.User> callback,
                                  String id,
                                  String name
    ) {
        Call<FrugalistResponse.User> userCall = getService().getUserOrCreate(id, name);

        userCall.enqueue(callback);
    }

    /**
     * PUT a user, adding or deleting a deal bookmark
     * @param callback
     * @param id
     * @param dealId
     * @param add
     */
    public static void doAddOrDeleteBookmark(Callback<FrugalistResponse.User> callback,
                                             String id,
                                             Long dealId,
                                             Boolean add
    ) {
        Call<FrugalistResponse.User> userCall = getService()
                .addOrDeleteUserBookmark(id, dealId, add);

        userCall.enqueue(callback);
    }

    /****************************************************************************
     * HELPERS
     ****************************************************************************/

    private static FrugalistAPI getService() {
        if (mFrugalistAPI == null) {
            mFrugalistAPI = ServiceGenerator.createService(FrugalistAPI.class);
        }
        return mFrugalistAPI;
    }

    private static class ServiceGenerator {

        public static final String API_BASE_URL = FrugalistAPI.server;

        private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(
                                GsonConverterFactory.create(
                                        // Create a gson builder with date format
                                        new GsonBuilder()
                                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                                .create()
                                )
                        );

        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return retrofit.create(serviceClass);
        }
    }

}
