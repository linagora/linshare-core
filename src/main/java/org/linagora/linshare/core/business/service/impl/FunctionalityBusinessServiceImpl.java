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
package org.linagora.linshare.core.business.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;

public class FunctionalityBusinessServiceImpl extends
		AbstractFunctionalityBusinessServiceImpl<Functionality> implements
		FunctionalityBusinessService {

	protected List<String> exclude = new ArrayList<String>();

	public FunctionalityBusinessServiceImpl(
			FunctionalityRepository functionalityRepository,
			AbstractDomainRepository abstractDomainRepository) {
		super(functionalityRepository, abstractDomainRepository);
		// A guest user can not create a guest, so guest functionalities are
		// useless.
		exclude.add(FunctionalityNames.GUESTS.toString());
		exclude.add(FunctionalityNames.INTERNAL_ENABLE_PERSONAL_SPACE.toString());
		exclude.add(FunctionalityNames.GUESTS__EXPIRATION.toString());
		exclude.add(FunctionalityNames.GUESTS__EXPIRATION.toString());
		exclude.add(FunctionalityNames.GUESTS__RESTRICTED.toString());
		exclude.add(FunctionalityNames.GUESTS__CAN_UPLOAD.toString());
		exclude.add(FunctionalityNames.GUESTS__EXPIRATION_ALLOW_PROLONGATION
				.toString());
	}

	@Override
	protected BusinessException getBusinessNotFoundException() {
		return new BusinessException(BusinessErrorCode.FUNCTIONALITY_NOT_FOUND, "Functionality not found.");
	}

	@Override
	public Functionality getFunctionality(AbstractDomain domain, String functionalityId) throws BusinessException {
		// TODO Auto-generated method stub
		Functionality functionality = super.getFunctionality(domain, functionalityId);
		if (domain.isGuestDomain()) {
			if (exclude.contains(functionality.getIdentifier())) {
				throw getBusinessNotFoundException();
			}
		}
		return functionality;
	}

	@Override
	public Set<Functionality> getAllFunctionalities(AbstractDomain domain,
			List<String> excludesIn) {
		List<String> excludesTemp = new ArrayList<String>();
		if (excludesIn != null) {
			excludesTemp.addAll(excludesIn);
		}
		if (domain.isGuestDomain()) {
			excludesTemp.addAll(exclude);
		}
		Set<Functionality> functionalities = super.getAllFunctionalities(domain, excludesTemp);
		if (domain.isGuestDomain()) {
			// We can't strip it because the user front-end is always expecting the functionality even if it disabled.
			// For admin frontend
			Functionality func = new Functionality(FunctionalityNames.GUESTS,
					false,
					new Policy(Policies.FORBIDDEN, false),
					new Policy(Policies.FORBIDDEN, false),
					domain);
			func.setDisplayable(false);
			functionalities.add(func);
		}
		return functionalities;
	}
}
