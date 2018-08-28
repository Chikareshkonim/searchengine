package in.nimbo.moama.util;

import in.nimbo.moama.configmanager.PropertyType;

public enum CrawlerPropertyType implements PropertyType {
    CRAWLER_POLITE_TIME("crawler.polite..time"), CRAWLER_INTERNAL_LINK_ADD_TO_KAFKA("crawler.internal.link.add.to.kafka"),
    CRAWLER_NUMBER_OF_THREADS("crawler.number.of.threads"), CRAWLER_START_NEW_THREAD_DELAY_MS("crawler.start.new.thread.delay.ms"),
    CRAWLER_MIN_OF_EACH_THREAD_QUEUE("crawler.min.of.each.thread.queue"), CRAWLER_THREAD_PRIORITY("crawler.thread.priority"),
    CRAWLER_DOMAIN_CHECKER_HASH_PRIME("crawler.domain.checker.hash.prime"), CRAWLER_DUPLICATE_HASH_PRIME("crawler.duplicate.hash.prime"),
    CRAWLER_SHUFFLE_SIZE("crawler.shuffle.size"), HBASE_TABLE("hbase.table"),
    HBASE_FAMILY_OUTLINKS("hbase.family.outlinks"), HBASE_FAMILY_SCORE("hbase.family.score");

    private String type;

    CrawlerPropertyType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}
