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
package org.linagora.linshare.core.business.service;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.UserDailyStat;

public interface UserDailyStatBusinessService{

	/**
	 * 
	 * @param user : the targeted user
	 * @param currentUsedSpace the current used space for the targeted user.
	 * @param today
	 * @return UserDailyStat
	 */
	UserDailyStat create(User user, Long currentUsedSpace, Date today);

	List<UserDailyStat> findBetweenTwoDates(User user, Date beginDate, Date endDate);

	void deleteBeforeDate(Date date);

	List<Account> findAccountBetweenTwoDates(Date beginDate, Date endDate);

	List<String> findUuidAccountBetweenTwoDates(Date beginDate, Date endDate);
}
