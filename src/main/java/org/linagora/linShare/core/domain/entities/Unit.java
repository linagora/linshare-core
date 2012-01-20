package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.FileSizeUnit;
import org.linagora.linShare.core.domain.constants.UnitType;

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
