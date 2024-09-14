package cn.seiua.skymatrix.client.httpclient;

import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.Call;

public class Task<V> {
    private CallBack<V> callBack;
    private Call call;

    private TypeReference target;

    public CallBack<V> getCallBack() {
        return callBack;
    }

    public Task(CallBack<V> callBack, Call call, TypeReference type) {
        this.callBack = callBack;
        this.call = call;
        this.target = type;
    }

    public TypeReference getTarget() {
        return target;
    }

    public void setCallBack(CallBack<V> callBack) {
        this.callBack = callBack;
    }

    public Call getCall() {
        return call;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    public void setTarget(TypeReference target) {
        this.target = target;
    }
}
