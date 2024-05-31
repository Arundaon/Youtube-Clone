package arundaon.ytclone.services;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.LoginUserRequest;
import arundaon.ytclone.models.RegisterUserRequest;
import arundaon.ytclone.models.UpdateUserRequest;
import arundaon.ytclone.models.UserResponse;
import arundaon.ytclone.repositories.UserRepository;
import arundaon.ytclone.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private ValidationService validationService;
    private UserRepository userRepository;

    public UserService(ValidationService validationService, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.validationService = validationService;
    }

    @Transactional
    public void register(RegisterUserRequest request){
        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User Already Registered");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        userRepository.save(user);
    }

    public UserResponse getCurrentUser(User user){
        return UserResponse.builder().username(user.getUsername()).name(user.getName()).profile(user.getProfile()).build();
    };

    @Transactional
    public void updateUser(User user, UpdateUserRequest request){
        if(Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }
        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }
        if(Objects.nonNull(request.getBio())){
            user.setBio(request.getBio());
        }
        userRepository.save(user);
    }

}
