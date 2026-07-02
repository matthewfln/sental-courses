package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
        } catch (IOException e) {
            System.out.println("Не удалось загрузить config.properties, будут использованы значения по умолчанию.");
        }
    }

    public static int getStaleMonths() {
        return Integer.parseInt(properties.getProperty("stale.book.months", "6"));
    }

    public static boolean isAutoFulfillRequests() {
        return Boolean.parseBoolean(properties.getProperty("auto.fulfill.requests", "true"));
    }
}