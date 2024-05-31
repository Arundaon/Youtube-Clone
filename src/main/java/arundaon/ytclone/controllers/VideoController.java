package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.UpdateVideoRequest;
import arundaon.ytclone.models.UploadVideoRequest;
import arundaon.ytclone.models.VideoResponse;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.services.VideoService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class VideoController {
    VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping(path ="/api/videos",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> uploadVideo(User user, @RequestBody UploadVideoRequest request){
        videoService.upload(user,request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @PutMapping(path ="/api/videos/{videoId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<VideoResponse> updateVideo(User user, @PathVariable String videoId, @RequestBody UpdateVideoRequest request){
        request.setId(videoId);
        VideoResponse updatedVideo = videoService.update(user, request);
        return WebResponse.<VideoResponse>builder().data(updatedVideo).build();
    }

    @GetMapping(path="/api/videos/{videoId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<VideoResponse> getVideo(@PathVariable String videoId){
        return WebResponse.<VideoResponse>builder().data(videoService.getVideo(videoId)).build();
    }
}

