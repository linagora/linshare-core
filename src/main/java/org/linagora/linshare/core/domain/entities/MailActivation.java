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

public class MailActivation extends AbstractFunctionality {

	protected boolean enable;

	public MailActivation() {
		super();
	}

	public MailActivation(boolean enable) {
		super();
		this.enable = enable;
	}

	public boolean isEnable() {
		return enable;
	}

	@Override
	public String toString() {
		return "MailActivation [enable=" + enable + ", identifier="
				+ identifier + ", domain=" + domain + "]";
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	@Override
	public boolean hasSomeParam() {
		return true;
	}

	@Override
	public void updateFunctionalityValuesOnlyFrom(AbstractFunctionality functionality) {
		MailActivation f = (MailActivation) functionality;
		this.enable = f.isEnable();
	}

	@Override
	public void updateFunctionalityFrom(AbstractFunctionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}

	@Override
	public boolean businessEquals(AbstractFunctionality obj, boolean checkPolicies) {
		if(super.businessEquals(obj, checkPolicies)) {
			MailActivation ma = (MailActivation)obj;
			if(ma.isEnable() == enable) {
				logger.debug("MailActivation : " + this.toString() + " is equal to MailActivation " + obj.toString());
				return true;
			}
		}
		logger.debug("MailActivation : " + this.toString() + " is not equal to MailActivation " + obj.toString());
		return false;
	}
}
