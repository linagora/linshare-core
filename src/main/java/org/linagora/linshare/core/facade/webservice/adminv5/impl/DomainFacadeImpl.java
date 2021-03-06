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

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.facade.webservice.admin.impl.AdminGenericFacadeImpl;
import org.linagora.linshare.core.facade.webservice.adminv5.DomainFacade;
import org.linagora.linshare.core.facade.webservice.adminv5.dto.DomainDto;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;

import com.google.common.collect.Sets;

public class DomainFacadeImpl extends AdminGenericFacadeImpl implements DomainFacade {

	private final AbstractDomainService abstractDomainService;

	public DomainFacadeImpl(AccountService accountService, AbstractDomainService abstractDomainService) {
		super(accountService);
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public Set<DomainDto> findAll(boolean tree) {
		User authUser = checkAuthentication(Role.ADMIN);
		Set<DomainDto> domainDtos= Sets.newHashSet();
		if (tree) {
			String domainUuid = authUser.getDomain().getUuid();
			AbstractDomain abstractDomain = abstractDomainService.findById(domainUuid);
			DomainDto dto = DomainDto.getTreeUp(abstractDomain);
			for (AbstractDomain child : abstractDomainService.getSubDomainsByDomain(domainUuid)) {
				DomainDto childDto = DomainDto.getTree(child);
				for (AbstractDomain childNested : abstractDomainService.getSubDomainsByDomain(childDto.getUuid())) {
					childDto.addChild(DomainDto.getTree(childNested));
				}
				dto.addChild(childDto);
			}
			return Sets.newHashSet(dto);
		} else {
			List<AbstractDomain> entities = abstractDomainService.findAll(authUser);
			for (AbstractDomain abstractDomain : entities) {
				domainDtos.add(DomainDto.getLight(abstractDomain));
			}
		}
		return domainDtos;
	}

	@Override
	public DomainDto find(String domain, boolean tree, boolean detail) {
		AbstractDomain abstractDomain = abstractDomainService.findById(domain);
		if (tree) {
			DomainDto dto = DomainDto.getTreeUp(abstractDomain);
			for (AbstractDomain child : abstractDomainService.getSubDomainsByDomain(domain)) {
				DomainDto childDto = DomainDto.getTree(child);
				for (AbstractDomain childNested : abstractDomainService.getSubDomainsByDomain(childDto.getUuid())) {
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
}
