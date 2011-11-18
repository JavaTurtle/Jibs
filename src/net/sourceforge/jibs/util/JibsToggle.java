package net.sourceforge.jibs.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class JibsToggle {
	private static Logger logger = Logger.getLogger(JibsToggle.class);
	private Map<String, Boolean> toggleMap;
	private String[] maps = { "allowpip", "autoboard", "autodouble",
			"automove", "away", "bell", "crawford", "double", "greedy",
			"moreboards", "moves", "notify", "ratings", "ready", "report",
			"silent", "telnet", "watch", "wrap" };

	public JibsToggle(String toggles) {
		toggleMap = new HashMap<String, Boolean>();

		if (toggles != null) {
			for (int i = 0; i < toggles.length(); i++) {
				char value = toggles.charAt(i);

				if (value == '0') {
					toggleMap.put(maps[i], Boolean.FALSE);
				} else {
					toggleMap.put(maps[i], Boolean.TRUE);
				}
			}
		}
	}

	public Boolean get(String key) {
		Object obj = toggleMap.get(key);

		if (obj != null) {
			if (obj instanceof Boolean) {
				return (Boolean) obj;
			}

			return Boolean.FALSE;
		}

		logger.warn("JibsToggle:get(" + key + ") not defined.");

		return Boolean.FALSE;
	}

	public void set(String key, Boolean value) {
		toggleMap.put(key, value);
	}

	public Map<String, Boolean> getToggleMap() {
		return toggleMap;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < maps.length; i++) {
			Boolean bValue = toggleMap.get(maps[i]);

			if (bValue) {
				buffer.append("1");
			} else {
				buffer.append("0");
			}
		}

		return buffer.toString();
	}
}
