package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.LoginUserRequest;
import arundaon.ytclone.models.TokenResponse;
import arundaon.ytclone.models.UserResponse;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.repositories.UserRepository;
import arundaon.ytclone.security.BCrypt;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testUserNotRegistered() throws Exception {
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("password");


        mockMvc.perform(post("/api/auth/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                });
    }

    @Test
    public void passwordWrong() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setName("test");

        userRepository.save(user);
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("passwordsalah");


        mockMvc.perform(post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                });
    }

    @Test
    public void loginSuccess() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setName("test");

        userRepository.save(user);
        LoginUserRequest request = new LoginUserRequest();
        request.setUsername("test");
        request.setPassword("password");


        mockMvc.perform(post("/api/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<TokenResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData().getToken());
                    assertNotNull(response.getData().getExpiredAt());

                    User userInDB = userRepository.findById(user.getUsername()).orElse(null);
                    assertNotNull(userInDB);
                    assertEquals(userInDB.getToken(), response.getData().getToken());
                    assertEquals(userInDB.getExpiredAt(), response.getData().getExpiredAt());


                });
    }

    @Test
    public void logoutSuccess() throws Exception{
        User user = new User();
        user.setName("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setProfile("profile");
        user.setToken("mytoken");
        user.setExpiredAt(System.currentTimeMillis() + 100000L);

        userRepository.save(user);

        mockMvc.perform(delete("/api/auth/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "mytoken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertEquals("OK",response.getData());

        });
        User userDB = userRepository.findById(user.getUsername()).orElse(null);
        assertNotNull(userDB);
        assertNull(userDB.getToken());
        assertNull(userDB.getExpiredAt());
    }


@Test
    public void logoutFailed() throws Exception{

        mockMvc.perform(delete("/api/auth/logout")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-API-TOKEN", "mytoken")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result->{
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getErrors());
        });

    }

}