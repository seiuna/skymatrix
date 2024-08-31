package cn.seiua.skymatrix.client.auth.rq;

public class RqGenerateCode {
    private String type;
    private int count;
    private Payload payload;

    // Constructor
    public RqGenerateCode(String type, int count, Payload payload) {
        this.type = type;
        this.count = count;
        this.payload = payload;
    }

    // Default Constructor
    public RqGenerateCode() {
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Payload getPayload() {
        return payload;
    }

    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Code{" +
                "type='" + type + '\'' +
                ", count=" + count +
                ", payload=" + payload +
                '}';
    }
}
