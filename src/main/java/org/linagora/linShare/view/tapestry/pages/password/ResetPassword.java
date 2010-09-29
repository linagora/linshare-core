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
package org.linagora.linShare.view.tapestry.pages.password;

import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.chenillekit.tapestry.core.components.Kaptcha;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.enums.BusinessUserMessageType;
import org.linagora.linShare.view.tapestry.objects.BusinessUserMessage;
import org.linagora.linShare.view.tapestry.objects.MessageSeverity;
import org.linagora.linShare.view.tapestry.services.BusinessMessagesManagementService;
import org.linagora.linShare.view.tapestry.services.impl.MailContainerBuilder;
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

	@ApplicationState
	private ShareSessionObjects shareSessionObjects;

	@Inject
	private UserFacade userFacade;

    @Inject
    private BusinessMessagesManagementService businessMessagesManagementService;

	@Inject
	private Messages messages;
    
    @Inject
    private MailContainerBuilder mailBuilder;

	public boolean onValidate() {
		if (mail == null) {
			return false;
		}
		return true;
	}

	public Object onSuccess() {
		if (!kaptchaValid) {
	    	shareSessionObjects.addError(messages.get("pages.password.error.badcaptcha"));
			return this;
		}
		
		UserVo user = userFacade.findUser(mail);
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
