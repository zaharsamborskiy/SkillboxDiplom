package searchengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Site;
import searchengine.respository.SiteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SiteServiceImpl implements SiteService{

    private SiteRepository siteRepository;

    @Autowired
    public void setSiteRepository(SiteRepository siteRepository) {
        this.siteRepository = siteRepository;
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
    public  Site getByUrl(String url){
        return this.siteRepository.getByUrl(url).orElse(null);
    }
}
