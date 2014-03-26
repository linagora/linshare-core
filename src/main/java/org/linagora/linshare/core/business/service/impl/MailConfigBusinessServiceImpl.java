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
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailConfigRepository;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MailConfigBusinessServiceImpl implements MailConfigBusinessService {

	private final MailConfigRepository mailConfigRepository;
	private final AbstractDomainRepository abstractDomainRepository;

	public MailConfigBusinessServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final MailConfigRepository mailConfigRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.mailConfigRepository = mailConfigRepository;
	}

	@Override
	public MailConfig findByUuid(String uuid) {
		return mailConfigRepository.findByUuid(uuid);
	}

	@Override
	public void create(AbstractDomain domain, MailConfig cfg)
			throws BusinessException {
		MailConfig rootCfg = abstractDomainRepository.getUniqueRootDomain()
				.getCurrentMailConfiguration();

		// copy root domain's mailconfig
		cfg.setMailContents(Sets.newHashSet(rootCfg.getMailContents()));
		cfg.setMailFooters(Maps.newHashMap(rootCfg.getMailFooters()));
		cfg.setMailLayoutHtml(rootCfg.getMailLayoutHtml());
		cfg.setMailLayoutText(rootCfg.getMailLayoutText());

		mailConfigRepository.create(cfg);
	}

	@Override
	public void update(MailConfig cfg) throws BusinessException {
		try {
			mailConfigRepository.update(cfg);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					"Cannot update mailconfig " + cfg);
		}
	}

	@Override
	public void delete(MailConfig cfg) throws BusinessException {
		/*
		 * abort if this mailconfig is still in use by some domains
		 */
		if (!abstractDomainRepository.findByCurrentMailConfig(cfg).isEmpty()) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_IN_USE,
					"Cannot delete mailconfig " + cfg);
		}
		try {
			AbstractDomain domain = cfg.getDomain();

			domain.getMailConfigs().remove(cfg);
			abstractDomainRepository.update(domain);
			mailConfigRepository.delete(cfg);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					"Cannot delete mailconfig " + cfg);
		}
	}
}
