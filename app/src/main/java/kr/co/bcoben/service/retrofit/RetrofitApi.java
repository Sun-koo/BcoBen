package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.model.LoginResponseData;
import retrofit2.Call;
import retrofit2.http.POST;

public interface RetrofitApi {
    @POST("/app/user/login")
    Call<LoginResponseData> userLogin();
}
