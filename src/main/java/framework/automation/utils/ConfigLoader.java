package framework.automation.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads {@code config.properties} once when the class is initialized. Values are read with
 * sensible defaults if the file or a specific key is missing.
 */
@Slf4j
public final class ConfigLoader {

    private static final String CONFIG_RESOURCE = "config.properties";

    private static final Properties PROPERTIES = new Properties();

    private static final String DEFAULT_BASE_URL = "https://opencart.abstracta.us/";
    private static final int DEFAULT_EXPLICIT_WAIT_SECONDS = 10;
    private static final String DEFAULT_BROWSER = "chrome";

    static {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_RESOURCE)) {
            if (in == null) {
                log.warn("{} not found on the classpath; using default values.", CONFIG_RESOURCE);
            } else {
                PROPERTIES.load(in);
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Could not read " + CONFIG_RESOURCE + ": " + e.getMessage());
        }
    }

    private ConfigLoader() {
    }

    /**
     * Base URL of the application under test.
     */
    public static String getBaseUrl() {
        String url = PROPERTIES.getProperty("base.url", DEFAULT_BASE_URL).trim();
        return url.isEmpty() ? DEFAULT_BASE_URL : url;
    }

    /**
     * Maximum explicit wait ({@link org.openqa.selenium.support.ui.WebDriverWait}) in seconds.
     */
    public static int getExplicitWait() {
        return parsePositiveInt("timeout.explicit", DEFAULT_EXPLICIT_WAIT_SECONDS);
    }

    /**
     * Requested browser name (e.g. chrome, firefox, edge).
     */
    public static String getBrowser() {
        String b = PROPERTIES.getProperty("browser", DEFAULT_BROWSER).trim();
        return b.isEmpty() ? DEFAULT_BROWSER : b.toLowerCase();
    }

    /**
     * Driver implicit wait in seconds (for future use if wired globally).
     */
    public static int getImplicitWait() {
        return parsePositiveInt("timeout.implicit", 2);
    }

    /**
     * Whether the browser should run in headless mode.
     */
    public static boolean isHeadless() {
        return Boolean.parseBoolean(PROPERTIES.getProperty("headless", "false").trim());
    }

    private static int parsePositiveInt(String key, int defaultValue) {
        String raw = PROPERTIES.getProperty(key);
        if (raw == null || raw.isBlank()) {
            return defaultValue;
        }
        try {
            int value = Integer.parseInt(raw.trim());
            return value > 0 ? value : defaultValue;
        } catch (NumberFormatException e) {
            log.warn("Invalid value for {}: '{}'; using {}.", key, raw, defaultValue);
            return defaultValue;
        }
    }
}
