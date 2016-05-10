/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import org.linagora.linshare.core.business.service.AbstractFunctionalityBusinessService;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AbstractFunctionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.AbstractFunctionalityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public abstract class AbstractFunctionalityBusinessServiceImpl<T extends AbstractFunctionality> implements AbstractFunctionalityBusinessService<T> {

	private final static Logger logger = LoggerFactory.getLogger(AbstractFunctionalityBusinessServiceImpl.class);

	protected AbstractFunctionalityRepository<T> repository;

	protected AbstractDomainRepository abstractDomainRepository;

	public AbstractFunctionalityBusinessServiceImpl(AbstractFunctionalityRepository<T> functionalityRepository,
			AbstractDomainRepository abstractDomainRepository) {
		super();
		this.repository = functionalityRepository;
		this.abstractDomainRepository = abstractDomainRepository;
	}

	/**
	 * Helper : convert Functionality list to InnerFunctionality list.
	 *
	 * @param functionalities
	 * @return innerFunctionalities
	 */
	private Set<InnerFunctionality> convertToInnerFunctionality(Set<T> functionalities) {
		Set<InnerFunctionality> res = new HashSet<InnerFunctionality>();
		for (T f : functionalities) {
			res.add(new InnerFunctionality(f));
		}
		return res;
	}

	/**
	 * Helper : convert InnerFunctionality list to Functionality list.
	 * @param functionalities
	 * @param exclude
	 * @param domain
	 * @return
	 */
	private Set<T> convertToFunctionality(Set<InnerFunctionality> functionalities, List<String> exclude, AbstractDomain domain) {
		Set<T> res = new HashSet<T>();
		if (exclude == null) {
			exclude = new ArrayList<String>();
		}
		for (InnerFunctionality f : functionalities) {
			if(!exclude.contains(f.getFunctionality().getIdentifier())) {
				@SuppressWarnings("unchecked")
				T functionality = (T) f.getFunctionality().clone();
				functionality.setDomain(domain);
				initUpdateRight(functionality, domain);
				res.add(functionality);
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
		res.addAll(convertToInnerFunctionality(repository.findAll(domain)));

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

	private T getParentFunctionality(AbstractDomain domain, String functionalityIdentifier) {
		Assert.notNull(domain);
		Assert.notNull(functionalityIdentifier);

		T res = null;
		AbstractDomain parentDomain = domain.getParentDomain();
		if (parentDomain != null) {
			res = repository.findByDomain(parentDomain, functionalityIdentifier);
			// no functionality was found in the current parentDomain. Trying parentDomain of parentDomain, using recursive call.
			if (res == null) {
				res = getParentFunctionality(parentDomain, functionalityIdentifier);
			}
		}
		return res;
	}

	@Override
	public Set<T> getAllFunctionalities(AbstractDomain domain)
			throws BusinessException {
		return convertToFunctionality(this.getAllInnerFunctionalities(domain), null, domain);
	}

	@Override
	public Set<T> getAllFunctionalities(AbstractDomain domain, List<String> exclude) {
		return convertToFunctionality(this.getAllInnerFunctionalities(domain), exclude, domain);
	}

	@Override
	public void initUpdateRight(T functionality, AbstractDomain domain) {
		// ancestor functionality could be null
		// It is loaded in advance for performance purpose
		T ancestorFunc = getParentFunctionality(domain, functionality.getIdentifier());
		boolean parentAllowAPUpdate = activationPolicyIsMutable(functionality, domain, ancestorFunc);
		boolean parentAllowCPUpdate = false;
		boolean parentAllowDPUpdate = false;
		functionality.setDisplayable(false);
		functionality.getActivationPolicy().setParentAllowUpdate(parentAllowAPUpdate);
		Policy delegationPolicy = functionality.getDelegationPolicy();
		if (!parentAllowAPUpdate && functionality.getActivationPolicy().isForbidden()) {
			functionality.getConfigurationPolicy().setParentAllowUpdate(false);
			if (delegationPolicy != null) {
				delegationPolicy.setParentAllowUpdate(false);
			}
			functionality.setParentAllowParametersUpdate(false);
		} else {
			// CP update right
			parentAllowCPUpdate = configurationPolicyIsMutable(functionality, domain, ancestorFunc);
			functionality.getConfigurationPolicy().setParentAllowUpdate(parentAllowCPUpdate);
			// DP update right
			if (delegationPolicy != null) {
				parentAllowDPUpdate = delegationPolicyIsMutable(functionality, domain, ancestorFunc);
				delegationPolicy.setParentAllowUpdate(parentAllowDPUpdate);
			}
			// Parameters update right
			boolean parentAllowParametersUpdate = parametersAreMutable(functionality, domain, ancestorFunc);
			functionality.setParentAllowParametersUpdate(parentAllowParametersUpdate);
		}
		if (parentAllowAPUpdate) {
			functionality.setDisplayable(true);
		}
		if(parentAllowCPUpdate) {
			functionality.setDisplayable(true);
		}
		if(parentAllowDPUpdate) {
			functionality.setDisplayable(true);
		}
	}

	protected boolean activationPolicyIsMutable(T functionality, AbstractDomain domain, T ancestorFunc) throws BusinessException {
		Assert.notNull(functionality);
		Assert.notNull(domain);

		// Check if the current functionality belong to the current domain.
		if (functionality.getDomain().getUuid().equals(domain.getUuid())) {
			// The current functionality belong to the current domain.
			// We check if the parent domain allow the current domain to
			// modify/override activation policy configuration.
			if (ancestorFunc == null) {
				if (functionality.getActivationPolicy().isSystem()) {
					return false;
				}
				return true;
			} else if (ancestorFunc.getActivationPolicy().isMutable()) {
				return true;
			}
		} else {
			// the current functionality belongs to the parent functionality, so it could be considered as an ancestor.
			if (functionality.getActivationPolicy().isMutable()) {
				return true;
			}
		}
		return false;
	}

	protected boolean configurationPolicyIsMutable(T functionality, AbstractDomain domain, T ancestorFunc) throws BusinessException {
		Assert.notNull(functionality);
		Assert.notNull(domain);

		// Check if the current functionality belong to the current domain.
		if (functionality.getDomain().getUuid().equals(domain.getUuid())) {
			// we have to check if we have the permission to modify the configuration status of this functionality
			// We check if the parent domain allow the current domain to
			// modify/override activation policy configuration.
			if (ancestorFunc == null) {
				if (functionality.getConfigurationPolicy().isSystem()) {
					return false;
				}
				return true;
			} else if (ancestorFunc.getConfigurationPolicy().isMutable()) {
				return true;
			}
		} else {
			// The current functionality belong to a parent domain.
			if (functionality.getConfigurationPolicy().isMutable()) {
				return true;
			}
		}
		return false;
	}

	protected boolean parametersAreMutable(T functionality, AbstractDomain domain, T ancestorFunc) throws BusinessException {
		Assert.notNull(functionality);
		Assert.notNull(domain);

		// No need to check every conditions if there is no parameters to manage
		if (!functionality.hasSomeParam()) {
			return false;
		}
		// Check if the current functionality belong to the current domain.
		if (functionality.getDomain().getUuid().equals(domain.getUuid())) {
			// we have to check if we have the permission to modify the configuration status of this functionality
			// We check if the parent domain allow the current domain to
			// modify/override activation policy configuration.
			if (ancestorFunc == null) {
				// This functionality belongs to the root domain.
				if (functionality.isSystem()) {
					return false;
				}
				return true;
			} else {
				if (ancestorFunc.isSystem()) {
					return false;
				}
				if (ancestorFunc.getActivationPolicy().isForbidden()) {
					return false;
				}
				if (ancestorFunc.getConfigurationPolicy().isForbidden()) {
					return false;
				}
				return true;
			}
		} else {
			// The current functionality belong to a parent domain.
			if (functionality.isSystem()) {
				return false;
			}
			if (functionality.getActivationPolicy().isForbidden()) {
				return false;
			}
			return functionality.getConfigurationPolicy().getStatus();
		}
	}

	protected boolean delegationPolicyIsMutable(T functionality, AbstractDomain domain, T ancestorFunc) {
		Assert.notNull(functionality);
		Assert.notNull(domain);

		// Check if the current functionality belong to the current domain.
		if (functionality.getDomain().getUuid().equals(domain.getUuid())) {
			// we have to check if we have the permission to modify the configuration status of this functionality
			// We check if the parent domain allow the current domain to
			// modify/override activation policy configuration.
			if (ancestorFunc == null) {
				if (functionality.getDelegationPolicy() != null) {
					if (!functionality.getDelegationPolicy().isSystem()) {
						return true;
					}
				}
				return false;
			} else if (ancestorFunc.getDelegationPolicy() != null && ancestorFunc.getDelegationPolicy().isMutable()) {
				return true;
			}
		} else {
			// The current functionality belong to a parent domain.
			if (functionality.getDelegationPolicy() != null && functionality.getDelegationPolicy().isMutable()) {
				return true;
			}
		}
		return false;
	}

	protected  T getFunctionalityEntityByIdentifiers(AbstractDomain domain, String functionalityId) {
		Assert.notNull(domain);
		Assert.notNull(functionalityId);
		T fonc = repository.findByDomain(domain, functionalityId);
		if (fonc == null && domain.getParentDomain() != null) {
			fonc = getFunctionalityEntityByIdentifiers(domain.getParentDomain(), functionalityId);
		}
		return fonc;
	}

	@Override
	public T getFunctionality(AbstractDomain domain, String functionalityId) throws BusinessException {
		Assert.notNull(domain);
		Assert.notNull(functionalityId);
		T functionality = getFunctionalityEntityByIdentifiers(domain, functionalityId);
		if (functionality == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT, "Functionality not found.");
		}
		// Never returns the entity when we try to modify the functionality.
		// The current functionality returned could belong to a parent domain.
		// In this case, the functionality will be clone, linked to the input domain.
		@SuppressWarnings("unchecked")
		T clone = (T)functionality.clone();
		// Wet set the input domain to fake the out side world.
		clone.setDomain(domain);
		initUpdateRight(clone, domain);
		return clone;
	}

	@Override
	public T update(String domainId, T functionality) throws BusinessException {

		AbstractDomain currentDomain = findDomain(domainId);
		T entity = getFunctionalityEntityByIdentifiers(currentDomain, functionality.getIdentifier());

		if (entity.getDomain().getUuid().equals(functionality.getDomain().getUuid())) {
			// This functionality belongs to the current domain.
			logger.debug("this functionality belongs to the current domain");
			entity.updateFunctionalityFrom(functionality);
			repository.update(entity);
			permissionPropagationForActivationPolicy(entity);
			permissionPropagationForConfigurationPolicy(entity);
			if (entity.getDelegationPolicy() != null) {
				permissionPropagationForDelegationPolicy(entity);
			}
		} else {
			// This functionality does not belong to the current domain.
			logger.debug("this functionality does not belong to the current domain");
			if (!functionality.businessEquals(entity, true)) {
				// This functionality is different, it needs to be persist.
				functionality.setDomain(currentDomain);
				if (repository.findByDomain(currentDomain, functionality.getIdentifier()) == null) {
					repository.create(functionality);
					logger.info("Update by creation of a new functionality for : " + functionality.getIdentifier() + " link to domain : " + currentDomain.getUuid());
				} else {
					// TODO : to be check : This could really happen ? odd !
					logger.error("This should not happen ! You does not have the right to update the functionnality (All) '" + functionality +"' in domain '" + currentDomain +"'");
					throw new BusinessException(BusinessErrorCode.UNAUTHORISED_FUNCTIONALITY_UPDATE_ATTEMPT, "You does not have the right to update this functionality");
				}
			} else { // no differences
				logger.debug("functionality " + functionality.getIdentifier()+ " was not modified.");
			}
		}
		return this.getFunctionality(currentDomain, entity.getIdentifier());
	}

	@Override
	public void delete(String domainId, String functionalityId) throws IllegalArgumentException, BusinessException {
		Assert.notNull(domainId);
		Assert.notNull(functionalityId);

		AbstractDomain domain = findDomain(domainId);
		T functionality = getFunctionalityEntityByIdentifiers(domain, functionalityId);

		// The functionality belong to the current domain. We can delete it.
		if (functionality.getDomain().getUuid().equals(domainId)){
			logger.debug("suppression of the functionality : " + domainId + " : " + functionalityId);
			T rawFunc = repository.findByDomain(domain, functionalityId);
			repository.delete(rawFunc);
			domain.getFunctionalities().remove(rawFunc);
			abstractDomainRepository.update(domain);
		} else {
			logger.warn("You are currently in the domain : " + domainId + ". The functionaliy : " + functionalityId + ", you are trying to delete, belongs to the domain : " + functionality.getDomain().getUuid() + ".");
		}
	}

	private void deleteFunctionalityRecursivly(AbstractDomain domain, String functionalityIdentifier) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				Set<T> functionalities = repository.findAll(subDomain);
				for (T functionality : functionalities) {
					if(functionality.getIdentifier().equals(functionalityIdentifier)) {
						repository.delete(functionality);
						functionalities.remove(functionality);
						break;
					}
				}
				deleteFunctionalityRecursivly(subDomain, functionalityIdentifier);
			}
		}
	}

	private void updateActivationPolicyRecursivly(AbstractDomain domain, T functionality) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				for (T f : repository.findAll(subDomain)) {
					if(f.getIdentifier().equals(functionality.getIdentifier())) {
						f.getActivationPolicy().updatePolicyFrom(functionality.getActivationPolicy());
						repository.update(f);
						break;
					}
				}
				updateActivationPolicyRecursivly(subDomain, functionality);
			}
		}
	}

	private void updateConfigurationPolicyRecursivly(AbstractDomain domain, T functionality, boolean copyContent) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				for (T f : repository.findAll(subDomain)) {
					if(f.getIdentifier().equals(functionality.getIdentifier())) {
						f.getConfigurationPolicy().updatePolicyFrom(functionality.getConfigurationPolicy());
						if(copyContent) {
							f.updateFunctionalityValuesOnlyFrom(functionality);
						}
						repository.update(f);
						break;
					}
				}
				updateConfigurationPolicyRecursivly(subDomain, functionality, copyContent);
			}
		}
	}

	private void updateDelegationPolicyRecursivly(AbstractDomain domain, T functionality) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				for (T f : repository.findAll(subDomain)) {
					if(f.getIdentifier().equals(functionality.getIdentifier())) {
						f.getDelegationPolicy().updatePolicyFrom(functionality.getDelegationPolicy());
						repository.update(f);
						break;
					}
				}
				updateDelegationPolicyRecursivly(subDomain, functionality);
			}
		}
	}

	private void permissionPropagationForActivationPolicy(T functionalityEntity) throws IllegalArgumentException, BusinessException {
		if(functionalityEntity.getActivationPolicy().getPolicy().equals(Policies.FORBIDDEN)) {
			// We have to delete the activation policy of each functionality from all the sub  domains
			deleteFunctionalityRecursivly(functionalityEntity.getDomain(), functionalityEntity.getIdentifier());
		} else if(functionalityEntity.getActivationPolicy().getPolicy().equals(Policies.MANDATORY)) {
			// We have to update the activation policy of each functionality from all the sub domains
			updateActivationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity);
		}
	}

	private void permissionPropagationForConfigurationPolicy(T functionalityEntity) throws IllegalArgumentException, BusinessException {
		if(functionalityEntity.getConfigurationPolicy().getPolicy().equals(Policies.FORBIDDEN)) {
			// We have to update the configuration policy of each functionality from all the sub domains
			// The parameters of the current functionality are propagated to all sub functionalities
			updateConfigurationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity, true);
		} else if(functionalityEntity.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY)) {
			// We have to update the configuration policy of each functionality from all the sub domains
			updateConfigurationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity, false);
		}
	}

	private void permissionPropagationForDelegationPolicy(T functionalityEntity) throws IllegalArgumentException, BusinessException {
		if(functionalityEntity.getConfigurationPolicy().getPolicy().equals(Policies.FORBIDDEN)) {
			// We have to update the delegation policy of each functionality from all the sub domains
			updateDelegationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity);
		} else if(functionalityEntity.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY)) {
			// We have to update the delegation policy of each functionality from all the sub domains
			updateDelegationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity);
		}
	}

	private class InnerFunctionality {

		private final T functionality;

		private final String identifier;

		public InnerFunctionality(T functionality) {
			super();
			this.functionality = functionality;
			this.identifier = functionality.getIdentifier();
		}

		public T getFunctionality() {
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
			@SuppressWarnings("unchecked")
			InnerFunctionality other = (InnerFunctionality) obj;
			if (identifier == null) {
				if (other.identifier != null)
					return false;
			} else if (!identifier.equals(other.identifier))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "InnerFunctionality : " + identifier + "(" + functionality.getDomain().getUuid() + ")";
		}

	}

	private AbstractDomain findDomain(String domain) throws BusinessException {
		AbstractDomain abstractDomain = abstractDomainRepository.findById(domain);
		if (abstractDomain == null) {
			throw new BusinessException(BusinessErrorCode.DOMAIN_DO_NOT_EXIST, "The input domain does not exist.");
		}
		return abstractDomain;
	}
}
