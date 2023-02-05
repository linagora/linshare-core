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

public class ThreadMonthlyStat extends AccountStatistic {

	public ThreadMonthlyStat() {
		super();
	}

	public ThreadMonthlyStat(WorkGroup workGroup, AbstractDomain domain, AbstractDomain parentDomain, Long operationCount,
			Long deleteOperationCount, Long addOperationCount, Long addOperationSum, Long deleteOperationSum,
			Long diffOperationSum, Long actualOperationSum) {
		super(workGroup, domain, parentDomain, operationCount, deleteOperationCount, addOperationCount, addOperationSum,
				deleteOperationSum, diffOperationSum, actualOperationSum, StatisticType.WORK_GROUP_MONTHLY_STAT);
	}

	@Override
	public StatisticType getStatisticType() {
		return StatisticType.WORK_GROUP_MONTHLY_STAT;
	}

}
