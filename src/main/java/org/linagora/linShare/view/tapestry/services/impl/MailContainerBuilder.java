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
import org.apache.tapestry5.services.PersistentLocale;
import org.apache.tapestry5.services.Request;
import org.linagora.linShare.core.domain.constants.Language;
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

	final private static Logger logger=LoggerFactory.getLogger(MailContainerBuilder.class);
	
	public MailContainerBuilder(PropertiesSymbolProvider propertiesSymbolProvider,
			Templating templating, PersistentLocale persistentLocale,
			Request request, Asset containerTemplate, Asset containerTemplateTxt) {
		this.propertiesSymbolProvider = propertiesSymbolProvider;
		this.templating = templating;
		this.persistentLocale = persistentLocale;
		this.request = request;
		this.containerTemplate = containerTemplate;
		this.containerTemplateTxt = containerTemplateTxt;
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
    	if (((userVo.getLocale())!= null) && (!userVo.getLocale().equals(""))) {
    		userLocale = new Locale(userVo.getLocale());
    	}
    	Language language = WelcomeMessageUtils.getLanguageFromLocale(persistentLocale.get(), request.getLocale(), userLocale);
	
    	return new MailContainer(mailContentTxt, mailContent, customMessage, language, urlBase, urlInternal);
	}
}
