package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.MenuCheckListData;
import kr.co.bcoben.model.ProjectListData;
import kr.co.bcoben.model.ProjectMainData;
import kr.co.bcoben.model.ResponseData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {
    @FormUrlEncoded
    @POST("/app/user/login")
    Call<ResponseData<LoginData>> userLogin(@Field("id") String id, @Field("password") String pw, @Field("device_serial") String deviceSerial);

    @FormUrlEncoded
    @POST("/app/user/check_auth")
    Call<ResponseData> checkAuth(@Field("user_id") int id, @Field("auth_type") String type, @Field("auth_no") String authNo);

    @FormUrlEncoded
    @POST("/app/user/send_auth")
    Call<ResponseData<LoginData>> sendAuth(@Field("user_id") int id, @Field("auth_type") String type);

    @FormUrlEncoded
    @POST("/app/user/reset_password")
    Call<ResponseData<LoginData>> resetPassword(@Field("id") String id, @Field("phone") String phone, @Field("device_serial") String deviceSerial);

    @FormUrlEncoded
    @POST("/app/user/update_password")
    Call<ResponseData> updatePassword(@Field("user_id") int id, @Field("password") String pw);

    @FormUrlEncoded
    @POST("/app/user/logout")
    Call<ResponseData> logout(@Field("user_id") int id);

    @FormUrlEncoded
    @POST("/app/project/list")
    Call<ResponseData<ProjectListData>> projectList(@Field("user_id") int id);

    @FormUrlEncoded
    @POST("/app/project/data")
    Call<ResponseData<ProjectMainData>> projectData(@Field("user_id") int id, @Field("project_id") int projectId, @Field("order") String order);

    @FormUrlEncoded
    @POST("/app/project/reg_data_list")
    Call<ResponseData<MenuCheckListData>> projectRegDataList(@Field("user_id") int id);
}
