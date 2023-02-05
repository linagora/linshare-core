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
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailFooterLangFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterLangDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;

public class MailFooterLangFacadeImpl extends AdminGenericFacadeImpl implements
		MailFooterLangFacade {

	private final MailConfigService mailConfigService;

	public MailFooterLangFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
	}

	@Override
	public MailFooterLangDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		return new MailFooterLangDto(findFooterLang(authUser, uuid), getOverrideReadonly());
	}

	@Override
	public MailFooterLangDto create(MailFooterLangDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailFooterLang footerLang = new MailFooterLang();
		transform(authUser, footerLang, dto);
		return new MailFooterLangDto(mailConfigService.createFooterLang(authUser, footerLang));
	}

	@Override
	public MailFooterLangDto update(MailFooterLangDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailFooterLang footerLang = findFooterLang(authUser, dto.getUuid());

		transform(authUser, footerLang, dto);
		return new MailFooterLangDto(mailConfigService.updateFooterLang(authUser, footerLang));
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		mailConfigService.deleteFooterLang(authUser, uuid);
	}

	/*
	 * Helpers
	 */

	private void transform(User authUser, MailFooterLang footerLang,
			MailFooterLangDto dto) throws BusinessException {
		footerLang.setLanguage(dto.getLanguage().toInt());
		footerLang.setMailConfig(findConfig(authUser, dto.getMailConfig()));
		footerLang.setMailFooter(findFooter(authUser, dto.getMailFooter()));
	}

	private MailConfig findConfig(User authUser, String uuid)
			throws BusinessException {
		MailConfig mailConfig = mailConfigService.findConfigByUuid(authUser, uuid);

		if (mailConfig == null)
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					uuid + " not found.");
		return mailConfig;
	}

	private MailFooter findFooter(User authUser, String uuid)
			throws BusinessException {
		MailFooter mailFooter = mailConfigService.findFooterByUuid(authUser, uuid);

		if (mailFooter == null)
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_NOT_FOUND,
					uuid + " not found.");
		return mailFooter;
	}

	private MailFooterLang findFooterLang(User authUser, String uuid)
			throws BusinessException {
		MailFooterLang mailFooterLang = mailConfigService.findFooterLangByUuid(
				authUser, uuid);

		if (mailFooterLang == null)
			throw new BusinessException(
					BusinessErrorCode.MAILFOOTERLANG_NOT_FOUND, uuid
							+ " not found.");
		return mailFooterLang;
	}

	private boolean getOverrideReadonly() {
		return mailConfigService.isTemplatingOverrideReadonlyMode();
	}
}
