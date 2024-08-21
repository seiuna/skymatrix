package cn.seiua.skymatrix.client.httpclient;

import cn.seiua.skymatrix.client.component.Component;
import cn.seiua.skymatrix.client.component.Init;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class HttpClient {

    private OkHttpClient client;
    private ObjectMapper mapper = new ObjectMapper();

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, getTrustManager(), new SecureRandom());
            return sslcontext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TrustManager[] getTrustManager() {
        TrustManager[] trustManager = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };
        return trustManager;
    }

    @Init
    public void init() {
        this.client = new OkHttpClient.Builder().sslSocketFactory(getSSLSocketFactory(), (X509TrustManager) getTrustManager()[0]).build();
        new Thread(this::run).start();
    }

    private Request generateRequestPost(String jsonData, String url) {
        RequestBody requestBody = RequestBody.create(jsonData, MediaType.parse("application/json; charset=utf-8"));
        ;
        return new Request.Builder().post(requestBody).url(url).build();
    }

    public <V> void get(String url, CallBack<V> callBack, Class<V> type) throws IOException {
        get(url, callBack, type, null, null);
    }

    public <V> void get(String url, CallBack<V> callBack, Class<V> type, Object data, Map<String, String> headers) throws IOException {
        final Request.Builder builder = new Request.Builder()
                .url(url);
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        if (data != null) {
            String jsonData = this.mapper.writeValueAsString(data);
            RequestBody requestBody = RequestBody.create(jsonData, MediaType.parse("application/json; charset=utf-8"));
            builder.post(requestBody);
        }
        final Call call = client.newCall(builder.build());
        queue.add(new Task(callBack, call, type));
    }

    public <V> void post(String url, CallBack<V> callBack, Class<V> type) throws IOException {
        post(url, callBack, type, null, null);
    }


    public <V> void post(String url, CallBack<V> callBack, Class<V> type, Object data, Map<String, String> headers) throws IOException {
        final Request.Builder builder = new Request.Builder()
                .url(url);
        if (headers != null) {
            headers.forEach(builder::addHeader);
        }
        if (data != null) {
            String jsonData = this.mapper.writeValueAsString(data);
            RequestBody requestBody = RequestBody.create(jsonData, MediaType.parse("application/json; charset=utf-8"));
            builder.post(requestBody);
        }
        final Call call = client.newCall(builder.build());

        queue.add(new Task(callBack, call, type));
    }

    public Queue<Task> queue = new ConcurrentLinkedQueue<>();

    public void run() {
        while (true) {
            try {
                Task task = queue.poll();
                if (task != null) {
                    Response response = task.getCall().execute();
                    String data = response.body().string();
                    Object o = this.mapper.readValue(data, task.getType());
                    task.getCallBack().callBack(o, data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
