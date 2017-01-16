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
		User actor = checkAuthentication(Role.ADMIN);
		return new MailContentLangDto(findContentLang(actor, uuid), getOverrideReadonly());
	}

	@Override
	public MailContentLangDto create(MailContentLangDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailContentLang contentLang = new MailContentLang();

		contentLang.setLanguage(dto.getLanguage().toInt());
		contentLang.setMailConfig(findConfig(actor, dto.getMailConfig()));
		contentLang.setMailContent(findContent(actor, dto.getMailContent()));
		return new MailContentLangDto(mailConfigService.createContentLang(actor, contentLang));
	}

	@Override
	public MailContentLangDto update(MailContentLangDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailContentLang contentLang = findContentLang(actor, dto.getUuid());

		contentLang.setMailContent(findContent(actor, dto.getMailContent()));
		return new MailContentLangDto(mailConfigService.updateContentLang(actor, contentLang));
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		mailConfigService.deleteContentLang(actor, uuid);
	}

	/*
	 * Helpers
	 */

	private MailConfig findConfig(User actor, String uuid)
			throws BusinessException {
		MailConfig mailConfig = mailConfigService.findConfigByUuid(actor, uuid);

		if (mailConfig == null)
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					uuid + " not found.");
		return mailConfig;
	}

	private MailContent findContent(User actor, String uuid)
			throws BusinessException {
		MailContent mailContent = mailConfigService.findContentByUuid(actor, uuid);

		if (mailContent == null)
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_NOT_FOUND,
					uuid + " not found.");
		return mailContent;
	}

	private MailContentLang findContentLang(User actor, String uuid)
			throws BusinessException {
		MailContentLang mailContentLang = mailConfigService.findContentLangByUuid(
				actor, uuid);

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
