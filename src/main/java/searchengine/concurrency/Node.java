package searchengine.concurrency;

import java.util.concurrent.CopyOnWriteArraySet;

public class Node
{
    private String url;
    private volatile Node parent;
    private volatile CopyOnWriteArraySet<Node> children;
    private volatile int level;

    public Node(String url)
    {
        this.url = url;
        this.children = new CopyOnWriteArraySet<>();
        this.parent = null;
        this.level = 0;
    }

    public int setLevel()
    {
        if (parent == null)
        {
            return 0;
        }
        return 1 + parent.getLevel();
    }

    public int getLevel()
    {
        return level;
    }

    public String getUrl()
    {
        return url;
    }

    public CopyOnWriteArraySet<Node> getChildren()
    {
        return children;
    }

    public void setParent(Node parent)
    {
        synchronized (this)
        {
            this.parent = parent;
            this.level = setLevel();
        }
    }

    public void addChildren(Node page)
    {
        if (!children.contains(page) && page.getUrl().startsWith(url) && (!page.getUrl().equals(url)))
        {
            this.children.add(page);
            page.setParent(this);
        }
    }
}
