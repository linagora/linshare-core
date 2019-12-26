package org.linagora.linshare.core.facade.webservice.admin.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.DomainPermissionBusinessService;
import org.linagora.linshare.core.domain.constants.ExceptionStatisticType;
import org.linagora.linshare.core.domain.constants.ExceptionType;
import org.linagora.linshare.core.domain.constants.Role;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.admin.ExceptionStatisticAdminFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.ExceptionStatisticService;
import org.linagora.linshare.mongo.entities.ExceptionStatistic;

public class ExceptionStatisticAdminFacadeImpl extends AdminGenericFacadeImpl implements ExceptionStatisticAdminFacade {

	protected ExceptionStatisticService statisticService;

	protected AbstractDomainService abstractDomainService;

	protected DomainPermissionBusinessService permissionService;

	public ExceptionStatisticAdminFacadeImpl(
			AccountService accountService,
			ExceptionStatisticService statisticService,
			DomainPermissionBusinessService permissionService,
			AbstractDomainService abstractDomainService) {
		super(accountService);
		this.statisticService = statisticService;
		this.abstractDomainService = abstractDomainService;
		this.permissionService = permissionService;
	}

	@Override
	public ExceptionStatistic createExceptionStatistic(BusinessErrorCode errorCode, StackTraceElement[] stackTrace,
			ExceptionType type) {
		User authUser = getAuthentication();
		if (authUser == null) {
			return null;
		}
		return statisticService.createExceptionStatistic(errorCode, Arrays.toString(stackTrace), type, authUser);
	}

	@Override
	public Set<ExceptionStatistic> findBetweenTwoDates(String domainUuid, String beginDate, String endDate,
			List<ExceptionType> exceptionTypes, ExceptionStatisticType type) {
		User authUser = checkAuthentication(Role.ADMIN);
		Validate.notEmpty(domainUuid);
		AbstractDomain domain = abstractDomainService.findById(domainUuid);
		if (!permissionService.isAdminforThisDomain(authUser, domain)) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_READ_DOMAIN_ERROR,
					"You are not allowed to use this domain");
		}
		return statisticService.findBetweenTwoDates(authUser, domainUuid, beginDate, endDate, exceptionTypes,
				type);
	}

}
