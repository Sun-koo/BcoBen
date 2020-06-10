package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.model.LoginResponseData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {
    @FormUrlEncoded
    @POST("/app/user/login")
    Call<LoginResponseData> userLogin(@Field("user_id") String user_id, @Field("password") String password, @Field("device_id") String device_id);
}