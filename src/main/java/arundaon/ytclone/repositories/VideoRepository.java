package arundaon.ytclone.repositories;

import arundaon.ytclone.entities.User;
import arundaon.ytclone.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video,String>, JpaSpecificationExecutor<Video> {
    public Optional<Video> findByUserAndId(User user, String id);
}
