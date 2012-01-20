
package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.FunctionalityType;
import org.linagora.linShare.core.domain.vo.FunctionalityVo;
import org.linagora.linShare.core.domain.vo.StringValueFunctionalityVo;

public class StringValueFunctionality extends OneValueFunctionality<String> {

	public StringValueFunctionality() {
		super();
	}

	public StringValueFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain, String value) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.STRING;
	}
	
	@Override
	public boolean businessEquals(Functionality obj) {
		if(super.businessEquals(obj)) {
			StringValueFunctionality o = (StringValueFunctionality)obj;
			if(value.equals(o.getValue())) {
				logger.debug("StringValueFunctionality : " + this.toString() + " is equal to StringValueFunctionality " + obj.toString());
				return true;
			}
		}
		logger.debug("StringValueFunctionality : " + this.toString() + " is not equal to StringValueFunctionality " + obj.toString());
		return false;
	}
	
	@Override
	public void updateFunctionalityFrom(Functionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		StringValueFunctionality f = (StringValueFunctionality)functionality;
		this.value = f.getValue();
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {
		StringValueFunctionalityVo f = (StringValueFunctionalityVo)functionality;
		this.value = f.getValue();
	}
}
