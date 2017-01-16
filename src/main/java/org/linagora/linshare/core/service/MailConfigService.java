/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

package org.linagora.linshare.core.service;

import java.util.List;

import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;

/**
 * @author nbertrand
 */
public interface MailConfigService {

	/**
	 * Find all mail configurations visible by this domain.
	 * 
	 * @param domainId
	 * @return
	 */
	List<MailConfig> findAllConfigs(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail configuration.
	 * 
	 * @param uuid
	 * @return
	 */
	MailConfig findConfigByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Create a new mail configuration.
	 * 
	 * @param actor
	 * @param config
	 * @return
	 * @throws BusinessException
	 */
	MailConfig createConfig(User actor, MailConfig config) throws BusinessException;

	/**
	 * Update an existing mail configuration.
	 * 
	 * @param actor
	 * @param config
	 * @return
	 * @throws BusinessException
	 */
	MailConfig updateConfig(User actor, MailConfig config) throws BusinessException;

	/**
	 * Delete an existing mail configuration if it's not currently in use.
	 * 
	 * @param actor
	 * @param uuid
	 * @return TODO
	 * @throws BusinessException
	 */
	MailConfig deleteConfig(User actor, String uuid) throws BusinessException;

	/**
	 * Find all mail contents visible by this domain.
	 * 
	 * @param actor
	 * @param domainId
	 * @return
	 */
	List<MailContent> findAllContents(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail content.
	 * 
	 * @param actor
	 * @param uuid
	 * @return
	 */
	MailContent findContentByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Find a mail content used by a mail configuration by its language and
	 * type.
	 * 
	 * @param actor
	 * @param domainId
	 * @param lang
	 * @param type
	 * @return
	 */
	MailContent findContentFromDomain(User actor, String domainId, Language lang,
			MailContentType type) throws BusinessException;

	/**
	 * Create a new mail content.
	 * 
	 * @param actor
	 * @param content
	 * @return
	 * @throws BusinessException
	 */
	MailContent createContent(User actor, MailContent content)
			throws BusinessException;

	/**
	 * Update an existing mail content.
	 * 
	 * @param actor
	 * @param content
	 * @return 
	 * @throws BusinessException
	 */
	MailContent updateContent(User actor, MailContent content)
			throws BusinessException;

	/**
	 * Delete an existing mail content.
	 * 
	 * @param actor
	 * @param uuid
	 * @return TODO
	 * @throws BusinessException
	 */
	MailContent deleteContent(User actor, String uuid) throws BusinessException;

	/**
	 * Find a mail content lang.
	 * 
	 * @param actor
	 * @param uuid
	 * @return
	 * @throws BusinessException
	 */
	MailContentLang findContentLangByUuid(User actor, String uuid) throws BusinessException;

	/**
	 * Create a new mail content lang and add it to a mail configuration.
	 * 
	 * @param actor
	 * @param contentLang
	 * @return 
	 * @throws BusinessException
	 */
	MailContentLang createContentLang(User actor, MailContentLang contentLang) throws BusinessException;

	/**
	 * Update an existing mail content lang.
	 * 
	 * @param actor
	 * @param contentLang
	 * @return 
	 * @throws BusinessException
	 */
	MailContentLang updateContentLang(User actor, MailContentLang contentLang) throws BusinessException;

	/**
	 * Delete an existing mail content lang and remove it from its mail configuration.
	 * 
	 * @param actor
	 * @param uuid
	 * @throws BusinessException
	 */
	void deleteContentLang(User actor, String uuid) throws BusinessException;

	/**
	 * Find all mail footers visible by this domain.
	 * 
	 * @param actor
	 * @param domainId
	 * @return
	 */
	List<MailFooter> findAllFooters(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail footer.
	 * 
	 * @param actor
	 * @param uuid
	 * @return
	 */
	MailFooter findFooterByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Create a new mail footer.
	 * 
	 * @param actor
	 * @param footer
	 * @return
	 * @throws BusinessException
	 */
	MailFooter createFooter(User actor, MailFooter footer) throws BusinessException;

	/**
	 * Update an existing mail footer.
	 * 
	 * @param actor
	 * @param footer
	 * @return 
	 * @throws BusinessException
	 */
	MailFooter updateFooter(User actor, MailFooter footer) throws BusinessException;

	/**
	 * Delete an existing mail footer.
	 * 
	 * @param actor
	 * @param uuid
	 * @return TODO
	 * @throws BusinessException
	 */
	MailFooter deleteFooter(User actor, String uuid) throws BusinessException;

	/**
	 * Find a mail footer lang.
	 * 
	 * @param actor
	 * @param uuid
	 * @return
	 * @throws BusinessException
	 */
	MailFooterLang findFooterLangByUuid(User actor, String uuid) throws BusinessException;

	/**
	 * Create a new mail footer lang and add it to a mail configuration.
	 * 
	 * @param actor
	 * @param footerLang
	 * @return 
	 * @throws BusinessException
	 */
	MailFooterLang createFooterLang(User actor, MailFooterLang footerLang) throws BusinessException;

	/**
	 * Update an existing mail footer lang.
	 * 
	 * @param actor
	 * @param footerLang
	 * @throws BusinessException
	 */
	MailFooterLang updateFooterLang(User actor, MailFooterLang footerLang) throws BusinessException;

	/**
	 * Delete an existing mail footer lang and remove it from its mail configuration.
	 * 
	 * @param actor
	 * @param uuid
	 * @throws BusinessException
	 */
	void deleteFooterLang(User actor, String uuid) throws BusinessException;

	/**
	 * Find all mail layouts visible by this domain.
	 * 
	 * @param actor
	 * @param domainId
	 * @return
	 */
	List<MailLayout> findAllLayouts(User actor, String domainId)
			throws BusinessException;

	/**
	 * Find a mail layout.
	 * 
	 * @param actor
	 * @param uuid
	 * @return
	 */
	MailLayout findLayoutByUuid(User actor, String uuid)
			throws BusinessException;

	/**
	 * Create a new mail layout.
	 * 
	 * @param actor
	 * @param layout
	 * @return
	 * @throws BusinessException
	 */
	MailLayout createLayout(User actor, MailLayout layout) throws BusinessException;

	/**
	 * Update an existing mail layout.
	 * 
	 * @param actor
	 * @param layout
	 * @return 
	 * @throws BusinessException
	 */
	MailLayout updateLayout(User actor, MailLayout layout) throws BusinessException;

	/**
	 * Delete an existing mail layout.
	 * 
	 * @param actor
	 * @param uuid
	 * @return TODO
	 * @throws BusinessException
	 */
	MailLayout deleteLayout(User actor, String uuid) throws BusinessException;

	/**
	 * Check permissions for this user on a mail configuration.
	 * 
	 * @param actor
	 * @param config
	 * @return
	 */
	boolean hasRights(User actor, MailConfig config);

	/**
	 * Check permissions for this user on a mail content.
	 * 
	 * @param actor
	 * @param content
	 * @return
	 */
	boolean hasRights(User actor, MailContent content);

	/**
	 * Check permissions for this user on a mail footer.
	 * 
	 * @param actor
	 * @param footer
	 * @return
	 */
	boolean hasRights(User actor, MailFooter footer);

	/**
	 * Check permissions for this user on a mail layout.
	 * 
	 * @param actor
	 * @param layout
	 * @return
	 */
	boolean hasRights(User actor, MailLayout layout);

	boolean isTemplatingOverrideReadonlyMode();
}
