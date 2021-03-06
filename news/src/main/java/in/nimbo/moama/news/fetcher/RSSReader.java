package in.nimbo.moama.news.fetcher;

import in.nimbo.moama.news.RSSs;
import in.nimbo.moama.configmanager.ConfigManager;
import in.nimbo.moama.metrics.FloatMeter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static in.nimbo.moama.news.newsutil.NewsPropertyType.NEWS_READER_WAIT;

public class RSSReader implements Runnable {

    private NewsURLQueue<NewsInfo> newsQueue;
    private FloatMeter floatMeter = new FloatMeter("RSSReaderTime");
    private static final int SLEEP_TIME = ConfigManager.getInstance().getIntProperty(NEWS_READER_WAIT);
    private static final Logger LOGGER = LogManager.getRootLogger();

    public RSSReader(NewsURLQueue<NewsInfo> newsQueue) {
        this.newsQueue = newsQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                LOGGER.error("InterruptedException at RSSReader", e);
            }
            long initTime = System.currentTimeMillis();
            RSSs.getInstance().getRssToDomainMap().entrySet().stream().parallel().forEach(entry -> {
                if (RSSs.getInstance().isPolite(entry.getValue())) {
                    try {
                        newsQueue.addUrls(RSSParser.parse(entry.getKey(), entry.getValue()));
                    } catch (IOException e) {
                        LOGGER.error("IOException at RSSReader", e);
                    }
                }
            });
            floatMeter.add((float) (System.currentTimeMillis() - initTime) / 1000);
        }
    }
}
