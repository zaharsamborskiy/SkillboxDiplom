package searchengine.service;

import searchengine.model.Lemma;
import searchengine.model.Site;

import java.util.List;

public interface LemmaService {
    void add(Lemma lemma);

    void addAll(List<Lemma> lemmas);

    List<Lemma> getAllBySiteAndLemmaIn(Site site, List<String> list);
}
