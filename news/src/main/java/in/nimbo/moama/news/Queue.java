package in.nimbo.moama.news;

import in.nimbo.moama.news.fetcher.NewsURLQueue;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class Queue<T> implements NewsURLQueue<T> {
    private ArrayBlockingQueue<T> queue;
    private static final Logger LOGGER = Logger.getRootLogger();


    public Queue(int capacity) {
        queue = new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public List<T> getUrls() throws InterruptedException {
        return Collections.singletonList(queue.take());
    }

    @Override
    public final void addUrls(List<T> urls) {
        urls.forEach(url -> {
            try {
                queue.put(url);
            } catch (InterruptedException e) {
                LOGGER.error("Exception adding " + url + " to queue", e);
            }
        });
    }

    @Override
    public int size() {
        return queue.size();
    }

}
