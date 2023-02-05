/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.notifications.config;

import java.util.Map;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.StringTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

public class LinShareStringTemplateResolver extends StringTemplateResolver {

	private final static Logger logger = LoggerFactory.getLogger(LinShareStringTemplateResolver.class);

	final protected boolean insertLicenceTerm;

	final protected boolean templatingSubjectPrefix;

	public LinShareStringTemplateResolver(boolean insertLicenseTerm, boolean templatingSubjectPrefix) {
		super();
		this.insertLicenceTerm = insertLicenseTerm;
		this.templatingSubjectPrefix = templatingSubjectPrefix;
	}

	@Override
	protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate,
			String template, Map<String, Object> templateResolutionAttributes) {
		if (templateResolutionAttributes != null) {
			MailConfig cfg = (MailConfig) templateResolutionAttributes.get("mailConfig");
			if (cfg == null) {
				logger.error("critical error : missing mail configuration");
				logger.error("Template resolution aborted");
				return null;
			}
			Language lang = (Language) templateResolutionAttributes.get("lang");
			if (lang == null) {
				logger.error("critical error : missing language");
				logger.error("Template resolution aborted");
				return null;
			}
			boolean isSubject = false;
			if (template.contains(":subject")) {
				isSubject = true;
				template = template.split(":")[0];
			}
			if (MailContentType.contains(template)) {
				MailContentType mailKind = MailContentType.valueOf(template);
				MailContent mailContent = cfg.findContent(lang, mailKind);
				String content = mailContent.getBody();
				if (isSubject) {
					if (templatingSubjectPrefix) {
						content = "[" + mailKind.name() + "] " + mailContent.getSubject();
					} else {
						content = mailContent.getSubject();
					}
				}
				LinShareTemplateResource resource = new LinShareTemplateResource(content, template);
				resource.setMessages(getMessages(cfg, lang, mailContent));
				return resource;
			} else {
				if ("footer".equals(template)) {
					MailFooter f = cfg.findFooter(lang);
					String footer = formatFooter(f.getFooter(), lang);
					LinShareTemplateResource resource = new LinShareTemplateResource(footer, template);
					resource.setMessages(f.getMessages(lang));
					return resource;
				} else if ("layout".equals(template)) {
					return new LinShareTemplateResource(cfg.getMailLayoutHtml().getLayout(), template);
				} else if ("copyright".equals(template)) {
					if (insertLicenceTerm) {
						return new FooterCopyrightResource(template, lang);
					}
					return new FooterCopyrightEmptyResource(template, lang);
				}
			}
		}
		return new StringTemplateResource(template);

	}

	protected String getMessages(MailConfig cfg, Language lang, MailContent mailContent) {
		StringBuilder sb = new StringBuilder();
		String messagesLayout = cfg.getMailLayoutHtml().getMessages(lang);
		if (messagesLayout != null) {
			sb.append(messagesLayout);
			sb.append('\n');
		}
		String messages = mailContent.getMessages(lang);
		if (messages != null) {
			sb.append(messages);
		}
		return sb.toString();
	}

	final private String formatFooter(String footer, Language lang) {
		if (insertLicenceTerm) {
			if (lang.equals(Language.FRENCH)) {
				footer += "<br/>Vous utilisez la version libre et gratuite de <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, développée par Linagora © 2009–2022. Contribuez à la R&D du produit en souscrivant à une offre entreprise.<br/>";
			} else {
				footer += "<br/>You are using the Open Source and free version of <a href=\"http://www.linshare.org/\" title=\"LinShare\"><strong>LinShare</strong></a>™, powered by Linagora © 2009–2022. Contribute to Linshare R&D by subscribing to an Enterprise offer.<br/>";
			}
		}
		return footer;
	}

}
