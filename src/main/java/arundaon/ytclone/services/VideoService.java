package arundaon.ytclone.services;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import arundaon.ytclone.models.*;
import arundaon.ytclone.repositories.CommentRepository;
import arundaon.ytclone.repositories.VideoRepository;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class VideoService {
    private static final Logger log = LoggerFactory.getLogger(VideoService.class);
    private CommentRepository commentRepository;
    private VideoRepository videoRepository;
    private ValidationService validationService;

    @Value("${video.upload.directory}")
    private String uploadDir;

    public VideoService(VideoRepository videoRepository, ValidationService validationService, CommentRepository commentRepository) {
        this.videoRepository = videoRepository;
        this.validationService = validationService;
        this.commentRepository = commentRepository;
    }

    @Transactional
    public void upload(User user, UploadVideoRequest request){
        validationService.validate(request);

        String filePath = saveFile(request.getVideo());

        Video video = new Video();
        video.setId(generateId());
        video.setDescription(request.getDescription());
        video.setTitle(request.getTitle());
        video.setUser(user);
        video.setVideo(filePath);

        videoRepository.save(video);
    }

    @Transactional(readOnly = true)
    public VideoResponse getVideo(String id){
        Video video = videoRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        UserInfo uploader = new UserInfo(video.getUser().getName(),video.getUser().getUsername(),video.getUser().getProfile());

        List<VideoComment> comments = video.getComments().stream().map(comment ->{
            return new VideoComment(
                    comment.getId(),
                    new UserInfo(video.getUser().getName(),video.getUser().getUsername(),video.getUser().getProfile()),
                    comment.getCreatedAt(),
                    comment.getComment()
                    );
        }).toList();

        return VideoResponse.builder().id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription()).uploader(uploader).comments(comments).build();
    }

    @Transactional
    public UpdateVideoResponse update(User user, UpdateVideoRequest request){
        validationService.validate(request);
        Video video = videoRepository.findByUserAndId(user, request.getId())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));

        if(Objects.nonNull(request.getTitle())){
            video.setTitle(request.getTitle());
        }
        if(Objects.nonNull(request.getDescription())){
            video.setDescription(request.getDescription());
        }
        videoRepository.save(video);

        return UpdateVideoResponse.builder().id(video.getId()).title(video.getTitle()).description(video.getDescription()).build();
    }

    @Transactional
    public void remove(User user, String id){
        Video video = videoRepository.findByUserAndId(user,id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video not found"));
        commentRepository.deleteAllByVideo(video);
        videoRepository.delete(video);
    }

    @Transactional(readOnly = true)
    public Page<VideoInfo> search(String value, Integer pageNumber, Integer pageSize){
//        if(value == null || value.isEmpty()){
//            // TODO : Implement if null for optimization
//        }
        Specification<Video> specification = (root, criteriaQuery, criteriaBuilder) -> {
            Predicate predicate = criteriaBuilder.conjunction();
            if (Objects.nonNull(value)) {
                predicate = (criteriaBuilder.or(
                        criteriaBuilder.like(root.get("title"), "%" + value + "%"),
                        criteriaBuilder.like(root.get("description"), "%" + value + "%")
                ));
            }
            return predicate;
        };

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
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


    private String generateId() {
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

    private String saveFile(MultipartFile file){

            String fileName = file.getOriginalFilename();
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));

            if(!Set.of(".mp4",".flv",".mov", ".avi", ".3gpp", ".mpeg4", ".webm", ".mpegs", ".wmv")
                    .contains(fileExtension.toLowerCase())
            ) {
                throw new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Filetype is not supported");
            }

            String newFilename = System.currentTimeMillis() + fileExtension;

            Path path = Paths.get(Paths.get("").toAbsolutePath().toString(),uploadDir, newFilename);
            log.info(path.toString());
            log.info(uploadDir);
            try{
                Files.createDirectories(path.getParent());
                file.transferTo(path.toFile());
                return newFilename;
            }
            catch (Exception e){
                log.info(e.toString());
                log.info(e.getMessage());

                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "There are some problem while uploading the file");
            }

    }
}
