package arundaon.ytclone.models;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class VideoInfo {
    private String id;
    private String title;
    private String description;
    private String thumbnail;
    private UserInfo uploader;
    private LocalDateTime createdAt;
}
