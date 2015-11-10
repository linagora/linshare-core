package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.constants.EnsembleType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;

public interface QuotaService {

	boolean checkIfCanAddFile(Account actor, Account owner, Long fileSize, EnsembleType ensembleType) throws BusinessException;
}
