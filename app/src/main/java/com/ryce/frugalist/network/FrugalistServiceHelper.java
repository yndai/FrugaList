package com.ryce.frugalist.network;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.ryce.frugalist.util.Utils;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
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
     * Get deal by id
     * @param callback
     * @param id
     */
    public static void doGetDealById(Callback<FrugalistResponse.Deal> callback, Long id) {

        Call<FrugalistResponse.Deal> dealCall = getService().getDealById(id);

        dealCall.enqueue(callback);
    }

    /**
     * Get list of deals
     * @param context
     * @param callback
     */
    public static void doGetDealList(Context context, Callback<FrugalistResponse.DealList> callback) {

        // TODO: refactor this elsewhere...
        if (!Utils.isConnected(context)) {
            //Callback will be called, so we prevent a unnecessary notification
            callback.onFailure(null, new Exception("No internet!"));
            return;
        }

        Call<FrugalistResponse.DealList> dealListCall = getService().listDeals();

        dealListCall.enqueue(callback);
    }

    /**
     * Get list of nearby deals
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


    /****************************************************************************
     * USER SERVICES
     ****************************************************************************/

    public static void doGetOrCreateUser(Callback<FrugalistResponse.User> callback,
                                  String id,
                                  String name
    ) {
        Call<FrugalistResponse.User> userCall =
                getService().getUserOrCreate(id, name);

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
