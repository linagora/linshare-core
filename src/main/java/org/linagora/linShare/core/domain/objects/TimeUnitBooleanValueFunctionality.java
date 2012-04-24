
package org.linagora.linShare.core.domain.objects;

import org.linagora.linShare.core.domain.entities.TimeUnitClass;
import org.linagora.linShare.core.domain.entities.UnitBooleanValueFunctionality;

/**
 * This class is just for easy use. It is not an entity
 * @author fred
 *
 */
public class TimeUnitBooleanValueFunctionality extends UnitBooleanValueFunctionality{

	public TimeUnitBooleanValueFunctionality(UnitBooleanValueFunctionality f) {
		super();
		setActivationPolicy(f.getActivationPolicy());
		setConfigurationPolicy(f.getConfigurationPolicy());
		setDomain(f.getDomain());
		setId(f.getId());
		setIdentifier(f.getIdentifier());
		setSystem(f.isSystem());
		setUnit(f.getUnit());
		setValue(f.getValue());
	}
	
	public int toCalendarValue() {
		TimeUnitClass timeUnit = (TimeUnitClass)getUnit();
		return timeUnit.toCalendarValue();
	}
}
