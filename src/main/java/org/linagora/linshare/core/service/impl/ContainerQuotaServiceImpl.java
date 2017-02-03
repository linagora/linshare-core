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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.ContainerQuotaService;

public class ContainerQuotaServiceImpl extends GenericServiceImpl<Account, Quota> implements ContainerQuotaService {

	private ContainerQuotaBusinessService business;

	public ContainerQuotaServiceImpl(QuotaResourceAccessControl rac,
			ContainerQuotaBusinessService ensembleQuotaBusinessService) {
		super(rac);
		this.business = ensembleQuotaBusinessService;
	}

	@Override
	public List<ContainerQuota> findAll(Account actor) {
		return business.findAll();
	}

	@Override
	public List<ContainerQuota> findAll(Account actor, AbstractDomain domain) {
		return business.findAll(domain);
	}

	@Override
	public ContainerQuota find(Account actor, AbstractDomain domain, ContainerQuotaType containerQuotaType) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(domain, "Domain must be set.");
		Validate.notNull(containerQuotaType, "ContainerQuotaType must be set.");
//		checkReadPermission(actor, null, ContainerQuota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		ContainerQuota ensembleQuota = business.find(domain, containerQuotaType);
		if(ensembleQuota == null){
			throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND, "Can not found ensemble " + containerQuotaType.toString() + " quota of the domain "+domain.getUuid());
		}
		return ensembleQuota;
	}

	@Override
	public ContainerQuota find(Account actor, String uuid) {
		ContainerQuota entity = business.find(uuid);
		if(entity == null){
			throw new BusinessException(BusinessErrorCode.CONTAINER_QUOTA_NOT_FOUND, "Can not found quota container : " + uuid);
		}
		return entity;
	}

	@Override
	public ContainerQuota update(Account actor, ContainerQuota cq) {
		Validate.notNull(actor, "Acctor must be set.");
		Validate.notNull(cq, "Entity must be set.");
		// checkUpdatePermission(actor, null, ContainerQuota.class,
		// BusinessErrorCode.QUOTA_UNAUTHORIZED, null, domain);
		ContainerQuota entity = find(actor, cq.getUuid());
		return business.update(entity, cq);
	}

}
