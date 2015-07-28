package org.linagora.linshare.core.service;

import org.linagora.linshare.core.domain.entities.AbstractFunctionality;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.exception.BusinessException;

public interface AbstractFunctionalityService<T extends AbstractFunctionality> {

	Iterable<T> findAll(Account actor, String domainId) throws BusinessException;

	T find(Account actor, String domainId, String identifier) throws BusinessException;

	T update(Account actor, String domainId, T f) throws BusinessException;
}
