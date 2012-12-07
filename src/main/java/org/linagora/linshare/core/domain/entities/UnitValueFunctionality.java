
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.vo.FunctionalityVo;
import org.linagora.linshare.core.domain.vo.SizeValueFunctionalityVo;
import org.linagora.linshare.core.domain.vo.TimeValueFunctionalityVo;

public class UnitValueFunctionality extends OneValueFunctionality<Integer> {

	protected Unit<?> unit;
	
	public UnitValueFunctionality() {
		super();
	}

	public UnitValueFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain, Integer value, Unit<?> unit) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
		this.unit = unit;
	}
	
	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT;
	}

	public Unit<?> getUnit() {
		return unit;
	}

	public void setUnit(Unit<?> unit) {
		this.unit = unit;
	}
	
	@Override
	public boolean businessEquals(Functionality obj) {
		if(super.businessEquals(obj)) {
			UnitValueFunctionality o = (UnitValueFunctionality)obj;
			if(value.equals(o.getValue())) {
				if(unit.businessEquals(o.getUnit())) {
					logger.debug("UnitValueFunctionality : " + this.toString() + " is equal to UnitValueFunctionality " + obj.toString());
					return true;
				}
			}
		}
		logger.debug("UnitValueFunctionality : " + this.toString() + " is not equal to UnitValueFunctionality " + obj.toString());
		return false;
	}
	
	public Object clone() {
		UnitValueFunctionality func = null;
      	func = (UnitValueFunctionality) super.clone();
      	func.unit = (Unit<?>) unit.clone();
	    return func;
  	}
	
	@Override
	public void updateFunctionalityFrom(Functionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		UnitValueFunctionality f = (UnitValueFunctionality)functionality;
		this.value = f.getValue();
		this.unit.updateUnitFrom(f.getUnit());
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {
		
		if(functionality.getType().equals(FunctionalityType.UNIT_SIZE)) {
			SizeValueFunctionalityVo f = (SizeValueFunctionalityVo)functionality;
			if(f.getSize() != null) {
				this.value = f.getSize();
				FileSizeUnitClass sizeUnit = (FileSizeUnitClass)getUnit();
				sizeUnit.setUnitValue(f.getUnit());
			}
			
		} else if(functionality.getType().equals(FunctionalityType.UNIT_TIME)) {
			TimeValueFunctionalityVo f = (TimeValueFunctionalityVo)functionality;
			if(f.getTime() != null) {
				this.value = f.getTime();
				TimeUnitClass timeUnit = (TimeUnitClass)getUnit();
				timeUnit.setUnitValue(f.getUnit());
			}
		} else if(functionality.getType().equals(FunctionalityType.UNIT_BOOLEAN_TIME)) {
			TimeValueFunctionalityVo f = (TimeValueFunctionalityVo)functionality;
			if(f.getTime() != null) {
				this.value = f.getTime();
				TimeUnitClass timeUnit = (TimeUnitClass)getUnit();
				timeUnit.setUnitValue(f.getUnit());
			}
		}
	}
}
