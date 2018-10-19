/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.GroupLdapPattern;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.GroupPatternFacade;
import org.linagora.linshare.core.facade.webservice.admin.dto.GroupLdapPatternDto;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.GroupLdapPatternService;

import com.google.common.base.Strings;

public class GroupPatternFacadeImpl extends AdminGenericFacadeImpl implements GroupPatternFacade {

	private final GroupLdapPatternService groupLdapPatternService;

	public GroupPatternFacadeImpl(AccountService accountService,
			final GroupLdapPatternService groupPatternService) {
		super(accountService);
		this.groupLdapPatternService = groupPatternService;
	}

	@Override
	public Set<GroupLdapPatternDto> findAll() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		List<GroupLdapPattern> groupPatterns = groupLdapPatternService.findAll();
		Set<GroupLdapPatternDto> res = new HashSet<GroupLdapPatternDto>();
		for (GroupLdapPattern groupPattern : groupPatterns) {
			res.add(new GroupLdapPatternDto(groupPattern));
		}
		return res;
	}

	@Override
	public GroupLdapPatternDto find(String uuid) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(uuid, "group pattern uuid must be set.");
		return new GroupLdapPatternDto(groupLdapPatternService.find(uuid));
	}

	@Override
	public GroupLdapPatternDto create(GroupLdapPatternDto groupPattern) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		Validate.notEmpty(groupPattern.getLabel(), "domain pattern label must be set.");
		return new GroupLdapPatternDto(
				groupLdapPatternService.create(authUser, new GroupLdapPattern(groupPattern)));
	}

	@Override
	public GroupLdapPatternDto update(GroupLdapPatternDto groupPattern, String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		if (!Strings.isNullOrEmpty(uuid)) {
			groupPattern.setUuid(uuid);
		}
		Validate.notEmpty(groupPattern.getUuid(), "domain pattern uuid must be set.");
		return new GroupLdapPatternDto(
				groupLdapPatternService.update(authUser, new GroupLdapPattern(groupPattern)));
	}

	@Override
	public GroupLdapPatternDto delete(GroupLdapPatternDto groupPatternDto, String uuid) throws BusinessException {
		User authUser = checkAuthentication(Role.SUPERADMIN);
		GroupLdapPattern found = new GroupLdapPattern();
		if (!Strings.isNullOrEmpty(uuid)) {
			found = groupLdapPatternService.find(uuid);
		} else {
			Validate.notEmpty(groupPatternDto.getUuid(), "uuid must be set");
			found = groupLdapPatternService.find(groupPatternDto.getUuid());
		}
		Validate.notNull(found, "No groupLdapPattern found to delete");
		GroupLdapPattern pattern = groupLdapPatternService.delete(authUser, found, found.getUuid());
		return new GroupLdapPatternDto(pattern);
	}

}
