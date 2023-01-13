package searchengine.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;

@Transactional
@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Integer> {
    List<Lemma> getAllBySiteAndLemmaInOrderByFrequency(Site site, List<String> list);
}
