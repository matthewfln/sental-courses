package util;

import config.ConfigProperty;
import config.PropertyType;

public final class Config {
    private static Config instance;
    private static final int DEFAULT_STALE_MONTHS = 6;

    @ConfigProperty(propertyName = "stale.months", type = PropertyType.INTEGER)
    private int staleMonths = DEFAULT_STALE_MONTHS;

    @ConfigProperty(propertyName = "requests.complete.on.arrival", type = PropertyType.BOOLEAN)
    private boolean autoFulfillRequests = true;

    private Config() {
    }

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
