package arundaon.ytclone.repositories;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video,String>, JpaSpecificationExecutor<Video> {
    public Optional<Video> findByUserAndId(User user, String id);

    @Query("SELECT COUNT(l) FROM Video v JOIN v.likes l WHERE v.id = :videoId")
    long countLikesByVideoId(String videoId);

    @Query("SELECT COUNT(v) > 0 FROM Video v JOIN v.likes u WHERE v.id = :videoId AND u.username = :username")
    boolean hasUserLikedVideo(String videoId, String username);
}
