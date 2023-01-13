package searchengine.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.concurrency.Node;
import searchengine.concurrency.Nodes;
import searchengine.config.SitesList;
import searchengine.dto.Result;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.model.*;
import searchengine.service.*;
import searchengine.util.texts.TextUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ApiController {

    private StatisticsService statisticsService;
    private SiteService siteService;

    private SitesList sitesList;

    private PageService pageService;

    private IndexService indexService;

    @Autowired
    public void setStatisticsService(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Autowired
    public void setSiteService(SiteService siteService) {
        this.siteService = siteService;
    }

    @Autowired
    public void setSitesList(SitesList sitesList) {
        this.sitesList = sitesList;
    }

    @Autowired
    public void setPageService(PageService pageService) {
        this.pageService = pageService;
    }

    @Autowired
    public void setIndexService(IndexService indexService) {
        this.indexService = indexService;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    private ForkJoinPool forkJoinPool = new ForkJoinPool();
    private HashMap<Site, Boolean> booleanHashMap = new HashMap<>();

    @GetMapping("/startIndexing")
    public ResponseEntity<Result> startIndexing() {
        if (this.forkJoinPool.getActiveThreadCount() > 0) {
            return new ResponseEntity<>(new Result(false, "Индексация уже запущена"), HttpStatus.BAD_REQUEST);
        }

        List<searchengine.config.Site> sites = this.sitesList.getSites();

        for (searchengine.config.Site site : sites) {
            List<Site> res = this.siteService.deleteAllByUrl(site.getUrl());
            System.out.println(res);
            Site newSite = new Site(Status.INDEXING, LocalDateTime.now(), null, site.getUrl(), site.getName());
            this.siteService.add(newSite);

            this.booleanHashMap.put(newSite, false);

            Node nodeParent = new Node(site.getUrl());
            Boolean result = forkJoinPool.invoke(new Nodes(nodeParent, newSite, this.siteService, this.pageService, this.indexService));
            if (result) {
                newSite.setStatus(Status.INDEXED);
            } else {
                newSite.setStatus(Status.FAILED);
            }
            newSite.setStatusTime(LocalDateTime.now());
            this.siteService.add(newSite);
            this.booleanHashMap.put(newSite, true);
        }
        return new ResponseEntity<>(new Result(true), HttpStatus.OK);
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Result> stopIndexing() {
        if (this.forkJoinPool.getActiveThreadCount() == 0) {
            return new ResponseEntity<>(new Result(false, "Индексация не запущена"), HttpStatus.BAD_REQUEST);
        }

        for (var pair : this.booleanHashMap.entrySet()) {
            Site site = pair.getKey();
            boolean result = pair.getValue();
            if (!result) {
                site.setLastError("Индексация остановлена пользователем");
                site.setStatus(Status.FAILED);
                site.setStatusTime(LocalDateTime.now());
                this.siteService.add(site);
            }
        }

        this.forkJoinPool.shutdownNow();
        this.forkJoinPool = new ForkJoinPool();
        return new ResponseEntity<>(new Result(true), HttpStatus.OK);
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Result> indexPage(@RequestParam String url) {
        try {
            this.indexService.indexing(url);
            return new ResponseEntity<>(new Result(true), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Result(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @Autowired
    private LemmaService lemmaService;

    @GetMapping("/search")
    public void search(@RequestParam String query, @RequestParam(required = false) String site,
                       @RequestParam(required = false) Integer offset, @RequestParam(required = false) Integer limit){

        Site siteRes = this.siteService.getByUrl(site);
        TextUtil textUtil = new TextUtil(query);
        try {
            LinkedHashMap<String, Integer> wordsMap = textUtil.countWords();
            List<String> words = new ArrayList<>(wordsMap.keySet());
            List<Lemma> lemmaInSite = this.lemmaService.getAllBySiteAndLemmaIn(siteRes, words);
            /*int del = (int) (lemmaInSite.size() * 0.1);
            lemmaInSite = lemmaInSite.subList(0, lemmaInSite.size() - del);*/

            List<Index> indices = this.indexService.getAllByLemmaId(lemmaInSite.get(0).getId());

            for (int i = 1; i < lemmaInSite.size(); i++) {
                Lemma lemma = lemmaInSite.get(i);
                List<Index> collect = this.indexService.getAllByLemmaId(lemma.getId());
                indices.retainAll(collect);
            }

            //System.out.println(indices);


            float sumRank = 0;
            for(Index index : indices){
                sumRank += index.getRank();
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
