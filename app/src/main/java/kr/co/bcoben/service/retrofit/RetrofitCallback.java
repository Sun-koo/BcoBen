package kr.co.bcoben.service.retrofit;

import kr.co.bcoben.R;
import kr.co.bcoben.model.ResponseData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kr.co.bcoben.util.CommonUtil.showErrorMsg;
import static kr.co.bcoben.util.CommonUtil.showToast;

public abstract class RetrofitCallback implements Callback<ResponseData> {

    public abstract void onResponseData();

    @Override
    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
        if (response.body().isResult()) {
            onResponseData();
        } else {
            showErrorMsg(response.body().getError());
        }
    }

    @Override
    public void onFailure(Call<ResponseData> call, Throwable t) {
        showToast(R.string.toast_error_server);
    }
}
