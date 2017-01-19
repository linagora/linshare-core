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
package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.io.IOUtils;
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

	public MailContentFacadeImpl(final AccountService accountService,
			final MailConfigService mailConfigService,
			final AbstractDomainService abstractDomainService,
			final MailBuildingService mailBuildingService) {
		super(accountService);
		this.mailConfigService = mailConfigService;
		this.abstractDomainService = abstractDomainService;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public MailContentDto find(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		return new MailContentDto(findContent(actor, uuid), getOverrideReadonly());
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
	public MailContentDto delete(String uuid) throws BusinessException {
		User actor = checkAuthentication(Role.ADMIN);
		MailContent content = mailConfigService.deleteContent(actor, uuid);
		return new MailContentDto(content);
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
			mailContentsDto.add(new MailContentDto(mailContent, getOverrideReadonly()));
		}
		return mailContentsDto;
	}

	@Override
	public MailContainerDto fakeBuild(String mailContentUuid, String language, String mailConfigUuid, Integer flavor) {
		User actor = checkAuthentication(Role.ADMIN);
		MailContent content = findContent(actor, mailContentUuid);
		return fakeBuild(language, mailConfigUuid, actor, content, flavor);
	}

	@Override
	public MailContainerDto fakeBuild(MailContentDto dto, String language, String mailConfigUuid, Integer flavor) {
		User actor = checkAuthentication(Role.ADMIN);
		MailContent content = toFakeObject(dto);
		return fakeBuild(language, mailConfigUuid, actor, content, flavor);
	}

	@Override
	public Response fakeBuildHtml(String mailContentUuid, String language, String mailConfigUuid, boolean subject, Integer flavor) {
		MailContainerDto fakeBuild = fakeBuild(mailContentUuid, language, mailConfigUuid, flavor);
		InputStream stream = null;
		try {
			if (subject) {
				stream = IOUtils.toInputStream(fakeBuild.getSubject(), "UTF-8");
			} else {
				stream = IOUtils.toInputStream(fakeBuild.getContent(), "UTF-8");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		ResponseBuilder response = Response.ok(stream);
		response.header("Content-Type", "text/html; charset=UTF-8");
		response.header("Content-Transfer-Encoding", "binary");
		return response.build();
	}

	@Override
	public List<ContextMetadata> getAvailableVariables(String mailContentUuid) {
		User actor = checkAuthentication(Role.ADMIN);
		MailContent content = findContent(actor, mailContentUuid);
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
		return content;
	}

	private MailConfig findMailConfig(String mailConfigUuid, User actor) {
		MailConfig config = null;
		if (mailConfigUuid != null) {
			config = mailConfigService.findConfigByUuid(actor, mailConfigUuid);
		} else {
			config = abstractDomainService.getUniqueRootDomain().getCurrentMailConfiguration();
		}
		return config;
	}

	private MailContainerDto fakeBuild(String language, String mailConfigUuid, User actor, MailContent content, Integer flavor) {
		MailConfig config = getFakeConfig(mailConfigUuid, actor);
		Language languageEnum = getLanguage(language);
		MailContentType type = content.getType();
		config.replaceMailContent(languageEnum, type, content);
		MailContainerWithRecipient build = mailBuildingService.fakeBuild(type, config, languageEnum, flavor);
		return new MailContainerDto(build, type);
	}

	private Language getLanguage(String language) {
		Language languageEnum = Language.ENGLISH;
		if (language != null) {
			languageEnum = Language.valueOf(language);
		}
		return languageEnum;
	}

	private MailConfig getFakeConfig(String mailConfigUuid, User actor) {
		MailConfig config = findMailConfig(mailConfigUuid, actor);
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
		content.setDomain(findDomain(dto.getDomain()));
		content.setDescription(dto.getDescription());
		content.setVisible(dto.isVisible());
		content.setSubject(dto.getSubject());
		content.setBody(dto.getBody());
		content.setMailContentType(MailContentType.valueOf(
				dto.getMailContentType()).toInt());
		content.setMessagesEnglish(dto.getMessagesEnglish());
		content.setMessagesFrench(dto.getMessagesFrench());
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
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST,
					"Domain " + id + "doesn't exist.");
		}
		return domain;
	}

	private boolean getOverrideReadonly() {
		return mailConfigService.isTemplatingOverrideReadonlyMode();
	}

}
