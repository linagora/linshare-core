/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.view.tapestry.pages.help;

import java.io.IOException;
import java.util.Properties;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.objects.HelpsASO;
import org.slf4j.Logger;

/**
 * Help pages
 * @author ncharles
 *
 */
public class Index {
	@Inject 
	private Logger logger;

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

	/* ***********************************************************
	 *                      Injected services
	 ************************************************************ */

	@SuppressWarnings("unused")
	@SessionState
	private HelpsASO helpsASO;
	
	
	@SessionState(create=false)
	private UserVo userVo;
	
	@Inject
	private Messages messages;
	
	@SetupRender
	public void init(){
		helpsASO=null;
	}
	
	public boolean isUserAdmin(){
		if(userVo==null){ return false;} else return userVo.isAdministrator();
	};

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

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
		}
		if (prop.getProperty("Implementation-Version") != null) {
			return prop.getProperty("Implementation-Version");	
		} else {
			return "trunk";
		}
    }
}
