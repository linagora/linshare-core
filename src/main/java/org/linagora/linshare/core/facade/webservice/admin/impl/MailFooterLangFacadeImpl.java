/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
