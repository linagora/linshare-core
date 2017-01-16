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

import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.business.service.MailConfigBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContentLang;
import org.linagora.linshare.core.domain.entities.MailFooterLang;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.MailConfigRepository;
import org.linagora.linshare.core.repository.MailContentLangRepository;
import org.linagora.linshare.core.repository.MailFooterLangRepository;

public class MailConfigBusinessServiceImpl implements MailConfigBusinessService {

	private final MailConfigRepository mailConfigRepository;
	private final AbstractDomainRepository abstractDomainRepository;
	private final MailContentLangRepository mailContentLangRepository;
	private final MailFooterLangRepository mailFooterLangRepository;

	public MailConfigBusinessServiceImpl(
			final AbstractDomainRepository abstractDomainRepository,
			final MailConfigRepository mailConfigRepository,
			final MailContentLangRepository mailContentLangRepository,
			final MailFooterLangRepository mailFooterLangRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.mailConfigRepository = mailConfigRepository;
		this.mailContentLangRepository = mailContentLangRepository;
		this.mailFooterLangRepository = mailFooterLangRepository;
	}

	@Override
	public MailConfig findByUuid(String uuid) throws BusinessException {
		MailConfig mailConfig = mailConfigRepository.findByUuid(uuid);
		if (mailConfig == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Can not find mailConfig " + uuid);
		}
		return mailConfig;
	}

	@Override
	public MailConfig create(AbstractDomain domain, MailConfig cfg)
			throws BusinessException {
		MailConfig rootCfg = abstractDomainRepository.getUniqueRootDomain()
				.getCurrentMailConfiguration();

		Set<MailContentLang> rootMcl = rootCfg.getMailContentLangs();
		Map<Integer, MailFooterLang> rootMfl = rootCfg.getMailFooters();

		// copy root domain's mailconfig
		cfg.setMailLayoutHtml(rootCfg.getMailLayoutHtml());
		cfg.setReadonly(false);

		for (MailContentLang mcl : rootMcl) {
			MailContentLang tmp = new MailContentLang(mcl);
			tmp.setMailConfig(cfg);
			cfg.getMailContentLangs().add(tmp);
		}
		for (Map.Entry<Integer, MailFooterLang> e : rootMfl.entrySet()) {
			MailFooterLang tmp = new MailFooterLang(e.getValue());
			tmp.setMailConfig(cfg);
			cfg.getMailFooters().put(e.getKey(),
					tmp);
		}

		return mailConfigRepository.create(cfg);
	}

	@Override
	public MailConfig update(MailConfig cfg) throws BusinessException {
		try {
			return mailConfigRepository.update(cfg);
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

	@Override
	public MailContentLang findContentLangByUuid(String uuid) {
		return mailContentLangRepository.findByUuid(uuid);
	}

	@Override
	public MailContentLang createContentLang(MailContentLang contentLang)
			throws BusinessException {
		MailConfig config = contentLang.getMailConfig();
		Language lang = Language.fromInt(contentLang.getLanguage());
		MailContentType type = MailContentType.fromInt(contentLang
				.getMailContentType());

		if (mailContentLangRepository.findMailContent(config, lang, type) != null) {
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENTLANG_DUPLICATE,
					"Cannot create mail footer lang with language " + lang);
		}
		contentLang = mailContentLangRepository.create(contentLang);
		config.getMailContentLangs().add(contentLang);
		mailConfigRepository.update(config);
		return contentLang;
	}

	@Override
	public MailContentLang updateContentLang(MailContentLang contentLang)
			throws BusinessException {
		try {
			return mailContentLangRepository.update(contentLang);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENTLANG_NOT_FOUND,
					"Cannot update mailconfig " + contentLang);
		}
	}

	@Override
	public void deleteContentLang(MailContentLang contentLang)
			throws BusinessException {
		try {
			mailContentLangRepository.delete(contentLang);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENTLANG_NOT_FOUND,
					"Cannot delete mailconfig " + contentLang);
		}
	}

	@Override
	public MailFooterLang findFooterLangByUuid(String uuid) {
		return mailFooterLangRepository.findByUuid(uuid);
	}

	@Override
	public MailFooterLang createFooterLang(MailFooterLang footerLang)
			throws BusinessException {
		MailConfig config = footerLang.getMailConfig();

		if (config.getMailFooters().containsKey(footerLang.getLanguage())) {
			throw new BusinessException(
					BusinessErrorCode.MAILCONTENTLANG_DUPLICATE,
					"Cannot create mail footer lang with language "
							+ Language.fromInt(footerLang.getLanguage()));
		}
		footerLang = mailFooterLangRepository.create(footerLang);
		config.getMailFooters().put(footerLang.getLanguage(), footerLang);
		mailConfigRepository.update(config);
		return footerLang;
	}

	@Override
	public MailFooterLang updateFooterLang(MailFooterLang footerLang)
			throws BusinessException {
		try {
			return mailFooterLangRepository.update(footerLang);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(
					BusinessErrorCode.MAILFOOTERLANG_NOT_FOUND,
					"Cannot update mailconfig " + footerLang);
		}
	}

	@Override
	public void deleteFooterLang(MailFooterLang footerLang)
			throws BusinessException {
		try {
			mailFooterLangRepository.delete(footerLang);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(
					BusinessErrorCode.MAILFOOTERLANG_NOT_FOUND,
					"Cannot delete mailconfig " + footerLang);
		}
	}
}
