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
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.Facade.ThreadEntryFacade;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
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
    
    @Inject
    private ThreadEntryFacade threadEntryFacade; 


    /* ***********************************************************
     *                      Injected services
     ************************************************************ */

    @Inject
    private Messages messages;


    @SetupRender
    public void setupRender() {
    	logger.debug("setupRender()");
    	List<ThreadVo> allThread = threadEntryFacade.getAllThread();
        for (ThreadVo threadVo : allThread) {
        	logger.debug("thread name : " + threadVo.getName());
        	try {
				List<ThreadEntryVo> allThreadEntries = threadEntryFacade.getAllThreadEntryVo(userVo, threadVo);
				for (ThreadEntryVo threadEntryVo : allThreadEntries) {
					logger.debug("threadEntryVo name : " + threadEntryVo.getFileName());
					List<TagVo> tags = threadEntryVo.getTags();
					for (TagVo tagVo : tags) {
						logger.debug("tagVo : " + tagVo.toString());
					}
				}
				
			} catch (BusinessException e) {
				e.printStackTrace();
			}
		}
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
