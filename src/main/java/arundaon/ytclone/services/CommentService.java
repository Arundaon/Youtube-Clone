package arundaon.ytclone.services;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import arundaon.ytclone.models.CreateCommentRequest;
import arundaon.ytclone.repositories.CommentRepository;
import arundaon.ytclone.repositories.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

public class CommentService {
    private CommentRepository commentRepository;
    private ValidationService validationService;
    private VideoRepository videoRepository;

    public CommentService(CommentRepository commentRepository, ValidationService validationService) {
        this.commentRepository = commentRepository;
        this.validationService = validationService;
        this.videoRepository = videoRepository;
    }

    @Transactional
    public void create(User user, String videoId, CreateCommentRequest request){
        validationService.validate(request);
        Video video = videoRepository.findById(videoId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setVideo(video);
        comment.setComment(request.getComment());
        commentRepository.save(comment);
    }

    @Transactional
    public void remove(User user, String videoId, Long commentId){
        Video video = videoRepository.findById(videoId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));
        Comment comment = commentRepository.findFirstByUserAndVideoAndId(user, video, commentId)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        commentRepository.delete(comment);

    }
}
