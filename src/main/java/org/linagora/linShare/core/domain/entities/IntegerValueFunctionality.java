
package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.FunctionalityType;
import org.linagora.linShare.core.domain.vo.FunctionalityVo;
import org.linagora.linShare.core.domain.vo.IntegerValueFunctionalityVo;

public class IntegerValueFunctionality extends OneValueFunctionality<Integer> {

	public IntegerValueFunctionality() {
		super();
	}

	public IntegerValueFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain, Integer value) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
	}
	
	@Override
	public FunctionalityType getType() {
		return FunctionalityType.INTEGER;
	}
	
	@Override
	public boolean businessEquals(Functionality obj) {
		if(super.businessEquals(obj)) {
			IntegerValueFunctionality o = (IntegerValueFunctionality)obj;
			if(value.equals(o.getValue())) {
				logger.debug("IntegerValueFunctionality : " + this.toString() + " is equal to IntegerValueFunctionality " + obj.toString());
				return true;
			}
		}
		logger.debug("IntegerValueFunctionality : " + this.toString() + " is not equal to IntegerValueFunctionality " + obj.toString());
		return false;
	}
	
	@Override
	public void updateFunctionalityFrom(Functionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		IntegerValueFunctionality f = (IntegerValueFunctionality)functionality;
		this.value = f.getValue();
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {
		IntegerValueFunctionalityVo f = (IntegerValueFunctionalityVo)functionality;
		this.value = f.getValue();
	}
}
