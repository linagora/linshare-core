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
package org.linagora.linshare.core.service.impl;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

import org.linagora.linshare.core.service.TimeService;

public class TimeServiceImpl implements TimeService {

	public static final Period ONE_YEAR = Period.ofYears(1);

	@Override
	public Date dateNow() {
		return new Date();
	}

	@Override
	public LocalDate now() {
		return LocalDate.now();
	}

	@Override
	public Date previousYear() {
		// Adding one year on instant is failing due to TZ / DST -> have to convert to LocalDate
		return Date.from(dateNow()
			.toInstant()
			.atZone(ZoneId.systemDefault())
			.toLocalDate()
			.minus(ONE_YEAR)
			.atStartOfDay()
			.atZone(ZoneId.systemDefault())
			.toInstant());
	}
}
