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
package org.linagora.linshare.view.tapestry.pages.thread;

import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.Facade.ThreadEntryFacade;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Index {

	private static final Logger logger = LoggerFactory.getLogger(Index.class);
	

    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    @Property
    private UserVo userVo;

    @InjectPage
    private ProjectThread projectThreadPage;

    @Property
    @Persist
    private	List<ThreadVo> projects;
    
    @Property
    private ThreadVo currentProject;
    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

    @Inject
    private Messages messages;
    
    @Inject
    private ThreadEntryFacade threadEntryFacade; 


    @SetupRender
    public void setupRender() {
    	logger.debug("setupRender()");
    	projects = threadEntryFacade.getAllThread();
    }
    
    public Object onActionFromShowProjectThread(String lsUuid) {
    	logger.debug("Debut onActionFromShowProjectThread");
    	for (ThreadVo project : projects) {
    		logger.debug("Looping through projects list :");
    		logger.debug("current project name = " + project.getName());
    		logger.debug("current project lsUuid = " + project.getLsUuid());
    		logger.debug("selected lsUuid = " + lsUuid);
			if (project.getLsUuid().equals(lsUuid)) {
		    	projectThreadPage.setMySelectedProject(project);
		    	logger.debug("Projet " + project.getName() + "recupere");
		    	return projectThreadPage;
			}
		}
    	return null;
    }

    @AfterRender
    public void afterRender() {
        ;
    }

    Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }
}
