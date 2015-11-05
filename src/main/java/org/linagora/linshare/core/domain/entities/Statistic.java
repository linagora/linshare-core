package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.StatisticType;

public abstract class Statistic extends GenericStatistic {
	public Statistic() {
		super();
	}

	public Statistic(Account account, AbstractDomain domain, AbstractDomain parentDomain, Long actualOperationSum,
			Long operationCount, Long deleteOperationCount, Long createOperationCount, Long createOperationSum,
			Long deleteOperationSum, Long diffOperationSum, StatisticType statisticType) {
		super(account, domain, parentDomain, actualOperationSum, operationCount, deleteOperationCount,
				createOperationCount, createOperationSum, deleteOperationSum, diffOperationSum, statisticType);
	}
}
