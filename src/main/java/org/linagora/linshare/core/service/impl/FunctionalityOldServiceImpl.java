/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.AbstractFunctionalityBusinessService;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.service.FunctionalityOldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityOldServiceImpl implements FunctionalityOldService {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityOldServiceImpl.class);
	private final FunctionalityRepository functionalityRepository;
	private final AbstractDomainRepository abstractDomainRepository;
	private final AbstractFunctionalityBusinessService functionalityBusinessService;

	private static int CST_FUNC_AVAILABLES = 0;
	private static int CST_FUNC_ALTERABLES = 1;
	private static int CST_FUNC_EDITABLES = 2;

	// Activation policy modification
	private static int CST_MODIFICATION_TYPE_AP = 1;
	//	Configuration policy modification
	private static int CST_MODIFICATION_TYPE_CP = 2;
	//	Parameters modification
	private static int CST_MODIFICATION_TYPE_P = 3;
	
	@Deprecated
	public FunctionalityOldServiceImpl(
			FunctionalityRepository functionalityRepository,
			AbstractFunctionalityBusinessService functionalityBusinessService,
			AbstractDomainRepository domainRepository) {
		super();
		this.functionalityRepository = functionalityRepository;
		this.abstractDomainRepository = domainRepository;
		this.functionalityBusinessService = functionalityBusinessService;
	}

	/**
	 * This method returns an identifier list from functionalities
	 * 
	 * @param set
	 *            : list of functionality entities
	 * @return
	 */
	@Deprecated
	private List<String> getFunctionalityIdentifiers(Set<Functionality> set) {
		List<String> allfunctionalityIdentifiers = new ArrayList<String>();

		for (Functionality functionality : set) {
			if (!allfunctionalityIdentifiers.contains(functionality.getIdentifier())) {
				allfunctionalityIdentifiers.add(functionality.getIdentifier());
			}
		}
		return allfunctionalityIdentifiers;
	}

	@Deprecated
	private void updateActivationPolicyRecursivly(AbstractDomain domain, Functionality functionality) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				
				Set<Functionality> functionalities = subDomain.getFunctionalities();
				for (Functionality f : functionalities) {
					if(f.getIdentifier().equals(functionality.getIdentifier())) {
						f.getActivationPolicy().updatePolicyFrom(functionality.getActivationPolicy());
						functionalityRepository.update(f);
						break;
					}
				}
				updateActivationPolicyRecursivly(subDomain, functionality);
			}
		}
	}
	
	@Deprecated
	private void updateConfigurationPolicyRecursivly(AbstractDomain domain, Functionality functionality,boolean copyContent) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				
				Set<Functionality> functionalities = subDomain.getFunctionalities();
				for (Functionality f : functionalities) {
					if(f.getIdentifier().equals(functionality.getIdentifier())) {
						f.getConfigurationPolicy().updatePolicyFrom(functionality.getConfigurationPolicy());
						if(copyContent) {
							f.updateFunctionalityValuesOnlyFrom(functionality);
						}
						functionalityRepository.update(f);
						break;
					}
				}
				updateConfigurationPolicyRecursivly(subDomain, functionality, copyContent);
			}
		}
	}
	
	@Deprecated
	private void deleteFunctionalityRecursivly(AbstractDomain domain, String functionalityIdentifier) throws IllegalArgumentException, BusinessException {
		if(domain != null ) {
			
			for (AbstractDomain subDomain : domain.getSubdomain()) {
				
				Set<Functionality> functionalities = subDomain.getFunctionalities();
				for (Functionality functionality : functionalities) {
					if(functionality.getIdentifier().equals(functionalityIdentifier)) {
						functionalityRepository.delete(functionality);
						functionalities.remove(functionality);
						break;
					}
				}
				deleteFunctionalityRecursivly(subDomain, functionalityIdentifier);
			}
		}
	}
	
	@Deprecated
	private void permissionPropagationForActivationPolicy(Functionality functionalityEntity) throws IllegalArgumentException, BusinessException {
		if(functionalityEntity.getActivationPolicy().getPolicy().equals(Policies.FORBIDDEN)) {
			// We have to delete the activation policy of each functionality from all the sub  domains
			deleteFunctionalityRecursivly(functionalityEntity.getDomain(), functionalityEntity.getIdentifier());
			
		} else if(functionalityEntity.getActivationPolicy().getPolicy().equals(Policies.MANDATORY)) {
			// TODO : We have to update the activation policy of each functionality from all the sub domains
			updateActivationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity);
		}
	}
	
	@Deprecated
	private void permissionPropagationForConfigurationPolicy(Functionality functionalityEntity) throws IllegalArgumentException, BusinessException {
		if(functionalityEntity.getConfigurationPolicy().getPolicy().equals(Policies.FORBIDDEN)) {
			// We have to update the configuration policy of each functionality from all the sub domains
			// The parameters of the current functionality are propagated to all sub functionalities
			updateConfigurationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity, true);
			
		} else if(functionalityEntity.getConfigurationPolicy().getPolicy().equals(Policies.MANDATORY)) {
			// We have to update the configuration policy of each functionality from all the sub domains
			updateConfigurationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity, false);
		}
	}
	
	/**
	 * this method is designed to check if we can or can not modify a
	 * functionality. You can modify one element at the same time : activation
	 * policy, configuration policy or the functionality parameters.
	 * 
	 * @param currentDomain
	 * @param functionalityDto : the DTO which carries the modifications 
	 * @param functionalityEntity : the actual entity
	 * @param ancestorFunc : to check all the permissions
	 * @throws BusinessException
	 */
	@Deprecated
	private void checkAndUpdate(AbstractDomain currentDomain, Functionality functionalityDto, Functionality functionalityEntity, Functionality ancestorFunc) throws BusinessException {
		
		int cptCheck = 0;
		int cptCheckOk = 0;
		int flag = 0;

		// We only can modify one element at the same time : activation policy, configuration policy or parameters.

		// check if the two activation policies are different, and then if we have the permissions for modifications
		if (!functionalityDto.getActivationPolicy().businessEquals(functionalityEntity.getActivationPolicy())) {
			cptCheck += 1;
			flag = CST_MODIFICATION_TYPE_AP;
			if (ancestorFunc.getActivationPolicy().isMutable()) {
				cptCheckOk += 1;
			} else {
				throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW, "You try to modify an activation policy object that you are not allowed to !");
			}
		}

		// check if the two configuration policies are different, and then if we have the permissions for modifications 
		if (!functionalityDto.getConfigurationPolicy().businessEquals(functionalityEntity.getConfigurationPolicy())) {
			cptCheck += 1;
			flag = CST_MODIFICATION_TYPE_CP;
			if (functionalityDto.getActivationPolicy().getStatus() && ancestorFunc.getConfigurationPolicy().isMutable()) {
				cptCheckOk += 1;
			} else {
				throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW, "You try to modify a configuration policy object that you are not allowed to !");
			}
		}

		// check if functionality content without policies are different, then if we have the permissions for modifications
		if (!functionalityDto.businessEquals(functionalityEntity, false)) {
			if (cptCheck != 0) {
				// TODO: Bug here. There a case where the exception is thrown but it should not be.
				throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW, "You try to modify multiple parameters at the same time !");
			}
			if (ancestorFunc.isSystem()) {
				throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW, "You try to modify a system functionality !");
			}
			cptCheck += 1;
			flag = CST_MODIFICATION_TYPE_P;
			if (functionalityDto.getActivationPolicy().getStatus() 	&& functionalityDto.getConfigurationPolicy().getStatus()) {
				cptCheckOk += 1;
			} else {
				throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW, "You try to modify functionality parameters that you are not allowed to!");
			}
		}

		if (cptCheck == 0) {
			throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_UPDATE_FAILED, "you should not reach this point ! All parameters are equals !");
		} else if (cptCheck == 2) {
			throw new TechnicalException(TechnicalErrorCode.FUNCTIONALITY_ENTITY_MODIFICATION_NOT_ALLOW, "You try to modify multiple parameters at the same time !");
		} else if (cptCheck == 1) {
			if (cptCheck == cptCheckOk) {
				// All checks are OK.

				if(flag == CST_MODIFICATION_TYPE_AP) {
					functionalityEntity.updateFunctionalityFrom(functionalityDto);
					functionalityRepository.update(functionalityEntity);
					
					// cascade modifications
					permissionPropagationForActivationPolicy(functionalityEntity);
					
				} else if(flag == CST_MODIFICATION_TYPE_CP) {
					functionalityEntity.updateFunctionalityFrom(functionalityDto);
					functionalityRepository.update(functionalityEntity);
					
					// cascade modifications
					permissionPropagationForConfigurationPolicy(functionalityEntity);
					
				} else if(flag == CST_MODIFICATION_TYPE_P) {
					functionalityEntity.updateFunctionalityFrom(functionalityDto);
					functionalityRepository.update(functionalityEntity);
					
				} else {
					logger.error("no flag for modification type detected.");
				}
				
			} else {
				throw new TechnicalException( TechnicalErrorCode.FUNCTIONALITY_ENTITY_UPDATE_FAILED, "At least one check failed, odd !");
			}
		}
	}

	@Deprecated
	@Override
	public void update(Account actor, String domainIdentifier, Functionality functionality) throws BusinessException {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		update(actor, domain, functionality);
	}

	private Functionality getFunctionalityEntityByIdentifiers(AbstractDomain domain, String functionalityIdentifier) {
		Functionality fonc = functionalityRepository.findById(domain, functionalityIdentifier);
		if (fonc == null && domain.getParentDomain() != null) {
			fonc = getFunctionalityEntityByIdentifiers(domain.getParentDomain(), functionalityIdentifier);
		}
		return fonc;
	}
	
	@Deprecated
	@Override
	public void update(Account actor, AbstractDomain currentDomain, Functionality functionalityDto) throws BusinessException {

		logger.debug("Begin update");
		
		Functionality functionalityEntity = getFunctionalityEntityByIdentifiers(currentDomain, functionalityDto.getIdentifier());
		
		// Consistency check, and fixes.
		functionalityDto.getActivationPolicy().applyConsistency();
		functionalityDto.getConfigurationPolicy().applyConsistency();

		// Check if this functionality belongs to the current domain
		// Don't use the functionalityDto for accessing to domains, it's a clone without domain 
		
		logger.debug("Check if this functionality belongs to the current domain");
		if (functionalityEntity.getDomain().getIdentifier().equals(currentDomain.getIdentifier())) {
			logger.debug("this functionality belongs to the current domain");

			if (!functionalityDto.businessEquals(functionalityEntity, true)) {
				logger.debug("the functionality is different from the entity");
				
				// We check if it has an identical ancestor.
				if (functionalityEntity.getDomain().getParentDomain() != null) {
					logger.debug("This is not the root domain, the domain must have a parent");
					
					Functionality ancestorFunctionality = getFunctionalityEntityByIdentifiers(functionalityEntity.getDomain().getParentDomain(), functionalityDto.getIdentifier());
					if (functionalityDto.businessEquals(ancestorFunctionality, true)) {
						// This functionality is identical to its ancestor, we does not need to persist an other entity
						logger.debug("This functionality is identical to its ancestor, we need not to persist an other entity");
						
						functionalityRepository.delete(functionalityEntity);
						Set<Functionality> functionalities = functionalityEntity.getDomain().getFunctionalities();
						functionalities.remove(functionalityEntity);
						
						logger.debug("functionalityRepository.delete(functionalityEntity)");
					} else {
						logger.debug("the functionality is different from its ancestor");
						// This functionality is different from the entity, and from its ancestor, it needs to be updated.
						checkAndUpdate(currentDomain, functionalityDto, functionalityEntity, ancestorFunctionality);
					}

				} else { // No ancestors, this is a root functionality
					// This functionality is different from the entity, it needs to be updated.
					logger.debug("update(functionalityEntity) from dto (no ancestors)");
					functionalityEntity.updateFunctionalityFrom(functionalityDto);
					functionalityRepository.update(functionalityEntity);
					permissionPropagationForActivationPolicy(functionalityEntity);
					permissionPropagationForConfigurationPolicy(functionalityEntity);
					
				}
			} else { // no differences
				logger.debug("functionality " + functionalityDto.getIdentifier()+ " was not modified.");
			}
		} else {
			logger.debug("this functionality does not belong to the current domain");
			// This functionality does not belong to the current domain.
			if (!functionalityDto.businessEquals(functionalityEntity, true)) {
				// This functionality is different, it needs to be persist.
				functionalityDto.setDomain(currentDomain);

				functionalityRepository.create(functionalityDto);
				logger.info("Update by creation of a new functionality for : " + functionalityDto.getIdentifier() + " link to domain : " + currentDomain.getIdentifier());
			} else { // no differences
				logger.debug("functionality " + functionalityDto.getIdentifier()+ " was not modified.");
			}
		}
		logger.debug("End update");
	}
	
	@Deprecated
	private boolean checkCriteriaForAncestor(int criteria, Functionality functionality) {
		
		if (criteria == CST_FUNC_AVAILABLES) {
			// I have to check if I have the permission
			// to modify the activation status of this
			// functionality
			if (functionality.getActivationPolicy().isMutable()) {
				return true;
			}
		} else if (criteria == CST_FUNC_ALTERABLES) {
			// I have to check if I have the permission
			// to modify the configuration status of
			// this functionality
			if (functionality.getActivationPolicy().getStatus()) {
				if (functionality.getConfigurationPolicy().isMutable()) {
					return true;
				}
			}
		} else if (criteria == CST_FUNC_EDITABLES) {
			// I have to check if I have the permission
			// to modify the parameters of this
			// functionality
			if (!functionality.isSystem()) {
				if (functionality.getActivationPolicy().getStatus()) {
					if (functionality.getConfigurationPolicy().getStatus()) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	@Deprecated
	private boolean checkCriteriaForMySelf(int criteria, Functionality functionality, List<Functionality> parentFunctionalitites ) {
		if (criteria == CST_FUNC_AVAILABLES) {
			// I have to check if I have the permission to modify the activation status of this functionality
			if (!functionality.getActivationPolicy().isSystem()) {
				for (Functionality ancestor : parentFunctionalitites) {
					if (ancestor.getIdentifier().equals(functionality.getIdentifier())) { // same functionality but different entities
						// return true if the ancestor allows modifications
						return (ancestor.getActivationPolicy().isMutable());
					}
				}
				// No ancestor found, modifications are allowed.
				return true;
			}
		} else if (criteria == CST_FUNC_ALTERABLES) {
			
			// I have to check if I have the permission to modify the configuration status of this functionality
			if (functionality.getActivationPolicy().getStatus()) {
				if (!functionality.getConfigurationPolicy().isSystem()) {
					for (Functionality ancestor : parentFunctionalitites) {
						if(ancestor.getIdentifier().equals(functionality.getIdentifier())) { // same functionality but different entities
							// return true if the ancestor allows modifications
							return (ancestor.getConfigurationPolicy().isMutable());
						}
					}
					// No ancestor found, modifications are allowed.
					return true;
				}
			}
		} else if (criteria == CST_FUNC_EDITABLES) {
			// I have to check if I have the permission
			// to modify the parameters of this
			// functionality
			if (!functionality.isSystem()) {
				if (functionality.getActivationPolicy().getStatus()) {
					for (Functionality ancestor : parentFunctionalitites) {
						if(ancestor.getIdentifier().equals(functionality.getIdentifier())) { // same functionality but different entities
							// return true if the ancestor allows modifications
							return (ancestor.getConfigurationPolicy().getStatus());
						}
					}
					// No ancestor found, modifications are allowed.
					return true;
				}
			}
		}
		return false;
	}
}
