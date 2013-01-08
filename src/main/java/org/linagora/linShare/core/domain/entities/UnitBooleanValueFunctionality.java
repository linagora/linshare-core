
package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.FunctionalityType;
import org.linagora.linShare.core.domain.vo.FunctionalityVo;
import org.linagora.linShare.core.domain.vo.TimeValueBooleanFunctionalityVo;

public class UnitBooleanValueFunctionality extends UnitValueFunctionality {

	protected boolean bool;
	
	public UnitBooleanValueFunctionality() {
		super();
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT_BOOLEAN;
	}
	
	@Override
	public boolean businessEquals(Functionality obj, boolean checkPolicies) {
		if(super.businessEquals(obj, checkPolicies)) {
			UnitBooleanValueFunctionality o = (UnitBooleanValueFunctionality)obj;
			if(bool == o.isBool()) {
				logger.debug("UnitBooleanValueFunctionality : " + this.toString() + " is equal to UnitBooleanValueFunctionality " + obj.toString());
				return true;
			}
		}
		logger.debug("UnitBooleanValueFunctionality : " + this.toString() + " is not equal to UnitBooleanValueFunctionality " + obj.toString());
		return false;
	}
	
	public Object clone() {
		UnitBooleanValueFunctionality func = null;
      	func = (UnitBooleanValueFunctionality) super.clone();
	    return func;
  	}
	
	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		super.updateFunctionalityValuesOnlyFrom(functionality);
		UnitBooleanValueFunctionality f = (UnitBooleanValueFunctionality)functionality;
		this.bool = f.isBool();
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {
		
		if(functionality.getType().equals(FunctionalityType.UNIT_BOOLEAN_TIME)) {
			TimeValueBooleanFunctionalityVo f = (TimeValueBooleanFunctionalityVo)functionality;
			if(f.getTime() != null) {
				this.value = f.getTime();
				this.bool = f.isBool();
				TimeUnitClass timeUnit = (TimeUnitClass)getUnit();
				timeUnit.setUnitValue(f.getUnit());
			}
		} 
	}
}
