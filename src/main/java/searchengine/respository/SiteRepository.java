package searchengine.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SiteRepository extends JpaRepository<Site, Integer> {
    List<Site> deleteAllByUrl(String url);

    Optional<Site> getByUrl(String url);
}
