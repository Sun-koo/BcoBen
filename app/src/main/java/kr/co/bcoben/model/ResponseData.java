package kr.co.bcoben.model;

public class ResponseData<T extends DataModel> {
    private boolean result;
    private T data;

    public ResponseData(boolean result, T data) {
        this.result = result;
        this.data = data;
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
}
