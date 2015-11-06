package org.linagora.linshare.core.repository.hibernate;

import org.linagora.linshare.core.domain.entities.PlatformQuota;
import org.linagora.linshare.core.repository.PlatformQuotaRepository;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class PlatformQuotaRepositoryImpl extends GenericQuotaRepositoryImpl<PlatformQuota>implements PlatformQuotaRepository {

	public PlatformQuotaRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public PlatformQuota find() {
		return super.find(null, null, null);
	}
}
