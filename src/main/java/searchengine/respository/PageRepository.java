package searchengine.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Page;

@Repository
@Transactional
public interface PageRepository extends JpaRepository<Page, Integer> {
    Page getByPath(String url);
}
