/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
