package arundaon.ytclone.repositories;

import arundaon.ytclone.entities.Comment;
import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Integer> {

    List<Comment> findAllByVideo(Video video);

    Optional<Comment> findFirstByUserAndVideoAndId(User user, Video video, Long id);
}
