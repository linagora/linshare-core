package org.linagora.linshare.core.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.linagora.linshare.core.business.service.AnonymousUrlBusinessService;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.AnonymousUrl;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.LinShareNotSuchElementException;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.AnonymousUrlService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.utils.ArchiveZipStream;
import org.linagora.linshare.view.tapestry.objects.FileStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnonymousUrlServiceImpl implements AnonymousUrlService {

	private final AnonymousUrlBusinessService anonymousUrlBusinessService;
	
	private final AnonymousShareEntryService anonymousShareEntryService;
	
	private final NotifierService notifierService;
    
    private final MailContentBuildingService mailElementsFactory;
	
	private static final Logger logger = LoggerFactory.getLogger(AnonymousUrlService.class);
	
	
	public AnonymousUrlServiceImpl(AnonymousUrlBusinessService anonymousUrlBusinessService, AnonymousShareEntryService anonymousShareEntryService, NotifierService notifierService,
			MailContentBuildingService mailElementsFactory) {
		super();
		this.anonymousUrlBusinessService = anonymousUrlBusinessService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.notifierService = notifierService;
		this.mailElementsFactory = mailElementsFactory;
	}


	@Override
	public boolean exists(String uuid, String urlPath) {
		try {
			AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
			if(anonymousUrl.getUrlPath().endsWith(urlPath)) {
				return true;
			}
			logger.error("the source path is different than the anonymous url path : " + urlPath + " : " + anonymousUrl.getUrlPath());
		} catch (LinShareNotSuchElementException e) {
			logger.warn("the anonymousUrl '" + uuid + "' does not exist.");
		}
		return false;
	}


	@Override
	public boolean isProtectedByPassword(String uuid) throws LinShareNotSuchElementException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
		return !StringUtils.isEmpty(anonymousUrl.getPassword());
	}


	@Override
	public boolean isValid(String uuid, String password) throws LinShareNotSuchElementException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
		return isValid(anonymousUrl, password);
	}


	private boolean isValid(AnonymousUrl anonymousUrl, String password) {
		if(!anonymousUrlBusinessService.isExpired(anonymousUrl)) {
			if(password != null) {
				return anonymousUrlBusinessService.isValidPassword(anonymousUrl, password);
			} else {
				return true;
			}
		}
		return false;
	}


	@Override
	public List<AnonymousShareEntry> getAnonymousShareEntry(String uuid, String password)  throws LinShareNotSuchElementException{
		List<AnonymousShareEntry> res = new ArrayList<AnonymousShareEntry>();
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(uuid);
		if(isValid(anonymousUrl, password)) {
			res.addAll(anonymousUrl.getAnonymousShareEntries());				
		}
		return res;
	}


	@Override
	public InputStream retrieveFileStream(String anonymousUrlUuid, String anonymousShareEntryUuid, String password, MailContainer mailContainer) throws BusinessException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(anonymousUrlUuid);
		if(isValid(anonymousUrl, password)) {
			// anonymous share are not made with a thousand contacts, performance will not be poor most of the time.
			for (AnonymousShareEntry anonymousShareEntry : anonymousUrl.getAnonymousShareEntries()) {
				if(anonymousShareEntry.getUuid().equals(anonymousShareEntryUuid)) {
					return anonymousShareEntryService.getAnonymousShareEntryStream(anonymousShareEntry.getUuid(), mailContainer);
				}
			}
			String msg = "anonymousShareEntryUuid not found : " + anonymousShareEntryUuid;
			logger.debug(msg);
			throw new LinShareNotSuchElementException(msg);
		}
		String msg = "anonymousUrlUuid not valid : " + anonymousUrlUuid;
		logger.debug(msg);
		throw new LinShareNotSuchElementException(msg);
	}


	@Override
	public FileStreamResponse retrieveArchiveZipStream(String anonymousUrlUuid, String password, MailContainer mailContainer) throws BusinessException {
		AnonymousUrl anonymousUrl = anonymousUrlBusinessService.getAnonymousUrl(anonymousUrlUuid);
		if(isValid(anonymousUrl, password)) {
			
			List<String> docNames = new ArrayList<String>();
			Map<String,InputStream> map = new HashMap<String, InputStream>();
			
			for (AnonymousShareEntry anonymousShareEntry : anonymousUrl.getAnonymousShareEntries()) {
				map.put(anonymousShareEntry.getName(), anonymousShareEntryService.getAnonymousShareEntryStream(anonymousShareEntry.getUuid(), mailContainer));
				// FIXME : just send one mail for all files, not one by by file.
//				map.put(anonymousShareEntry.getName(), anonymousShareEntryService.getAnonymousShareEntryStream(anonymousShareEntry.getUuid()));
//				docNames.add(anonymousShareEntry.getName());
			}
			
			//prepare an archive zip
			ArchiveZipStream ai = new ArchiveZipStream(map);
			
//			//send a notification by mail to the owner
//			String email = shareEntry.getContact().getMail();
//			User owner = (User)shareEntry.getEntryOwner();
//			notifierService.sendAllNotifications(mailElementsFactory.buildMailAnonymousDownloadWithOneRecipient(owner, mailContainer, docNames, email, owner));
			
			return (new FileStreamResponse(ai,null));
		}
		String msg = "anonymousUrlUuid not valid : " + anonymousUrlUuid;
		logger.debug(msg);
		throw new LinShareNotSuchElementException(msg);
	}
	
}
