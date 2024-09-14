package cn.seiua.skymatrix.client.auth.rq;

import java.util.List;

public class Payload {
    private List<String> permissions;

    // Constructor
    public Payload(List<String> permissions) {
        this.permissions = permissions;
    }

    // Default Constructor
    public Payload() {
    }

    // Getters and Setters
    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "permissions=" + permissions +
                '}';
    }
}