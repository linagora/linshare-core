/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainService;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class DomainFacadeImpl extends AdminGenericFacadeImpl implements DomainFacade {

	private final DomainService domainService;

	public DomainFacadeImpl(AccountService accountService, DomainService domainService) {
		super(accountService);
		this.domainService = domainService;
	}

	@Override
	public Set<DomainDto> findAll(boolean tree) {
		User authUser = checkAuthentication(Role.ADMIN);
		Set<DomainDto> domainDtos= Sets.newHashSet();
		if (tree) {
			String domainUuid = authUser.getDomain().getUuid();
			AbstractDomain abstractDomain = domainService.find(authUser,domainUuid);
			DomainDto dto = DomainDto.getTreeUp(abstractDomain);
			for (AbstractDomain child : domainService.getSubDomainsByDomain(authUser, domainUuid)) {
				DomainDto childDto = DomainDto.getTree(child);
				for (AbstractDomain childNested : domainService.getSubDomainsByDomain(authUser, childDto.getUuid())) {
					childDto.addChild(DomainDto.getTree(childNested));
				}
				dto.addChild(childDto);
			}
			return Sets.newHashSet(dto);
		} else {
			List<AbstractDomain> entities = domainService.findAll(authUser);
			for (AbstractDomain abstractDomain : entities) {
				domainDtos.add(DomainDto.getLight(abstractDomain));
			}
		}
		return domainDtos;
	}

	@Override
	public DomainDto find(String domain, boolean tree, boolean detail) {
		// Missing Access control ?
		User authUser = checkAuthentication(Role.ADMIN);
		AbstractDomain abstractDomain = domainService.find(authUser, domain);
		if (tree) {
			DomainDto dto = DomainDto.getTreeUp(abstractDomain);
			for (AbstractDomain child : domainService.getSubDomainsByDomain(authUser, domain)) {
				DomainDto childDto = DomainDto.getTree(child);
				for (AbstractDomain childNested : domainService.getSubDomainsByDomain(authUser, childDto.getUuid())) {
					childDto.addChild(DomainDto.getTree(childNested));
				}
				dto.addChild(childDto);
			}
			return dto;
		}else if (detail) {
			return DomainDto.getFull(abstractDomain);
		} else {
			return DomainDto.getLight(abstractDomain);
		}
	}

	@Override
	public DomainDto create(DomainDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto.getParent(), "Domain parent object must be set.");
		String parentUuid = dto.getParent().getUuid();
		Validate.notEmpty(parentUuid, "Domain parent must be set.");
		AbstractDomain parentDomain = domainService.find(authUser, parentUuid);
		AbstractDomain create = domainService.create(authUser, dto.getName(), dto.getDescription(), dto.getType(), parentDomain);
		return DomainDto.getFull(create);
	}

	@Override
	public DomainDto update(String uuid, DomainDto dto) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notNull(dto, "Missing payload.");
		if (!Strings.isNullOrEmpty(uuid)) {
			dto.setUuid(uuid);
		}
		Validate.notEmpty(dto.getUuid(), "DomainDto's uuid or uuid path param must be set");
		Validate.notNull(dto.getType(), "Missing domain type");
		AbstractDomain domain = dto.getType().toDomain(dto);
		AbstractDomain update = domainService.update(authUser, dto.getUuid(), domain);
		return DomainDto.getFull(update);
	}

	@Override
	public DomainDto delete(String uuid, DomainDto dto) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		if (Strings.isNullOrEmpty(uuid)) {
			Validate.notNull(dto, "Missing domain uuid in the path param");
			uuid = dto.getUuid();
			Validate.notEmpty(uuid, "Missing domain uuid in the payload.");
		}
		if (uuid.equals(LinShareConstants.rootDomainIdentifier)) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You can't remove root domain.");
		}
		AbstractDomain domain = domainService.find(authUser, uuid);
		// TODO: delete domain or mark it to purge
		// TODO: delete all users into this domain
		// TODO: Do we handle nested doamins or or forbid deletion if nested domains exist ? 
		return DomainDto.getFull(domain);
	}

}
