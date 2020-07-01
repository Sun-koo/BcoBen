package kr.co.bcoben.service.retrofit;

import java.util.List;
import java.util.Map;

import kr.co.bcoben.model.AppUpdateData;
import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.MenuCheckListData;
import kr.co.bcoben.model.PlanDataList;
import kr.co.bcoben.model.PointListData;
import kr.co.bcoben.model.PointInputData;
import kr.co.bcoben.model.PointResponseData;
import kr.co.bcoben.model.ProjectListData;
import kr.co.bcoben.model.ProjectMainData;
import kr.co.bcoben.model.ProjectResearchList;
import kr.co.bcoben.model.ResearchCheckData;
import kr.co.bcoben.model.ResearchIdData;
import kr.co.bcoben.model.ResearchSpinnerData;
import kr.co.bcoben.model.ResponseData;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface RetrofitApi {
    @FormUrlEncoded
    @POST("/app/user/login")
    Call<ResponseData<LoginData>> userLogin(@Field("id") String id, @Field("password") String pw, @Field("device_serial") String deviceSerial);

    @FormUrlEncoded
    @POST("/app/user/check_auth")
    Call<ResponseData> checkAuth(@Field("user_id") int userId, @Field("auth_type") String type, @Field("auth_no") String authNo);

    @FormUrlEncoded
    @POST("/app/user/send_auth")
    Call<ResponseData<LoginData>> sendAuth(@Field("user_id") int userId, @Field("auth_type") String type);

    @FormUrlEncoded
    @POST("/app/user/reset_password")
    Call<ResponseData<LoginData>> resetPassword(@Field("id") String id, @Field("phone") String phone, @Field("device_serial") String deviceSerial);

    @FormUrlEncoded
    @POST("/app/user/update_password")
    Call<ResponseData> updatePassword(@Field("user_id") int userId, @Field("password") String pw);

    @FormUrlEncoded
    @POST("/app/user/logout")
    Call<ResponseData> logout(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("/app/project/list")
    Call<ResponseData<ProjectListData>> projectList(@Field("user_id") int userId);

    @FormUrlEncoded
    @POST("/app/project/data")
    Call<ResponseData<ProjectMainData>> projectData(@Field("user_id") int userId, @Field("project_id") int projectId);

    @FormUrlEncoded
    @POST("/app/project/research_list")
    Call<ResponseData<ProjectResearchList>> projectResearchList(@Field("user_id") int userId, @Field("project_id") int projectId, @Field("facility_id") int facilityId, @Field("order") String order);

    @FormUrlEncoded
    @POST("/app/project/reg_data_list")
    Call<ResponseData<MenuCheckListData>> projectRegDataList(@Field("user_id") int userId);

    @Multipart
    @POST("/app/project/register")
    Call<ResponseData<ProjectListData>> regProject(@Part("user_id") int userId, @Part("project_name") RequestBody projectName, @Part("start_date") RequestBody startDate, @Part("end_date") RequestBody endDate,
                                                   @Part("grade_name") String gradeName, @Part("facility_list") RequestBody facilityList, @Part("research_list") RequestBody researchList,
                                                   @Part List<MultipartBody.Part> planList);

    @FormUrlEncoded
    @POST("/app/project/research/register")
    Call<ResponseData<ResearchIdData>> regResearch(@Field("user_id") int userId, @Field("project_id") int projectId, @Field("facility_name") String facilityName, @Field("fac_cate_name") String facCateName,
                                                   @Field("structure_name") String structureName, @Field("research_name") String researchName, @Field("tot_count") int totCount);

    @FormUrlEncoded
    @POST("/app/project/item_click")
    Call<ResponseData> itemClick(@Field("user_id") int userId, @Field("project_id") int projectId, @Field("item_id") int itemId);

    @FormUrlEncoded
    @POST("/app/research/plan_list")
    Call<ResponseData<PlanDataList>> planList(@Field("user_id") int userId, @Field("research_id") int researchId);

    @FormUrlEncoded
    @POST("/app/research/data")
    Call<ResponseData<ResearchSpinnerData>> researchData (@Field("user_id") int userId, @Field("research_id") int researchId);

    @FormUrlEncoded
    @POST("/app/research/check_data")
    Call<ResponseData<ResearchCheckData>> researchCheckData(@Field("user_id") int userId, @Field("research_id") int researchId, @Field("project_id") int projectId, @Field("facility_id") int facilityId, @Field("fac_cate_id") int facCateId, @Field("structure_id") int structureId, @Field("research_type_id") int researchTypeId);

    @FormUrlEncoded
    @POST("/app/research/point_list")
    Call<ResponseData<PointListData>> researchPointList(@Field("user_id") int userId, @Field("research_id") int researchId, @Field("plan_id") int planId);

    @FormUrlEncoded
    @POST("/app/research/point_reg_data")
    Call<ResponseData<PointInputData>> researchPointRegisterData(@Field("user_id") int userId, @Field("research_id") int researchId);

    @Multipart
    @POST("/app/research/register_point")
    Call<ResponseData<PointResponseData>> researchRegisterPoint(@Part("user_id") int userId, @Part("research_id") int researchId, @Part("plan_id") int planId, @Part("point_x") int pointX, @Part("point_y") int pointY, @Part("count") int count,
                                                                @PartMap Map<String, RequestBody> partMap, @Part List<MultipartBody.Part> fileList);

    @Multipart
    @POST("/app/research/update_point")
    Call<ResponseData<PointResponseData>> researchUpdatePoint(@Part("user_id") int userId, @Part("research_id") int researchId, @Part("plan_id") int planId, @Part("point_x") int pointX, @Part("point_y") int pointY, @Part("count") int count,
                                                              @PartMap Map<String, RequestBody> partMap, @Part List<MultipartBody.Part> fileList);

    @FormUrlEncoded
    @POST("/app/research/delete_point")
    Call<ResponseData> researchDeletePoint(@Field("user_id") int userId, @Field("point_id_list") List<Integer> deletePointList);

    @POST("/app/update")
    Call<ResponseData<AppUpdateData>> appUpdate();
}
