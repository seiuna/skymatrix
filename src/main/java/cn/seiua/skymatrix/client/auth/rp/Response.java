package cn.seiua.skymatrix.client.auth.rp;

public class Response<V> {
    private String path;
    private int code;
    private String message;
    private V data;

    // Constructor
    public Response(String path, int code, String message, V data) {
        this.path = path;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Default Constructor
    public Response() {
    }

    // Getters and Setters
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public V getData() {
        return data;
    }

    public void setData(V data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Response{" +
                "path='" + path + '\'' +
                ", code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}