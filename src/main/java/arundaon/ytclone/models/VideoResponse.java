package arundaon.ytclone.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoResponse {
    private String id;
    private String title;
    private String description;

    private UserInfo uploader;
    private List<VideoComment> comments;
}

