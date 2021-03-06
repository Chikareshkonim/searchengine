package in.nimbo.moama.crawler.domainvalidation;

import in.nimbo.moama.configmanager.ConfigManager;
import in.nimbo.moama.util.CrawlerPropertyType;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class HashDuplicateChecker {
    private static HashDuplicateChecker ourInstance;
    private static final Logger errorLogger = Logger.getLogger("error");
    public static HashDuplicateChecker getInstance() {
        if(ourInstance == null){
            ourInstance = new HashDuplicateChecker();
        }
        return ourInstance;
    }
    private static int hashPrime ;
    private byte[] linkHashTableTime;
    private final byte[] twoPowers;
    private HashDuplicateChecker() {
        hashPrime = Integer.parseInt(ConfigManager.getInstance().getProperty(CrawlerPropertyType.CRAWLER_DUPLICATE_HASH_PRIME));
        linkHashTableTime = new byte[hashPrime / 8 + 1];
        twoPowers= new byte[]{0b1, 0b10, 0b100, 0b1000, 0b10000, 0b100000, 0b1000000, -128};//-128 = 10000000
    }
    public void loadHashTable() throws IOException {
        try {
            linkHashTableTime= Files.readAllBytes(new File("duplicateHashTable.information").toPath());
        } catch (IOException e) {
            HashDuplicateChecker.getInstance().saveHashTable();
            throw e;
        }
    }
    public void confirm(String url) {
        int hash = hash(url) % hashPrime;
        if (hash < 0)
            hash += hashPrime;
        int hasht = hash / 8;
        int index = hash % 8;
        if (index<0)
            index+=8;
        linkHashTableTime[hasht]|=twoPowers[index];
    }
    public boolean isDuplicate(String url) {
        int hash = hash(url) % hashPrime;
        if (hash < 0)
            hash += hashPrime;
        int hasht = hash / 8;
        int index = hash % 8;
        if (index<0)
            index+=8;
        return (linkHashTableTime[hasht] & twoPowers[index]) != 0;
    }
    public void saveHashTable(){
        try(FileOutputStream fileOutputStream=new FileOutputStream("duplicateHashTable.information")){
            fileOutputStream.write(linkHashTableTime);
        } catch (IOException e) {
            errorLogger.error(e.getMessage());
        }
    }
    private int hash(Object object){
        return object.hashCode();
    }

}
