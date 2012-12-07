/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
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
