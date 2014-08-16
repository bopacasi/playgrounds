package playground;

import java.util.Properties;

import static java.util.Map.Entry;

/**
 * Created on 16.06.14 8:45.
 */
public class SystemProperties {
    public static void main(String[] args) {
        final Properties properties = System.getProperties();

        for (Entry<Object, Object> entry : properties.entrySet()) {
            System.out.printf("%38s : %-38s%n",
                    entry.getKey(),
                    entry.getValue());
        }
    }
}
