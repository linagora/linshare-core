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
import java.util.UUID;

import org.linagora.linshare.core.business.service.MailLayoutBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.repository.MailLayoutRepository;

public class MailLayoutBusinessServiceImpl implements MailLayoutBusinessService {

	private final AbstractDomainRepository abstractDomainRepository;

	private final MailLayoutRepository mailLayoutRepository;

	private final MailConfigRepository mailConfigRepository;

	public MailLayoutBusinessServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final MailLayoutRepository mailLayoutRepository,
			final MailConfigRepository mailConfigRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.mailLayoutRepository = mailLayoutRepository;
		this.mailConfigRepository = mailConfigRepository;
	}

	@Override
	public MailLayout findByUuid(String uuid) {
		return mailLayoutRepository.findByUuid(uuid);
	}

	@Override
	public MailLayout create(AbstractDomain domain, MailLayout layout)
			throws BusinessException {
		layout.setUuid(UUID.randomUUID().toString());
		layout.setCreationDate(new Date());
		layout.setModificationDate(new Date());
		domain.getMailLayouts().add(layout);
		abstractDomainRepository.update(domain);
		return findByUuid(layout.getUuid());
	}

	@Override
	public MailLayout update(MailLayout footer) throws BusinessException {
		try {
			return mailLayoutRepository.update(footer);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(BusinessErrorCode.MAILLAYOUT_NOT_FOUND,
					"Cannot update footer " + footer);
		}
	}

	@Override
	public void delete(MailLayout val) throws BusinessException {
		AbstractDomain domain = val.getDomain();

		if (mailConfigRepository.isMailLayoutReferenced(val)) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_IN_USE,
					"Cannot delete mail footer as it's still in use.");
		}
		domain.getMailContents().remove(val);
		abstractDomainRepository.update(domain);
		mailLayoutRepository.delete(val);
	}
}
