/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
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
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.UserFacade;
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
    
    @Inject
    private UserFacade userFacade;
    
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
    	logger.debug("FOOBAR" + currentThread.getName());
    	member = null;
    	if (editMemberId == null) {
    		logger.debug("No member selected, identifier is null");
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
    		logger.debug("Member doesn't exist. Can't find a user with this identifier in members list");
    		member = new ThreadMemberVo();
    	}
    }
    
    public void onSuccessFromMemberForm() throws BusinessException {
    	member.setAdmin(admin);
    	member.setCanUpload(canUpload);
    	threadEntryFacade.updateMember(userVo, member, currentThread);
    }
    
	public void onSubmit() {
    	logger.info("onSubmit");
    }
}
