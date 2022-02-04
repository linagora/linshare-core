/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
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
