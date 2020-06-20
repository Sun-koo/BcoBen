package kr.co.bcoben.service.retrofit;

import java.util.List;

import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.MenuCheckListData;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.ProjectListData;
import kr.co.bcoben.model.ProjectMainData;
import kr.co.bcoben.model.ResearchIdData;
import kr.co.bcoben.model.ResponseData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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

    @Multipart
    @POST("/app/project/register")
    Call<ResponseData<ProjectListData>> regProject(@Part("user_id") int id, @Part("project_name") RequestBody projectName, @Part("start_date") RequestBody startDate, @Part("end_date") RequestBody endDate,
                                                   @Part("grade_id") int gradeId, @Part("facility_list") RequestBody facilityList, @Part("research_list") RequestBody researchList,
                                                   @Part List<MultipartBody.Part> planList);

    @FormUrlEncoded
    @POST("/app/project/research/register")
    Call<ResponseData<ResearchIdData>> regResearch(@Field("user_id") int id, @Field("project_id") int projectId, @Field("facility_name") String facilityName, @Field("fac_cate_name") String facCateName,
                                                   @Field("structure_name") String structureName, @Field("research_name") String researchName, @Field("tot_count") int totCount);

    @FormUrlEncoded
    @POST("/app/project/item_click")
    Call<ResponseData> itemClick(@Field("user_id") int id, @Field("project_id") int projectId, @Field("item_id") int itemId);

    @FormUrlEncoded
    @POST("/app/research/plan_list")
    Call<ResponseData<PlanDataList>> planList(@Field("user_id") int id, @Field("research_id") int researchId);

//    @FormUrlEncoded
//    @POST("/app/research/data")
//    Call<ResponseData<>> researchData (@Field("user_id") int id, @Field("research_id") int researchId);
}
