import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * OpenWeatherMap API keys, read at startup so that no key is ever committed.
 *
 * Looked up in this order:
 *   1. environment variables OWM_API_KEY and (optional) OWM_API_KEY_BACKUP
 *   2. config.properties in the working directory, with the same two names
 *      (copy config.properties.example; the real file is git-ignored)
 */
public final class ApiKeys {
    private static final String[] NAMES = {"OWM_API_KEY", "OWM_API_KEY_BACKUP"};
    private static final List<String> KEYS = load();

    private ApiKeys() {}

    private static List<String> load() {
        Properties file = new Properties();
        try (InputStream in = new FileInputStream("config.properties")) {
            file.load(in);
        } catch (IOException ignored) {
            // no config file: environment variables only
        }

        List<String> keys = new ArrayList<>();
        for (String name : NAMES) {
            String value = System.getenv(name);
            if (value == null || value.isBlank()) value = file.getProperty(name);
            if (value != null && !value.isBlank()) keys.add(value.trim());
        }
        if (keys.isEmpty()) {
            System.err.println("No OpenWeatherMap API key found. Set OWM_API_KEY, "
                    + "or copy config.properties.example to config.properties and fill it in.");
        }
        return Collections.unmodifiableList(keys);
    }

    /** How many keys are configured (0, 1 or 2). */
    public static int count() {
        return KEYS.size();
    }

    /** The key at {@code index}, wrapping around; empty if none is configured. */
    public static String get(int index) {
        return KEYS.isEmpty() ? "" : KEYS.get(Math.floorMod(index, KEYS.size()));
    }
}
