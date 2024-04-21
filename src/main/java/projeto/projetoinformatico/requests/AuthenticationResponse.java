package projeto.projetoinformatico.requests;

import lombok.Data;
import projeto.projetoinformatico.model.users.User;

@Data
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private UserResponse user;

    public AuthenticationResponse(String accessToken, String refreshToken, UserResponse user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public AuthenticationResponse() {

    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
