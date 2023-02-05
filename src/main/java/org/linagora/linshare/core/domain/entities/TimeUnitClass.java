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

import org.linagora.linshare.core.domain.constants.TimeUnit;
import org.linagora.linshare.core.domain.constants.UnitType;

public class TimeUnitClass extends Unit<TimeUnit> {

		
	public TimeUnitClass() {
		super();
	}
	
	@Override
	public UnitType getUnitType() {
		return UnitType.TIME;
	}

	@Override
	public String toString() {
		return getUnitType().toString() + ":" + unitValue.toString();
	}

	public TimeUnitClass(TimeUnit unit_value) {
		super(unit_value);
	}
	
	public int toCalendarValue() {
		return unitValue.toCalendarValue();
	}
}
