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

import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.Lists;

/** File size unit.
 */
public enum FileSizeUnit {
    KILO(0),
    MEGA(1),
    GIGA(2);

	private int value;

	private FileSizeUnit(final int value) {
		this.value = value;
	}

	public int toInt() {
		return value;
	}

	public static FileSizeUnit fromInt(final int value) {
		for (FileSizeUnit unit : values()) {
			if (unit.value == value) {
				return unit;
			}
		}
		throw new IllegalArgumentException(
				"Doesn't match an existing FileSizeUnit");
	}

	public long getPlainSize(final long size) {
		return size * BigInteger.valueOf(2).pow(10 * (value + 1)).longValue();
	}

	public long getSiSize(final long size) {
		return size * BigInteger.valueOf(1000).pow(value + 1).longValue();
	}

	public long fromPlainSize(final long size) {
		return size / BigInteger.valueOf(2).pow(10 * (value + 1)).longValue();
	}

	public long fromSiSize(final long size) {
		return size / BigInteger.valueOf(1000).pow(value + 1).longValue();
	}

	public static FileSizeUnit getMaxExactPlainSizeUnit(final long size) {
		FileSizeUnit maxUnit = FileSizeUnit.KILO;
		for (FileSizeUnit unit: FileSizeUnit.values()) {
			if (size % BigInteger.valueOf(2).pow(10 * (unit.value + 1)).longValue() == 0) {
				maxUnit = unit;
			}
		}
		return maxUnit;
	}

	public static List<String> strValues() {
		List<String> list = Lists.newArrayList();
		for (FileSizeUnit unit : values()) {
			list.add(unit.toString());
		}
		return list;
	}
}
