package com.accemy.mahindralogger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MahindraLoggerService {

    String UAT_ENDPOINT = "http://mapi.sitsyouv.com/"; //Test Environment
    String PROD_ENDPOINT = "https://mapi.mahindrasyouv.com/"; //Live Environment

//    @Headers({
//            "x-functions-key: J3C4/9R8RGQzBYApFH6hm1VyUZ7M7XUhjzanFBpbaGDB1YAUyHXl0A==", //Test Environment
////            "x-functions-key: pxKosrfIutY9y1K7bdWvVxQEGB/MjGWqWTsZd8airyaX0rJcIgJjwA==", //Live Environment
//            "Content-Type: application/json"
//    })
    @POST("/api/SaveCustomerActivities")
    Call<MahindraLogResponse> postData(@Body RequestBody data);

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static MahindraLoggerService  newMahindraLoggerService(final boolean isProd) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Request original = chain.request();

                            Request request = original.newBuilder()
                                    .header("x-functions-key", isProd ? "pxKosrfIutY9y1K7bdWvVxQEGB/MjGWqWTsZd8airyaX0rJcIgJjwA==" : "J3C4/9R8RGQzBYApFH6hm1VyUZ7M7XUhjzanFBpbaGDB1YAUyHXl0A==")
                                    .header("Content-Type", "application/json")
                                    .method(original.method(), original.body())
                                    .build();

                            return chain.proceed(request);
                        }
                    })
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(isProd ? MahindraLoggerService.PROD_ENDPOINT : MahindraLoggerService.UAT_ENDPOINT)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(MahindraLoggerService.class);
        }
    }
}
