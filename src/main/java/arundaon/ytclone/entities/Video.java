package arundaon.ytclone.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="videos")
public class Video {
    @Id
    private String id;
    private String title;
    private String video;
    private String description;
    private String thumbnail;
    private Long views= 0L;

    @Column(name="created_at")
    private LocalDateTime createdAt;



    @ManyToOne
    @JoinColumn(name="username",referencedColumnName = "username")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "video_likes",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "username"))
    private Set<User> likes;

    @OneToMany(mappedBy = "video")
    private List<Comment> comments;
}
