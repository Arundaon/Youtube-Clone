package arundaon.ytclone.services;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import arundaon.ytclone.models.*;
import arundaon.ytclone.repositories.CommentRepository;
import arundaon.ytclone.repositories.VideoRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class VideoService {
    private CommentRepository commentRepository;
    private VideoRepository videoRepository;
    private ValidationService validationService;

    public VideoService(VideoRepository videoRepository, ValidationService validationService, CommentRepository commentRepository) {
        this.videoRepository = videoRepository;
        this.validationService = validationService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void upload(User user, UploadVideoRequest request){
        validationService.validate(request);
        Video video = new Video();
        video.setId(generateId());
        // TODO: make upload video api
        video.setDescription(request.getDescription());
        video.setTitle(request.getTitle());
        video.setVideo(request.getVideo());
        video.setUser(user);

        videoRepository.save(video);
    }

    @Transactional(readOnly = true)
    public VideoResponse getVideo(String id){
        Video video = videoRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        UserInfo uploader = new UserInfo(video.getUser().getName(),video.getUser().getUsername(),video.getUser().getProfile());

        List<VideoComment> comments = video.getComments().stream().map(comment ->{
            return new VideoComment(
                    new UserInfo(video.getUser().getName(),video.getUser().getUsername(),video.getUser().getProfile()),
                    comment.getCreatedAt(),
                    comment.getComment()
                    );
        }).toList();

        return VideoResponse.builder()
                .title(video.getTitle())
                .description(video.getDescription()).uploader(uploader).comments(comments).build();
    }

    @Transactional
    public VideoResponse update(User user, UpdateVideoRequest request){
        validationService.validate(request);
        Video video = videoRepository.findById(request.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        if(Objects.nonNull(request.getTitle())){
            video.setTitle(request.getTitle());
        }
        if(Objects.nonNull(request.getDescription())){
            video.setDescription(request.getDescription());
        }
        videoRepository.save(video);

        return VideoResponse.builder().title(video.getTitle()).description(video.getDescription()).build();
    }

    @Transactional
    public void remove(User user, String id){
        Video video = videoRepository.findByUserAndId(user,id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));
        videoRepository.delete(video);
    }

    @Transactional(readOnly = true)
    public Page<VideoInfo> search(String value){
        if(value == null || value.isEmpty()){
            // TODO : Implement if null for optimization
        }
        Specification<Video> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List < Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("video"), value));
            if (Objects.nonNull(value)) {
                predicates.add(criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + value + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + value + "%")
                ));
            }
            return criteriaQuery.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(0, 20);
        Page<Video> videos = videoRepository.findAll(specification, pageable);
        List<VideoInfo> videoInfos = videos.getContent().stream().map( video->{
            return new VideoInfo(
                    video.getId(),
                    video.getTitle(),
                    video.getDescription(),
                    video.getThumbnail(),
                    new UserInfo(video.getUser().getName(),video.getUser().getUsername(),video.getUser().getProfile()),
                    video.getCreatedAt()
            );
        }
        ).toList();

        return new PageImpl<>(videoInfos,pageable,videos.getTotalElements());
    }


    public String generateId() {
        UUID originalUUID = UUID.randomUUID();
        byte[] uuidBytes = asByteArray(originalUUID);
        String base64UUID = Base64.getUrlEncoder().encodeToString(uuidBytes);
        return base64UUID.substring(0, 11);
    }

    private byte[] asByteArray(UUID uuid) {
        long mostSignificantBits = uuid.getMostSignificantBits();
        long leastSignificantBits = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];
        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (mostSignificantBits >>> (8 * (7 - i)));
            buffer[8 + i] = (byte) (leastSignificantBits >>> (8 * (7 - i)));
        }
        return buffer;
    }
}
