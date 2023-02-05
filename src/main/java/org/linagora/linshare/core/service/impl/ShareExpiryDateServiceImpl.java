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
package org.linagora.linshare.core.service.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareExpiryRule;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ShareExpiryDateService;

public class ShareExpiryDateServiceImpl implements ShareExpiryDateService {

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	public ShareExpiryDateServiceImpl(FunctionalityReadOnlyService functionalityService) {
		this.functionalityReadOnlyService = functionalityService;
	}


	/**
     * Compute the expiration date of a document share
     * @param document : the document to be shared
     * @param owner : owner of the document
     * @return the expiration date
     */
	@Override
	public Calendar computeShareExpiryDate(DocumentEntry document, Account owner) {
		TimeUnitValueFunctionality shareExpirationTimeFunctionality = functionalityReadOnlyService.getDefaultShareExpiryTimeFunctionality(owner.getDomain());
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
				if (document.getSize() < shareExpiryRule.getShareSizeUnit().getPlainSize(shareExpiryRule.getShareSize())) {
					Calendar expiration = GregorianCalendar.getInstance();
					expiration.add(shareExpiryRule.getShareExpiryUnit().toCalendarValue(), shareExpiryRule.getShareExpiryTime());
					return expiration;
				}
			}
		}
		// nothing has been decided
		return defaultExpiration;
	}

	@Override
	public Date computeMinShareExpiryDateOfList(
			Set<DocumentEntry> documents, Account owner) {
		Calendar expiryCalendar = null;
		int docNumber = documents.size();

		if (docNumber == 0) {
			expiryCalendar = Calendar.getInstance();
		} else {
			for (DocumentEntry documentEntry : documents) {
				if (expiryCalendar == null) {
					expiryCalendar = computeShareExpiryDate(documentEntry, owner);
				} else {
					Calendar tempExpiryCalendar = computeShareExpiryDate(
							documentEntry, owner);
					if (tempExpiryCalendar.before(expiryCalendar)) {
						expiryCalendar = tempExpiryCalendar;
					}
				}
			}
		}
		return expiryCalendar.getTime();
	}
}
