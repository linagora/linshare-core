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
package org.linagora.linshare.core.batches.impl;

import java.util.List;

import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.ShareEntryRepository;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class provides shares management methods.
 */
public class ShareManagementBatchImpl implements ShareManagementBatch {

	private static final Logger logger = LoggerFactory.getLogger(ShareManagementBatchImpl.class);
	
    private final ShareEntryService shareEntryService;
    
    private final AnonymousShareEntryService anonymousShareEntryService;
    
    private final ShareEntryRepository shareEntryRepository;
    
    private final AnonymousShareEntryRepository anonymousShareEntryRepository;
    
    private final DocumentEntryRepository documentEntryRepository;

    private final AccountRepository<Account> accountRepository;
    
    private final FunctionalityService functionalityService;
    
    private final DocumentEntryService documentEntryService;


	public ShareManagementBatchImpl(ShareEntryService shareEntryService, AnonymousShareEntryService anonymousShareEntryService, ShareEntryRepository shareEntryRepository,
			AnonymousShareEntryRepository anonymousShareEntryRepository, DocumentEntryRepository documentEntryRepository, AccountRepository<Account> accountRepository,
			FunctionalityService functionalityService, DocumentEntryService documentEntryService) {
		super();
		this.shareEntryService = shareEntryService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.shareEntryRepository = shareEntryRepository;
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.accountRepository = accountRepository;
		this.functionalityService = functionalityService;
		this.documentEntryService = documentEntryService;
	}

	public void cleanOutdatedShares() {
		logger.info("Begin clean outdated shares");
		removeAllExpiredShareEntries();
		removeAllExpiredAnonymousShareEntries();
		logger.info("End clean outdated shares");
    }
    
    public void notifyUpcomingOutdatedShares() {
    }


	private void removeAllExpiredAnonymousShareEntries() {
		SystemAccount systemAccount = accountRepository.getSystemAccount();
		
		List<AnonymousShareEntry> expiredEntries = anonymousShareEntryRepository.findAllExpiredEntries();
		logger.info(expiredEntries.size() + " expired anonymous share(s) found to be delete.");
		for (AnonymousShareEntry shareEntry : expiredEntries) {
			AbstractDomain domain = shareEntry.getEntryOwner().getDomain();
			
			TimeUnitBooleanValueFunctionality shareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(domain);
			// test if this functionality is enable for the current domain.
			if(shareExpiryTimeFunctionality.getActivationPolicy().getStatus()) {
				try {
					
					boolean doDeleteDoc = false; 
					DocumentEntry documentEntry = shareEntry.getDocumentEntry();
					
					// if this field is set, we must delete the document entry when the share entry is expired.
					if(shareExpiryTimeFunctionality.isBool()) {
						logger.debug("document removing with expired share is enable.");
						long sum = documentEntryRepository.getRelatedEntriesCount(shareEntry.getDocumentEntry());
						// we check if the current share is the last related entry to the document
						if(sum -1 <= 0) {
							doDeleteDoc = true;
							logger.debug("current document " + documentEntry.getUuid() + " need to be deleted.");
						}
					}
					
					anonymousShareEntryService.deleteShare(systemAccount, shareEntry);
					if(doDeleteDoc) {
						documentEntryService.deleteExpiredDocumentEntry(systemAccount, documentEntry);
					}
					
				} catch (BusinessException e) {
					logger.error("Can't delete expired anonymous share : " + shareEntry.getUuid());
					logger.debug(e.toString());
				}
			}
		}
	}
	

	private void removeAllExpiredShareEntries() {
		SystemAccount systemAccount = accountRepository.getSystemAccount();
		
		List<ShareEntry> expiredEntries = shareEntryRepository.findAllExpiredEntries();
		logger.info(expiredEntries.size() + " expired share(s) found to be delete.");
		for (ShareEntry shareEntry : expiredEntries) {
			AbstractDomain domain = shareEntry.getEntryOwner().getDomain();
			
			TimeUnitBooleanValueFunctionality shareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(domain);
			// test if this functionnality is enable for the current domain.
			if(shareExpiryTimeFunctionality.getActivationPolicy().getStatus()) {
				try {
					boolean doDeleteDoc = false; 
					DocumentEntry documentEntry = shareEntry.getDocumentEntry();
					
					// if this field is set, we must delete the document entry when the share entry is expired.
					if(shareExpiryTimeFunctionality.isBool()) {
						long sum = documentEntryRepository.getRelatedEntriesCount(shareEntry.getDocumentEntry());
						// we check if the current share is the last related entry to the document
						if(sum -1 <= 0) {
							doDeleteDoc = true;
							logger.debug("current document " + documentEntry.getUuid() + " need to be deleted.");
						}
					}
					
					shareEntryService.deleteShare(systemAccount, shareEntry);
					if(doDeleteDoc) {
						documentEntryService.deleteExpiredDocumentEntry(systemAccount, documentEntry);
					}
				} catch (BusinessException e) {
					logger.error("Can't delete expired share : " + shareEntry.getUuid());
					logger.debug(e.toString());
				}
			}
		}
	}
	
}
