package com.accemy.mahindralogger;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MahindraLoggerService {

    String ENDPOINT = "http://mapi.sitsyouv.com/";

    @Headers({
            "x-functions-key: J3C4/9R8RGQzBYApFH6hm1VyUZ7M7XUhjzanFBpbaGDB1YAUyHXl0A==",
            "Content-Type: application/json"
    })
    @POST("/api/SaveCustomerActivities")
    Call<MahindraLogResponse> postData(@Body RequestBody data);

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static MahindraLoggerService  newMahindraLoggerService() {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(MahindraLoggerService.ENDPOINT)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(MahindraLoggerService.class);
        }
    }
}
