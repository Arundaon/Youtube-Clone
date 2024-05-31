package arundaon.ytclone.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="users")
public class    User {
    @Id
    private String username;
    private String password;
    private String name;
    private String token;

    private String profile;
    private String bio;

    @Column(name="expired_at")
    private Long expiredAt;

    @OneToMany(mappedBy = "user")
    private List<Video> videos;

    @ManyToMany(mappedBy = "likes")
    private Set<Video> likedVideos;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;
}
