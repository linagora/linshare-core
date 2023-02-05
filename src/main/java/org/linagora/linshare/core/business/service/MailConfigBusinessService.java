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
package org.linagora.linshare.core.business.service;

import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailConfigBusinessService {

	MailConfig findByUuid(String uuid) throws BusinessException;

	MailConfig create(AbstractDomain domain, MailConfig cfg) throws BusinessException;

	MailConfig update(MailConfig cfg) throws BusinessException;

	void delete(MailConfig cfg) throws BusinessException;

	MailContentLang findContentLangByUuid(String uuid);

	MailContentLang createContentLang(MailContentLang contentLang)
			throws BusinessException;

	MailContentLang updateContentLang(MailContentLang contentLang)
			throws BusinessException;

	void deleteContentLang(MailContentLang contentLang)
			throws BusinessException;

	MailFooterLang findFooterLangByUuid(String uuid);

	MailFooterLang createFooterLang(MailFooterLang footerLang) throws BusinessException;

	MailFooterLang updateFooterLang(MailFooterLang footerLang) throws BusinessException;

	void deleteFooterLang(MailFooterLang footerLang) throws BusinessException;
 
	List<MailFooterLang> findMailsFooterLangByMailConfig(MailConfig mailConfig) throws BusinessException;
		
	List<MailContentLang> findMailsContentLangByMailContent(MailContent mailContent) throws BusinessException;
}
