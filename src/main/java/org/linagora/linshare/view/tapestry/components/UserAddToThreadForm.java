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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.facade.UserFacade;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAddToThreadForm {

	/* ***********************************************************
     *                         Parameters
     ************************************************************ */
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<ThreadVo> threads;
    
    @Parameter(required = true, defaultPrefix = BindingConstants.PROP)
    @Property
    private List<UserVo> newMemberList;
    

    /* ***********************************************************
     *                      Injected services
     ************************************************************ */
    @Inject
    private ThreadEntryFacade threadEntryFacade;
    @Inject
    private UserFacade userFacade;

    @InjectComponent
    private Form userAddToThread;

    @Inject
    private Messages messages;

	/* ***********************************************************
     *                Properties & injected symbol, ASO, etc
     ************************************************************ */
    @SessionState
    @Property
    private UserVo userLoggedIn;

    @SessionState
    private ShareSessionObjects shareSessionObjects;
    
	@Property
	private ThreadVo threadSelected;
	
    @Property
    @Persist
    private List<ThreadVo> threadsAllowed;
	
    @Property
    @Persist(value="flash")
    private boolean readOnly;
    
    private List<ThreadVo> selectedThreads;

	private static final Logger logger = LoggerFactory.getLogger(UserAddToThreadForm.class);

    /* ***********************************************************
     *                   Event handlers&processing
     ************************************************************ */
    
    @SetupRender
    public void init(){
		threadsAllowed = new ArrayList<ThreadVo>();
		if (threads == null && newMemberList != null && newMemberList.size() > 0) {
			try {
				threadsAllowed.addAll(threadEntryFacade.getAllMyThreadWhereAdmin(userLoggedIn));
				threadsAllowed.removeAll(threadEntryFacade.getAllMyThread(newMemberList.get(0)));
			} catch (BusinessException e) {
				logger.error("cannot retrieve user infos" + e.getMessage());
				logger.debug(e.toString());
			}
		}
		else {
			threadsAllowed.addAll(threads);
			if (newMemberList != null && newMemberList.size() > 0)
				threadsAllowed.removeAll(threadEntryFacade.getAllMyThread(newMemberList.get(0)));
		}
    }

    public boolean isSelected() {
        return false;
    }

    public void setSelected(boolean selected) {
        if (selectedThreads == null) {
        	selectedThreads = new ArrayList<ThreadVo>();
        }
        if (selected) {
        	selectedThreads.add(threadSelected);
        }
        else {
        	selectedThreads.remove(threadSelected);
        }
    }

    public void onSuccess() throws BusinessException {
    	if (selectedThreads != null && newMemberList != null) {
    		for (UserVo user : newMemberList) {
	        	List<ThreadVo> threadsOfUserAdded = threadEntryFacade.getAllMyThread(user);
	        	logger.info("threads user : " + Arrays.toString(threadsOfUserAdded.toArray()));
		    	logger.info("selected threads : " + Arrays.toString(selectedThreads.toArray()));
	        	List<ThreadVo> threadsWhereUserWillBeAdded = new ArrayList<ThreadVo>();
		    	for (ThreadVo threadVo : selectedThreads) {
					if (!threadsOfUserAdded.contains(threadVo)) {
						threadsWhereUserWillBeAdded.add(threadVo);
					} else {
						shareSessionObjects.addWarning(messages.format("components.userAddToThread.alreadyIn", user.getCompleteName(), threadVo.getName()));
					}
				}
		    	if (threadsWhereUserWillBeAdded.size() > 0) {
		    		for (ThreadVo threadVo : threadsWhereUserWillBeAdded) {
						threadEntryFacade.addMember(userLoggedIn, threadVo, user, readOnly);
						shareSessionObjects.setReloadThreadsNeeded(true);
						shareSessionObjects.addMessage(messages.format("components.userAddToThread.success", user.getCompleteName(), threadVo.getName()));
					}
		    	}
	    	}
    	}
    	readOnly = false;
	}
    
    @CleanupRender
    public void cleanupRender(){
    	userAddToThread.clearErrors();
    }
}