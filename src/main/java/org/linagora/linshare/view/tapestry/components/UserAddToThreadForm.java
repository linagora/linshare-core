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
 *   (c) 2008 Threade Linagora - http://linagora.org
 *
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
				threadsAllowed.addAll(threadEntryFacade.getAllMyAdminThread(userLoggedIn));
				// TODO XXX HACK : considers there's only one member in the list
				threadsAllowed.removeAll(threadEntryFacade.getAllMyThread(newMemberList.get(0)));
			} catch (BusinessException e) {
				logger.error("cannot retrieve user infos" + e.getMessage());
				logger.debug(e.toString());
			}
		}
		else {
			threadsAllowed.addAll(threads);
			// TODO XXX HACK : considers there's only one member in the list
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
						threadEntryFacade.addMember(threadVo, userLoggedIn, user, readOnly);
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