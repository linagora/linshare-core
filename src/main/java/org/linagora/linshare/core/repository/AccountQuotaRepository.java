package org.linagora.linshare.core.repository;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;

public interface AccountQuotaRepository extends GenericQuotaRepository<AccountQuota> {

	AccountQuota find(Account account);
}
