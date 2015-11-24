package org.linagora.linshare.core.repository;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.GenericStatistic;

public interface GenericStatisticRepository<T extends GenericStatistic> extends AbstractRepository<T> {

	List<T> findBetweenTwoDates(Account account, AbstractDomain domain, AbstractDomain parentDomain, Date beginDate, Date endDate, StatisticType statisticType);

	void deleteBeforeDate(Date date);

	List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate);

	List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate);

	List<AbstractDomain> findDomainBetweenTwoDates(Date beginDate, Date endDate);

	List<String> findIdentifierDomainBetweenTwoDates(Date beginDate, Date endDate);

	T create(T entity);

	Long sumOfOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfDeleteOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfCreateOperationCount(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfCreateOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfDeleteOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfDiffOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);

	Long sumOfActualOperationSum(AbstractDomain domain, Account account, Date beginDate, Date endDate);
}
