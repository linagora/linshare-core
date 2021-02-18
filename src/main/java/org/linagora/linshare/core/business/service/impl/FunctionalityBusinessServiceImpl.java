/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
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
		exclude.add(FunctionalityNames.INTERNAL_CAN_UPLOAD.toString());
		exclude.add(FunctionalityNames.GUESTS__EXPIRATION.toString());
		exclude.add(FunctionalityNames.GUESTS__EXPIRATION.toString());
		exclude.add(FunctionalityNames.GUESTS__RESTRICTED.toString());
		exclude.add(FunctionalityNames.GUESTS__CAN_UPLOAD.toString());
		exclude.add(FunctionalityNames.GUESTS__EXPIRATION_ALLOW_PROLONGATION
				.toString());
	}

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
