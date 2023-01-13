package searchengine.service;

import searchengine.model.Page;

public interface PageService {
    void add(Page page);

    Page getByUrl(String url);
}
