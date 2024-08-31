package cn.seiua.skymatrix.client.auth.rp;

import java.util.List;

public class RpCode {
    private String action;
    private List<String> permissions;

    // Constructor
    public RpCode(String action, List<String> permissions) {
        this.action = action;
        this.permissions = permissions;
    }

    // Default Constructor
    public RpCode() {
    }

    // Getters and Setters
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public String toString() {
        return "Data{" +
                "action='" + action + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
