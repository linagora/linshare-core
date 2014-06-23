package org.linagora.linshare.core.facade.webservice.admin.impl;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.TechnicalAccountPermission;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.TechnicalAccountPermissionFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.TechnicalAccountPermissionService;
import org.linagora.linshare.webservice.dto.TechnicalAccountPermissionDto;

public class TechnicalAccountPermissionFacadeImpl extends AdminGenericFacadeImpl
		implements TechnicalAccountPermissionFacade {

	private final TechnicalAccountPermissionService technicalAccountPermissionService;
	
	private final AbstractDomainService domainService;

	public TechnicalAccountPermissionFacadeImpl(final AccountService accountService,
			final TechnicalAccountPermissionService technicalAccountPermissionService,
			final AbstractDomainService domainService) {
		super(accountService);
		this.technicalAccountPermissionService = technicalAccountPermissionService;
		this.domainService = domainService;
	}

	@Override
	public TechnicalAccountPermissionDto find(String uuid) throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(uuid, "uuid must be set.");
		TechnicalAccountPermission permission = technicalAccountPermissionService.find(actor, uuid);
		technicalAccountPermissionService.delete(actor, permission);
		return new TechnicalAccountPermissionDto(permission);
	}

	@Override
	public TechnicalAccountPermissionDto update(TechnicalAccountPermissionDto dto)
			throws BusinessException {
		User actor = checkAuth();
		Validate.notEmpty(dto.getUuid(), "uuid must be set.");
		TechnicalAccountPermission tap = new TechnicalAccountPermission(dto);
		for (String domain: dto.getDomains()) {
			if (domain != null)
				tap.addDomain(domainService.findById(domain));
		}
		TechnicalAccountPermission permission = technicalAccountPermissionService.update(actor, tap);
		return new TechnicalAccountPermissionDto(permission);
	}

	/**
	 * Helpers
	 */
	
	private User checkAuth() throws BusinessException {
		return checkAuthentication(Role.SUPERADMIN);
	}
}
