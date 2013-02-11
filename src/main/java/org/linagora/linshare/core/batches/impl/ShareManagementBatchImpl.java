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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.linagora.linshare.core.batches.ShareManagementBatch;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.objects.TimeUnitBooleanValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.AnonymousShareEntryRepository;
import org.linagora.linshare.core.repository.AnonymousUrlRepository;
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
    
    private final AnonymousUrlRepository anonymousUrlRepository;


	public ShareManagementBatchImpl(ShareEntryService shareEntryService, AnonymousShareEntryService anonymousShareEntryService, ShareEntryRepository shareEntryRepository,
			AnonymousShareEntryRepository anonymousShareEntryRepository, DocumentEntryRepository documentEntryRepository, AccountRepository<Account> accountRepository,
			FunctionalityService functionalityService, DocumentEntryService documentEntryService, AnonymousUrlRepository anonymousUrlRepository) {
		super();
		this.shareEntryService = shareEntryService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.shareEntryRepository = shareEntryRepository;
		this.anonymousShareEntryRepository = anonymousShareEntryRepository;
		this.documentEntryRepository = documentEntryRepository;
		this.accountRepository = accountRepository;
		this.functionalityService = functionalityService;
		this.documentEntryService = documentEntryService;
		this.anonymousUrlRepository = anonymousUrlRepository;
	}


	@Override
	public void cleanOutdatedShares() {
		logger.info("Begin clean outdated shares");
		removeAllExpiredShareEntries();
		removeAllExpiredAnonymousShareEntries();
		removeAllExpiredAnonymousUrl();
		logger.info("End clean outdated shares");
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
					
					DocumentEntry documentEntry = shareEntry.getDocumentEntry();
					boolean doDeleteDoc = documentSuppressionIsNeeded(documentEntry);
					
					anonymousShareEntryService.deleteShare(systemAccount, shareEntry);
					if(doDeleteDoc) {
						documentEntryService.deleteExpiredDocumentEntry(systemAccount, documentEntry);
					}
					
				} catch (BusinessException e) {
					logger.error("Can't delete expired anonymous share : " + shareEntry.getUuid()  + " : " + e.getMessage() );
					logger.debug(e.toString());
				}
			}
		}
	}


	private boolean documentSuppressionIsNeeded(DocumentEntry documentEntry) {
		boolean doDeleteDoc = false;
		AbstractDomain domain = documentEntry.getEntryOwner().getDomain();
		long sum = documentEntryRepository.getRelatedEntriesCount(documentEntry);
		TimeUnitBooleanValueFunctionality shareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(domain);

		if(shareExpiryTimeFunctionality.getActivationPolicy().getStatus()) {
			// we check if the current share is the last related entry to the document
			if(sum -1 <= 0) {
				// if this field is set, we must delete the document entry when the share entry is expired.
				if(shareExpiryTimeFunctionality.isBool()) {
					doDeleteDoc = true;
					logger.debug("current document " + documentEntry.getUuid() + " need to be deleted.");
				} else {
					
					TimeUnitValueFunctionality fileExpirationTimeFunctionality = functionalityService.getDefaultFileExpiryTimeFunctionality(domain);
					
					Calendar deletionDate = Calendar.getInstance();
					deletionDate.add(fileExpirationTimeFunctionality.toCalendarUnitValue(), fileExpirationTimeFunctionality.getValue());
					documentEntry.setExpirationDate(deletionDate);
						
					try {
						documentEntryRepository.update(documentEntry);
					} catch (IllegalArgumentException e) {
						logger.error("current document " + documentEntry.getUuid() + " can't not be updated." + e.getMessage());
						logger.debug("exception:" + e.toString());
					} catch (BusinessException e) {
						logger.error("current document " + documentEntry.getUuid() + " can't not be updated." + e.getMessage());
						logger.debug("exception:" + e.toString());
					}
				}
			}
		} else {
			logger.warn("Share expiration is not enable.");
		}
		return doDeleteDoc;
	}
	

	private void removeAllExpiredShareEntries() {
		SystemAccount systemAccount = accountRepository.getSystemAccount();
		
		List<ShareEntry> expiredEntries = shareEntryRepository.findAllExpiredEntries();
		logger.info(expiredEntries.size() + " expired share(s) found to be delete.");
		for (ShareEntry shareEntry : expiredEntries) {
			AbstractDomain domain = shareEntry.getEntryOwner().getDomain();
			
			TimeUnitBooleanValueFunctionality shareExpiryTimeFunctionality = functionalityService.getDefaultShareExpiryTimeFunctionality(domain);
			// test if this functionality is enable for the current domain.
			if(shareExpiryTimeFunctionality.getActivationPolicy().getStatus()) {
				try {
					DocumentEntry documentEntry = shareEntry.getDocumentEntry();
					boolean doDeleteDoc = documentSuppressionIsNeeded(documentEntry);
					
					shareEntryService.deleteShare(systemAccount, shareEntry);
					if(doDeleteDoc) {
						documentEntryService.deleteExpiredDocumentEntry(systemAccount, documentEntry);
					}
				} catch (BusinessException e) {
					logger.error("Can't delete expired share : " + shareEntry.getUuid()  + " : " + e.getMessage() );
					logger.debug(e.toString());
				}
			}
		}
	}
	
	
	private void removeAllExpiredAnonymousUrl() {
		List<AnonymousUrl> allExpiredUrl = anonymousUrlRepository.getAllExpiredUrl();
		logger.info(allExpiredUrl.size() + " expired anonymous url(s) found to be delete.");
		for (AnonymousUrl anonymousUrl : allExpiredUrl) {
			try {
				anonymousUrlRepository.delete(anonymousUrl);
			} catch (IllegalArgumentException e) {
				logger.error("Can't delete expired anonymous url : " + anonymousUrl.getUuid() + " : " + e.getMessage() );
				logger.debug(e.toString());
			} catch (BusinessException e) {
				logger.error("Can't delete expired anonymous url : " + anonymousUrl.getUuid() + " : " + e.getMessage() );
				logger.debug(e.toString());
			}
		}
	}
	
	
	
	@Override
    public void notifyUpcomingOutdatedShares() {
		
		SystemAccount systemAccount = accountRepository.getSystemAccount();
		
		StringValueFunctionality notificationBeforeExpirationFunctionality = functionalityService.getShareNotificationBeforeExpirationFunctionality(systemAccount.getDomain());
		
		List<Integer> datesForNotifyUpcomingOutdatedShares = new ArrayList<Integer>();
		
        String[] dates = notificationBeforeExpirationFunctionality.getValue().split(",");
        for (String date : dates) {
        	datesForNotifyUpcomingOutdatedShares.add(Integer.parseInt(date));
		}
		
        for (Integer day : datesForNotifyUpcomingOutdatedShares) {
        	
	        List<ShareEntry> shares = shareEntryRepository.findUpcomingExpiredEntries(day);
	        logger.info(shares.size() + " upcoming (in "+ day.toString()+" days) outdated share(s) found to be notified.");
	        for (ShareEntry share : shares) {
	        	if (share.getDownloaded() < 1) {
	        		shareEntryService.sendUpcomingOutdatedShareEntryNotification(systemAccount, share, day);
	        	}
	        }

	        List<AnonymousShareEntry> anonymousShareEntries = anonymousShareEntryRepository.findUpcomingExpiredEntries(day);
	        logger.info(anonymousShareEntries.size() + " upcoming (in "+day.toString()+" days) outdated anonymous share Url(s) found to be notified.");
	        
	        for (AnonymousShareEntry anonymousShareEntry : anonymousShareEntries) {
	        	if(anonymousShareEntry.getDownloaded() < 1) {
	        		anonymousShareEntryService.sendUpcomingOutdatedShareEntryNotification(systemAccount, anonymousShareEntry, day);
	        	}
			}
	        
        }
    }
}
