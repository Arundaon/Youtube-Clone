package arundaon.ytclone.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VideoComment {
    private UserInfo user;
    private LocalDateTime createdAt;
    private String comment;
}
