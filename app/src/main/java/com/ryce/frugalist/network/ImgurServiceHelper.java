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
 * Created by Tony on 2016-02-07.
 *
 * Http requests & JSON marshalling should go here
 */
public class ImgurServiceHelper {

    private static final String IMGUR_CLIENT_ID = "Client-ID c09bf20a6cf8625";
    private static final MediaType IMAGE_MEDIA_TYPE = MediaType.parse("image/*");

    private static ImgurServiceHelper ourInstance = new ImgurServiceHelper();
    public static ImgurServiceHelper getInstance() {
        return ourInstance;
    }
    // disallow instantiation
    private ImgurServiceHelper() {
    }

    /**
     * Perform image upload to Imgur
     *
     * @param context
     * @param request
     */
    public void doPostImage(Context context, ImgurRequest request, Callback<ImgurResponse> callback) {

        if (!Utils.isConnected(context)) {
            //Callback will be called, so we prevent a unnecessary notification
            callback.onFailure(null, new Exception("No internet!"));
            return;
        }

        // construct a multipart request body
        RequestBody fileBody = RequestBody.create(IMAGE_MEDIA_TYPE, request.image);
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
        multipartBuilder.addFormDataPart("image", request.image.getName(), fileBody);

        // init request
        Call<ImgurResponse> imagePostCall =
                ServiceGenerator.createService(ImgurAPI.class).postImage(
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
