package kr.co.bcoben.service.retrofit;

import org.json.JSONObject;

import kr.co.bcoben.R;
import kr.co.bcoben.model.DataModel;
import kr.co.bcoben.model.ResponseData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kr.co.bcoben.util.CommonUtil.showErrorMsg;
import static kr.co.bcoben.util.CommonUtil.showToast;

public abstract class RetrofitCallbackModel<T extends DataModel> implements Callback<ResponseData<T>> {

    public abstract void onResponseData(T data);

    @Override
    public void onResponse(Call<ResponseData<T>> call, Response<ResponseData<T>> response) {
        if (response.body() != null) {
            if (response.body().isResult()) {
                onResponseData(response.body().getData());
            } else {
                showErrorMsg(response.body().getError());
            }
        } else {
            if (response.errorBody() != null) {
                try {
                    JSONObject errorObj = new JSONObject(response.errorBody().string());
                    String error = errorObj.optString("error", "");
                    if (!error.equals("")) {
                        showErrorMsg(error);
                    }
                } catch (Exception e) {}
            }
        }
    }

    @Override
    public void onFailure(Call<ResponseData<T>> call, Throwable t) {
        showToast(R.string.toast_error_server);
    }
}
