package cn.seiua.skymatrix.client.auth.rq;

public class RqLogin {
    private String password;
    private String username;
    private boolean guest;

    // Constructor
    public RqLogin(String password, String username, boolean guest) {
        this.password = password;
        this.username = username;
        this.guest = guest;
    }

    // Default Constructor
    public RqLogin() {
    }

    // Getters and Setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    @Override
    public String toString() {
        return "User{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", guest=" + guest +
                '}';
    }
}
