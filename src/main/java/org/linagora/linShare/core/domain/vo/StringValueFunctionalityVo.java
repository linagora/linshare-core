package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.constants.FunctionalityType;

public class StringValueFunctionalityVo extends FunctionalityVo {

	protected String value;

	public StringValueFunctionalityVo(String identifier, String domainIdentifier, String value) {
		super(identifier, domainIdentifier);
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "Functionality identifier is : " + domainIdentifier + " : " + identifier + " :: " + this.getValue();
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.STRING;
	}
	
	
}

