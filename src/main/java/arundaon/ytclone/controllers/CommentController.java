package arundaon.ytclone.controllers;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.models.CreateCommentRequest;
import arundaon.ytclone.models.WebResponse;
import arundaon.ytclone.services.CommentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class CommentController {

    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }



    @PostMapping(path ="/api/videos/{videoId}/comments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> publishComment(User user, @PathVariable String videoId, @RequestBody CreateCommentRequest request){
        commentService.create(user, videoId, request);
        return WebResponse.<String>builder().data("OK").build();

    }

    @DeleteMapping(path="/api/videos/{videoId}/comments/{commentId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    WebResponse<String> removeVideo(User user, @PathVariable String videoId, @PathVariable Long commentId){
        commentService.remove(user, videoId, commentId);
        return WebResponse.<String>builder().data("OK").build();
    }
}
