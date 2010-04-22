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
package org.linagora.linShare.view.tapestry.services.impl;

import java.io.IOException;
import java.util.Locale;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ValidationMessagesSource;
import org.linagora.linShare.core.domain.constants.Language;
import org.linagora.linShare.core.domain.entities.GroupMembershipStatus;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.view.tapestry.services.Templating;
import org.linagora.linShare.view.tapestry.utils.WelcomeMessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Build the mail container to give to the facades and services.
 * 
 * @author sduprey
 *
 */
public class MailContainerBuilder {

	private PropertiesSymbolProvider propertiesSymbolProvider;
	private Templating templating;
    private PersistentLocale persistentLocale;
    private Request request;
    private Asset containerTemplate;
    private Asset containerTemplateTxt;
    private ValidationMessagesSource validationMessagesSource;

	final private static Logger logger=LoggerFactory.getLogger(MailContainerBuilder.class);
	
	public MailContainerBuilder(PropertiesSymbolProvider propertiesSymbolProvider,
			Templating templating, PersistentLocale persistentLocale,
			Request request, Asset containerTemplate, Asset containerTemplateTxt,
			ValidationMessagesSource validationMessagesSource) {
		this.propertiesSymbolProvider = propertiesSymbolProvider;
		this.templating = templating;
		this.persistentLocale = persistentLocale;
		this.request = request;
		this.containerTemplate = containerTemplate;
		this.containerTemplateTxt = containerTemplateTxt;
		this.validationMessagesSource = validationMessagesSource;
	}

	public MailContainer buildMailContainer(UserVo userVo, String customMessage) {
        String mailContent = null;
        String mailContentTxt = null;

		String urlBase=propertiesSymbolProvider.valueForSymbol("linshare.info.url.base");
		String urlInternal=propertiesSymbolProvider.valueForSymbol("linshare.info.url.internal");
		
		try {
			mailContent = templating.readFullyTemplateContent(containerTemplate.getResource().openStream());
			mailContentTxt = templating.readFullyTemplateContent(containerTemplateTxt.getResource().openStream());
			
		} catch (IOException e) {
			logger.error("Bad mail template", e);
			throw new TechnicalException(TechnicalErrorCode.MAIL_EXCEPTION,"Bad template",e);
		}
		Locale userLocale = null;
    	if (userVo != null && userVo.getLocale()!= null && (!userVo.getLocale().equals(""))) {
    		userLocale = new Locale(userVo.getLocale());
    	}
    	Locale locale = WelcomeMessageUtils.getNormalisedLocale(persistentLocale.get(), request.getLocale(), userLocale);
    	Language language = WelcomeMessageUtils.getLanguageFromLocale(locale);
    	Messages messages = validationMessagesSource.getValidationMessages(locale);
    	
    	MailContainer mailContainer = new MailContainer(mailContentTxt, mailContent, customMessage, language);
    	mailContainer.addData("urlBase", urlBase);
    	mailContainer.addData("urlInternal", urlInternal);
    	mailContainer.addData("GroupMembershipStatus."+GroupMembershipStatus.ACCEPTED.toString(), messages.get("GroupMembershipStatus."+GroupMembershipStatus.ACCEPTED.toString()));
    	mailContainer.addData("GroupMembershipStatus."+GroupMembershipStatus.REJECTED.toString(), messages.get("GroupMembershipStatus."+GroupMembershipStatus.REJECTED.toString()));
    	mailContainer.addData("GroupMembershipStatus."+GroupMembershipStatus.WAITING_APPROVAL.toString(), messages.get("GroupMembershipStatus."+GroupMembershipStatus.WAITING_APPROVAL.toString()));
    	return mailContainer;
	}
}
