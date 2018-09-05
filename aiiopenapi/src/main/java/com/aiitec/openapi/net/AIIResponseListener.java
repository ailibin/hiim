package com.aiitec.openapi.net;

public interface AIIResponseListener<T> {

    public T get();

    public void onFailure(String content, int index);

    public void onSuccess(T response, int index);

    public void onStart(int index);

    public void onFinish(int index);

    public void onLoginOut(int index);

    public void onCache(T content, int index);

    public void onOptionFast(int type, int index);

    public void onServiceError(String content, int status, int index);

}
