package searchengine.service;

import searchengine.model.Index;

import java.util.List;

public interface IndexService {
    void add(Index index);

    boolean indexing(String url);

    List<Index> getAllByLemmaId(int id);
}
