package kr.co.bcoben.model;

public class ResponseData<T extends DataModel> {
    private boolean result;
    private T data;
    private String error;

    public ResponseData(boolean result, T data, String error) {
        this.result = result;
        this.data = data;
        this.error = error;
    }

    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
