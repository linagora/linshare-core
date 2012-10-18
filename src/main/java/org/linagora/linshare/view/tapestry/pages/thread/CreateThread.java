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

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.pages.thread.Index;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.utils.XSSFilter;
import org.owasp.validator.html.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateThread {
	
	private static Logger logger = LoggerFactory.getLogger(CreateThread.class);
	
	@SessionState
    private UserVo loginUser;
	
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String name;
	
	@InjectComponent
	private Form createForm;

	@Inject
	private ThreadEntryFacade threadEntryFacade;
	
    @Inject
    private Messages messages;
    
    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;
    
	@Inject
	private Policy antiSamyPolicy;
	
	private XSSFilter filter;
	
	@SetupRender
	public void init() {
		if (name == null) {
			name = "";
		}
	}
	
	public Object onActionFromCancel() {
		name = null;
		return Index.class;
	}
	
	public Object onSuccessFromCreateForm() {
		logger.info("Entering onSuccessFromCreateThreadForm");
		filter = new XSSFilter(antiSamyPolicy, messages);
		try {
    		name = filter.clean(name);
    		if (filter.hasError()) {
    			logger.debug("XSSFilter found some tags and striped them.");
    			businessMessagesManagementService.notify(filter.getWarningMessage());
    		}
    	} catch (BusinessException e) {
    		businessMessagesManagementService.notify(e);
    	}
		try {
			threadEntryFacade.createThread(loginUser, name);
			businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.THREAD_CREATION_SUCCESS, MessageSeverity.INFO));
		} catch (BusinessException e) {
			logger.error("Can not create thread : " + e.getMessage());
			logger.debug(e.toString());
    		businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.THREAD_CREATION_FAIL, MessageSeverity.ERROR));
		}
		name = null;
		return Index.class;
	}
}
