/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailLayoutFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;
import org.linagora.linshare.webservice.dto.MailLayoutDto;

import com.google.common.collect.Lists;

public class MailLayoutFacadeImpl extends AdminGenericFacadeImpl implements
		MailLayoutFacade {

	private final MailConfigService mailConfigService;

	private final AbstractDomainService abstractDomainService;

	public MailLayoutFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public MailLayoutDto find(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return new MailLayoutDto(findLayout(actor, uuid));
	}

	@Override
	public void create(MailLayoutDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailLayout layout = new MailLayout();
		transform(layout, dto);
		mailConfigService.createLayout(actor, layout);
	}

	@Override
	public void update(MailLayoutDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailLayout layout = findLayout(actor, dto.getUuid());
		transform(layout, dto);
		mailConfigService.updateLayout(actor, layout);
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		mailConfigService.deleteLayout(actor, uuid);
	}

	@Override
	public List<MailLayoutDto> getMailLayouts(String domainIdentifier)
			throws BusinessException {
		User user = checkAuthentication();
		Validate.notEmpty(domainIdentifier, "domain identifier must be set.");

		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		// TODO : check if the current user has the right to get MailContent of this domain

		ArrayList<MailLayoutDto> mailLayoutsDto = Lists.newArrayList();
		for (MailLayout mailContent : domain.getMailLayouts()) {
			mailLayoutsDto.add(new MailLayoutDto(mailContent));
		}
		return mailLayoutsDto;
	}

	/*
	 * Helpers
	 */

	private void transform(MailLayout layout, MailLayoutDto dto)
			throws BusinessException {
		layout.setDomain(findDomain(dto.getDomain()));
		layout.setName(dto.getName());
		layout.setVisible(dto.isVisible());
		layout.setLayout(dto.getLayout());
		layout.setPlaintext(dto.isPlaintext());
	}

	private MailLayout findLayout(User actor, String uuid)
			throws BusinessException {
		MailLayout mailLayout = mailConfigService.findLayoutByUuid(actor, uuid);

		if (mailLayout == null)
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_NOT_FOUND,
					uuid + " not found.");
		return mailLayout;
	}

	private AbstractDomain findDomain(String id) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(id);

		if (domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXISTS,
					"Domain " + id + "doesn't exist.");
		}
		return domain;
	}
}
