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
package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.UUID;

import org.linagora.linshare.core.business.service.MailFooterBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailFooterLangRepository;
import org.linagora.linshare.core.repository.MailFooterRepository;

public class MailFooterBusinessServiceImpl implements MailFooterBusinessService {

	private final AbstractDomainRepository abstractDomainRepository;

	private final MailFooterRepository mailFooterRepository;

	private final MailFooterLangRepository mailFooterLangRepository;

	public MailFooterBusinessServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final MailFooterRepository mailFooterRepository,
			final MailFooterLangRepository mailFooterLangRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.mailFooterRepository = mailFooterRepository;
		this.mailFooterLangRepository = mailFooterLangRepository;
	}

	@Override
	public MailFooter findByUuid(String uuid) {
		return mailFooterRepository.findByUuid(uuid);
	}

	@Override
	public MailFooter create(AbstractDomain domain, MailFooter footer)
			throws BusinessException {
		footer.setUuid(UUID.randomUUID().toString());
		footer.setCreationDate(new Date());
		footer.setModificationDate(new Date());
		domain.getMailFooters().add(footer);
		abstractDomainRepository.update(domain);
		return findByUuid(footer.getUuid());
	}

	@Override
	public MailFooter update(MailFooter footer) throws BusinessException {
		try {
			return mailFooterRepository.update(footer);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(BusinessErrorCode.MAILFOOTER_NOT_FOUND,
					"Cannot update footer " + footer);
		}
	}

	@Override
	public void delete(MailFooter val) throws BusinessException {
		AbstractDomain domain = val.getDomain();

		if (mailFooterLangRepository.isMailFooterReferenced(val)) {
			throw new BusinessException(BusinessErrorCode.MAILCONTENT_IN_USE,
					"Cannot delete mail footer as it's still in use.");
		}
		domain.getMailContents().remove(val);
		abstractDomainRepository.update(domain);
		mailFooterRepository.delete(val);
	}
}
