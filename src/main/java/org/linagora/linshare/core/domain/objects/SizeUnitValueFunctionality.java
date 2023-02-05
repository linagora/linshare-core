/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.domain.objects;

import org.linagora.linshare.core.domain.entities.FileSizeUnitClass;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
/**
 * This class is just for easy use. It is not an entity
 * @author fred
 *
 */
public class SizeUnitValueFunctionality extends UnitValueFunctionality {
	
	public SizeUnitValueFunctionality(UnitValueFunctionality f) {
		super();
		setActivationPolicy(f.getActivationPolicy());
		setConfigurationPolicy(f.getConfigurationPolicy());
		if (f.getDelegationPolicy() != null) {
			setDelegationPolicy(f.getDelegationPolicy());
		}
		setDomain(f.getDomain());
		setId(f.getId());
		setIdentifier(f.getIdentifier());
		setSystem(f.isSystem());
		setValueUsed(f.getValueUsed());
		if (f.getValueUsed()) {
			setUnit(f.getUnit());
			setValue(f.getValue());
		}
		setMaxValueUsed(f.getMaxValueUsed());
		if (f.getMaxValueUsed()) {
			setMaxValue(f.getMaxValue());
			setMaxUnit(f.getMaxUnit());
		}
		setUnlimited(f.getUnlimited());
		setUnlimitedUsed(f.getUnlimitedUsed());
	}

	public long getSiSize() {
		FileSizeUnitClass sizeUnit = (FileSizeUnitClass)getUnit();
		return sizeUnit.getSiSize(getValue());
	}

	public long getMaxSiSize() {
		FileSizeUnitClass sizeUnit = (FileSizeUnitClass)getMaxUnit();
		return sizeUnit.getSiSize(getMaxValue());
	}
}
