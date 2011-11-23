package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestProperties {
	private static Properties properties;

	public static String getKey(String key) {
		try {
			if (properties == null) {
				properties = new Properties();
				properties.load(TestProperties.class
						.getResourceAsStream("/test.properties"));
			}
			return properties.getProperty(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
