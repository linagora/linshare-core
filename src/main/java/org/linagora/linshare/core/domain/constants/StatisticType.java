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
package org.linagora.linshare.core.domain.constants;

public enum StatisticType {

	USER_DAILY_STAT,
	USER_WEEKLY_STAT,
	USER_MONTHLY_STAT,

	WORK_GROUP_DAILY_STAT,
	WORK_GROUP_WEEKLY_STAT,
	WORK_GROUP_MONTHLY_STAT,

	DOMAIN_DAILY_STAT,
	DOMAIN_WEEKLY_STAT,
	DOMAIN_MONTHLY_STAT;

	public static StatisticType fromString(String s) {
		try {
			return StatisticType.valueOf(s.toUpperCase());
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Doesn't match an existing StatiticType");
		}
	}
}
