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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.linagora.linshare.core.business.service.MailContentBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailContentLangRepository;
import org.linagora.linshare.core.repository.MailContentRepository;

public class MailContentBusinessServiceImpl implements
		MailContentBusinessService {

	private final AbstractDomainRepository abstractDomainRepository;

	private final MailContentRepository mailContentRepository;

	private final MailContentLangRepository mailContentLangRepository;

	public MailContentBusinessServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final MailContentRepository mailContentRepository,
			final MailContentLangRepository mailContentLangRepository)
			throws BusinessException {
		this.abstractDomainRepository = abstractDomainRepository;
		this.mailContentRepository = mailContentRepository;
		this.mailContentLangRepository = mailContentLangRepository;
	}

	@Override
	public MailContent findByUuid(String uuid) {
		return mailContentRepository.findByUuid(uuid);
	}

	@Override
	public MailContent find(String domainId, Language lang, MailContentType type)
			throws BusinessException {
		return this.find(findDomain(domainId), lang, type);
	}

	@Override
	public MailContent find(AbstractDomain domain, Language lang,
			MailContentType type) throws BusinessException {
		MailConfig cfg = domain.getCurrentMailConfiguration();
		return mailContentLangRepository.findMailContent(cfg, lang, type);
	}

	@Override
	public List<MailContent> findAll(AbstractDomain domain, Language lang,
			MailContentType type) {
		return mailContentRepository.findAll(domain, lang, type);
	}

	@Override
	public MailContent create(String domainId, MailContent content)
			throws BusinessException {
		return this.create(findDomain(domainId), content);
	}

	@Override
	public MailContent create(AbstractDomain domain, MailContent content)
			throws BusinessException {
		content.setUuid(UUID.randomUUID().toString());
		content.setCreationDate(new Date());
		content.setModificationDate(new Date());
		domain.getMailContents().add(content);
		abstractDomainRepository.update(domain);
		return findByUuid(content.getUuid());
	}

	@Override
	public MailContent update(MailContent content) throws BusinessException {
		try {
			return mailContentRepository.update(content);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENT_NOT_FOUND,
					"Cannot update mailcontent " + content);
		}
	}

	@Override
	public void delete(MailContent content) throws BusinessException {
		AbstractDomain domain = content.getDomain();

		if (mailContentLangRepository.isMailContentReferenced(content)) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_IN_USE,
					"Cannot delete mail content as it's still in use.");
		}
		domain.getMailContents().remove(content);
		abstractDomainRepository.update(domain);
		mailContentRepository.delete(content);
	}

	private AbstractDomain findDomain(String domainId) throws BusinessException {
		AbstractDomain domain = abstractDomainRepository.findById(domainId);

		if (domain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Cannot find mail content for domain: " + domainId
							+ ". Domain doesn't exist.");
		}
		return domain;
	}
}
