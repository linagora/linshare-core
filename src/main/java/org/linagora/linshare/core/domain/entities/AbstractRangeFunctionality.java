
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
	
	public Object clone() {
		AbstractRangeFunctionality<U,T> func = null;
      	func = (AbstractRangeFunctionality<U,T>) super.clone();
      	func.minUnit = (T) minUnit.clone();
      	func.maxUnit = (T) maxUnit.clone();
	    return func;
  	}
}
