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
package org.linagora.linshare.view.tapestry.pages.password;

import java.util.List;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.chenillekit.tapestry.core.components.Kaptcha;
import org.linagora.linshare.core.Facade.AbstractDomainFacade;
import org.linagora.linshare.core.Facade.UserFacade;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linshare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linshare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linshare.view.tapestry.objects.MessageSeverity;
import org.linagora.linshare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linshare.view.tapestry.services.impl.MailContainerBuilder;
import org.slf4j.Logger;

public class ResetPassword {
	@Property
	@Persist
	private String mail;
	
	@SuppressWarnings("unused")
	@Component
	private Kaptcha kaptcha;

	@Property
	private boolean kaptchaValid;

	@Inject
	private Logger logger;

	@SessionState
	private ShareSessionObjects shareSessionObjects;
	
	@Inject
	private UserFacade userFacade;

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private Messages messages;
    
    @Inject
    private MailContainerBuilder mailBuilder;
    
	@Inject @Symbol("linshare.domain.visible")
	@Property
	private boolean domainVisible;
	
    @Inject
	private AbstractDomainFacade domainFacade;
	
	@Persist
	@Property
	private List<String> availableDomains;

    @Property
    @Persist
    private String selectedDomainId;
        
    @SetupRender
	public void init() throws BusinessException {
		if (domainVisible) {
			availableDomains = domainFacade.getAllDomainIdentifiers();
		}
	}

	public boolean onValidate() {
		if (mail == null) {
			return false;
		}
		return true;
	}

	public Object onSuccess() throws BusinessException {
		if (!kaptchaValid) {
	    	shareSessionObjects.addError(messages.get("pages.password.error.badcaptcha"));
			return this;
		}
		logger.debug("Capsha is valid, finding user in " + selectedDomainId + " ... ");
		
		UserVo user = userFacade.findUserForResetPassordForm(mail, selectedDomainId);
		logger.debug("user found ... ");
		if (null == user) {
			shareSessionObjects.addError(messages.get("pages.password.error.badmail"));
			return this;
		}

		if (!user.isGuest()) {
			shareSessionObjects.addError(messages.get("pages.password.error.notguest"));
			return this;
		}

		MailContainer mailContainer = mailBuilder.buildMailContainer(user, null);

		try {
			userFacade.resetPassword(user, mailContainer);
		} catch (BusinessException e) {
			// should never occur.
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		businessMessagesManagementService.notify(new BusinessUserMessage(BusinessUserMessageType.PASSWORD_RESET_SUCCESS,
                MessageSeverity.INFO));
		mail=null;

		return this;
	}
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addError(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

}
