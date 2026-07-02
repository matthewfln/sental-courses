package util;

import config.ConfigProperty;
import config.PropertyType;

public class Config {
    private static Config instance;

    @ConfigProperty(propertyName = "stale.months", type = PropertyType.INTEGER)
    private int staleMonths = 6;

    @ConfigProperty(propertyName = "requests.complete.on.arrival", type = PropertyType.BOOLEAN)
    private boolean autoFulfillRequests = true;

    private Config() {}

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public int getStaleMonths() {
        return staleMonths;
    }

    public boolean isAutoFulfillRequests() {
        return autoFulfillRequests;
    }
}