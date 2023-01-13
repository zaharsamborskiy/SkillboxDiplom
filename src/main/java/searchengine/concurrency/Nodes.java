package searchengine.concurrency;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.model.Page;
import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.service.IndexService;
import searchengine.service.PageService;
import searchengine.service.SiteService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.RecursiveTask;

public class Nodes extends RecursiveTask<Boolean> {
    private Node node;
    private static Set<String> allChildrens = new TreeSet<>();

    private Site site;

    private SiteService siteService;
    private PageService pageService;

    private IndexService indexService;

    public Nodes(Node url, Site site, SiteService siteService, PageService pageService, IndexService indexService) {
        this.node = url;
        this.site = site;
        this.siteService = siteService;
        this.pageService = pageService;
        this.indexService = indexService;
    }


    @Override
    protected Boolean compute() {
        Set<Nodes> taskSet = new HashSet<>();
        try {
            Thread.sleep(250);

            Connection.Response res = Jsoup.connect(node.getUrl())
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .execute();
            Document doc = res.parse();
            this.pageService.add(new Page(this.site, node.getUrl(), res.statusCode(), doc.html()));
            this.site.setStatusTime(LocalDateTime.now());
            this.siteService.add(this.site);
            this.indexService.indexing(node.getUrl());

            Elements href = doc.select("a[href]");
            for (Element e : href) {
                String page = e.absUrl("href");
                if (correctLink(page)) {
                    allChildrens.add(page);
                    node.addChildren(new Node(page));
                }
            }
        } catch (HttpStatusException e) {
            this.pageService.add(new Page(this.site, node.getUrl(), e.getStatusCode(), "No content"));
            this.site.setStatusTime(LocalDateTime.now());
            this.siteService.add(this.site);
        } catch (IOException e) {
            //e.printStackTrace();
            this.site.setStatusTime(LocalDateTime.now());
            this.site.setLastError(e.getClass().getSimpleName() + ";" + e.getMessage());
            this.siteService.add(this.site);
            return false;
        }
        catch (InterruptedException e){

        }

        for (Node pages : node.getChildren()) {
            Nodes task = new Nodes(new Node(pages.getUrl()), this.site, this.siteService, this.pageService, this.indexService);
            task.fork();
            taskSet.add(task);
        }

        for (Nodes task : taskSet) {
            Nodes pagepage = task;
            //pagepage.fork();
            if (task.node.getChildren().size() >= 1) {
                node.addChildren(pagepage.node);
            }
            task.join();
        }
        return true;
    }


    private boolean correctLink(String url) {
        return ((!url.isEmpty())
                && (url.startsWith(node.getUrl()))
                && (!url.contains("@"))
                && (!allChildrens.contains(url))
                && (!url.contains("#"))
                && (!url.matches("([^\\s]+(\\.(?i)(jpg|png|gif|bmp|pdf))$)")));
    }

}
