package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;
import org.linagora.linshare.core.domain.constants.UnitType;

public class FileSizeUnitClass extends Unit<FileSizeUnit> {

		
	public FileSizeUnitClass() {
		super();
	}
	
	@Override
	public UnitType getUnitType() {
		return UnitType.SIZE;
	}

	@Override
	public String toString() {
		return getUnitType().toString() + ":" + unitValue.toString();
	}

	public FileSizeUnitClass(FileSizeUnit unitvalue) {
		super(unitvalue);
	}
	
	public long getPlainSize(long size) {
		return unitValue.getPlainSize(size);
	}
}
