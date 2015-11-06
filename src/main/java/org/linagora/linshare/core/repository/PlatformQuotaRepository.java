package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.PlatformQuota;

public interface PlatformQuotaRepository extends GenericQuotaRepository<PlatformQuota> {

	PlatformQuota find();
}
