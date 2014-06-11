/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
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

import java.util.HashSet;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailContentFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;
import org.linagora.linshare.webservice.dto.MailContentDto;

public class MailContentFacadeImpl extends AdminGenericFacadeImpl implements
		MailContentFacade {

	private final MailConfigService mailConfigService;

	private final AbstractDomainService abstractDomainService;

	public MailContentFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public MailContentDto find(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return new MailContentDto(findContent(actor, uuid));
	}

	@Override
	public MailContentDto create(MailContentDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailContent content = new MailContent();
		transform(content, dto);
		return new MailContentDto(mailConfigService.createContent(actor,
				content));
	}

	@Override
	public MailContentDto update(MailContentDto dto) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailContent content = findContent(actor, dto.getUuid());

		transform(content, dto);
		return new MailContentDto(mailConfigService.updateContent(actor, content));
	}

	@Override
	public void delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		mailConfigService.deleteContent(actor, uuid);
	}

	@Override
	public Set<MailContentDto> findAll(String domainIdentifier, boolean only)
			throws BusinessException {
		User user = checkAuthentication(Role.ADMIN);
		if (domainIdentifier == null) {
			domainIdentifier = user.getDomainId();
		}

		AbstractDomain domain = abstractDomainService
				.retrieveDomain(domainIdentifier);
		// TODO : check if the current user has the right to get MailContent of
		// this domain
		Set<MailContentDto> mailContentsDto = new HashSet<MailContentDto>();
		Iterable<MailContent> contents = only ? domain.getMailContents()
				: mailConfigService.findAllContents(user, domainIdentifier);
		for (MailContent mailContent : contents) {
			mailContentsDto.add(new MailContentDto(mailContent));
		}
		return mailContentsDto;
	}

	/*
	 * Helpers
	 */

	private void transform(MailContent content, MailContentDto dto)
			throws BusinessException {
		content.setDomain(findDomain(dto.getDomain()));
		content.setName(dto.getName());
		content.setVisible(dto.isVisible());
		content.setPlaintext(dto.isPlaintext());
		content.setLanguage(dto.getLanguage().toInt());
		content.setGreetings(dto.getGreetings());
		content.setSubject(dto.getSubject());
		content.setBody(dto.getBody());
		content.setMailContentType(MailContentType.valueOf(
				dto.getMailContentType()).toInt());
	}

	private MailContent findContent(User actor, String uuid)
			throws BusinessException {
		MailContent mailContent = mailConfigService.findContentByUuid(actor,
				uuid);

		if (mailContent == null)
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENT_NOT_FOUND, uuid
							+ " not found.");
		return mailContent;
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
