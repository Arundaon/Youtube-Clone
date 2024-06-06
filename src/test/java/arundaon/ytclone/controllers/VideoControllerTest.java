package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import arundaon.ytclone.models.*;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class VideoControllerTest {

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
    void createVideoSuccessful() throws Exception {
        createATestUser();
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "videos/test_video.mp4",
                "video/mp4",
                "video content".getBytes()
        );
        mockMvc.perform(
                        multipart("/api/videos")
                                .file(videoFile)
                                .param("title", "my video")
                                .param("description","my description")
                                .header("X-API-TOKEN","mytoken")
                )
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("OK",response.getData());
                    List<Video> video = videoRepository.findAll();
                    assertEquals(1, video.size());
                    assertEquals("my video", video.get(0).getTitle());
                    assertEquals("my description", video.get(0).getDescription());

                });
    }

    @Test
    void createVideoUnauthorized() throws Exception {
        createATestUser();
        MockMultipartFile videoFile = new MockMultipartFile(
                "video",
                "video/test_video.mp4",
                "video/mp4",
                "video content".getBytes()
        );
        mockMvc.perform(
                        multipart("/api/videos")
                                .file(videoFile)
                                .param("title", "my video")
                                .param("description","my description")
                                .header("X-API-TOKEN","mytokeninvalid")
                )
                .andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());

                });
    }


    @Test
    void getVideoSuccesful() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        get("/api/videos/video1")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<VideoResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getData());
                    assertNotNull(response.getData().getId());
                    assertNotNull(response.getData().getVideo());
                    assertNotNull(response.getData().getTitle());
                    assertNotNull(response.getData().getUploader());
                    assertNotNull(response.getData().getComments());
                    assertNotNull(response.getData().getCreatedAt());

                });
    }

    @Test
    void getNonexistentVideo() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        get("/api/videos/videothatdoesntexist")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<VideoResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());

                });
    }

    @Test
    void updateVideoSuccessful() throws Exception {
        User user= createATestUser();
        Video video = new Video();
        video.setId("videoid");
        video.setTitle("best video ever");
        video.setVideo("video");
        video.setDescription("description");
        video.setUser(user);

        videoRepository.save(video);

        UpdateVideoRequest request = new UpdateVideoRequest(video.getId(),"newtitle","newdesc");


        mockMvc.perform(
                        patch("/api/videos/videoid")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<VideoResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("videoid", response.getData().getId());
                    assertEquals("newtitle", response.getData().getTitle());
                    assertEquals("newdesc", response.getData().getDescription());

                });
    }

    @Test
    void updateVideoVideoNotFound() throws Exception {
        User user= createATestUser();
        Video video = new Video();
        video.setId("videoid");
        video.setTitle("best video ever");
        video.setVideo("video");
        video.setDescription("description");
        video.setUser(user);

        videoRepository.save(video);

        UpdateVideoRequest request = new UpdateVideoRequest(video.getId(),"newtitle","newdesc");


        mockMvc.perform(
                        patch("/api/videos/videoidsalah")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<VideoResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());

                });
    }

    @Test
    void updateVideoWrongToken() throws Exception {
        User user= createATestUser();
        Video video = new Video();
        video.setId("videoid");
        video.setTitle("best video ever");
        video.setVideo("video");
        video.setDescription("description");
        video.setUser(user);

        videoRepository.save(video);

        UpdateVideoRequest request = new UpdateVideoRequest(video.getId(),"newtitle","newdesc");


        mockMvc.perform(
                        patch("/api/videos/videoid")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mywrongtoken")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<VideoResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());

                });
    }

    @Test
    void updateOtherUsersVideo() throws Exception {
        createTestUsersAndVideos();


        UpdateVideoRequest request = new UpdateVideoRequest("video2","newtitle","newdesc");


        mockMvc.perform(
                        patch("/api/videos/video2")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken1")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<VideoResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());

                });
    }

    @Test
    void SearchVideoNoParams() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        get("/api/videos")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken1"))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<List<VideoInfo>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals(3,response.getData().size());
                    assertEquals(0,response.getPaging().getCurrent());
                    assertEquals(10,response.getPaging().getSize());
                    assertEquals(1,response.getPaging().getTotal());
                });
    }

    @Test
    void SearchVideoWithParams() throws Exception {
        createTestUsersAndVideos();


        UpdateVideoRequest request = new UpdateVideoRequest("video2","newtitle","newdesc");

        mockMvc.perform(
                        get("/api/videos")
                                .queryParam("value","title1")
                                .queryParam("page","0")
                                .queryParam("size","100")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken1")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<List<VideoInfo>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals(1, response.getData().size());
                    assertEquals(0, response.getPaging().getCurrent() );
                    assertEquals(100, response.getPaging().getSize());
                    assertEquals(1, response.getPaging().getTotal());

                });
    }
    @Test
    void SearchVideoNoResult() throws Exception {
        createTestUsersAndVideos();


        UpdateVideoRequest request = new UpdateVideoRequest("video2","newtitle","newdesc");

        mockMvc.perform(
                        get("/api/videos")
                                .queryParam("value","videonores")
                                .queryParam("page","0")
                                .queryParam("size","100")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken1")
                                .content(objectMapper.writeValueAsString(request)))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<List<VideoInfo>> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });

                    assertEquals(response.getData().size(), 0);
                    assertEquals(response.getPaging().getCurrent(), 0);
                    assertEquals(response.getPaging().getSize(), 100);
                    assertEquals(response.getPaging().getTotal(), 0);

                });
    }

    @Test
    void deleteVideoSuccessful() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        delete("/api/videos/video2")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken2"))
                .andExpectAll(status().isOk())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertEquals("OK", response.getData());
                    Video video = videoRepository.findById("video2").orElse(null);
                    assertNull(video);
                });
    }

    @Test
    void deleteNonexistentVideo() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        delete("/api/videos/nonexistent")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken2"))
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                });
    }

    @Test
    void deleteOtherUsersVideo() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        delete("/api/videos/video2")
                                .accept(MediaType.APPLICATION_JSON)
                                .header("X-API-TOKEN","mytoken1"))
                .andExpectAll(status().isNotFound())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    Video video = videoRepository.findById("video2").orElse(null);
                    assertNotNull(video);
                });
    }

    @Test
    void deleteWithInvalidToken() throws Exception {
        createTestUsersAndVideos();

        mockMvc.perform(
                        delete("/api/videos/video2")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpectAll(status().isUnauthorized())
                .andDo(result->{
                    WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
                    });
                    assertNotNull(response.getErrors());
                    Video video = videoRepository.findById("video2").orElse(null);
                    assertNotNull(video);

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