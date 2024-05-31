package arundaon.ytclone.services;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.LoginUserRequest;
import arundaon.ytclone.models.TokenResponse;
import arundaon.ytclone.repositories.UserRepository;
import arundaon.ytclone.security.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private ValidationService validationService;
    private UserRepository userRepository;
    public AuthService(ValidationService validationService, UserRepository userRepository) {
        this.validationService = validationService;
        this.userRepository = userRepository;
    }
    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(request.getUsername()).orElseThrow(()-> {
            return new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or password is incorrect");
        });

        if(!(BCrypt.checkpw(request.getPassword(),user.getPassword()))){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Username or password is incorrect");
        }
        user.setToken(UUID.randomUUID().toString());
        user.setExpiredAt(next30Days());
        userRepository.save(user);
        return TokenResponse.builder().token(user.getToken()).expiredAt(user.getExpiredAt()).build();
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setExpiredAt(null);

        userRepository.save(user);
    }

    private Long next30Days(){
        return System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000);
    }
}
