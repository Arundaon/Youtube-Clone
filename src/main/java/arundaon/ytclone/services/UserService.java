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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    private ValidationService validationService;
    private UserRepository userRepository;
    @Value("${video.upload.directory}")
    private String uploadDir;

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
        return UserResponse.builder().username(user.getUsername()).name(user.getName()).bio(user.getBio()).profile(user.getProfile()).build();
    };

    @Transactional
    public void updateUser(User user, UpdateUserRequest request){
        validationService.validate(request);
        if(Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }
        if(Objects.nonNull(request.getPassword())){
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }
        if(Objects.nonNull(request.getBio())){
            user.setBio(request.getBio());
        }

        log.info(String.valueOf(request.getProfile()));
        if(Objects.nonNull(request.getProfile())){
            log.info("---not null---");
            String filePath = savePicture(request.getProfile(),user.getUsername());
            user.setProfile(filePath);
        }
        userRepository.save(user);
    }

    public String savePicture(MultipartFile file, String username){

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));

        if(!Set.of(".jpg",".jpeg",".png", ".bmp")
                .contains(fileExtension.toLowerCase())
        ) {
            throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Filetype is not supported");
        }

        String newFilename = username + fileExtension;

        Path path = Paths.get(Paths.get("").toAbsolutePath().toString(),uploadDir+"/profiles/", newFilename);

        try{
            Files.createDirectories(path.getParent());
            file.transferTo(path.toFile());
            return "profiles/"+newFilename;
        }
        catch (Exception e){
            log.info(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There are some problem when uploading the file");
        }


    }
}
