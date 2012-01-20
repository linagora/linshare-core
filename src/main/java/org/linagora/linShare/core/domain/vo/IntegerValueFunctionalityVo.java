package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.constants.FunctionalityType;

public class IntegerValueFunctionalityVo extends FunctionalityVo {

	protected Integer value;

	public IntegerValueFunctionalityVo(String identifier, String domainIdentifier, Integer value) {
		super(identifier, domainIdentifier);
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Functionality identifier is : " + domainIdentifier + " : " + identifier + " :: " + this.getValue();
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.INTEGER;
	}
	
}
