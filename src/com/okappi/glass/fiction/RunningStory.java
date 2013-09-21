package com.okappi.glass.fiction;

import com.google.glassware.MainServlet;

import java.util.HashMap;
import java.util.logging.Logger;

public class RunningStory {
	private static final Logger LOG = Logger.getLogger(MainServlet.class.getSimpleName());
	private static final HashMap<String, String> stories = new HashMap<String, String>();

	private static final RunningStory _theInstance = new RunningStory();

	private RunningStory() {
	}

	public static RunningStory getInstance() {
		return _theInstance;
	}
	public void put(String userId, String url) {
		LOG.info("pushing record: "+userId+" has "+url);
		stories.put(userId, url);
	}
	public String get(String userId) {
		LOG.info("getting record: "+userId+" size: "+stories.size());
		return stories.get(userId);
	}
	public void remove(String deviceId) {
		stories.remove(deviceId);
	}
	
}