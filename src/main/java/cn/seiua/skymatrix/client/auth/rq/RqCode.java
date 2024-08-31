package cn.seiua.skymatrix.client.auth.rq;

public class RqCode {
    private String code;

    // Constructor
    public RqCode(String code) {
        this.code = code;
    }

    // Default Constructor
    public RqCode() {
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Code{" +
                "code='" + code + '\'' +
                '}';
    }
}
