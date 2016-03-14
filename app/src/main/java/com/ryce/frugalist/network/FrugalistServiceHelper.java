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

    private static FrugalistServiceHelper ourInstance = new FrugalistServiceHelper();
    public static FrugalistServiceHelper getInstance() {
        return ourInstance;
    }
    // disallow instantiation
    private FrugalistServiceHelper() {
    }

    // cached service object
    private FrugalistAPI mFrugalistAPI;

    /**
     * Get list of deals
     * @param context
     * @param callback
     */
    public void doGetDealList(Context context, Callback<FrugalistResponse.DealList> callback) {

        if (!Utils.isConnected(context)) {
            //Callback will be called, so we prevent a unnecessary notification
            callback.onFailure(null, new Exception("No internet!"));
            return;
        }

        // init request
        Call<FrugalistResponse.DealList> dealListCall = getService().listDeals();

        // execute request
        dealListCall.enqueue(callback);
    }

    /**
     * Convienice getter
     * @return
     */
    private FrugalistAPI getService() {
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
