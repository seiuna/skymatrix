package cn.seiua.skymatrix.client.auth;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.fabricmc.loader.api.FabricLoader;
import okhttp3.*;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class AuthClient {
    private static final String BASE_VERSION = "0203";
    private static final String URL_REMOTE = "http://auth.seiua.cn";
    private static final String URL_LOCAL = "http://localhost:11451";
    public static String reason;
    private static String token;
    private File f = new File(FabricLoader.getInstance().getGameDir().toFile(), "skymatrix/account.json");
    private File tt = new File(FabricLoader.getInstance().getGameDir().toFile(), "skymatrix/cache/");
    private ObjectMapper mapper = new ObjectMapper();
    private UserAccount account;
    private OkHttpClient client;

    public URL getUrl(String path) {
        String u = FabricLoader.getInstance().isDevelopmentEnvironment() ? URL_LOCAL : URL_REMOTE;
        try {
            return URI.create(u + path).toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean loadLocal() {
        try {
            if (!f.exists()) {
                f.createNewFile();
                FileUtils.write(f, "{}", "utf-8");
                return false;
            } else {
                account = mapper.readValue(FileUtils.readFileToString(f, "utf-8"), UserAccount.class);
                return true;
            }
        } catch (IOException e) {
            return false;
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

    private SSLSocketFactory getSSLSocketFactory() {
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, getTrustManager(), new SecureRandom());
            return sslcontext.getSocketFactory();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken(String username, String password) {
        final Request.Builder builder = new Request.Builder()
                .url(getUrl("/auth/login"));
        String jsonData = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        RequestBody requestBody = RequestBody.create(jsonData, MediaType.parse("application/json; charset=utf-8"));
        builder.post(requestBody);
        final Call call = client.newCall(builder.build());
        try {
            Response response = call.execute();
            if (response.code() == 200) {
                String data = response.body().string();
                JSONObject json = JSONObject.parseObject(data);
                String token = json.getJSONObject("data").getString("token");
                account.setToken(token);
                account.setUsername(username);
                account.setPassword(password);
                FileUtils.write(f, mapper.writeValueAsString(account), "utf-8");
                return account.getToken();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getResource(String token, String version, String type, String md5) {
        final Request.Builder builder = new Request.Builder()
                .url(getUrl("/user/resource"));
        builder.addHeader("Authorization", "Bearer " + token);
        String jsonData = "{\"version\":\"" + version + "\",\"type\":\"" + type + "\",\"md5\":\"" + md5 + "\"}";
        RequestBody requestBody = RequestBody.create(jsonData, MediaType.parse("application/json; charset=utf-8"));
        builder.post(requestBody);
        final Call call = client.newCall(builder.build());
        try {
            Response response = call.execute();
            if (response.code() == 200) {
                String data = response.body().string();
                JSONObject json = JSONObject.parseObject(data);
                return json.getJSONObject("data").getString("target");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public File getResource() {
        if (this.loadLocal()) {
            this.getToken(account.getUsername(), account.getPassword());
            String b64 = this.getResource(account.getToken(), BASE_VERSION, "pro", "null");
            byte[] b = Base64.getDecoder().decode(b64);
            try {
                File tf = File.createTempFile("skymatrix-" + BASE_VERSION, ".xxx");
                FileUtils.writeByteArrayToFile(tf, b);
                return tf;
            } catch (IOException e) {
                reason = e.getMessage();
                throw new RuntimeException(e);
            }
        }
        reason = "Token is invalid! or username or password is no set!";
        return null;
    }


}
