/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
 * Linshare R&D by subscribing to an Enterprise offer!” infobox and in the
 * e-mails sent with the Program, (ii) retain all hypertext links between
 * LinShare and linshare.org, between linagora.com and Linagora, and (iii)
 * refrain from infringing Linagora intellectual property rights over its
 * trademarks and commercial brands. Other Additional Terms apply, see
 * <http://www.linagora.com/licenses/> for more details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and <http://www.linagora.com/licenses/> for the Additional Terms
 * applicable to LinShare software.
 */
package org.linagora.linshare.core.domain.entities;

import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.vo.FunctionalityVo;
import org.linagora.linshare.core.domain.vo.IntegerValueFunctionalityVo;

public class IntegerValueFunctionality extends OneValueFunctionality<Integer> {

	public IntegerValueFunctionality() {
		super();
	}

	public IntegerValueFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain, Integer value) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, value);
	}
	
	@Override
	public FunctionalityType getType() {
		return FunctionalityType.INTEGER;
	}
	
	@Override
	public boolean businessEquals(Functionality obj, boolean checkPolicies) {
		if(super.businessEquals(obj, checkPolicies)) {
			IntegerValueFunctionality o = (IntegerValueFunctionality)obj;
			if(value.equals(o.getValue())) {
				logger.debug("IntegerValueFunctionality : " + this.toString() + " is equal to IntegerValueFunctionality " + obj.toString());
				return true;
			}
		}
		logger.debug("IntegerValueFunctionality : " + this.toString() + " is not equal to IntegerValueFunctionality " + obj.toString());
		return false;
	}
	
	@Override
	public void updateFunctionalityFrom(Functionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		IntegerValueFunctionality f = (IntegerValueFunctionality)functionality;
		this.value = f.getValue();
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFromVo(FunctionalityVo functionality) {
		IntegerValueFunctionalityVo f = (IntegerValueFunctionalityVo)functionality;
		this.value = f.getValue();
	}
}
