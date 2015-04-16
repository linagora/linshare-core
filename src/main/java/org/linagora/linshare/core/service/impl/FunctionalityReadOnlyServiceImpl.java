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
package org.linagora.linshare.core.service.impl;

import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityReadOnlyServiceImpl implements
		FunctionalityReadOnlyService {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityReadOnlyServiceImpl.class);

	private final AbstractDomainRepository abstractDomainRepository;

	private final FunctionalityRepository functionalityRepository;

	public FunctionalityReadOnlyServiceImpl(
			AbstractDomainRepository abstractDomainRepository,
			FunctionalityRepository functionalityRepository) {
		super();
		this.abstractDomainRepository = abstractDomainRepository;
		this.functionalityRepository = functionalityRepository;
	}

	@Override
	public Functionality get(String domainIdentifier, String functionalityIdentifier) {
		AbstractDomain domain = abstractDomainRepository.findById(domainIdentifier);
		Functionality functionality = _getFunctionality(domain, functionalityIdentifier);
		// Always return a read only functionality.
		return (Functionality)functionality.clone();
	}

	/**
	 * This method should not be used except by the FunctionalityReadOnlyServiceImpl.get method.
	 * @param domain
	 * @param functionalityIdentifier
	 * @return
	 */
	private  Functionality _getFunctionality(AbstractDomain domain, String functionalityIdentifier) {
		Functionality fonc = functionalityRepository.findById(domain, functionalityIdentifier);
		if (fonc == null && domain.getParentDomain() != null) {
			fonc = _getFunctionality(domain.getParentDomain(), functionalityIdentifier);
		}
		return fonc;
	}

	/*
	 * Adapter
	 */
	private  Functionality _getFunctionality(AbstractDomain domain, FunctionalityNames fn) {
		return _getFunctionality(domain, fn.toString());
	}

	@Override
	public TimeUnitBooleanValueFunctionality getDefaultShareExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitBooleanValueFunctionality((UnitBooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_EXPIRATION));
	}

	@Override
	public TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.FILE_EXPIRATION));
	}

	@Override
	public SizeUnitValueFunctionality getGlobalQuotaFunctionality(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.QUOTA_GLOBAL));
	}

	@Override
	public SizeUnitValueFunctionality getUserQuotaFunctionality(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.QUOTA_USER));
	}

	@Override
	public Functionality getGuestFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS);
	}

	@Override
	public TimeUnitValueFunctionality getGuestAccountExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.ACCOUNT_EXPIRATION));
	}

	@Override
	public StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.TIME_STAMPING);
	}

	@Override
	public StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.DOMAIN_MAIL);
	}

	@Override
	public Functionality getMimeTypeFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.MIME_TYPE);
	}

	@Override
	public Functionality getEnciphermentFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.ENCIPHERMENT);
	}

	@Override
	public Functionality getAntivirusFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.ANTIVIRUS);
	}

	@Override
	public Functionality getAnonymousUrlFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL);
	}

	@Override
	public Functionality getSecuredAnonymousUrlFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.SECURED_ANONYMOUS_URL);
	}

	@Override
	public Functionality getRestrictedGuestFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.RESTRICTED_GUEST);
	}

	@Override
	public SizeUnitValueFunctionality getUserMaxFileSizeFunctionality(AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.FILESIZE_MAX));
	}

	@Override
	public Functionality getSignatureFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.SIGNATURE);
	}

	@Override
	public StringValueFunctionality getCustomLogoFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.CUSTOM_LOGO);
	}

	@Override
	public StringValueFunctionality getCustomLinkLogoFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.LINK_LOGO);
	}

	@Override
	public Functionality getUserCanUploadFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.USER_CAN_UPLOAD);
	}

	@Override
	public IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain) {
		return (IntegerValueFunctionality) _getFunctionality(domain, FunctionalityNames.COMPLETION);
	}

	@Override
	public Functionality getUserTabFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.TAB_USER);
	}

	@Override
	public Functionality getThreadCreationPermissionFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.CREATE_THREAD_PERMISSION);
	}

	@Override
	public Functionality getUpdateFilesFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.UPDATE_FILE);
	}

	@Override
	public Functionality getAuditTabFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.TAB_AUDIT);
	}


	@Override
	public Functionality getThreadTabFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.TAB_THREAD);
	}

	@Override
	public Functionality getHelpTabFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.TAB_HELP);
	}

	@Override
	public Functionality getListTabFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.TAB_LIST);
	}

	@Override
	public StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.SHARE_NOTIFICATION_BEFORE_EXPIRATION);
	}

	@Override
	public StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.NOTIFICATION_URL);
	}

	@Override
	public StringValueFunctionality getUploadRequestFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST);
	}

	@Override
	public TimeUnitValueFunctionality getUploadRequestActivationTimeFunctionality(
			AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_ACTIVATION));
	}

	@Override
	public TimeUnitValueFunctionality getUploadRequestExpiryTimeFunctionality(
			AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_EXPIRATION));
	}

	@Override
	public BooleanValueFunctionality getUploadRequestGroupedFunctionality(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__GROUPED_MODE);
	}

	@Override
	public IntegerValueFunctionality getUploadRequestMaxFileCountFunctionality(
			AbstractDomain domain) {
		return (IntegerValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_FILE_COUNT);
	}

	@Override
	public SizeUnitValueFunctionality getUploadRequestMaxFileSizeFunctionality(
			AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_FILE_SIZE));
	}

	@Override
	public SizeUnitValueFunctionality getUploadRequestMaxDepositSizeFunctionality(
			AbstractDomain domain) {
		return new SizeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__MAXIMUM_DEPOSIT_SIZE));
	}

	@Override
	public StringValueFunctionality getUploadRequestNotificationLanguageFunctionality(
			AbstractDomain domain) {
		return (StringValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__NOTIFICATION_LANGUAGE);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestSecureUrlFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__SECURED_URL);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestCanCloseFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__CAN_CLOSE);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestProlongationFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__PROLONGATION);
	}

	@Override
	public BooleanValueFunctionality getUploadRequestCandDeleteFileFunctionality(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__CAN_DELETE);
	}

	@Override
	public TimeUnitValueFunctionality getUploadRequestNotificationTimeFunctionality(
			AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality) _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__DELAY_BEFORE_NOTIFICATION));
	}

	@Override
	public Functionality getUploadPropositionFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.UPLOAD_PROPOSITION);
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
