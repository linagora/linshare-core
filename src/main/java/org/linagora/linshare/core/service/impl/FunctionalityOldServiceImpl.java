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
package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
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

	private static int CST_FUNC_AVAILABLES = 0;
	private static int CST_FUNC_ALTERABLES = 1;
	private static int CST_FUNC_EDITABLES = 2;

	// Activation policy modification
	private static int CST_MODIFICATION_TYPE_AP = 1;
	//	Configuration policy modification
	private static int CST_MODIFICATION_TYPE_CP = 2;
	//	Parameters modification
	private static int CST_MODIFICATION_TYPE_P = 3;
	
	
	public FunctionalityOldServiceImpl(
			FunctionalityRepository functionalityRepository,
			AbstractDomainRepository domainRepository) {
		super();
		this.functionalityRepository = functionalityRepository;
		this.abstractDomainRepository = domainRepository;
	}

	@Override
	public Functionality findById(long id) {
		Functionality func = functionalityRepository.findById(id);
		if (func == null) {
			throw new TechnicalException(
					TechnicalErrorCode.FUNCTIONALITY_ENTITY_NOT_FOUND,
					"error with entity Functionality, it does not exists !!");
		}
		return func;
	}

	/**
	 * This method returns an identifier list from functionalities
	 * 
	 * @param set
	 *            : list of functionality entities
	 * @return
	 */
	private List<String> getFunctionalityIdentifiers(Set<Functionality> set) {
		List<String> allfunctionalityIdentifiers = new ArrayList<String>();

		for (Functionality functionality : set) {
			if (!allfunctionalityIdentifiers.contains(functionality.getIdentifier())) {
				allfunctionalityIdentifiers.add(functionality.getIdentifier());
			}
		}
		return allfunctionalityIdentifiers;
	}

	/**
	 * This method return a list of functionality including all its
	 * functionalities and its parent's functionalities
	 * 
	 * @param domain
	 *            entity
	 * @return functionality list
	 */
	@Override
	public List<Functionality> getAllFunctionalities(AbstractDomain domain) {

		if (domain != null) {
			// Add all functionalities from this domain
			List<Functionality> allfunctionalities = new ArrayList<Functionality>(domain.getFunctionalities());
			// Add all functionality identifiers from this domain
			List<String> allfunctionalityIdentifiers = getFunctionalityIdentifiers(domain.getFunctionalities());

			if (domain.getParentDomain() != null) {
				List<Functionality> parentFunctionalitites = getAllFunctionalities(domain.getParentDomain());
				for (Functionality functionality : parentFunctionalitites) {
					if (!allfunctionalityIdentifiers.contains(functionality.getIdentifier())) {
						allfunctionalities.add(functionality);
						allfunctionalityIdentifiers.add(functionality.getIdentifier());
					}
				}
			}
			return allfunctionalities;
		}
		return null;
	}

	/**
	 * This method return a list of functionality including all its
	 * functionalities and its parent's functionalities
	 * 
	 * @param domainIdentifier
	 *            domain entity identifier
	 * @return functionality list
	 */
	@Override
	public List<Functionality> getAllFunctionalities(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		return getAllFunctionalities(domain);
	}

	/**
	 * This method return the functionality by its IDENTIFIER
	 * 
	 * @param domainIdentifier
	 *            domain entity identifier
	 * @return functionality
	 */
	@Override
	public Functionality getFunctionalityByIdentifiers(String domainIdentifier, String functionalityIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality functionality = getFunctionalityEntityByIdentifiers(domain, functionalityIdentifier);
		// Never returns the entity when we try to modify the functionality.
		return (Functionality)functionality.clone();
	}

	private  Functionality getFunctionalityEntityByIdentifiers(AbstractDomain domain, String functionalityIdentifier) {
		Functionality fonc = functionalityRepository.findById(domain, functionalityIdentifier);
		if (fonc == null && domain.getParentDomain() != null) {
			fonc = getFunctionalityEntityByIdentifiers(domain.getParentDomain(), functionalityIdentifier);
		}
		return fonc;
	}

	
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
	
	private void permissionPropagationForActivationPolicy(Functionality functionalityEntity) throws IllegalArgumentException, BusinessException {
		if(functionalityEntity.getActivationPolicy().getPolicy().equals(Policies.FORBIDDEN)) {
			// We have to delete the activation policy of each functionality from all the sub  domains
			deleteFunctionalityRecursivly(functionalityEntity.getDomain(), functionalityEntity.getIdentifier());
			
		} else if(functionalityEntity.getActivationPolicy().getPolicy().equals(Policies.MANDATORY)) {
			// TODO : We have to update the activation policy of each functionality from all the sub domains
			updateActivationPolicyRecursivly(functionalityEntity.getDomain(), functionalityEntity);
		}
	}
	
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

	@Override
	public void update(String domainIdentifier, Functionality functionality) throws BusinessException {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		update(domain, functionality);
	}

	@Override
	public void update(AbstractDomain currentDomain, Functionality functionalityDto) throws BusinessException {

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
	
	private List<Functionality> getAllFunctionality(AbstractDomain domain, int criteria) {
		if (domain != null) {
			
			List<Functionality> allFunctionalities = new ArrayList<Functionality>();
			List<Functionality> parentFunctionalitites = new ArrayList<Functionality>();

			// Add all my functionality identifiers
			List<String> allFunctionalityIdentifiers = getFunctionalityIdentifiers(domain.getFunctionalities());

			if (domain.getParentDomain() != null) {
				parentFunctionalitites = getAllFunctionalities(domain.getParentDomain());
				for (Functionality functionality : parentFunctionalitites) {
					if (!allFunctionalityIdentifiers.contains(functionality.getIdentifier())) {
						// this functionality is not in the allfunctionalityIdentifiers list
						allFunctionalityIdentifiers.add(functionality.getIdentifier());

						if (functionality.getDomain().getPersistenceId() != domain.getPersistenceId()) {
							if(checkCriteriaForAncestor(criteria, functionality)) {
								allFunctionalities.add(functionality);
							}
						}
					}
				}
			}
			
			// Add all my functionalities
			for (Functionality functionality : domain.getFunctionalities()) {
				if(checkCriteriaForMySelf(criteria, functionality, parentFunctionalitites)) {
					allFunctionalities.add(functionality);
				}
			}
			
			
			return allFunctionalities;
		}
		return null;
	}

	@Override
	public List<Functionality> getAllAvailableFunctionalities(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		return getAllAvailableFunctionalities(domain);
	}

	@Override
	public List<Functionality> getAllAvailableFunctionalities(AbstractDomain domain) {
		return getAllFunctionality(domain, CST_FUNC_AVAILABLES);
	}

	@Override
	public List<Functionality> getAllAlterableFunctionalities(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		return getAllAlterableFunctionalities(domain);
	}

	@Override
	public List<Functionality> getAllAlterableFunctionalities(AbstractDomain domain) {
		return getAllFunctionality(domain, CST_FUNC_ALTERABLES);
	}

	@Override
	public List<Functionality> getAllEditableFunctionalities(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		return getAllEditableFunctionalities(domain);
	}

	@Override
	public List<Functionality> getAllEditableFunctionalities(AbstractDomain domain) {
		return getAllFunctionality(domain, CST_FUNC_EDITABLES);
	}

	@Override
	public TimeUnitBooleanValueFunctionality getDefaultShareExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitBooleanValueFunctionality((UnitBooleanValueFunctionality)getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.SHARE_EXPIRATION));
	}

	@Override
	public TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.FILE_EXPIRATION));
	}

	@Override
	public SizeUnitValueFunctionality getGlobalQuotaFunctionality(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.QUOTA_GLOBAL));
	}

	@Override
	public SizeUnitValueFunctionality getUserQuotaFunctionality(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.QUOTA_USER));
	}

	@Override
	public Functionality getGuestFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.GUESTS);
	}

	@Override
	public TimeUnitValueFunctionality getGuestAccountExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.ACCOUNT_EXPIRATION));
	}

	@Override
	public StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.TIME_STAMPING);
	}
	
	@Override
	public StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.DOMAIN_MAIL);
	}

	@Override
	public Functionality getMimeTypeFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.MIME_TYPE);
	}

	@Override
	public Functionality getEnciphermentFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.ENCIPHERMENT);
	}

	@Override
	public Functionality getAntivirusFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.ANTIVIRUS);
	}

	@Override
	public Functionality getAnonymousUrlFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.ANONYMOUS_URL);
	}

	@Override
	public Functionality getSecuredAnonymousUrlFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.SECURED_ANONYMOUS_URL);
	}

	@Override
	public Functionality getRestrictedGuestFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.RESTRICTED_GUEST);
	}

	@Override
	public SizeUnitValueFunctionality getUserMaxFileSizeFunctionality(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.FILESIZE_MAX));
	}

	@Override
	public Functionality getSignatureFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.SIGNATURE);
	}

	@Override
	public StringValueFunctionality getCustomLogoFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.CUSTOM_LOGO);
	}

	@Override
	public StringValueFunctionality getCustomLinkLogoFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.LINK_LOGO);
	}
	
	@Override
	public Functionality getUserCanUploadFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.USER_CAN_UPLOAD);
	}

	@Override
	public IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain) {
		return (IntegerValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.COMPLETION);
	}

	@Override
	public Functionality getUserTabFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.TAB_USER);
	}
	
	@Override
	public Functionality getThreadCreationPermissionFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.CREATE_THREAD_PERMISSION);
	}

	@Override
	public Functionality getUpdateFilesFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.UPDATE_FILE);
	}
	
	@Override
	public Functionality getAuditTabFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.TAB_AUDIT);
	}

	
	@Override
	public Functionality getThreadTabFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.TAB_THREAD);
	}

	@Override
	public Functionality getHelpTabFunctionality(AbstractDomain domain) {
		return getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.TAB_HELP);
	}
	
	
	@Override
	public StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.SHARE_NOTIFICATION_BEFORE_EXPIRATION);
	}
	
	@Override
	public StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) getFunctionalityEntityByIdentifiers(domain, FunctionalityNames.NOTIFICATION_URL);
	}

	@Override
	public boolean isSauAllowed(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality funcAU = getAnonymousUrlFunctionality(domain);
		// We check if Anonymous Url are activated.
		if(funcAU.getActivationPolicy().getStatus()) {
			Functionality funcSAU = getSecuredAnonymousUrlFunctionality(domain);
			return funcSAU.getActivationPolicy().getPolicy().equals(Policies.ALLOWED);
		}
		return false;
	}
	
	@Override
	public boolean isSauMadatory(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality func = getSecuredAnonymousUrlFunctionality(domain);
		return func.getActivationPolicy().getPolicy().equals(Policies.MANDATORY);
	}
	
	@Override
	public boolean getDefaultSauValue(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality func = getSecuredAnonymousUrlFunctionality(domain);
		return func.getActivationPolicy().getStatus();
	}
	
	
	@Override
	public boolean getDefaultRestrictedGuestValue(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality func = getRestrictedGuestFunctionality(domain);
		return func.getActivationPolicy().getStatus();
	}
	
	@Override
	public boolean isRestrictedGuestAllowed(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality funcRG = getRestrictedGuestFunctionality(domain);
		return funcRG.getActivationPolicy().getPolicy().equals(Policies.ALLOWED);
	}
	
	@Override
	public boolean isRestrictedGuestMadatory(String domainIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality func = getRestrictedGuestFunctionality(domain);
		return func.getActivationPolicy().getPolicy().equals(Policies.MANDATORY);
	}
	
	@Override
	public boolean isCustomLogoActiveInRootDomain() throws BusinessException {
		return this.getCustomLogoFunctionality(abstractDomainRepository.getUniqueRootDomain()).getActivationPolicy().getStatus();
	}
	
	@Override
	public String getCustomLogoUrlInRootDomain() throws BusinessException {
		return this.getCustomLogoFunctionality(abstractDomainRepository.getUniqueRootDomain()).getValue();
	}
	
	@Override
	public boolean isCustomLinkLogoActiveInRootDomain() throws BusinessException {
		return this.getCustomLinkLogoFunctionality(abstractDomainRepository.getUniqueRootDomain()).getActivationPolicy().getStatus();
	}
	
	@Override
	public String getCustomLinkLogoInRootDomain() throws BusinessException {
		return this.getCustomLinkLogoFunctionality(abstractDomainRepository.getUniqueRootDomain()).getValue();
	}
	
	@Override
	public String getCustomNotificationURLInRootDomain() throws BusinessException {
		return this.getCustomNotificationUrlFunctionality(abstractDomainRepository.getUniqueRootDomain()).getValue();
	}
}
