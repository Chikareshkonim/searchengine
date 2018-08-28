package in.nimbo.moama.crawler;

import in.nimbo.moama.configmanager.ConfigManager;
import in.nimbo.moama.document.Link;
import in.nimbo.moama.UrlHandler;
import in.nimbo.moama.document.WebDocument;
import in.nimbo.moama.crawler.domainvalidation.DomainFrequencyHandler;
import in.nimbo.moama.crawler.domainvalidation.HashDuplicateChecker;
import in.nimbo.moama.crawler.language.LangDetector;
import in.nimbo.moama.exception.DomainFrequencyException;
import in.nimbo.moama.exception.DuplicateLinkException;
import in.nimbo.moama.exception.IllegalLanguageException;
import in.nimbo.moama.exception.URLException;
import in.nimbo.moama.metrics.JMXManager;
import in.nimbo.moama.metrics.Metrics;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    private static Logger errorLogger = Logger.getLogger("error");
    private static LangDetector langDetector;
    private static Parser parser;
    private static HashDuplicateChecker HashDuplicateChecker;
    private static DomainFrequencyHandler domainTimeHandler;

    public synchronized static Parser getInstance(ConfigManager configManager) {
        HashDuplicateChecker = HashDuplicateChecker.getInstance();
        domainTimeHandler = DomainFrequencyHandler.getInstance();
        if (parser == null)
            parser = new Parser();
        return parser;
    }

    public static void setLangDetector(LangDetector langDetector) {
        Parser.langDetector = langDetector;
    }

    public WebDocument parse(String url, JMXManager jmxManager) throws IllegalLanguageException, IOException {
        String text;
        Document document = Jsoup.connect(url).validateTLSCertificates(false).get();
        HashDuplicateChecker.confirm(url);
        Metrics.numberOfUrlReceived++;
        jmxManager.markNewUrlReceived();
        WebDocument webDocument = new WebDocument();
        text = document.text();
        checkLanguage(document, text);
        Metrics.numberOfLanguagePassed++;
        jmxManager.markNewLanguagePassed();
        Link[] links = UrlHandler.getLinks(document.getElementsByTag("a"), new URL(url).getHost());
        webDocument.setTextDoc(text);
        webDocument.setTitle(document.title());
        webDocument.setPageLink(url);
        webDocument.setLinks(new ArrayList<>(Arrays.asList(links)));
        Metrics.numberOFCrawledPage++;
        jmxManager.markNewCrawledPage();
        return webDocument;

    }

    private void checkLanguage(Document document, String text) throws IllegalLanguageException {
        try {
            String lang = document.getElementsByAttribute("lang").get(0).attr("lang").toLowerCase();
            if (lang.equals("en") || lang.startsWith("en-")) {
                return;
            }
            throw new IllegalLanguageException();
        } catch (RuntimeException e) {
            //getElementsByAttribute throws a NullPointerException if document doesn't have lang tag
            langDetector.languageCheck(text);
        }
    }
}

