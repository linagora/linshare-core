package org.linagora.linshare.core.business.service;

import java.util.Date;

import org.linagora.linshare.core.domain.entities.PlatformQuota;
import org.linagora.linshare.core.exception.BusinessException;

public interface PlatformQuotaBusinessService {

	PlatformQuota find() throws BusinessException;

	boolean exist();

	PlatformQuota createOrUpdate(Date today) throws BusinessException;

	PlatformQuota create(PlatformQuota entity) throws BusinessException;

	PlatformQuota update(PlatformQuota entity) throws BusinessException;
}
