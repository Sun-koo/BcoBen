package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.model.LoginResponseData;
import kr.co.bcoben.model.ResponseData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {
    @FormUrlEncoded
    @POST("/app/user/login")
    Call<ResponseData<LoginResponseData>> userLogin(@Field("user_id") String id, @Field("password") String pw, @Field("device_id") String deviceId);
}
