package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.EnsembleQuota;

public interface EnsembleQuotaRepository extends GenericQuotaRepository<EnsembleQuota> {

	EnsembleQuota find(AbstractDomain domain, EnsembleType ensembleType);
}
