/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.constants;

import java.math.BigInteger;

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
}
