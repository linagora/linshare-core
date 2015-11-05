package org.linagora.linshare.core.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.UserMonthlyStat;
import org.linagora.linshare.core.exception.BusinessException;

public interface MonthlyStatService {

	UserMonthlyStat create(Account actor, Account owner, Date beginDate, Date endDate) throws BusinessException;

	List<UserMonthlyStat> findBeforeDate(Account actor, Date creationDate) throws BusinessException;

	void deleteBeforeDate(Account actor, Date creationDate) throws BusinessException;

	List<Account> findAccountBetweenTwoDates(Account actor, Date beginDate, Date endDate) throws BusinessException;

	List<UserMonthlyStat> findByAccount(Account actor, Account owner) throws BusinessException;

	List<UserMonthlyStat> findByDomain(Account actor, AbstractDomain domain) throws BusinessException;
}
