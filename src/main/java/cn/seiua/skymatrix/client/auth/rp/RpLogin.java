package cn.seiua.skymatrix.client.auth.rp;

public class RpLogin {
    private String token;

    // Constructor
    public RpLogin(String token) {
        this.token = token;
    }

    // Default Constructor
    public RpLogin() {
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Data{" +
                "token='" + token + '\'' +
                '}';
    }
}
