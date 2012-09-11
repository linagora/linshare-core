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
package org.linagora.linshare.view.tapestry.services.impl;

import java.util.Locale;

import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ValidationMessagesSource;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.view.tapestry.utils.WelcomeMessageUtils;

/**
 * Build the mail container to give to the facades and services.
 * 
 * @author sduprey
 *
 */
public class MailContainerBuilder {

    private PersistentLocale persistentLocale;
    private Request request;
    private ValidationMessagesSource validationMessagesSource;
	
	public MailContainerBuilder(PersistentLocale persistentLocale,
			Request request, ValidationMessagesSource validationMessagesSource) {
		this.persistentLocale = persistentLocale;
		this.request = request;
		this.validationMessagesSource = validationMessagesSource;
	}

	public MailContainer buildMailContainer(UserVo userVo, String customMessage) {
		
		Locale userLocale = null;
    	if (userVo != null && userVo.getLocale()!= null && (!userVo.getLocale().equals(""))) {
    		userLocale = new Locale(userVo.getLocale());
    	}
    	Locale locale = WelcomeMessageUtils.getNormalisedLocale(persistentLocale.get(), request.getLocale(), userLocale);
    	Language language = WelcomeMessageUtils.getLanguageFromLocale(locale);
    	Messages messages = validationMessagesSource.getValidationMessages(locale);
    	
    	MailContainer mailContainer = new MailContainer(customMessage, language);
    	return mailContainer;
	}
}
