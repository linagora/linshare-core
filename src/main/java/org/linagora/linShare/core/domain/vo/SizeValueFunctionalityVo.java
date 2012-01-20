package org.linagora.linShare.core.domain.vo;

import org.linagora.linShare.core.domain.constants.FileSizeUnit;
import org.linagora.linShare.core.domain.constants.FunctionalityType;
import org.linagora.linShare.core.domain.constants.UnitType;

public class SizeValueFunctionalityVo extends FunctionalityVo {

	protected Integer size;
	
	protected FileSizeUnit unit;

	public SizeValueFunctionalityVo(String identifier, String domainIdentifier, Integer size, FileSizeUnit unit) {
		super(identifier, domainIdentifier);
		this.size = size;
		this.unit = unit;
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.UNIT_SIZE;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public FileSizeUnit getUnit() {
		return unit;
	}

	public void setUnit(FileSizeUnit unit) {
		this.unit = unit;
	}

	@Override
	public String toString() {
		return "Functionality identifier is : " + domainIdentifier + " : " + identifier + " :: " + getSize() + " : " + getUnit();
	}
	
}
