/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.DomainPattern;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.DomainPatternFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.UserProviderService;
import org.linagora.linshare.webservice.dto.DomainPatternDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainPatternFacadeImpl extends AdminGenericFacadeImpl implements DomainPatternFacade {

	private static final Logger logger = LoggerFactory.getLogger(DomainPatternFacadeImpl.class);
	
	private final UserProviderService userProviderService;

	public DomainPatternFacadeImpl(final AccountService accountService, final UserProviderService userProviderService) {
		super(accountService);
		this.userProviderService = userProviderService;
	}

	@Override
	public List<DomainPatternDto> getDomainPatterns() throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		List<DomainPattern> domainPatterns = userProviderService.findAllUserDomainPattern();
		List<DomainPatternDto> res = new ArrayList<DomainPatternDto>();
		for (DomainPattern domainPattern : domainPatterns) {
			res.add(new DomainPatternDto(domainPattern));
		}
		return res;
	}
	
	@Override
	public List<DomainPatternDto> getModels() throws BusinessException {
		List<DomainPattern> domainPatterns = userProviderService.findAllDomainPattern();
		List<DomainPatternDto> res = new ArrayList<DomainPatternDto>();
		for (DomainPattern domainPattern : domainPatterns) {
			res.add(new DomainPatternDto(domainPattern));
		}
		return res;
	}

	@Override
	public void updateDomainPattern(DomainPatternDto domainPatternDto) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		userProviderService.updateDomainPattern(new DomainPattern(domainPatternDto));
	}

	@Override
	public void createDomainPattern(DomainPatternDto domainPatternDto) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		userProviderService.createDomainPattern(new DomainPattern(domainPatternDto));
	}

	@Override
	public void deleteDomainPattern(DomainPatternDto domainPatternDto) throws BusinessException {
		checkAuthentication(Role.SUPERADMIN);
		userProviderService.deletePattern(domainPatternDto.getIdentifier());
	}

}
