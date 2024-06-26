package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.RegisterUserRequest;
import arundaon.ytclone.models.UpdateUserRequest;
import arundaon.ytclone.models.UserResponse;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.repositories.CommentRepository;
import arundaon.ytclone.repositories.UserRepository;
import arundaon.ytclone.repositories.VideoRepository;
import arundaon.ytclone.security.BCrypt;
import arundaon.ytclone.services.VideoService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        videoRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {

        RegisterUserRequest request = RegisterUserRequest.builder().username("ary").name("arundaon").password("password").build();

        mockMvc.perform(
                post("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("OK",response.getData());
                });

    }

    @Test
    void testRegisterFailed() throws Exception{
        RegisterUserRequest request = RegisterUserRequest.builder().username("ab").password("password").name("myname").build();

        mockMvc.perform(post("/api/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isBadRequest()).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });

    }

    @Test
    void testUserRegistered() throws Exception{
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setName("test");
        userRepository.save(user);
        RegisterUserRequest request = RegisterUserRequest.builder().username("test").password("password").name("test").build();

        mockMvc.perform(post("/api/users").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isConflict()).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserTokenInvalid() throws Exception{
        mockMvc.perform(get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "invalidtoken")
        ).andExpectAll(status().isUnauthorized()).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserWithoutToken() throws Exception{
        mockMvc.perform(get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(status().isUnauthorized()).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserAuthorized() throws Exception{
        User user = new User();
        user.setName("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setProfile("profile");
        user.setBio("mybio");
        user.setToken("mytoken");
        user.setExpiredAt(System.currentTimeMillis() + 100000L);

        userRepository.save(user);

        mockMvc.perform(get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "mytoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result->{
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNull(response.getErrors());
            assertEquals("test",response.getData().getUsername());
            assertEquals("test",response.getData().getName());
            assertEquals("profile",response.getData().getProfile());
            assertEquals("mybio",response.getData().getBio());
        });
    }

    @Test
    void getUserTokenExpired() throws Exception{
        User user = new User();
        user.setName("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setProfile("profile");
        user.setToken("mytoken");
        user.setExpiredAt(System.currentTimeMillis() - 1000L);

        userRepository.save(user);

        mockMvc.perform(get("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "mytoken")
        ).andExpectAll(status().isUnauthorized()).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getErrors());
        });
    }

// update user

    @Test
    void updateUserSuccess() throws Exception{
        User user = new User();
        user.setName("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setToken("mytoken");
        user.setExpiredAt(System.currentTimeMillis() + 100000L);
        userRepository.save(user);

        MockMultipartFile profile = new MockMultipartFile(
                "image",
                "test_image.png",
                "image/png",
                Files.readAllBytes(new File("contents/profiles/test_image.png").toPath())
//                "test image".getBytes()
//                Files.readAllBytes(Paths.get(Paths.get("").toAbsolutePath().toString(),"contents/profiles/", "test_image.png"))
                );


        mockMvc.perform(multipart("/api/users/current")
                    .file(profile)
                    .param("name","newTest")
                    .param("password","newPass123")
                    .param("bio","hello there!")
                    .header("X-API-TOKEN", "mytoken")
                        .contentType("multipart/form-data")

        )
                .andExpectAll(
                status().isOk()
        ).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("OK",response.getData());

            User updated = userRepository.findById(user.getUsername()).orElse(null );
            assertEquals("test",updated.getUsername());
            assertEquals("newTest",updated.getName());
            assertTrue(BCrypt.checkpw("newPass123",updated.getPassword()));
            assertEquals("hello there!", updated.getBio());
        });
    }

    @Test
    void updateUserFailed() throws Exception{

        mockMvc.perform(multipart("/api/users/current")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "mytoken")
                .param("name","newTest")
                .param("password","newPass123")
                .param("bio","hello there!")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });
    }


}