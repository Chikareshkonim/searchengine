package in.nimbo.moama;

import in.nimbo.moama.configmanager.ConfigManager;
import in.nimbo.moama.news.Queue;
import in.nimbo.moama.news.fetcher.NewsFetcher;
import in.nimbo.moama.news.fetcher.NewsInfo;
import in.nimbo.moama.news.fetcher.NewsURLQueue;
import in.nimbo.moama.news.fetcher.RSSReader;
import in.nimbo.moama.news.listener.Function;
import in.nimbo.moama.news.listener.Listener;
import org.apache.log4j.Logger;

import java.io.IOException;

import static in.nimbo.moama.configmanager.ConfigManager.FileType.PROPERTIES;
import static in.nimbo.moama.news.newsutil.NewsPropertyType.NEWS_LISTENER_PORT;
import static in.nimbo.moama.news.newsutil.NewsPropertyType.NEWS_QUEUE_CAPACITY;


public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException {
        LOGGER.trace("news started");
        ConfigManager.getInstance().load(App.class.getResourceAsStream("/news.properties"), PROPERTIES);
        LOGGER.info("configs loaded");
        new Listener().listen(Function.class, ConfigManager.getInstance().getIntProperty(NEWS_LISTENER_PORT));
        LOGGER.info("listener started");
        int newsCapacity = ConfigManager.getInstance().getIntProperty(NEWS_QUEUE_CAPACITY);
        NewsURLQueue<NewsInfo> news = new Queue<>(newsCapacity);
        RSSReader reader = new RSSReader(news);
        LOGGER.trace("created rss reader");
        new Thread(reader).start();
        NewsFetcher fetcher = new NewsFetcher(news);
        LOGGER.trace("created news fetcher");
        new Thread(fetcher).start();
    }
}
