package com.ryce.frugalist.network;

import android.content.Context;

import com.ryce.frugalist.util.Utils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Handles JSON marshalling and unmarshalling of Imgur API
 *
 * Created by Tony on 2016-02-07.
 */
public class ImgurServiceHelper {

    private static final String IMGUR_CLIENT_ID = "Client-ID c09bf20a6cf8625";
    private static final MediaType IMAGE_MEDIA_TYPE = MediaType.parse("image/*");

    // disallow instantiation
    private ImgurServiceHelper() {
    }

    // service object
    private static ImgurAPI mImgurAPI;

    /**
     * Perform image upload to Imgur
     *
     * @param context
     * @param request
     */
    public static void doPostImage(Context context, ImgurRequest request, Callback<ImgurResponse> callback) {

        if (!Utils.isConnected(context)) {
            // Callback will be called, so we prevent a unnecessary notification
            callback.onFailure(null, new Exception("No internet!"));
            return;
        }

        // construct a multipart request body
        RequestBody fileBody = RequestBody.create(IMAGE_MEDIA_TYPE, request.image);
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.addFormDataPart("image", request.image.getName(), fileBody);

        // init request
        Call<ImgurResponse> imagePostCall = getService().postImage(
                IMGUR_CLIENT_ID,
                request.title,
                request.description,
                request.albumId,
                null,
                fileBody
            );

        // execute request
        imagePostCall.enqueue(callback);
    }

    private static ImgurAPI getService() {
        if (mImgurAPI == null) {
            mImgurAPI = ServiceGenerator.createService(ImgurAPI.class);
        }
        return mImgurAPI;
    }

    private static class ServiceGenerator {

        public static final String API_BASE_URL = ImgurAPI.server;

        private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        private static Retrofit.Builder builder =
                new Retrofit.Builder()
                        .baseUrl(API_BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create());

        public static <S> S createService(Class<S> serviceClass) {
            Retrofit retrofit = builder.client(httpClient.build()).build();
            return retrofit.create(serviceClass);
        }
    }

}
