package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.RegisterUserRequest;
import arundaon.ytclone.models.UpdateUserRequest;
import arundaon.ytclone.models.UserResponse;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.repositories.UserRepository;
import arundaon.ytclone.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private UserService userService;
    public UserController(UserService userService) {this.userService = userService;}

    @PostMapping(path ="/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> register(@RequestBody RegisterUserRequest request) {
        System.out.println("RegisterUserRequest: " + request);
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/users/current",produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<UserResponse> getCurrentUser(User user){
        return WebResponse.<UserResponse>builder().data(userService.getCurrentUser(user)).build();
    }

    @PostMapping(path = "/api/users/current", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> updateCurrentUser(User user, @ModelAttribute UpdateUserRequest request){
        userService.updateUser(user, request);
        return WebResponse.<String>builder().data("OK").build();
    }

}
