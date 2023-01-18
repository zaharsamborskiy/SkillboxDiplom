package searchengine.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.Index;

import java.util.List;

@Transactional
@Repository
public interface IndexRepository extends JpaRepository<Index, Integer> {

    List<Index> getAllByPageId(int id);
    List<Index> getAllByLemmaId(int id);

}
