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
package org.linagora.linshare.core.domain.entities;


public abstract class OneValueFunctionality<U> extends Functionality {

	protected U value;

	public OneValueFunctionality() {
		super();
	}

	public OneValueFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain,U value) {
		super(identifier, system, activationPolicy, configurationPolicy, domain);
		this.value = value;
	}

	@Override
	public boolean hasSomeParam() {
		return true;
	}

	public U getValue() {
		return value;
	}

	public void setValue(U value) {
		this.value = value;
	}

	/**
	 * Check activation policy, delegation policy if exists and user value if
	 * defined
	 *
	 * @param userValue
	 *            : false, true, or null if user have not defined a value.
	 * @return integer
	 */
	public U getFinalValue(U userValue) {
		U result = getValue();
		if (getDelegationPolicy() != null && getDelegationPolicy().getStatus()) {
			if (userValue != null) {
				result = userValue;
			}
		}
		return result;
	}
}
