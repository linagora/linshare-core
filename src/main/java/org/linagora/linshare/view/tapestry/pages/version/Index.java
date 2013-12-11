package org.linagora.linshare.view.tapestry.pages.version;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {

	private static Logger logger = LoggerFactory.getLogger(Index.class);
	
	 public String getVersion() {
	    	Properties prop = new Properties();
	    	try {
	    		if (this.getClass().getResourceAsStream("/version.properties") != null) {
	    			prop.load(this.getClass().getResourceAsStream("/version.properties"));
	    		} else {
	    			logger.debug("Impossible to load version.properties, Is this a dev environnement?");
	    		}
			} catch (IOException e) {
				 logger.debug("Impossible to load version.properties, Is this a dev environnement?");
				 logger.debug(e.toString());
			}
			if (prop.getProperty("Implementation-Version") != null) {
				return prop.getProperty("Implementation-Version");	
			} else {
				return "trunk";
			}
	    }
}
