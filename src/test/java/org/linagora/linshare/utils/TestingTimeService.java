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
package org.linagora.linshare.utils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.linagora.linshare.core.service.TimeService;

public class TestingTimeService implements TimeService {

	private Date reference;

	@Override
	public Date dateNow() {
		return reference;
	}

	@Override
	public LocalDate now() {
		return LocalDate.ofInstant(reference.toInstant(), ZoneId.systemDefault());
	}

	@Override
	public Date previousYear() {
		// Not in used in tests now
		return dateNow();
	}

	public void setReference(Date reference) {
		this.reference = reference;
	}
}
