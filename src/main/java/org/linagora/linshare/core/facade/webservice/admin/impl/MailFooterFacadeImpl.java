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

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
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


	final private DomainPermissionBusinessService domainPermissionService;

	public MailFooterFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService,
			final DomainPermissionBusinessService domainPermissionService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
		this.domainPermissionService = domainPermissionService;
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

		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		if (!domainPermissionService.isAdminforThisDomain(user, domain)) {
			throw new BusinessException("You are not allowed to manage this domain.");
		}

		Set<MailFooterDto> mailFootersDto = new HashSet<MailFooterDto>();
		Iterable<MailFooter> footers = only ? domain.getMailFooters()
				: mailConfigService.findAllVisibleFooters(domainIdentifier);
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
