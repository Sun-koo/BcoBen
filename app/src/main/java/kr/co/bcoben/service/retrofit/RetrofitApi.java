package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.model.LoginData;
import kr.co.bcoben.model.ProjectListData;
import kr.co.bcoben.model.ResponseData;
import kr.co.bcoben.model.ArchitectureListData;
import kr.co.bcoben.model.FacCateListData;
import kr.co.bcoben.model.FacilityListData;
import kr.co.bcoben.model.ResearchListData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {
    @FormUrlEncoded
    @POST("/app/user/login")
    Call<ResponseData<LoginData>> userLogin(@Field("user_id") String id, @Field("password") String pw, @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("/app/main/user_projectlist")
    Call<ResponseData<ProjectListData>> userProjectList(@Field("company_id") String companyId);

    @FormUrlEncoded
    @POST("/app/main/user_facilitylist")
    Call<ResponseData<FacilityListData>> userFacilityList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/main/user_grouplist")
    Call<ResponseData<FacCateListData>> userFacCateList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/main/user_structurelist")
    Call<ResponseData<ArchitectureListData>> userArchList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/main/user_checktypelist")
    Call<ResponseData<ResearchListData>> userResearchList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/main/reg_facility")
    Call<ResponseData<FacilityListData>> regFacility(@Field("project_id") String projectId, @Field("facility") String facility);

    @FormUrlEncoded
    @POST("/app/main/reg_fac_cate")
    Call<ResponseData<FacCateListData>> regFacCate(@Field("project_id") String projectId, @Field("fac_cate") String facCate);

    @FormUrlEncoded
    @POST("/app/main/reg_architecture")
    Call<ResponseData<ArchitectureListData>> regArchitecture(@Field("project_id") String projectId, @Field("architecture") String architecture);

    @FormUrlEncoded
    @POST("/app/main/reg_research")
    Call<ResponseData<ResearchListData>> regResearch(@Field("project_id") String projectId, @Field("research") String research, @Field("count") String count);
}
