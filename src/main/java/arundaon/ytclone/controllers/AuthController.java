package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.LoginUserRequest;
import arundaon.ytclone.models.TokenResponse;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.services.AuthService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
    private AuthService authService;
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(path="/api/auth/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
        TokenResponse data = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(data).build();
    }

    @DeleteMapping(path="/api/auth/logout", produces = MediaType.APPLICATION_JSON_VALUE)
    public WebResponse<String> logout(User user){
        authService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    }
}
