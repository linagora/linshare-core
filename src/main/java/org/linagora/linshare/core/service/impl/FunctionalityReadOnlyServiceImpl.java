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

import java.util.Calendar;

import org.linagora.linshare.core.business.service.DomainBusinessService;
import org.linagora.linshare.core.domain.constants.FunctionalityNames;
import org.linagora.linshare.core.domain.constants.Policies;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.LanguageEnumValueFunctionality;
import org.linagora.linshare.core.domain.entities.Policy;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.UnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.FunctionalityRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FunctionalityReadOnlyServiceImpl implements
		FunctionalityReadOnlyService {

	protected final Logger logger = LoggerFactory.getLogger(FunctionalityReadOnlyServiceImpl.class);

	private final DomainBusinessService domainBusinessService;

	private final FunctionalityRepository functionalityRepository;

	public FunctionalityReadOnlyServiceImpl(
			DomainBusinessService domainBusinessService,
			FunctionalityRepository functionalityRepository) {
		super();
		this.domainBusinessService = domainBusinessService;
		this.functionalityRepository = functionalityRepository;
	}


	private AbstractDomain getRootDomain() throws BusinessException {
		return domainBusinessService.getUniqueRootDomain();
	}

	@Override
	public Functionality get(String domainIdentifier, String functionalityIdentifier) throws BusinessException {
		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
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
		Functionality fonc = functionalityRepository.findByDomain(domain, functionalityIdentifier);
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
	public TimeUnitValueFunctionality getDefaultShareExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_EXPIRATION));
	}

	@Override
	public BooleanValueFunctionality getDefaultShareExpiryTimeDeletionFunctionality(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_EXPIRATION__DELETE_FILE_ON_EXPIRATION);
	}

	@Override
	public TimeUnitValueFunctionality getDefaultFileExpiryTimeFunctionality(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.DOCUMENT_EXPIRATION));
	}

	@Override
	public Calendar getDefaultFileExpiryTime(AbstractDomain domain) {
		Calendar expirationDate = Calendar.getInstance();
		TimeUnitValueFunctionality fileExpirationTimeFunctionality = getDefaultFileExpiryTimeFunctionality(domain);
		expirationDate.add(fileExpirationTimeFunctionality.toCalendarValue(), fileExpirationTimeFunctionality.getValue());
		return expirationDate;
	}

	@Override
	public Functionality getGuests(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS);
	}

	@Override
	public BooleanValueFunctionality getGuestsRestricted(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.GUESTS__RESTRICTED);
	}

	@Override
	public BooleanValueFunctionality getGuestsCanUpload(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.GUESTS__CAN_UPLOAD);
	}

	@Override
	public TimeUnitValueFunctionality getGuestsExpiration(AbstractDomain domain) {
		return new TimeUnitValueFunctionality((UnitValueFunctionality)_getFunctionality(domain, FunctionalityNames.GUESTS__EXPIRATION));
	}

	@Override
	public Functionality getGuestsExpirationDateProlongation(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS__EXPIRATION_ALLOW_PROLONGATION);
	}

	@Override
	public StringValueFunctionality getTimeStampingFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.TIME_STAMPING);
	}

	@Override
	public StringValueFunctionality getDomainMailFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.DOMAIN__MAIL);
	}

	@Override
	public Functionality getMimeTypeFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.MIME_TYPE);
	}

	@Override
	public Functionality getEnciphermentFunctionality(AbstractDomain domain) {
		Functionality functionality = _getFunctionality(domain, FunctionalityNames.ENCIPHERMENT);
		functionality = getForbiddenFunctionnality(functionality);
		return functionality;
	}

	@Override
	public Functionality getAntivirusFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.ANTIVIRUS);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrl(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrlNotification(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL__NOTIFICATION);
	}

	@Override
	public StringValueFunctionality getAnonymousURLNotificationUrl(AbstractDomain domain) {
		return (StringValueFunctionality)_getFunctionality(domain, FunctionalityNames.ANONYMOUS_URL__NOTIFICATION_URL);
	}

	@Override
	public BooleanValueFunctionality getAcknowledgement(AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.SHARE_CREATION_ACKNOWLEDGEMENT_FOR_OWNER);
	}

	@Override
	public BooleanValueFunctionality getUndownloadedSharedDocumentsAlert(
			AbstractDomain domain) {
		return (BooleanValueFunctionality)_getFunctionality(domain, FunctionalityNames.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT);
	}

	@Override
	public IntegerValueFunctionality getUndownloadedSharedDocumentsAlertDuration(AbstractDomain domain) {
		return (IntegerValueFunctionality) _getFunctionality(domain, FunctionalityNames.UNDOWNLOADED_SHARED_DOCUMENTS_ALERT__DURATION);
	}

	@Override
	public BooleanValueFunctionality getAnonymousUrl(String domainIdentifier) {
		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
		return getAnonymousUrl(domain);
	}

	@Override
	public Functionality getRestrictedGuestFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.GUESTS__RESTRICTED);
	}

	@Override
	public Functionality getUserCanUploadFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.INTERNAL_CAN_UPLOAD);
	}

	@Override
	public IntegerValueFunctionality getCompletionFunctionality(AbstractDomain domain) {
		return (IntegerValueFunctionality) _getFunctionality(domain, FunctionalityNames.COMPLETION);
	}

	@Override
	public Functionality getWorkGroupCreationRight(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.WORK_GROUP__CREATION_RIGHT);
	}

	@Override
	public Functionality getWorkGroupFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.WORK_GROUP);
	}

	@Override
	public Functionality getContactsListFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.CONTACTS_LIST);
	}

	@Override
	public Functionality getContactsListCreationFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.CONTACTS_LIST__CREATION_RIGHT);
	}

	@Override
	public StringValueFunctionality getShareNotificationBeforeExpirationFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.SHARE_NOTIFICATION_BEFORE_EXPIRATION);
	}

	@Override
	public StringValueFunctionality getCustomNotificationUrlFunctionality(AbstractDomain domain) {
		return (StringValueFunctionality) _getFunctionality(domain, FunctionalityNames.DOMAIN__NOTIFICATION_URL);
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
	public LanguageEnumValueFunctionality getUploadRequestNotificationLanguageFunctionality(
			AbstractDomain domain) {
		return (LanguageEnumValueFunctionality)_getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST__NOTIFICATION_LANGUAGE);
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
	public Functionality getUploadRequestEnableTemplateFunctionality(
			AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.UPLOAD_REQUEST_ENABLE_TEMPLATE);
	}

	@Override
	public Functionality getUploadPropositionFunctionality(AbstractDomain domain) {
		return _getFunctionality(domain, FunctionalityNames.UPLOAD_PROPOSITION);
	}

	@Override
	public Functionality getCmisFunctionality(AbstractDomain domain) {
		Functionality functionality = _getFunctionality(domain, FunctionalityNames.CMIS);
		functionality = getForbiddenFunctionnality(functionality);
		return functionality;
	}

	@Override
	public Functionality getCmisDocumentsFunctionality(AbstractDomain domain) {
		Functionality functionality = _getFunctionality(domain, FunctionalityNames.CMIS);
		functionality = getForbiddenFunctionnality(functionality);
		return functionality;
	}

	@Override
	public Functionality getCmisThreadsFunctionality(AbstractDomain domain) {
		Functionality functionality = _getFunctionality(domain, FunctionalityNames.CMIS);
		functionality = getForbiddenFunctionnality(functionality);
		return functionality;
	}

	private Functionality getForbiddenFunctionnality(Functionality functionality) {
		if (functionality == null) {
			Policy activation = new Policy(Policies.FORBIDDEN, false, true);
			functionality = new Functionality();
			functionality.setIdentifier(FunctionalityNames.CMIS.toString());
			functionality.setActivationPolicy(activation);
			functionality.setConfigurationPolicy(activation);
		}
		return functionality;
	}

	@Override
	public String getCustomNotificationURLInRootDomain() throws BusinessException {
		return this.getCustomNotificationUrlFunctionality(getRootDomain()).getValue();
	}

	@Override
	public BooleanValueFunctionality getAcknowledgement(String domainIdentifier) {
		AbstractDomain domain = domainBusinessService.findById(domainIdentifier);
		return getAcknowledgement(domain);
	}
}
