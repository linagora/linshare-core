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
		@SuppressWarnings("rawtypes")
		Unit<?> other = (Unit) obj;
		if (persistenceId != other.persistenceId)
			return false;
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void updateUnitFrom(Unit<?> unit) {
		this.unitValue = (U) unit.getUnitValue();
	}
}
