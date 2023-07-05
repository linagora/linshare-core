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
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailLayoutFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailLayoutDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;

public class MailLayoutFacadeImpl extends AdminGenericFacadeImpl implements
		MailLayoutFacade {

	private final MailConfigService mailConfigService;

	private final AbstractDomainService abstractDomainService;

	final private DomainPermissionBusinessService domainPermissionService;

	public MailLayoutFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService,
			final DomainPermissionBusinessService domainPermissionService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
		this.domainPermissionService = domainPermissionService;
	}

	@Override
	public MailLayoutDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		return new MailLayoutDto(findLayout(authUser, uuid), getOverrideReadonly());
	}

	@Override
	public MailLayoutDto create(MailLayoutDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailLayout layout = new MailLayout();
		transform(layout, dto);
		return new MailLayoutDto(mailConfigService.createLayout(authUser, layout));
	}

	@Override
	public MailLayoutDto update(MailLayoutDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailLayout layout = findLayout(authUser, dto.getUuid());
		transform(layout, dto);
		return new MailLayoutDto(mailConfigService.updateLayout(authUser, layout));
	}

	@Override
	public MailLayoutDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailLayout layout = mailConfigService.deleteLayout(authUser, uuid);
		return new MailLayoutDto(layout);
	}

	@Override
	public Set<MailLayoutDto> findAll(String domainIdentifier, boolean only)
			throws BusinessException {
		User user = checkAuthentication(Role.ADMIN);
		if (domainIdentifier == null) {
			domainIdentifier = user.getDomainId();
		}

		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		if (!domainPermissionService.isAdminforThisDomain(user, domain)) {
			throw new BusinessException("You are not allowed to manage this domain.");
		}

		Set<MailLayoutDto> mailLayoutsDto = new HashSet<MailLayoutDto>();
		Iterable<MailLayout> layouts = only ? domain.getMailLayouts()
				: mailConfigService.findAllLayouts(user, domainIdentifier);
		for (MailLayout mailLayout : layouts) {
			mailLayoutsDto.add(new MailLayoutDto(mailLayout, getOverrideReadonly()));
		}
		return mailLayoutsDto;
	}

	/*
	 * Helpers
	 */

	private void transform(MailLayout layout, MailLayoutDto dto)
			throws BusinessException {
		layout.setDomain(findDomain(dto.getDomain()));
		layout.setDescription(dto.getDescription());
		layout.setVisible(dto.isVisible());
		layout.setLayout(dto.getLayout());
		layout.setMessagesEnglish(dto.getMessagesEnglish());
		layout.setMessagesFrench(dto.getMessagesFrench());
		layout.setMessagesRussian(dto.getMessagesRussian());
		layout.setMessagesVietnamese(dto.getMessagesVietnamese());
	}

	private MailLayout findLayout(User authUser, String uuid)
			throws BusinessException {
		MailLayout mailLayout = mailConfigService.findLayoutByUuid(authUser, uuid);

		if (mailLayout == null)
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_NOT_FOUND,
					uuid + " not found.");
		return mailLayout;
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
