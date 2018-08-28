package in.nimbo.moama;

import com.google.protobuf.ServiceException;
import in.nimbo.moama.configmanager.ConfigManager;
import in.nimbo.moama.util.PropertyType;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static in.nimbo.moama.configmanager.ConfigManager.FileType.PROPERTIES;

public class HBaseManager {
    TableName tableName;
    String family1;
    String family2;
    ConfigManager configManager;
    static Logger errorLogger = Logger.getLogger("error");
    Configuration configuration;
    static int sizeLimit = 0;
    final List<Put> puts;

    HBaseManager(String configPath) {
        try {
            configManager = new ConfigManager(new File(getClass().getClassLoader().getResource(configPath).getFile()).getAbsolutePath(), PROPERTIES);
        } catch (IOException e) {
            errorLogger.error("Loading properties failed");
        }
        configuration = HBaseConfiguration.create();
        configuration.addResource(getClass().getResourceAsStream("/hbase-site.xml"));
        tableName = TableName.valueOf(configManager.getProperty(PropertyType.H_BASE_TABLE));
        family1 = configManager.getProperty(PropertyType.H_BASE_CONTENT_FAMILY);
        family2 = configManager.getProperty(PropertyType.H_BASE_RANK_FAMILY);
        sizeLimit = Integer.parseInt(configManager.getProperty(PropertyType.PUT_SIZE_LIMIT));
        puts = new ArrayList<>();
        boolean status = false;
        while (!status) {
            try {
                HBaseAdmin.checkHBaseAvailable(configuration);
                status = true;
            } catch (ServiceException | IOException e) {
                errorLogger.error(e.getMessage());
            }
        }
    }

    public boolean createTable() {
        try (Connection connection = ConnectionFactory.createConnection(configuration)) {
            Admin admin = connection.getAdmin();
            HTableDescriptor hTableDescriptor = new HTableDescriptor(tableName);
            hTableDescriptor.addFamily(new HColumnDescriptor(family1));
            hTableDescriptor.addFamily(new HColumnDescriptor(family2));
            if (!admin.tableExists(tableName))
                admin.createTable(hTableDescriptor);
            admin.close();
            connection.close();
            return true;

        } catch (IOException e) {
            errorLogger.error(e.getMessage());
            return false;
        }
    }

    String generateRowKeyFromUrl(String link) {
        String domain;
        try {
            domain = new URL(link).getHost();
        } catch (MalformedURLException e) {
            domain = "ERROR";
        }
        String[] urlSections = link.split(domain);
        String[] domainSections = domain.split("\\.");
        StringBuilder domainToHBase = new StringBuilder();
        for (int i = domainSections.length - 1; i >= 0; i--) {
            domainToHBase.append(domainSections[i]);
            if (i == 0) {
                if (!link.startsWith(domain)) {
                    domainToHBase.append(".").append(urlSections[0]);
                }
            } else {
                domainToHBase.append(".");
            }
        }
        return domainToHBase + "-" + urlSections[urlSections.length - 1];
    }
}
