package client;

public class RequestInfo {

    public enum AuthType { NONE, BASIC, TOKEN }

    private String url;
    private String body;
    private AuthType authType = AuthType.NONE;
    private String user;
    private String password;
    private String token;

    public RequestInfo(String url, String body) {
        this.url = url;
        this.body = body;
    }

    public RequestInfo basic(String user, String password) {
        this.authType = AuthType.BASIC;
        this.user = user;
        this.password = password;
        return this;
    }

    public RequestInfo token(String token) {
        this.authType = AuthType.TOKEN;
        this.token = token;
        return this;
    }

    public String getUrl() { return url; }
    public String getBody() { return body; }
    public AuthType getAuthType() { return authType; }
    public String getUser() { return user; }
    public String getPassword() { return password; }
    public String getToken() { return token; }
}