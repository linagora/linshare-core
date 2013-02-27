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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.ShareExpiryDateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareExpiryDateServiceImpl implements ShareExpiryDateService {
	private static final Logger logger = LoggerFactory.getLogger(ShareExpiryDateServiceImpl.class);
	private final FunctionalityService functionalityService;

	public ShareExpiryDateServiceImpl(FunctionalityService functionalityService) {
		this.functionalityService = functionalityService;
	}
	
	
	/**
     * Compute the expiration date of a document share
     * @param doc : the document to be shared
     * @return the expiration date
     */
	@Override
	public Calendar computeShareExpiryDate(DocumentEntry document, Account owner) {
		TimeUnitBooleanValueFunctionality shareExpirationTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(owner.getDomain());
		Calendar defaultExpiration = null;
    	
		if(shareExpirationTimeFunctionality.getActivationPolicy().getStatus()) {
				
			List<ShareExpiryRule> shareRules = owner.getDomain().getShareExpiryRules();
	
			// set the default exp time
			if (shareExpirationTimeFunctionality.getValue() != null) {
				defaultExpiration = GregorianCalendar.getInstance();
				defaultExpiration.add(shareExpirationTimeFunctionality.toCalendarValue(), shareExpirationTimeFunctionality.getValue());
			}
			if ((shareRules == null) || (shareRules.size() == 0)) {
				return defaultExpiration;
			}
	
			// luckily, the shareExpiryRules are ordered according to the size,
			// increasing
			for (ShareExpiryRule shareExpiryRule : shareRules) {
				if (document.getDocument().getSize() < shareExpiryRule.getShareSizeUnit().getPlainSize(shareExpiryRule.getShareSize())) {
					Calendar expiration = GregorianCalendar.getInstance();
					expiration.add(shareExpiryRule.getShareExpiryUnit().toCalendarValue(), shareExpiryRule.getShareExpiryTime());
					return expiration;
				}
			}
		}
		// nothing has been decided
		return defaultExpiration;
	}


	/**
     * Compute the minimal expiration date of a list of documents
     * @param doc : the list of documents
     * @return the minimal expiration date
     */
	@Override
	public Calendar computeMinShareExpiryDateOfList(List<DocumentEntry> documents, Account owner) {
		Calendar expiryCalDate;
		int docNumber = documents.size();
		
		if (docNumber == 0) {
			expiryCalDate = Calendar.getInstance();
		}
		else {
			expiryCalDate = computeShareExpiryDate(documents.get(0), owner);
			logger.debug("Expiration date n°0 : "+expiryCalDate.getTime());
			for (int i = 1; i < docNumber; i++) {
				Calendar expiryCalDateAutre = computeShareExpiryDate(documents.get(i), owner);
				logger.debug("Expiration date n°"+i+" : "+expiryCalDateAutre.getTime());
				if (expiryCalDateAutre.before(expiryCalDate)) expiryCalDate = expiryCalDateAutre;
			}
		}
		return expiryCalDate;
	}
}
