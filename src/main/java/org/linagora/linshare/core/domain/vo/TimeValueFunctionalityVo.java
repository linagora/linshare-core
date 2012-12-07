package org.linagora.linshare.core.domain.vo;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.constants.TimeUnit;

public class TimeValueFunctionalityVo extends FunctionalityVo {

	protected Integer time;
	
	protected TimeUnit unit;

	public TimeValueFunctionalityVo(String identifier, String domainIdentifier, Integer time, TimeUnit unit) {
		super(identifier, domainIdentifier);
		this.time = time;
		this.unit = unit;
	}
	
	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT_TIME;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public TimeUnit getUnit() {
		return unit;
	}

	public void setUnit(TimeUnit unit) {
		this.unit = unit;
	}
	
	@Override
	public String toString() {
		return "Functionality identifier is : " + domainIdentifier + " : " + identifier + " :: " + getTime() + " : " + getUnit();
	}
}
