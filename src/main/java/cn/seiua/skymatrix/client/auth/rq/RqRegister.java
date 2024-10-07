package cn.seiua.skymatrix.client.auth.rq;

public class RqRegister {
    private String email;
    private String username;
    private String password;
    private String invite_code;

    // Constructor
    public RqRegister(String email, String username, String password, String inviteCode) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.invite_code = inviteCode;
    }

    // Default Constructor
    public RqRegister() {
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteCode() {
        return invite_code;
    }

    public void setInviteCode(String inviteCode) {
        this.invite_code = inviteCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", inviteCode='" + invite_code + '\'' +
                '}';
    }
}
