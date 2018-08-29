package in.nimbo.moama.crawler.domainvalidation;

import in.nimbo.moama.configmanager.ConfigManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

public class DomainFrequencyHandlerTest {

    @Test
    public void isAllow() throws IOException {
        ConfigManager.getInstance().load(getClass().getResourceAsStream("/test.properties"),ConfigManager.FileType.PROPERTIES);
        DomainFrequencyHandler.getInstance().isAllow(host(("https://aws.amazon.com/marketplace/pp/B01GSSXSV0/&ref_=_mkt_ste_menu?nc2=h_l3_ms"))) ;
        Assert.assertFalse(DomainFrequencyHandler.getInstance().isAllow(host("https://aws.amazon.com/glacier/?nc2=h_mo")));
    }

    private String  host(String s) throws MalformedURLException {
        return new URL(s).getHost();
    }
}