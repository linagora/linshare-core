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

package org.linagora.linshare.core.business.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.FunctionalityBusinessService;
import org.linagora.linshare.core.domain.constants.DomainType;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.FunctionalityType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class FunctionalityBusinessServiceImpl implements FunctionalityBusinessService {

	private final static Logger logger = LoggerFactory.getLogger(FunctionalityBusinessServiceImpl.class);
	
	private FunctionalityRepository functionalityRepository;

	private AbstractDomainRepository abstractDomainRepository;

	public FunctionalityBusinessServiceImpl(FunctionalityRepository functionalityRepository,
			AbstractDomainRepository abstractDomainRepository) {
		super();
		this.functionalityRepository = functionalityRepository;
		this.abstractDomainRepository = abstractDomainRepository;
	}

	private class InnerFunctionality {

		private final Functionality functionality;

		private final String identifier;

		public InnerFunctionality(Functionality functionality) {
			super();
			this.functionality = functionality;
			this.identifier = functionality.getIdentifier();
		}

		public Functionality getFunctionality() {
			return functionality;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((identifier == null) ? 0 : identifier.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InnerFunctionality other = (InnerFunctionality) obj;
			if (identifier == null) {
				if (other.identifier != null)
					return false;
			} else if (!identifier.equals(other.identifier))
				return false;
			return true;
		}
	}

	/**
	 * Helper : convert Functionality list to InnerFunctionality list.
	 * 
	 * @param functionalities
	 * @return innerFunctionalities
	 */
	private Set<InnerFunctionality> convertToInnerFunctionality(Set<Functionality> functionalities) {
		Set<InnerFunctionality> res = new HashSet<InnerFunctionality>();
		for (Functionality f : functionalities) {
			res.add(new InnerFunctionality(f));
		}
		return res;
	}

	/**
	 * Helper : convert InnerFunctionality list to Functionality list.
	 * 
	 * @param functionalities
	 * @param exclude TODO
	 * @return innerFunctionalities
	 */
	private Set<Functionality> convertToFunctionality(Set<InnerFunctionality> functionalities, List<String> exclude) {
		Set<Functionality> res = new HashSet<Functionality>();
		if (exclude == null) {
			exclude = new ArrayList<String>();
		}
		for (InnerFunctionality f : functionalities) {
			if(!exclude.contains(f.getFunctionality().getIdentifier())) {
				res.add(f.getFunctionality());
			}
		}
		return res;
	}

	/**
	 * get all functionalities from current domain, then ask parent domains to
	 * get missing ones, recursively until all root domain is reached. At this
	 * point all LinShare functionalities are collected in the result list.
	 * 
	 * @param domain
	 * @return result contains unique list of InnerFunctionality.
	 *         InnerFunctionality is an association of functionality identifier
	 *         and a functionality.
	 */
	private Set<InnerFunctionality> getAllInnerFunctionalities(AbstractDomain domain) {
		Assert.notNull(domain);

		Set<InnerFunctionality> res = new HashSet<InnerFunctionality>();

		// Copy all functionalities from this domain to result list
		res.addAll(convertToInnerFunctionality(domain.getFunctionalities()));

		if (domain.getParentDomain() != null) {
			// get parent functionalities using recursive call
			for (InnerFunctionality innerFunctionality : this.getAllInnerFunctionalities(domain.getParentDomain())) {
				// only functionality identifier is compared.
				if (!res.contains(innerFunctionality)) {
					res.add(innerFunctionality);
				}
			}
		}
		return res;
	}

	private Functionality getParentFunctionality(AbstractDomain domain, String functionalityIdentifier) {
		Assert.notNull(domain);
		Assert.notNull(functionalityIdentifier);

		Functionality res = null;
		AbstractDomain parentDomain = domain.getParentDomain();
		if (parentDomain != null) {
			for (Functionality f : parentDomain.getFunctionalities()) {
				// only functionality identifier is compared.
				if (f.getIdentifier().equals(functionalityIdentifier)) {
					res = f;
					break;
				}
			}
			// no functionality was found in the current parentDomain. Trying parentDomain of parentDomain, using recursive call.
			if (res == null) {
				res = getParentFunctionality(parentDomain, functionalityIdentifier);
			}
		}
		return res;
	}

	@Override
	public Set<Functionality> getAllFunctionalities(AbstractDomain domain) {
		// Check if the current argument is a real domain.
		AbstractDomain abstractDomain = abstractDomainRepository.findById(domain.getIdentifier());
		if (abstractDomain != null) {
			List<String> exclude = new ArrayList<String>();
			if(domain.getDomainType().equals(DomainType.GUESTDOMAIN)) {
				// A guest user can not create a guest, so account expiration for guests is useless.
				exclude.add(FunctionalityNames.ACCOUNT_EXPIRATION);
			}
			return convertToFunctionality(this.getAllInnerFunctionalities(abstractDomain), exclude);
		}
		return null;
	}

	@Override
	public Set<Functionality> getAllFunctionalities(String domain) {
		AbstractDomain abstractDomain = abstractDomainRepository.findById(domain);
		return getAllFunctionalities(abstractDomain);
	}

	@Override
	public boolean activationPolicyIsMutable(Functionality functionality, String domain) {
		Assert.notNull(functionality);
		Assert.notNull(domain);

		// Check if the current functionality belong to the current domain.
		if (!functionality.getDomain().getIdentifier().equals(domain)) {
			// The current functionality belong to a parent domain.
			AbstractDomain abstractDomain = abstractDomainRepository.findById(domain);
			Functionality ancestorFunc = getParentFunctionality(abstractDomain, functionality.getIdentifier());
			// We check if the parent domain allow the current domain to
			// modify/override activation policy configuration.
			if (ancestorFunc == null || ancestorFunc.getActivationPolicy().isMutable()) {
				return true;
			}
		} else {
			// The current functionality belong to the current domain.
			if (!functionality.getActivationPolicy().isSystem()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean configurationPolicyIsMutable(Functionality functionality, String domain) {
		Assert.notNull(functionality);
		Assert.notNull(domain);
		
		// Check if the current functionality belong to the current domain.
		if (!functionality.getDomain().getIdentifier().equals(domain)) {
			// The current functionality belong to a parent domain.
			AbstractDomain abstractDomain = abstractDomainRepository.findById(domain);
			Functionality ancestorFunc = getParentFunctionality(abstractDomain, functionality.getIdentifier());
			// We check if the parent domain allow the current domain to
			// modify/override activation policy configuration.
			if (ancestorFunc == null) {
				return true;
			}
			if (ancestorFunc.getActivationPolicy().getStatus()) {
				if (ancestorFunc.getConfigurationPolicy().isMutable()) {
					return true;
				}
			}
		} else {
			// I have to check if I have the permission to modify the configuration status of this functionality
			if (functionality.getActivationPolicy().getStatus()) {
				if (!functionality.getConfigurationPolicy().isSystem()) {
					return true;
				}
			}
		}
		return false;
	}
	
	private  Functionality getFunctionalityEntityByIdentifiers(AbstractDomain domain, String functionalityId) {
		Assert.notNull(domain);
		Assert.notNull(functionalityId);
		Functionality fonc = functionalityRepository.findById(domain, functionalityId);
		if (fonc == null && domain.getParentDomain() != null) {
			fonc = getFunctionalityEntityByIdentifiers(domain.getParentDomain(), functionalityId);
		}
		return fonc;
	}
	
	@Override
	public Functionality getFunctionality(String domainId, String functionalityId) {
		Assert.notNull(domainId);
		Assert.notNull(functionalityId);
		
		AbstractDomain domain = abstractDomainRepository.findById(domainId);
		Functionality functionality = getFunctionalityEntityByIdentifiers(domain, functionalityId);
		// Never returns the entity when we try to modify the functionality.
		// The current functionality returned could belong to a parent domain. 
		// In this case, the functionality will be clone, linked to the current domain and the updated by the FonctionalityBusiness update method.
		return (Functionality)functionality.clone();
	}

	@Override
	public void update(String domainId, Functionality functionality) throws BusinessException {

		AbstractDomain domain = abstractDomainRepository.findById(domainId);
		Functionality entity = getFunctionalityEntityByIdentifiers(domain, functionality.getIdentifier());
		
		
		if (entity.getDomain().getIdentifier().equals(functionality.getDomain().getIdentifier())) {
			logger.debug("this functionality belongs to the current domain");
			
		} else {
//			logger.debug("this functionality does not belong to the current domain");
//			// This functionality does not belong to the current domain.
//			if (!functionalityDto.businessEquals(functionalityEntity, true)) {
//				// This functionality is different, it needs to be persist.
//				functionalityDto.setDomain(currentDomain);
//
//				functionalityRepository.create(functionalityDto);
//				logger.info("Update by creation of a new functionality for : " + functionalityDto.getIdentifier() + " link to domain : " + currentDomain.getIdentifier());
//			} else { // no differences
//				logger.debug("functionality " + functionalityDto.getIdentifier()+ " was not modified.");
//			}
		}
	}

	@Override
	public void delete(String domainId, String functionalityId) throws IllegalArgumentException, BusinessException {
		Assert.notNull(domainId);
		Assert.notNull(functionalityId);
		
		Functionality f = getFunctionality(domainId, functionalityId);

		// The functionality belong to the current domain. We can delete it.
		if (f.getDomain().getIdentifier().equals(domainId)){
			logger.debug("suppression of the functionality : " + domainId + " : " + functionalityId);
			AbstractDomain domain = abstractDomainRepository.findById(domainId);
			Functionality rawFunc = functionalityRepository.findById(domain, functionalityId);
			functionalityRepository.delete(rawFunc);
			domain.getFunctionalities().remove(rawFunc);
			abstractDomainRepository.update(domain);
		} else {
			logger.warn("You are try to delete the functionality "  + domainId + " : " + functionalityId + " which does not belong to the current domain : " + f.getDomain().getIdentifier());
		}	
	}
}
