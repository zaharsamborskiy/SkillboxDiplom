package searchengine.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.controllers.ApiController;
import searchengine.dto.PageData;
import searchengine.model.*;
import searchengine.respository.SiteRepository;
import searchengine.util.texts.TextUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class SiteServiceImpl implements SiteService {
    private Logger logger = LoggerFactory.getLogger(SiteServiceImpl.class);

    private SiteRepository siteRepository;

    private LemmaService lemmaService;

    private IndexService indexService;

    @Autowired
    public void setSiteRepository(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
    }

    @Autowired
    public void setLemmaService(LemmaService lemmaService) {
        this.lemmaService = lemmaService;
    }

    @Autowired
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @Override
    public List<Site> deleteAllByUrl(String url) {
        return this.siteRepository.deleteAllByUrl(url);
    }

    @Override
    public void add(Site site) {
        this.siteRepository.save(site);
    }

    @Override
    public List<Site> getAll() {
        return this.siteRepository.findAll();
    }

    @Override
    public Site get(int id) {
        return this.siteRepository.findById(id).orElse(null);
    }

    @Override
    public Site getByUrl(String url) {
        return this.siteRepository.getByUrl(url).orElse(null);
    }

    @Override
    public List<PageData> searchSite(String query, String site, Integer offset, Integer limit) {
        List<PageData> res = new ArrayList<>();
        if (site == null) {
            List<Site> sites = this.getAll();
            for (Site siteRes : sites) {
                try {
                    List<PageData> pageData = getData(siteRes, query, offset, limit);
                    res.addAll(pageData);
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        } else {
            try {
                Site siteRes = this.getByUrl(site);
                res = getData(siteRes, query, offset, limit);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return res;
    }

    public List<PageData> getData(Site siteRes, String query, Integer offset, Integer limit) throws IOException {
        TextUtil textUtil = new TextUtil(query);
        LinkedHashMap<String, Integer> wordsMap = textUtil.countWords();
        List<String> words = new ArrayList<>(wordsMap.keySet());
        List<Lemma> lemmaInSite = this.lemmaService.getAllBySiteAndLemmaIn(siteRes, words);
        if (lemmaInSite.size() == 0) {
            return new ArrayList<>();
        }
        List<Index> indices = this.indexService.getAllByLemmaId(lemmaInSite.get(0).getId());

        for (int i = 1; i < lemmaInSite.size(); i++) {
            Lemma lemma = lemmaInSite.get(i);
            List<Index> collect = this.indexService.getAllByLemmaId(lemma.getId());
            indices.retainAll(collect);
        }
        List<PageRelev> pageRelevance = getPageRelevance(indices);

        return getPageData(siteRes, offset, limit, words, pageRelevance);
    }

    private static List<PageData> getPageData(Site siteRes, Integer offset, Integer limit, List<String> words, List<PageRelev> pageRelevance) {
        return pageRelevance.stream().map(x -> new PageData(siteRes.getUrl(), siteRes.getName(),
                        x.getPage().getPath().replace(siteRes.getUrl(), ""),
                        x.getPage().getContent().substring(x.getPage().getContent().indexOf("<title>") + 7,
                                x.getPage().getContent().indexOf("</title>")),
                        TextUtil.getSnippets(x.getPage().getContent(), words),
                        x.getRelev())).skip(offset == null ? 0 : offset)
                .limit(limit == null ? pageRelevance.size() : limit).toList();
    }

    private static List<PageRelev> getPageRelevance(List<Index> indices) {
        List<PageRelev> pageRelevs = new ArrayList<>();

        for (Index index : indices) {
            //нахождение суммы всех ранк для взятой страницы
            Page page = index.getPage();
            float sum = 0;
            for (Index value : indices) {
                if (value.getPage().getId() == page.getId()) {
                    sum += value.getRank();
                }
            }
            pageRelevs.add(new PageRelev(page, sum));
        }

        float max = (float) pageRelevs.stream().mapToDouble(PageRelev::getRelev).max().orElse(1);

        //нахождение относительная релевантность
        for (PageRelev pageRelev : pageRelevs) {
            pageRelev.divide(max);
        }

        pageRelevs.sort(null);
        pageRelevs = pageRelevs.stream().distinct().toList();
        return pageRelevs;
    }
}
