package com.ryce.frugalist.network;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Tony on 2016-03-13.
 */
public interface FrugalistAPI {

    // TODO: just a local test server for now
    String server = "http://192.168.1.102:8888/_ah/api/frugalist/v1/";


    /****************************************
     * Deal API
     ****************************************/

    /**
     * List all deals
     */
    @GET("deal/list")
    Call<FrugalistResponse.DealList> listDeals();
}
