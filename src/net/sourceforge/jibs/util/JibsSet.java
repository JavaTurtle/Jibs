/*
 * Created on 30.03.2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sourceforge.jibs.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class JibsSet {
	private static Logger logger = Logger.getLogger(JibsSet.class);
	private Map<String, String> setMap;

	public JibsSet(String boardStyle, String lineLength, String pageLength,
			String redoubles, String sortwho, String timeZone) {
		setMap = new HashMap<String, String>();
		setMap.put("boardstyle", boardStyle);
		setMap.put("linelength", lineLength);
		setMap.put("pagelength", pageLength);
		setMap.put("redoubles", redoubles);
		setMap.put("sortwho", sortwho);
		setMap.put("timezone", timeZone);
	}

	public String get(String key) {
		String obj = setMap.get(key);

		if (obj != null) {
			return obj;
		}

		logger.warn("JibsSet:get(" + key + ") not defined.");

		return null;
	}

	public Map<String, String> getSetMap() {
		return setMap;
	}
}
