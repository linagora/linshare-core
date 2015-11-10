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

import java.util.Date;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.DomainQuotaBusinessService;
import org.linagora.linshare.core.business.service.EnsembleQuotaBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.PlatformQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.DomainQuota;
import org.linagora.linshare.core.domain.entities.EnsembleQuota;
import org.linagora.linshare.core.domain.entities.PlatformQuota;
import org.linagora.linshare.core.domain.entities.Quota;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.QuotaResourceAccessControl;
import org.linagora.linshare.core.service.QuotaService;

public class QuotaServiceImpl extends GenericServiceImpl<Account, Quota>implements QuotaService {

	private AccountQuotaBusinessService accountQuotaBusinessService;
	private DomainQuotaBusinessService domainQuotaBusinessService;
	private EnsembleQuotaBusinessService ensembleQuotaBusinessService;
	private PlatformQuotaBusinessService platformQuotaBusinessService;
	private OperationHistoryBusinessService operationHistoryBusinessService;

	public QuotaServiceImpl(QuotaResourceAccessControl rac,
			AccountQuotaBusinessService accountQuotaBusinessService,
			DomainQuotaBusinessService domainQuotaBusinessService,
			EnsembleQuotaBusinessService ensembleQuotaBusinessService,
			PlatformQuotaBusinessService platformQuotaBusinessService,
			OperationHistoryBusinessService operationHistoryBusinessService) {
		super(rac);
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.domainQuotaBusinessService = domainQuotaBusinessService;
		this.ensembleQuotaBusinessService = ensembleQuotaBusinessService;
		this.platformQuotaBusinessService = platformQuotaBusinessService;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
	}

	@Override
	public boolean checkIfCanAddFile(Account actor, Account owner, Long fileSize, EnsembleType ensembleType)
			throws BusinessException {
		Validate.notNull(actor, "Actor must be set.");
		Validate.notNull(owner, "Owner must be set.");
		Validate.notNull(fileSize, "FileSize must be set.");
		Validate.notNull(ensembleType, "EnsembleType must be set.");
		checkReadPermission(actor, owner, Quota.class, BusinessErrorCode.QUOTA_UNAUTHORIZED, null);
		boolean canUserAddInAccountSpace = canUserAddInAccountSpace(owner, fileSize);
		boolean canUserAddInDomainSpace = canUserAddInDomainSpace(owner.getDomain(), fileSize);
		boolean canUserAddInEnsembleSpace = canUserAddInEnsembleSpace(owner.getDomain(), ensembleType, fileSize);
		boolean canUserAddInPlatformSpace = canUserAddInPlatform(fileSize);
		return canUserAddInAccountSpace && canUserAddInDomainSpace && canUserAddInEnsembleSpace
				&& canUserAddInPlatformSpace;
	}

	private boolean canUserAddInAccountSpace(Account account, Long fileSize) throws BusinessException {
		AccountQuota accountQuota = accountQuotaBusinessService.find(account);
		Long quota = accountQuota.getQuota();
		Long currentValue = accountQuota.getCurrentValue();
		Long fileSizeMax = accountQuota.getFileSizeMax();
		Long todayConsumption = operationHistoryBusinessService.sumOperationValue(account, null, new Date(), null,
				null);
		Long totalConsumption = currentValue + todayConsumption + fileSize;
		return fileSize < fileSizeMax && totalConsumption < quota;
	}

	private boolean canUserAddInDomainSpace(AbstractDomain domain, Long fileSize) throws BusinessException {
		DomainQuota domainQuota = domainQuotaBusinessService.find(domain);
		Long quota = domainQuota.getQuota();
		Long currentValue = domainQuota.getCurrentValue();
		Long fileSizeMax = domainQuota.getFileSizeMax();
		Long todayConsumption = operationHistoryBusinessService.sumOperationValue(null, domain, new Date(), null, null);
		Long totalConsumption = currentValue + todayConsumption + fileSize;
		return fileSize < fileSizeMax && totalConsumption < quota;
	}

	private boolean canUserAddInEnsembleSpace(AbstractDomain domain, EnsembleType ensembleType, Long fileSize)
			throws BusinessException {
		EnsembleQuota ensembleQuota = ensembleQuotaBusinessService.find(domain, ensembleType);
		Long quota = ensembleQuota.getQuota();
		Long currentValue = ensembleQuota.getCurrentValue();
		Long fileSizeMax = ensembleQuota.getFileSizeMax();
		Long todayConsumption = operationHistoryBusinessService.sumOperationValue(null, domain, new Date(), null,
				ensembleType);
		Long totalConsumption = currentValue + todayConsumption + fileSize;
		return fileSize < fileSizeMax && totalConsumption < quota;
	}

	private boolean canUserAddInPlatform(Long fileSize) throws BusinessException {
		PlatformQuota platformQuota = platformQuotaBusinessService.find();
		Long quota = platformQuota.getQuota();
		Long currentValue = platformQuota.getCurrentValue();
		Long fileSizeMax = platformQuota.getFileSizeMax();
		Long todayConsumption = operationHistoryBusinessService.sumOperationValue(null, null, new Date(), null, null);
		Long totalConsumption = currentValue + todayConsumption + fileSize;
		return fileSize < fileSizeMax && totalConsumption < quota;
	}
}
