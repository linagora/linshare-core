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

import java.util.Calendar;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Defines a unit of time.
 */
public enum TimeUnit {
	DAY(0), WEEK(1), MONTH(2);

	private int value;

	private TimeUnit(int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public long toLong() {
		return (long) toInt();
	}

	public int toCalendarValue() {
		switch (this) {
		case DAY:
			return Calendar.DATE;
		case WEEK:
			return Calendar.WEEK_OF_MONTH;
		case MONTH:
			return Calendar.MONTH;
		default:
			throw new IllegalArgumentException(
					"Doesn't match an existing TimeUnit");
		}
	}

	public static TimeUnit fromInt(int value) {
		for (TimeUnit unit : values()) {
			if (unit.value == value) {
				return unit;
			}
		}
		throw new IllegalArgumentException("Doesn't match an existing TimeUnit");
	}

	public static TimeUnit fromInt(long value) {
		if (value > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(
					"Doesn't match an existing TimeUnit");
		}
		return fromInt((int) value);
	}

	public static List<String> strValues() {
		List<String> list = Lists.newArrayList();
		for (TimeUnit unit : values()) {
			list.add(unit.toString());
		}
		return list;
	}
}
