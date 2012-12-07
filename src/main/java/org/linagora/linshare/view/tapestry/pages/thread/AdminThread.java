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

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.components.WindowWithEffects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdminThread {

	private static final Logger logger = LoggerFactory.getLogger(AdminThread.class);
	
    @SessionState
    @Property
    private ShareSessionObjects shareSessionObjects;

    @SessionState
    @Property
    private UserVo userVo;
    
    @Property
    @Persist
    private ThreadVo currentThread;
    
    @Component(parameters = {"style=bluelighting", "show=false", "width=520", "height=180"})
    private WindowWithEffects memberEditWindow;

    @InjectComponent
    private Zone memberEditTemplateZone;
    
	@Property
	@Persist
	private List<ThreadMemberVo> members;
	
	@Property
	private ThreadMemberVo member;
	
    @Inject
    private Messages messages;
    
    @Inject
    private ThreadEntryFacade threadEntryFacade;

    @Persist
    @Property
	private String selectedMemberId;
    
	@Inject
	private Block adminBlock, userBlock, restrictedUserBlock;
    
    /*
     * Assuming currentThread isn't be null
     */
    @SetupRender
    public void init() {
    	try {
    		members = threadEntryFacade.getThreadMembers(currentThread);
		} catch (BusinessException e) {
    		logger.error(e.getMessage());
    		logger.debug(e.toString());
		}
    }
    
    public Object onActivate() {
    	if (currentThread == null) {
    		return Index.class;
    	}
    	try {
			if (!threadEntryFacade.userIsAdmin(userVo, currentThread)) {
				logger.info("Unauthorized");
				return ThreadContent.class;
			}
		} catch (BusinessException e) {
			e.printStackTrace();
			return ThreadContent.class;
		}
    	return null;
    }
    
	/*
	 * Handle page layout with Tapestry Blocks
	 */
	public Object getType() {
		return (member.isAdmin() ? adminBlock : member.isCanUpload() ? userBlock : restrictedUserBlock);
	}
    
    /*
     * Called externally before calling the page.
     * Refer to Tapestry Documentation about passing data from page to page
     * Setup render will fail if this is not called (currentThread would be null)
     */
    public void setSelectedCurrentThread(ThreadVo currentThread) {
    	this.currentThread = currentThread;
    }
    
    public Zone onActionFromEditMember(String identifier) {
    	logger.info("Trying to edit member with identifier : " + identifier);
    	selectedMemberId = identifier;
    	return memberEditTemplateZone;
    }

}
