package arundaon.ytclone.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoResponse {
    private String id;
    private String title;
    private String video;
    private String description;
    private LocalDateTime createdAt;

    private UserInfo uploader;
    private List<VideoComment> comments;
}

