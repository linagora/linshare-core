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
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.MailFooter;
import org.linagora.linshare.core.domain.entities.MailLayout;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailConfigFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailConfigDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailFooterDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;

import com.google.common.collect.Sets;

public class MailConfigFacadeImpl extends AdminGenericFacadeImpl implements
		MailConfigFacade {

	private final MailConfigService mailConfigService;

	private final AbstractDomainService abstractDomainService;

	public MailConfigFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public Set<MailConfigDto> findAll(String domainId, boolean only)
			throws BusinessException {
		User user = checkAuthentication(Role.ADMIN);
		if (domainId == null) {
			domainId = user.getDomainId();
		}

		AbstractDomain domain = abstractDomainService.retrieveDomain(domainId);
		// TODO : check if the current user has the right to get MailConfig of
		// this domain
		Set<MailConfigDto> mailConfigsDto = new HashSet<MailConfigDto>();
		Iterable<MailConfig> configs = only ? domain.getMailConfigs()
				: mailConfigService.findAllConfigs(user, domainId);
		for (MailConfig mailConfig : configs) {
			mailConfigsDto.add(new MailConfigDto(mailConfig, getOverrideReadonly()));
		}
		return mailConfigsDto;
	}

	@Override
	public MailConfigDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		return new MailConfigDto(findConfig(authUser, uuid), getOverrideReadonly());
	}

	@Override
	public MailConfigDto create(MailConfigDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailConfig config = new MailConfig();
		transform(config, dto);
		return new MailConfigDto(mailConfigService.createConfig(authUser, config));
	}

	@Override
	public MailConfigDto update(MailConfigDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailConfig config = findConfig(authUser, dto.getUuid());
		transform(config, dto);
		config.setMailLayoutHtml(findLayout(authUser, dto.getMailLayout()));
		return new MailConfigDto(mailConfigService.updateConfig(authUser, config));
	}

	@Override
	public MailConfigDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailConfig config = mailConfigService.deleteConfig(authUser, uuid);
		return new MailConfigDto(config);
	}

	@Override
	public Set<MailContentDto> findAllContents(String mailConfigUuid,
			String mailContentType) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(mailConfigUuid, "mailConfigUuid must be set.");
		Validate.notEmpty(mailContentType, "mailContentType must be set.");

		MailConfig cfg = mailConfigService.findConfigByUuid(authUser, mailConfigUuid);
		MailContentType type = MailContentType.valueOf(mailContentType.toUpperCase());
		Set<MailContentDto> ret = Sets.newHashSet();

		List<MailContent> all = mailConfigService.findAllContents(authUser,
				cfg.getDomain().getUuid());
		// TODO Optimization needed.
		for (MailContent mc : all) {
			if (mc.getMailContentType() == type.toInt()) {
				ret.add(new MailContentDto(mc, getOverrideReadonly()));
			}
		}
		return ret;
	}

	@Override
	public Set<MailFooterDto> findAllFooters(String mailConfigUuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(mailConfigUuid, "mailConfigUuid must be set.");

		MailConfig cfg = mailConfigService.findConfigByUuid(authUser, mailConfigUuid);
		Set<MailFooterDto> ret = Sets.newHashSet();

		List<MailFooter> all = mailConfigService.findAllFooters(authUser, cfg
				.getDomain().getUuid());
		for (MailFooter footer : all) {
			ret.add(new MailFooterDto(footer, getOverrideReadonly()));
		}
		return ret;
	}

	/*
	 * Helpers
	 */

	private void transform(MailConfig config, MailConfigDto dto)
			throws BusinessException {
		config.setDomain(findDomain(dto.getDomain()));
		config.setName(dto.getName());
		config.setVisible(dto.isVisible());
	}

	private MailConfig findConfig(User authUser, String uuid)
			throws BusinessException {
		MailConfig config = mailConfigService.findConfigByUuid(authUser, uuid);

		if (config == null) {
			throw new BusinessException(BusinessErrorCode.MAILCONFIG_NOT_FOUND,
					"Mail config " + uuid + " doesn't exist.");
		}
		return config;
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


	@Override
	public void assign(String domainUuid, String mailConfigUuid) {
		User actor = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid, "Domain uuid must be set.");
		Validate.notEmpty(mailConfigUuid, "Mail config uuid must be set.");
		mailConfigService.assign(actor, domainUuid, mailConfigUuid);
	}
}
