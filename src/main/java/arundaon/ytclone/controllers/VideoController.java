package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.*;
import arundaon.ytclone.services.VideoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
public class VideoController {
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(path ="/api/videos",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> uploadVideo(User user, @ModelAttribute UploadVideoRequest request){
        videoService.upload(user,request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PatchMapping(path ="/api/videos/{videoId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<UpdateVideoResponse> updateVideo(User user, @PathVariable String videoId, @RequestBody UpdateVideoRequest request){
        request.setId(videoId);
        UpdateVideoResponse updatedVideo = videoService.update(user, request);
        return WebResponse.<UpdateVideoResponse>builder().data(updatedVideo).build();
    }

    @GetMapping(path="/api/videos/{videoId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<VideoResponse> getVideo(@PathVariable String videoId){
        return WebResponse.<VideoResponse>builder().data(videoService.getVideo(videoId)).build();
    }

    @GetMapping(path="/api/videos",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<List<VideoInfo>> searchVideo(
            @RequestParam(required = false,defaultValue = "") String value,
            @RequestParam(required = false,defaultValue = "0") Integer page,
            @RequestParam(required = false,defaultValue = "10") Integer size
    ){

        Page<VideoInfo> results = videoService.search(value,page, size);
        return WebResponse.<List<VideoInfo>>builder()
                .data(results.getContent())
                .paging(PagingResponse.builder()
                        .current(results.getNumber())
                        .total(results.getTotalPages())
                        .size(results.getSize())
                        .build())
                .build();
    }

    @DeleteMapping(path="/api/videos/{videoId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> removeVideo(User user, @PathVariable String videoId){
        videoService.remove(user,videoId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PatchMapping(path = "/api/videos/{videoId}/views",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> viewVideo(@PathVariable String videoId){
        videoService.incrementViews(videoId);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PostMapping(path = "/api/videos/{videoId}/like",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> likeVideo(User user, @PathVariable String videoId, @RequestBody LikeRequest request){

        if (request.isLike()){
          videoService.likeVideo(user, videoId);
        }

        else{
            videoService.unlikeVideo(user, videoId);
        }

        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(path = "/api/videos/{videoId}/like",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<LikeResponse> getLike(User user, @PathVariable String videoId){

        return WebResponse.<LikeResponse>builder().data(videoService.userLiked(user,videoId)).build();
    }
}

