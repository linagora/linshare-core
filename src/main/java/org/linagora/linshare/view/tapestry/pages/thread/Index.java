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

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.facade.FunctionalityFacade;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
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
    private ThreadContent threadContent;

    @Property
    @Persist
    private List<ThreadVo> threads;
    
    @Property
    private ThreadVo currentThread;
    
    @Property
    private boolean showThreadTab;
    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

    @Inject
    private Messages messages;
    
    @Inject
    private ThreadEntryFacade threadEntryFacade;
    
    @Inject
    private FunctionalityFacade functionalityFacade; 


    @SetupRender
    public void setupRender() {
    	logger.debug("setupRender()");
    	threads = threadEntryFacade.getAllMyThread(userVo);
    	showThreadTab = functionalityFacade.isEnableThreadTab(userVo.getDomainIdentifier());
    }

    public Object onActionFromShowThreadContent(String lsUuid) {
    	logger.debug("Debut onActionFromShowThreadContent");
    	for (ThreadVo thread : threads) {
			if (thread.getLsUuid().equals(lsUuid)) {
		    	threadContent.setMySelectedThread(thread);
		    	logger.debug("Projet " + thread.getName() + "recupere");
		    	return threadContent;
			}
		}
    	return null;
    }
    
    public Object onActionFromAddThread() {
    	return null;
    }
    
    @AfterRender
    public void afterRender() {
    }
    
    /**
	 * Format the creation date for good displaying using DateFormatUtils of
	 * apache commons lib.
	 * 
	 * @return creation date the date in localized format.
	 */
	public String getCreationDate() {
		SimpleDateFormat formatter = new SimpleDateFormat(messages.get("global.pattern.timestamp"));
		return formatter.format(currentThread.getCreationDate().getTime());
	}

    Object onException(Throwable cause) {
        shareSessionObjects.addError(messages.get("global.exception.message"));
        logger.error(cause.getMessage());
        cause.printStackTrace();
        return this;
    }
}
