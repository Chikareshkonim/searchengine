package in.nimbo.moama.fetcher;

import in.nimbo.moama.RSSs;

import java.io.IOException;

public class RSSReader implements Runnable {

    private NewsURLQueue<NewsInfo> newsQueue;

    public RSSReader(NewsURLQueue<NewsInfo> newsQueue) {
        this.newsQueue = newsQueue;
    }

    @Override
    public void run() {
        while (true) {
            RSSs.getInstance().getRssToDomainMap().entrySet().stream().parallel().forEach(entry -> {
                try {
                    newsQueue.addUrls(RSSParser.parse(entry.getKey(), entry.getValue()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}