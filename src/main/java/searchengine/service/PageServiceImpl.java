package searchengine.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.Page;
import searchengine.respository.PageRepository;

@Service
public class PageServiceImpl implements PageService{
    private PageRepository pageRepository;

    @Autowired
    public void setPageRepository(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    @Override
    public void add(Page page) {
        this.pageRepository.save(page);
    }

    @Override
    public Page getByUrl(String url) {
        return this.pageRepository.getByPath(url);
    }
}
