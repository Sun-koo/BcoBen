package kr.co.bcoben.service.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import kr.co.bcoben.model.UserData;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
//    private static final String BASE_URL = "http://211.218.126.231";
    private static final String BASE_URL = "http://192.168.0.4:8080";

    public static RetrofitApi getRetrofitApi() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                String deviceId = UserData.getInstance().getDeviceId();
                Request newRequest = chain.request();
                if (deviceId != null && !deviceId.equals("")) {
                    newRequest = newRequest.newBuilder().addHeader("device-id", deviceId).build();
                }
                return chain.proceed(newRequest);
            }
        });

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create(RetrofitApi.class);
    }
}
