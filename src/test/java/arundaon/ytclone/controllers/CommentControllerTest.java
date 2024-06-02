package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import arundaon.ytclone.models.CreateCommentRequest;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.repositories.CommentRepository;
import arundaon.ytclone.repositories.UserRepository;
import arundaon.ytclone.repositories.VideoRepository;
import arundaon.ytclone.security.BCrypt;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
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
    void createCommentSuccessful() throws Exception {
        createTestUsersAndVideos();

        CreateCommentRequest request = new CreateCommentRequest("hey");

        mockMvc.perform(
                        post("/api/videos/video1/comments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken2")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("OK", response.getData());
                    assertEquals(10, commentRepository.count());

                });
    }

    @Test
    void createCommentVideoNotFound() throws Exception {
        createTestUsersAndVideos();

        CreateCommentRequest request = new CreateCommentRequest("hey");

        mockMvc.perform(
                        post("/api/videos/videonotfound/comments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken2")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());


                });
    }

    @Test
    void createCommentUnauthorized() throws Exception {
        createTestUsersAndVideos();

        CreateCommentRequest request = new CreateCommentRequest("hey");

        mockMvc.perform(
                        post("/api/videos/video1/comments")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken2invalid")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                });
    }

    @Test
    void deleteCommentSuccessful() throws Exception {
        createTestUsersAndVideos();
        Comment comment = commentRepository.findFirstByVideoId("video1").orElse(null);
        mockMvc.perform(
                        delete("/api/videos/video1/comments/"+comment.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken1"))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("OK", response.getData());
                    Comment deletedComment = commentRepository.findById( comment.getId().intValue()).orElse(null);
                    assertNull(deletedComment);
                });
    }

    @Test
    void deleteOtherUsersComment() throws Exception {
        createTestUsersAndVideos();
        Comment comment = commentRepository.findFirstByVideoId("video1").orElse(null);
        mockMvc.perform(
                        delete("/api/videos/video1/comments/"+comment.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken2"))
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    Comment deletedComment = commentRepository.findFirstByVideoId("video1").orElse(null);
                    assertNotNull(deletedComment);
                });
    }

    @Test
    void deleteCommentInvalidToken() throws Exception {
        createTestUsersAndVideos();
        Comment comment = commentRepository.findFirstByVideoId("video1").orElse(null);
        mockMvc.perform(
                        delete("/api/videos/video1/comments/"+comment.getId())
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytokenisinvalid"))
                .andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    Comment deletedComment = commentRepository.findFirstByVideoId("video1").orElse(null);
                    assertNotNull(deletedComment);
                });
    }










    void createTestUsersAndVideos(){
        User []users = new User[3];
        Video []videos = new Video[3];

        for(int i = 1; i <= 3; i++){
            User user = new User();
            user.setName("test"+i);
            user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
            user.setUsername("test"+i);
            user.setProfile("profile"+i);
            user.setToken("mytoken"+i);
            user.setExpiredAt(System.currentTimeMillis() + 100000L);
            userRepository.save(user);
            users[i-1] = user;

            Video video = new Video();
            video.setUser(user);
            video.setId("video"+i);
            video.setVideo("video"+i);
            video.setTitle("title"+i);
            video.setDescription("description"+i);
            videoRepository.save(video);
            videos[i-1] = video;

        }

        for(int i = 1; i <= 3; i++){
            for(int j = 1; j <= 3; j++){
                Comment comment = new Comment();
                comment.setUser(users[j-1]);
                comment.setVideo(videos[i-1]);
                comment.setComment("hello world "+j);

                commentRepository.save(comment);
            }
        }
    }
    User createATestUser(){
        User user = new User();
        user.setName("test");
        user.setPassword(BCrypt.hashpw("password",BCrypt.gensalt()));
        user.setUsername("test");
        user.setProfile("profile");
        user.setToken("mytoken");
        user.setExpiredAt(System.currentTimeMillis() + 100000L);

        userRepository.save(user);
        return user;
    }
}