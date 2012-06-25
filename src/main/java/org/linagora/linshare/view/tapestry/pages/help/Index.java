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
package org.linagora.linshare.view.tapestry.pages.help;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.Facade.FunctionalityFacade;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.objects.HelpsASO;
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
	
	
	@SuppressWarnings("unused")
	@SessionState(create=false)
	private UserVo userVo;
	
	@SuppressWarnings("unused") // used in tml
	@Inject
	private Messages messages;
	
    @Inject
    private FunctionalityFacade functionalityFacade;
    
    @Property
    private boolean showHelp;
    
	@SetupRender
	public void init(){
		showHelp = userVo.isSuperAdmin() | functionalityFacade.isEnableHelpTab(userVo.getDomainIdentifier());
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
}
