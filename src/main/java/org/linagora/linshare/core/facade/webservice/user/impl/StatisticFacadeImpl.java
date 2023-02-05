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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;

import org.linagora.linshare.core.domain.constants.StatisticType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Statistic;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.common.dto.StatisticDto;
import org.linagora.linshare.core.facade.webservice.user.StatisticFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.StatisticService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class StatisticFacadeImpl extends GenericFacadeImpl implements
		StatisticFacade {
	private StatisticService statisticService;

	public StatisticFacadeImpl(AccountService accountService,
			StatisticService statisticService) {
		super(accountService);
		this.statisticService = statisticService;
	}

	public List<StatisticDto> findBetweenTwoDates(String actorUuid, String beginDate, String endDate,
			StatisticType statisticType) throws BusinessException {
		User authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		List<Statistic> listStat = statisticService.findBetweenTwoDates(authUser, actor, null, beginDate, endDate,
				statisticType);
		return ImmutableList.copyOf(Lists.transform(listStat, StatisticDto.toDto()));
	}
}
