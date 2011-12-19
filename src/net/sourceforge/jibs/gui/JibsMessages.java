package net.sourceforge.jibs.gui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.ibatis.io.Resources;
import org.apache.log4j.Logger;

public class JibsMessages {
	private static Logger logger = Logger.getLogger(JibsMessages.class);
	private Map<String, String> messageMap = null;

	public JibsMessages(String filename) {
		BufferedReader buffer = null;

		try {
			Reader messageReader = Resources.getResourceAsReader(filename);
			logger.info("Reading message file (" + filename + ")");
			buffer = new BufferedReader(messageReader);

			messageMap = new HashMap<String, String>();

			String line;

			while ((line = buffer.readLine()) != null) {
				if (!line.startsWith("#")) {
					StringTokenizer stoken = new StringTokenizer(line, "=");

					while (stoken.hasMoreTokens()) {
						String par = stoken.nextToken();
						String value = "";

						if (stoken.hasMoreTokens()) {
							value = stoken.nextToken();

							// already defined ?
							if (messageMap.containsKey(par)) {
								logger.warn("" + par + " already defined.");
							}

							messageMap.put(par, value);
//							logger.debug(par + "=" + value);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.warn(e);
		} catch (IOException e) {
			logger.warn(e);
		} finally {
			try {
				if (buffer != null) {
					buffer.close();
				}
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	public String convert(String string) {
		return load(string);
	}

	public synchronized String convert(String string, Object... parameter) {
		String s = load(string);
		String format = MessageFormat.format(s, parameter);
		return format;
	}

	private String load(String ident) {
		String s = (String) messageMap.get(ident);

		if (s == null) {
			logger.warn("Message:\"" + ident + "\" not found.");
			s = "";
		}

		return s;
	}

	public Map<String, String> getMessageMap() {
		return messageMap;
	}
	
}
