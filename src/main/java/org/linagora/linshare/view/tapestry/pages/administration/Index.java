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
package org.linagora.linshare.view.tapestry.pages.administration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.AbstractDomainFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Index {

    private static final int FACTORMULTI = 1024;
    
	private static Logger logger = LoggerFactory.getLogger(Index.class);

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

	@Inject
	private Messages messages;
    @Inject
    private AbstractDomainFacade domainFacade;
    
    @Inject
    private UserFacade userFacade;
    
    @SessionState
    private UserVo loginUser;
    

    /* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */

	@Persist
	@Property
	private List<String> domains;
	
	@Persist
	@Property
	private String selectedDomain;
	
	@Property
	@Persist
	private boolean superadmin;
	
	@Property
	@Persist
	private boolean admin;
	
	@Property
	private boolean noDomain;
    

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    @SetupRender
    public void init() throws BusinessException {
    	
    	superadmin = loginUser.isSuperAdmin();
    	admin = loginUser.isAdministrator();
    	    	    	
    	if (superadmin||admin) {
    		domains = domainFacade.getAllDomainIdentifiers(loginUser);
    	}
    	
    	if (domains == null || domains.isEmpty()) {
    		
    		noDomain = true;
    		
    	} else {
    	
    		noDomain = false;
    		
	    	if (selectedDomain == null) {
	    		if (!(domains == null || domains.size() < 1)) {
	    			selectedDomain = domains.get(0);
	    		}
	    	}  
    	}
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
			 logger.debug(e.toString());
		}
		if (prop.getProperty("Implementation-Version") != null) {
			return prop.getProperty("Implementation-Version");	
		} else {
			return "trunk";
		}
    }
    
    public boolean getShowMimeTypeView() {
    	if(!noDomain) {
    		logger.debug("selectedDomain : " + selectedDomain );
    		return domainFacade.isMimeTypeFilterEnableFor(selectedDomain, loginUser);
    	}
    	return false;
    }

    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }
    
}
