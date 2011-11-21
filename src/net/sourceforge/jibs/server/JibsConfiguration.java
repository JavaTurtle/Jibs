package net.sourceforge.jibs.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class JibsConfiguration {
	private static Logger logger = Logger.getLogger(JibsConfiguration.class);
	private JibsServer jibsServer = null;
	private Map<String, String> allParameter = null;

	public JibsConfiguration(String fileName) {
		readFile(fileName);
	}

	private void readFile(String fileName) {
		BufferedReader buffer = null;

		try {
			logger.info("Reading configuration file (" + fileName + ")");
			buffer = new BufferedReader(new FileReader(fileName));

			allParameter = new HashMap<String, String>();

			String line;

			while ((line = buffer.readLine()) != null) {
				if (!line.startsWith("#")) {
					StringTokenizer stoken = new StringTokenizer(line, "=");

					while (stoken.hasMoreTokens()) {
						String par = stoken.nextToken();
						String value = "";

						if (stoken.hasMoreTokens()) {
							value = stoken.nextToken();
						}

						allParameter.put(par, value);
						logger.debug(par + "=" + value);
					}
				}
			}
		} catch (FileNotFoundException e) {
			jibsServer.logException(e);
		} catch (IOException e) {
			jibsServer.logException(e);
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				jibsServer.logException(e);
			}
		}
	}

	public String getResource(String key) {
		String value = (String) allParameter.get(key);

		if (value == null) {
			logger.error("Parameter " + key + " not found in configuration");
		}

		return value;
	}
}
