package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.TimeUnit;

public class TimeValueBooleanFunctionalityVo extends TimeValueFunctionalityVo {
	
	protected boolean bool;
	
	public TimeValueBooleanFunctionalityVo(String identifier, String domainIdentifier, Integer time, TimeUnit unit, boolean bool) {
		super(identifier, domainIdentifier, time, unit);
		this.bool = bool;
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	@Override
	public String toString() {
		return "Functionality identifier is : " + domainIdentifier + " : " + identifier + " :: " + getTime() + " : " + getUnit() + " : " + isBool();
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT_BOOLEAN_TIME;
	}
	
	
}
