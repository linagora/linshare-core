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
package org.linagora.linshare.webservice.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;

public class StatisticServiceUtils {

	public StatisticServiceUtils() {
	}

	protected Pair<Date, Date> checkDatesInitialization(String beginDate, String endDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date bDate = null;
		Date eDate = null;
		try {
			if (endDate == null) {
				Calendar endCalendar = new GregorianCalendar();
				endCalendar.set(Calendar.HOUR_OF_DAY, 23);
				endCalendar.set(Calendar.MINUTE, 59);
				endCalendar.set(Calendar.SECOND, 59);
				endCalendar.add(Calendar.SECOND, 1);
				eDate = endCalendar.getTime();
			} else {
				eDate = formatter.parse(endDate);
			}
			if (beginDate == null) {
				Calendar cal = new GregorianCalendar();
				cal.setTime(eDate);
				cal.add(Calendar.MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				bDate = cal.getTime();
			} else {
				bDate = formatter.parse(beginDate);
			}
		} catch (ParseException e) {
			throw new BusinessException(BusinessErrorCode.STATISTIC_DATE_PARSING_ERROR, "Can not parse the dates.");
		}
		return new ImmutablePair<>(bDate, eDate);
	}
}
