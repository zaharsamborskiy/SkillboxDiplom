package searchengine.service;

import searchengine.dto.PageData;
import searchengine.model.Site;

import java.util.List;
import java.util.Optional;

public interface SiteService {
    List<Site> deleteAllByUrl(String url);

    void add(Site site);

    List<Site> getAll();

    Site get(int id);

    Site getByUrl(String url);

    List<PageData> searchSite(String query, String site, Integer offset, Integer limit);
}
