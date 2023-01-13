package searchengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Index;
import searchengine.model.Lemma;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.respository.IndexRepository;
import searchengine.util.texts.TextUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class IndexServiceImpl implements IndexService {
    private IndexRepository indexRepository;

    private LemmaService lemmaService;

    private PageService pageService;


    @Autowired
    public void setIndexRepository(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @Autowired
    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    @Override
    public void add(Index index) {
        this.indexRepository.save(index);
    }

    @Autowired
    public void setLemmaService(LemmaService lemmaService) {
        this.lemmaService = lemmaService;
    }


    @Override
    public boolean indexing(String url) {
        Page page = this.pageService.getByUrl(url);
        if (page == null) {
            throw new IllegalArgumentException("Данная страница находится за пределами сайтов," +
                    "указанных в конфигурационном файле");
        }

        List<Index> indices = this.indexRepository.getAllByPageId(page.getId());
        if (indices.size() > 0) {
            return true;
        }

        Site site = page.getSite();

        try {
            TextUtil textUtil = new TextUtil(page.getContent());
            LinkedHashMap<String, Integer> words = textUtil.countWords();
            for (var entry : words.entrySet()) {
                String word = entry.getKey();
                Lemma lemma = site.getLemmas().stream().filter(x -> x.getLemma().equals(word)).findFirst().orElse(null);
                if (lemma == null) {
                    lemma = new Lemma(site, word, 1);
                } else {
                    lemma.setFrequency(lemma.getFrequency() + 1);
                }
                int count = entry.getValue();
                this.lemmaService.add(lemma);
                this.add(new Index(page, lemma, count));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public List<Index> getAllByLemmaId(int id){
        return this.indexRepository.getAllByLemmaId(id);
    }
}
