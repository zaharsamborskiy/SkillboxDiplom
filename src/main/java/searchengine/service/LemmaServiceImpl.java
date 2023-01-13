package searchengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Lemma;
import searchengine.model.Site;
import searchengine.respository.LemmaRepository;

import java.util.List;

@Service
public class LemmaServiceImpl implements LemmaService{
    private LemmaRepository lemmaRepository;

    @Autowired
    public void setLemmaRepository(LemmaRepository lemmaRepository) {
        this.lemmaRepository = lemmaRepository;
    }

    @Override
    public void add(Lemma lemma) {
        this.lemmaRepository.save(lemma);
    }

    public void addAll(List<Lemma> lemmas){
        this.lemmaRepository.saveAll(lemmas);
    }

    @Override
    public List<Lemma> getAllBySiteAndLemmaIn(Site site, List<String> list){
        return this.lemmaRepository.getAllBySiteAndLemmaInOrderByFrequency(site, list);
    }
}
