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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailContentLangFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentLangDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;

public class MailContentLangFacadeImpl extends AdminGenericFacadeImpl implements
		MailContentLangFacade {

	private final MailConfigService mailConfigService;

	public MailContentLangFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
	}

	@Override
	public MailContentLangDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		return new MailContentLangDto(findContentLang(authUser, uuid), getOverrideReadonly());
	}

	@Override
	public MailContentLangDto create(MailContentLangDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContentLang contentLang = new MailContentLang();

		contentLang.setLanguage(dto.getLanguage().toInt());
		contentLang.setMailConfig(findConfig(authUser, dto.getMailConfig()));
		contentLang.setMailContent(findContent(authUser, dto.getMailContent()));
		return new MailContentLangDto(mailConfigService.createContentLang(authUser, contentLang));
	}

	@Override
	public MailContentLangDto update(MailContentLangDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContentLang contentLang = findContentLang(authUser, dto.getUuid());

		contentLang.setMailContent(findContent(authUser, dto.getMailContent()));
		return new MailContentLangDto(mailConfigService.updateContentLang(authUser, contentLang));
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		mailConfigService.deleteContentLang(authUser, uuid);
	}

	/*
	 * Helpers
	 */

	private MailConfig findConfig(User authUser, String uuid)
			throws BusinessException {
		MailConfig mailConfig = mailConfigService.findConfigByUuid(authUser, uuid);

		if (mailConfig == null)
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					uuid + " not found.");
		return mailConfig;
	}

	private MailContent findContent(User authUser, String uuid)
			throws BusinessException {
		MailContent mailContent = mailConfigService.findContentByUuid(authUser, uuid);

		if (mailContent == null)
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_NOT_FOUND,
					uuid + " not found.");
		return mailContent;
	}

	private MailContentLang findContentLang(User authUser, String uuid)
			throws BusinessException {
		MailContentLang mailContentLang = mailConfigService.findContentLangByUuid(
				authUser, uuid);

		if (mailContentLang == null)
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENTLANG_NOT_FOUND, uuid
							+ " not found.");
		return mailContentLang;
	}

	private boolean getOverrideReadonly() {
		return mailConfigService.isTemplatingOverrideReadonlyMode();
	}
}
