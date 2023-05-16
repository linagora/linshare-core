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
package org.linagora.linshare.core.facade.webservice.adminv5.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AllowDomain;
import org.linagora.linshare.core.domain.entities.DenyAllDomain;
import org.linagora.linshare.core.domain.entities.DomainPolicy;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.fields.DomainField;
import org.linagora.linshare.core.domain.entities.fields.SortOrder;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DomainPolicyService;
import org.linagora.linshare.core.service.DomainService;
import org.linagora.linshare.core.service.UserService;
import org.linagora.linshare.webservice.utils.PageContainer;
import org.linagora.linshare.webservice.utils.PageContainerAdaptor;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

public class DomainFacadeImpl extends AdminGenericFacadeImpl implements DomainFacade {

	private final DomainService domainService;

	private final DomainPolicyService domainPolicyService;

	private final UserService userService;

	private static PageContainerAdaptor<AbstractDomain, DomainDto> pageContainerAdaptor = new PageContainerAdaptor<>();

	public DomainFacadeImpl(
			AccountService accountService,
			DomainService domainService,
			DomainPolicyService domainPolicyService,
			UserService userService) {
		super(accountService);
		this.domainService = domainService;
		this.domainPolicyService = domainPolicyService;
		this.userService = userService;
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
	public PageContainer<DomainDto> findAll(
			Optional<String> domainType,
			Optional<String> name, Optional<String> description,
			Optional<String> parentUuid,
			SortOrder sortOrder, DomainField sortField,
			Integer pageNumber, Integer pageSize) {
		User authUser = checkAuthentication(Role.ADMIN);
		PageContainer<AbstractDomain> container = new PageContainer<>(pageNumber, pageSize);
		container = domainService.findAll(authUser, domainType, name, description, parentUuid, sortOrder, sortField, container);
		PageContainer<DomainDto> dto = pageContainerAdaptor.convert(container, DomainDto.toDto());
		return dto;
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
	public DomainDto create(DomainDto dto, boolean dedicatedDomainPolicy, String addItToDomainPolicy) {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notNull(dto.getParent(), "Domain parent object must be set.");
		String parentUuid = dto.getParent().getUuid();
		Validate.notEmpty(parentUuid, "Domain parent must be set.");
		AbstractDomain parentDomain = domainService.find(authUser, parentUuid);
		AbstractDomain created = domainService.create(authUser, dto.getName(), dto.getDescription(), dto.getType(), parentDomain, dto.getDefaultEmailLanguage());
		if(dedicatedDomainPolicy) {
			DomainPolicy policy = new DomainPolicy(dto.getName());
			DomainPolicy domainPolicy = domainPolicyService.create(policy);
			policy.getDomainAccessPolicy().addRule(new AllowDomain(created));
			policy.getDomainAccessPolicy().addRule(new DenyAllDomain());
			created.setPolicy(domainPolicy);
		} else if (addItToDomainPolicy != null)	{
			DomainPolicy domainPolicy = domainPolicyService.find(addItToDomainPolicy);
			domainPolicy.getDomainAccessPolicy().getRules().add(0, new AllowDomain(created));
			created.setPolicy(domainPolicy);
		}
		return DomainDto.getFull(created);
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
		if (!domainService.getSubDomainsByDomain(authUser, uuid).isEmpty()) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_FORBIDDEN, "You can't remove a parent domain.");
		}
		AbstractDomain domain = domainService.find(authUser, uuid);
		domainService.markToPurge(authUser, uuid);
		userService.deleteAllUsersFromDomain(authUser, uuid);
		return DomainDto.getFull(domain);
	}

}
