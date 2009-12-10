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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.ApplicationState;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.linagora.linShare.core.Facade.UserFacade;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.beans.ShareSessionObjects;
import org.linagora.linShare.view.tapestry.pages.Index;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.slf4j.Logger;

public class ResetPassword {
	@Property
	private String mail;

	@Inject
	private Logger logger;

	@InjectPage
	private Index index;

	@ApplicationState
	private ShareSessionObjects shareSessionObjects;

	@Inject
	private UserFacade userFacade;

	@Inject
	@Path("context:templates/reset-password.html")
	private Asset guestMailTemplate;

	@Inject
	@Path("context:templates/reset-password.txt")
	private Asset guestMailTemplateTxt;

	@Inject
	private Templating templating;

	@Inject
	private Messages messages;

	@Property
	private String currentMessage;

	public boolean onValidate() {
		if (mail == null) {
			return false;
		}
		return true;
	}

	public Object onSuccess() {
		String mailContent = null;
		String mailContentTxt = null;
		logger.debug(mail);
		
		UserVo user = userFacade.findUser(mail);
		if (null == user) {
			shareSessionObjects.addMessage(messages.get("pages.password.error.badmail"));
			return this;
		}

		if (!user.isGuest()) {
			shareSessionObjects.addMessage(messages.get("pages.password.error.notguest"));
			return this;
		}
		
		Map<String,String> hash=new HashMap<String, String>();
        hash.put("${mail}", mail);
		
		try {
			mailContent = templating.getMessage(guestMailTemplate.getResource()
					.openStream(), hash);
			mailContentTxt = templating.getMessage(guestMailTemplateTxt
					.getResource().openStream(), hash);
		} catch (IOException e) {
			logger.error("Bad mail template", e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,
					"Bad template", e);
		}

		try {
			userFacade.resetPassword(user, messages
					.get("mail.user.guest.resetpassword.subject"), mailContent,
					mailContentTxt);
		} catch (BusinessException e) {
			// should never occur.
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		shareSessionObjects.addMessage(messages.get("pages.password.success"));

		return this;
	}

	public List<String> getNotificationMessage() {
		List<String> notificationMessage = shareSessionObjects.getMessages();
		shareSessionObjects.setMessages(new ArrayList<String>());
		return notificationMessage;
	}

	public List<String> getMessagesInfo() {
		return shareSessionObjects.getMessages();
	}
    
    Object onException(Throwable cause) {
    	shareSessionObjects.addMessage(messages.get("global.exception.message"));
    	logger.error(cause.getMessage());
    	cause.printStackTrace();
    	return this;
    }

}
