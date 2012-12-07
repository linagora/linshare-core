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
package org.linagora.linshare.view.tapestry.components;

import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadMemberEditForm {
	
	private static Logger logger = LoggerFactory.getLogger(ThreadMemberEditForm.class);
	
    @SessionState
    @Property
    private UserVo userVo;
	
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<ThreadMemberVo> members;
    
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private String editMemberId;
    
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private ThreadVo currentThread;
    
    @Property
    @Persist
    private ThreadMemberVo member;

    @Property
	private boolean admin;

    @Property
	private boolean canUpload;
    
    @Property
    private String fullName;
    
    @Inject
    private ThreadEntryFacade threadEntryFacade;
    
    @SetupRender
    public void init() {
    	member = null;
    	if (editMemberId == null) {
    		logger.error("No member selected, identifier is null");
    	}
    	else {
    		logger.info("editMemberId : " + editMemberId);
    		for (ThreadMemberVo m : members) {
    			logger.info("Comparing member id : " + m.getLsUuid() + " with editMemberId : " + editMemberId);
    			if (m.getLsUuid().equals(editMemberId)) {
    				member = m;
    				admin = member.isAdmin();
    				canUpload = member.isCanUpload();
    				fullName = member.getFullName();
    			}
    		}
    	}
    	if (member == null) {
    		logger.error("Member doesn't existst. Can't find a user with this identifier in members list");
    		member = new ThreadMemberVo();
    	}
    }
    
    public void onSuccessFromMemberForm() {
    	member.setAdmin(admin);
    	member.setCanUpload(canUpload);
    	threadEntryFacade.updateMember(userVo, member, currentThread);
    }
    
	public void onSubmit() {
    	logger.info("onSubmit");
    }
}
