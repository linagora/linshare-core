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

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.MailContentType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.MailConfig;
import org.linagora.linshare.core.domain.entities.MailContent;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.MailContentFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContainerDto;
import org.linagora.linshare.core.facade.webservice.admin.dto.MailContentDto;
import org.linagora.linshare.core.notifications.dto.ContextMetadata;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.MailConfigService;

public class MailContentFacadeImpl extends AdminGenericFacadeImpl implements
		MailContentFacade {

	private final MailConfigService mailConfigService;

	private final AbstractDomainService abstractDomainService;

	private final MailBuildingService mailBuildingService;

	private final DomainPermissionBusinessService domainPermissionService;

	public MailContentFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService,
			final MailBuildingService mailBuildingService,
			final DomainPermissionBusinessService domainPermissionService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
		this.mailBuildingService = mailBuildingService;
		this.domainPermissionService = domainPermissionService;
	}

	@Override
	public MailContentDto find(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		return new MailContentDto(findContent(authUser, uuid), getOverrideReadonly());
	}

	@Override
	public MailContentDto create(MailContentDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContent content = new MailContent();
		transform(content, dto);
		return new MailContentDto(mailConfigService.createContent(authUser,
				content));
	}

	@Override
	public MailContentDto update(MailContentDto dto) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContent content = findContent(authUser, dto.getUuid());

		transform(content, dto);
		return new MailContentDto(mailConfigService.updateContent(authUser, content));
	}

	@Override
	public MailContentDto delete(String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContent content = mailConfigService.deleteContent(authUser, uuid);
		return new MailContentDto(content);
	}

	@Override
	public Set<MailContentDto> findAll(String domainIdentifier, boolean only)
			throws BusinessException {
		User user = checkAuthentication(Role.ADMIN);
		if (domainIdentifier == null) {
			domainIdentifier = user.getDomainId();
		}

		AbstractDomain domain = abstractDomainService.retrieveDomain(domainIdentifier);
		if (!domainPermissionService.isAdminForThisDomain(user, domain)){
			throw new BusinessException("You are not allowed to manage this domain.");
		}

		Set<MailContentDto> mailContentsDto = new HashSet<MailContentDto>();
		Iterable<MailContent> contents = only ? domain.getMailContents()
				: mailConfigService.findAllVisibleContents(user, domainIdentifier);
		for (MailContent mailContent : contents) {
			mailContentsDto.add(new MailContentDto(mailContent, getOverrideReadonly()));
		}
		return mailContentsDto;
	}

	@Override
	public MailContainerDto fakeBuild(String mailContentUuid, String language, String mailConfigUuid, Integer flavor) {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContent content = findContent(authUser, mailContentUuid);
		return fakeBuild(language, mailConfigUuid, authUser, content, flavor);
	}

	@Override
	public MailContainerDto fakeBuild(MailContentDto dto, String language, String mailConfigUuid, Integer flavor) {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContent content = toFakeObject(dto);
		return fakeBuild(language, mailConfigUuid, authUser, content, flavor);
	}

	@Override
	public Response fakeBuildHtml(String mailContentUuid, String language, String mailConfigUuid, boolean subject, Integer flavor) {
		MailContainerDto fakeBuild = fakeBuild(mailContentUuid, language, mailConfigUuid, flavor);
		InputStream stream = null;
		if (subject) {
			stream = IOUtils.toInputStream(fakeBuild.getSubject(), "UTF-8");
		} else {
			stream = IOUtils.toInputStream(fakeBuild.getContent(), "UTF-8");
		}
		ResponseBuilder response = Response.ok(stream);
		response.header("Content-Type", "text/html; charset=UTF-8");
		response.header("Content-Transfer-Encoding", "binary");
		return response.build();
	}

	@Override
	public List<ContextMetadata> getAvailableVariables(String mailContentUuid) {
		User authUser = checkAuthentication(Role.ADMIN);
		MailContent content = findContent(authUser, mailContentUuid);
		MailContentType type = content.getType();
		return mailBuildingService.getAvailableVariables(type);
	}

	private MailContent toFakeObject(MailContentDto dto) {
		MailContent content = new MailContent();
		content.setSubject(dto.getSubject());
		content.setBody(dto.getBody());
		content.setMailContentType(MailContentType.valueOf(
				dto.getMailContentType()).toInt());
		content.setMessagesEnglish(dto.getMessagesEnglish());
		content.setMessagesFrench(dto.getMessagesFrench());
		content.setMessagesRussian(dto.getMessagesRussian());
		content.setMessagesVietnamese(dto.getMessagesVietnamese());
		return content;
	}

	private MailConfig findMailConfig(String mailConfigUuid, User authUser) {
		MailConfig config = null;
		if (mailConfigUuid != null) {
			config = mailConfigService.findConfigByUuid(authUser, mailConfigUuid);
		} else {
			config = abstractDomainService.getUniqueRootDomain().getCurrentMailConfiguration();
		}
		return config;
	}

	private MailContainerDto fakeBuild(String language, String mailConfigUuid, User authUser, MailContent content, Integer flavor) {
		MailConfig config = getFakeConfig(mailConfigUuid, authUser);
		Language languageEnum = getLanguage(language);
		MailContentType type = content.getType();
		config.replaceMailContent(languageEnum, type, content);
		MailContainerWithRecipient build = mailBuildingService.fakeBuild(type, config, languageEnum, flavor, false);
		return new MailContainerDto(build, type);
	}

	private Language getLanguage(String language) {
		Language languageEnum = Language.ENGLISH;
		if (language != null) {
			languageEnum = Language.valueOf(language);
		}
		return languageEnum;
	}

	private MailConfig getFakeConfig(String mailConfigUuid, User authUser) {
		MailConfig config = findMailConfig(mailConfigUuid, authUser);
		try {
			return config.clone();
		} catch (CloneNotSupportedException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new BusinessException("unexpected error");
		}
	}

	/*
	 * Helpers
	 */

	private void transform(MailContent content, MailContentDto dto)
			throws BusinessException {
		String mailContentType = dto.getMailContentType();
		if (StringUtils.isBlank(mailContentType) || !MailContentType.contains(mailContentType)) {
			throw new BusinessException(BusinessErrorCode.BAD_REQUEST, "Mail content type missing or unknown : " + mailContentType);
		}

		content.setDomain(findDomain(dto.getDomain()));
		content.setDescription(dto.getDescription());
		content.setVisible(dto.isVisible());
		content.setSubject(dto.getSubject());
		content.setBody(dto.getBody());
		content.setMailContentType(MailContentType.valueOf(mailContentType).toInt());
		content.setMessagesEnglish(dto.getMessagesEnglish());
		content.setMessagesFrench(dto.getMessagesFrench());
		content.setMessagesRussian(dto.getMessagesRussian());
		content.setMessagesVietnamese(dto.getMessagesVietnamese());
	}

	private MailContent findContent(User authUser, String uuid)
			throws BusinessException {
		MailContent mailContent = mailConfigService.findContentByUuid(authUser,
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
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Domain " + id + "doesn't exist.");
		}
		return domain;
	}

	private boolean getOverrideReadonly() {
		return mailConfigService.isTemplatingOverrideReadonlyMode();
	}

}
