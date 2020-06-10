package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.model.LoginResponseData;
import kr.co.bcoben.model.ResponseData;
import kr.co.bcoben.model.UserArchListResponseData;
import kr.co.bcoben.model.UserFacCateListResponseData;
import kr.co.bcoben.model.UserFacilityListResponseData;
import kr.co.bcoben.model.UserProjectListResponseData;
import kr.co.bcoben.model.UserResearchListResponseData;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {
    @FormUrlEncoded
    @POST("/app/user/login")
    Call<ResponseData<LoginResponseData>> userLogin(@Field("user_id") String id, @Field("password") String pw, @Field("device_id") String deviceId);

    @FormUrlEncoded
    @POST("/app/user/user_projectlist")
    Call<ResponseData<UserProjectListResponseData>> userProjectList(@Field("company_id") String companyId);

    @FormUrlEncoded
    @POST("/app/user/user_facilitylist")
    Call<ResponseData<UserFacilityListResponseData>> userFacilityList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/user/user_facility_grouplist")
    Call<ResponseData<UserFacCateListResponseData>> userFacCateList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/user/user_structurelist")
    Call<ResponseData<UserArchListResponseData>> userArchList(@Field("project_id") String projectId);

    @FormUrlEncoded
    @POST("/app/user/user_checktypelist")
    Call<ResponseData<UserResearchListResponseData>> userResearchList(@Field("project_id") String projectId);
}
