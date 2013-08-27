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


public abstract class AbstractRangeFunctionality<U,T extends Unit<?>> extends Functionality {
	protected U min;

	protected U max;

	protected T minUnit;

	protected T maxUnit;

	public U getMin() {
		return min;
	}

	public void setMin(U min) {
		this.min = min;
	}

	public U getMax() {
		return max;
	}

	public void setMax(U max) {
		this.max = max;
	}

	public T getMinUnit() {
		return minUnit;
	}

	public void setMinUnit(T minUnit) {
		this.minUnit = minUnit;
	}

	public T getMaxUnit() {
		return maxUnit;
	}

	public void setMaxUnit(T maxUnit) {
		this.maxUnit = maxUnit;
	}

	public AbstractRangeFunctionality() {
		super();
	}

	public AbstractRangeFunctionality(String identifier, boolean system, Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain, U min, U max, T minUnit, T maxUnit ) {
		super(identifier, system, activationPolicy, configurationPolicy, domain);
		this.min = min;
		this.max = max;
		this.minUnit = minUnit;
		this.maxUnit = maxUnit;
	}

	@Override
	public boolean businessEquals(Functionality obj, boolean checkPolicies) {
		if(super.businessEquals(obj, checkPolicies)) {
			AbstractRangeFunctionality<?,?> a = (AbstractRangeFunctionality<?,?>)obj;
			if(min.equals(a.getMin())) {
				if(max.equals(a.getMax())) {
					if(minUnit.businessEquals(a.getMinUnit())) {
						if(maxUnit.businessEquals(a.getMaxUnit())) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public Object clone() {
		AbstractRangeFunctionality<U,T> func = null;
      	func = (AbstractRangeFunctionality<U,T>) super.clone();
      	func.minUnit = (T) minUnit.clone();
      	func.maxUnit = (T) maxUnit.clone();
	    return func;
  	}
}
