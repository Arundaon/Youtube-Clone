package arundaon.ytclone.models;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CompleteVideoResponse {
    // the video
    private String title;
    private String description;

    // uploader
    private User user;
    private List<Comment> comments;

}
