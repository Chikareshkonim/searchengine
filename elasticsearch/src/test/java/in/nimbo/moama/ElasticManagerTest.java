package in.nimbo.moama;

import in.nimbo.moama.configmanager.ConfigManager;
import in.nimbo.moama.document.WebDocument;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class ElasticManagerTest {
    private RestHighLevelClient client;
    private IndexRequest indexRequest;
    private BulkRequest bulkRequest;
    private ElasticManager elasticManager;

    @Before
    public void setUp() throws IOException {
        ConfigManager.getInstance().load(getClass().getResourceAsStream("/config.properties"), ConfigManager.FileType.PROPERTIES);
        elasticManager = new ElasticManager();
    }

    @Test
    public void testElastic() {
        client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("46.4.120.138", 9200, "http")));
        indexRequest = new IndexRequest("newspages", "_doc");
        bulkRequest = new BulkRequest();
        WebDocument documentTest = new WebDocument();
        documentTest.setPageLink("http://b.com");
        documentTest.setTextDoc("this is b");
        documentTest.setTitle("woww");
        documentTest.setLinks(new ArrayList<>());
        JSONObject document = new JSONObject();
        document.put("url", "f.com");
        document.put("content", "");
        document.put("title", "yes");
        document.put("date", "2015-01-01");
        System.out.println(document);
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder();
            try {
                builder.startObject();
                {
                    Set<String> keys = document.keySet();
                    keys.forEach(key -> {
                        try {
                            if (!key.equals("outLinks")) {
                                builder.field(key, document.get(key));
                                System.out.println(key);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("ERROR");
                        }
                    });
                }
                System.out.println(builder.toString());
                builder.endObject();
                indexRequest.source(builder);
                bulkRequest.add(indexRequest);
                indexRequest = new IndexRequest("test", "_doc");
                if (bulkRequest.estimatedSizeInBytes() >= 1 ||
                        bulkRequest.numberOfActions() >= 1) {
                    System.out.println(bulkRequest.numberOfActions());
                    client.bulk(bulkRequest);
                    bulkRequest = new BulkRequest();
                    System.out.println("added                     ");
//                    jmxManager.markNewAddedToElastic();
                }
            } catch (IOException e) {
                System.out.println("ERROR");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void putTest() {
        Map<String, String> document = new HashMap<>();
        document.put("pageLink", "naive.com");
        document.put("content", "gdshgssjsjfsjsfj");
        document.put("title", "yes");
        document.put("date", "Sun, 02 Sep 2018 10:33:34 +0430");
        List<Map<String, String>> list = new ArrayList<>();
        list.add(document);
        elasticManager.myput(list);
    }

    @Test
    public void aggTest() throws IOException {
        assertEquals("group",
                elasticManager.newsWordTrends("Sun, 02 Sep 2018")
                        .get(0));
    }

    @Test
    public void getTermvectorTest() throws IOException {
        JSONArray test = new JSONArray();
        test.put("e04102ef6b844805d8d8ef79070a7c6e");
        test.put("fa3ddd0d9534d0679a14b8eb3e0dace8");
        System.out.println(elasticManager.getTermVector(test.toString()));
    }
}