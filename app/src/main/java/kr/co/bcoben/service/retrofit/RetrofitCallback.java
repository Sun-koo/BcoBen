package kr.co.bcoben.service.retrofit;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kr.co.bcoben.R;
import kr.co.bcoben.model.ResponseData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static kr.co.bcoben.util.CommonUtil.showErrorMsg;
import static kr.co.bcoben.util.CommonUtil.showToast;

public abstract class RetrofitCallback implements Callback<ResponseData> {
    private final String TAG = "RetrofitCallback";

    public abstract void onResponseData();

    @Override
    public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
        Log.e(TAG, response.raw().request().url().url().toString());
        if (response.body() != null) {
            if (response.body().isResult()) {
                onResponseData();
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
    public void onFailure(Call<ResponseData> call, Throwable t) {
        showToast(R.string.toast_error_server);
    }
}
