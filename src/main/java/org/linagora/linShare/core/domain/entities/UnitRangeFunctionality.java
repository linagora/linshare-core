
package org.linagora.linShare.core.domain.entities;

import org.linagora.linShare.core.domain.constants.FunctionalityType;

public class UnitRangeFunctionality extends AbstractRangeFunctionality<Integer,Unit<?>> {

	public UnitRangeFunctionality() {
		super();
	}

	public UnitRangeFunctionality(String identifier, boolean system,
			Policy activationPolicy, Policy configurationPolicy,
			AbstractDomain domain, Integer min, Integer max,
			Unit<?> minUnit, Unit<?> maxUnit) {
		super(identifier, system, activationPolicy, configurationPolicy, domain, min,
				max, minUnit, maxUnit);
	}

	@Override
	public FunctionalityType getType() {
		return FunctionalityType.RANGE_UNIT;
	}
	
	@Override
	public void updateFunctionalityFrom(Functionality functionality) {
		super.updateFunctionalityFrom(functionality);
		this.updateFunctionalityValuesOnlyFrom(functionality);
	}
	
	@Override
	public void updateFunctionalityValuesOnlyFrom(Functionality functionality) {
		UnitRangeFunctionality u = (UnitRangeFunctionality)functionality;
		this.min = u.getMin();
		this.max = u.getMax();
		this.minUnit.updateUnitFrom(u.getMinUnit());
		this.maxUnit.updateUnitFrom(u.getMaxUnit());
	}
	
}
