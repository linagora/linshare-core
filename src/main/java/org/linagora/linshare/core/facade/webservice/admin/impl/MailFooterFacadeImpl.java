/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
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

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailFooterFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;

public class MailFooterFacadeImpl extends AdminGenericFacadeImpl implements
		MailFooterFacade {

	private final MailConfigService mailConfigService;

	private final AbstractDomainService abstractDomainService;

	public MailFooterFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public MailFooterDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		return new MailFooterDto(findFooter(authUser, uuid), getOverrideReadonly());
	}

	@Override
	public MailFooterDto create(MailFooterDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailFooter footer = new MailFooter();
		transform(footer, dto);
		return new MailFooterDto(mailConfigService.createFooter(authUser, footer));
	}

	@Override
	public MailFooterDto update(MailFooterDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailFooter footer = findFooter(authUser, dto.getUuid());

		transform(footer, dto);
		return new MailFooterDto(mailConfigService.updateFooter(authUser, footer));
	}

	@Override
	public MailFooterDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailFooter footer = mailConfigService.deleteFooter(authUser, uuid);
		return new MailFooterDto(footer);
	}

	@Override
	public Set<MailFooterDto> findAll(String domainIdentifier, boolean only)
			throws BusinessException {
		User user = checkAuthentication(Role.ADMIN);
		if (domainIdentifier == null) {
			domainIdentifier = user.getDomainId();
		}

		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		// TODO : check if the current user has the right to get MailContent of
		// this domain

		Set<MailFooterDto> mailFootersDto = new HashSet<MailFooterDto>();
		Iterable<MailFooter> footers = only ? domain.getMailFooters()
				: mailConfigService.findAllFooters(user, domainIdentifier);
		for (MailFooter mailFooter : footers) {
			mailFootersDto.add(new MailFooterDto(mailFooter, getOverrideReadonly()));
		}
		return mailFootersDto;

	}

	/*
	 * Helpers
	 */

	private void transform(MailFooter footer, MailFooterDto dto)
			throws BusinessException {
		footer.setDomain(findDomain(dto.getDomain()));
		footer.setDescription(dto.getDescription());
		footer.setVisible(dto.isVisible());
		footer.setFooter(dto.getFooter());
		footer.setMessagesEnglish(dto.getMessagesEnglish());
		footer.setMessagesFrench(dto.getMessagesFrench());
		footer.setMessagesRussian(dto.getMessagesRussian());
	}

	private MailFooter findFooter(User authUser, String uuid)
			throws BusinessException {
		MailFooter mailFooter = mailConfigService.findFooterByUuid(authUser, uuid);

		if (mailFooter == null)
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_NOT_FOUND,
					uuid + " not found.");
		return mailFooter;
	}

	private AbstractDomain findDomain(String id) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(id);

		if (domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Domain " + id + "doesn't exist.");
		}
		return domain;
	}

	private boolean getOverrideReadonly() {
		return mailConfigService.isTemplatingOverrideReadonlyMode();
	}
}
