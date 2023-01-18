package searchengine.model;

import java.util.Objects;

public class PageRelev implements Comparable<PageRelev>{
    private Page page;
    private float relev;

    public PageRelev(Page page, float relev) {
        this.page = page;
        this.relev = relev;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public float getRelev() {
        return relev;
    }

    public void setRelev(float relev) {
        this.relev = relev;
    }

    public void divide(float max) {
        this.relev /= max;
    }

    @Override
    public int compareTo(PageRelev o) {
        return -Float.compare(this.relev, o.relev);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PageRelev pageRelev = (PageRelev) o;
        return Objects.equals(page, pageRelev.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(page);
    }
}
