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

import org.linagora.linshare.core.domain.constants.UnitType;

public abstract class Unit<U> implements Cloneable {

	/**
	 * Database persistence identifier
	 */
	private long persistenceId;
	
	protected U unitValue;

	public long getPersistenceId() {
		return persistenceId;
	}

	public void setPersistenceId(long persistenceId) {
		this.persistenceId = persistenceId;
	}

	public U getUnitValue() {
		return unitValue;
	}

	public void setUnitValue(U unitValue) {
		this.unitValue = unitValue;
	}

	public abstract UnitType getUnitType();
	
	public Unit() {
		super();
	}

	public Unit(U unitValue) {
		super();
		this.unitValue = unitValue;
	}

	public boolean businessEquals(Object obj) {
		Unit<?> u = (Unit<?>)obj;
		if(unitValue.equals(u.getUnitValue())) {
			if(getUnitType().toInt() == u.getUnitType().toInt()) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		Unit<U> u = null;
		    try {
		      	u = (Unit<U>) super.clone();
		    } catch(CloneNotSupportedException cnse) {
		      	cnse.printStackTrace(System.err);
		    }
		    
		    u.persistenceId=0;
		    return u;
  	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (int) (persistenceId ^ (persistenceId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Unit<?> other = (Unit<?>) obj;
		if (persistenceId != other.persistenceId)
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void updateUnitFrom(Unit<?> unit) {
		this.unitValue = (U) unit.getUnitValue();
	}
}
