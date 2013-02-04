package org.linagora.linshare.core.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.Role;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.utils.DocumentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentEntryServiceImpl implements DocumentEntryService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryServiceImpl.class);
	
	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final LogEntryService logEntryService;
	private final AbstractDomainService abstractDomainService;
	private final FunctionalityService functionalityService;
	private final MimeTypeService mimeTypeService;
	private final VirusScannerService virusScannerService;
	
	
	
	public DocumentEntryServiceImpl(DocumentEntryBusinessService documentEntryBusinessService, LogEntryService logEntryService, AbstractDomainService abstractDomainService,
			FunctionalityService functionalityService, MimeTypeService mimeTypeService, VirusScannerService virusScannerService) {
		super();
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
	}


	@Override
	public DocumentEntry createDocumentEntry(Account actor, InputStream stream, Long size, String fileName) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());
		
		BufferedInputStream bufStream = new BufferedInputStream(stream);
		String mimeType = documentEntryBusinessService.getMimeType(bufStream);
		checkSpace(size, fileName, actor);
		
		DocumentUtils util = new DocumentUtils();
		
		/*
		 * XXX : (#416) not efficient, neither in term of space nor time
		 * 		- getFileFromBufferefInputStream close bufStream and stream
		 * 		- use the tempFile to scan for virus
		 * 		getFileFromBufferedInputStream souldn't close streams implicitly?
		 */
		File tempFile =  util.getFileFromBufferedInputStream(bufStream, fileName);

		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityService.getMimeTypeFunctionality(domain);
		if(mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(fileName, mimeType, actor);
		}
		
		Functionality antivirusFunctionality = functionalityService.getAntivirusFunctionality(domain);
		if(antivirusFunctionality.getActivationPolicy().getStatus()) {
			checkVirus(fileName, actor, tempFile);
		}

		// want a timestamp on doc ?
		String timeStampingUrl = null;
		StringValueFunctionality timeStampingFunctionality = functionalityService.getTimeStampingFunctionality(domain);
		if(timeStampingFunctionality.getActivationPolicy().getStatus()) {
			 timeStampingUrl = timeStampingFunctionality.getValue();
		}
				
				
		Functionality enciphermentFunctionality = functionalityService.getEnciphermentFunctionality(domain);
		Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
		
		// We need to set an expiration date in case of file cleaner activation.
		DocumentEntry docEntry = documentEntryBusinessService.createDocumentEntry(actor, tempFile, size, fileName, checkIfIsCiphered, timeStampingUrl, mimeType, getDocumentExpirationDate(domain));
	
		FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_UPLOAD, "Creation of a file", docEntry.getName(), docEntry.getDocument().getSize(), docEntry.getDocument().getType());
		logEntryService.create(logEntry);
		
		addDocSizeToGlobalUsedQuota(docEntry.getDocument(), domain);

		tempFile.delete(); // remove the temporary file
		return docEntry;
	}
	
	@Override
	public DocumentEntry updateDocumentEntry(Account actor, String docEntryUuid, InputStream stream, Long size, String fileName) throws BusinessException {
		DocumentEntry originalEntry = documentEntryBusinessService.findById(docEntryUuid);
		if (!originalEntry.getEntryOwner().equals(actor)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update this document.");
		}
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());
		String originalFileName = originalEntry.getName();
		long oldDocSize = originalEntry.getDocument().getSize();
		BufferedInputStream bufStream = new BufferedInputStream(stream);
		String mimeType = documentEntryBusinessService.getMimeType(bufStream);
		checkSpace(size, fileName, actor);
		
		DocumentUtils util = new DocumentUtils();
		File tempFile =  util.getFileFromBufferedInputStream(bufStream, fileName);
		
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityService.getMimeTypeFunctionality(domain);
		if(mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(fileName, mimeType, actor);
		}
		
		Functionality antivirusFunctionality = functionalityService.getAntivirusFunctionality(domain);
		if(antivirusFunctionality.getActivationPolicy().getStatus()) {
			checkVirus(fileName, actor, tempFile);
		}

		// want a timestamp on doc ?
		String timeStampingUrl = null;
		StringValueFunctionality timeStampingFunctionality = functionalityService.getTimeStampingFunctionality(domain);
		if(timeStampingFunctionality.getActivationPolicy().getStatus()) {
			 timeStampingUrl = timeStampingFunctionality.getValue();
		}
		
		Functionality enciphermentFunctionality = functionalityService.getEnciphermentFunctionality(domain);
		Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
		
		
		// We need to set an expiration date in case of file cleaner activation.
		DocumentEntry documentEntry = documentEntryBusinessService.updateDocumentEntry(actor, originalEntry, tempFile, size, fileName, checkIfIsCiphered, timeStampingUrl, mimeType, getDocumentExpirationDate(domain));
		
		//put new file name in log
		//if the file is updated/replaced with a new file (new file name)
		//put new file name in log
		String logText = originalFileName;
		if (!logText.equalsIgnoreCase(documentEntry.getName())) {
			logText = documentEntry.getName() + " [" +logText+"]";
		}	
		
		FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_UPDATE, "Update of a file", logText, documentEntry.getDocument().getSize(), documentEntry.getDocument().getType());
		logEntryService.create(logEntry);
		
		removeDocSizeFromGlobalUsedQuota(oldDocSize, domain);
		addDocSizeToGlobalUsedQuota(documentEntry.getDocument(), domain);

		tempFile.delete(); // remove the temporary file
		return documentEntry;
	}

	private Calendar getDocumentExpirationDate(AbstractDomain domain) {
		Calendar expirationDate = Calendar.getInstance();
		TimeUnitValueFunctionality fileExpirationTimeFunctionality = functionalityService.getDefaultFileExpiryTimeFunctionality(domain);
		expirationDate.add(fileExpirationTimeFunctionality.toCalendarUnitValue(), fileExpirationTimeFunctionality.getValue());
		return expirationDate;
	}

	@Override
	public DocumentEntry duplicateDocumentEntry(Account actor, String docEntryUuid) throws BusinessException {
		DocumentEntry documentEntry = documentEntryBusinessService.findById(docEntryUuid);
		// TODO : Check the current doc entry id is shared with the actor (if not, you should not have the right to duplicate it)
//		if (!documentEntry.getEntryOwner().equals(actor)) {
//			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update this document.");
//		}
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());
		
		checkSpace(documentEntry.getDocument().getSize(), documentEntry.getName(), actor);

		// want a timestamp on doc ?
		String timeStampingUrl = null;
		StringValueFunctionality timeStampingFunctionality = functionalityService.getTimeStampingFunctionality(domain);
		if(timeStampingFunctionality.getActivationPolicy().getStatus()) {
			 timeStampingUrl = timeStampingFunctionality.getValue();
		}
		
		// We need to set an expiration date in case of file cleaner activation.
		documentEntryBusinessService.duplicateDocumentEntry(documentEntry, actor, timeStampingUrl, getDocumentExpirationDate(domain));
		
		FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_UPLOAD, "Creation of a file", documentEntry.getName(), documentEntry.getDocument().getSize(), documentEntry.getDocument().getType());
		logEntryService.create(logEntry);
		
		addDocSizeToGlobalUsedQuota(documentEntry.getDocument(), domain);

		return documentEntry;
	}
	
	@Override
	public void deleteInconsistentDocumentEntry(SystemAccount actor, DocumentEntry documentEntry) throws BusinessException {
		Account owner = documentEntry.getEntryOwner();
		try {
			
			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) > 0) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this document. It still exists shares.");
			}
			
			AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomain().getIdentifier());
			removeDocSizeFromGlobalUsedQuota(documentEntry.getDocument().getSize(), domain);
			
			FileLogEntry logEntry  = new FileLogEntry(owner, LogAction.FILE_INCONSISTENCY, "File removed because of inconsistence. Please contact your administrator.",  documentEntry.getName(), documentEntry.getDocument().getSize(), documentEntry.getDocument().getType());
			logEntryService.create(LogEntryService.WARN, logEntry);
			documentEntryBusinessService.deleteDocumentEntry(documentEntry);
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete file " + documentEntry.getName() + " of user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public void deleteExpiredDocumentEntry(SystemAccount actor, DocumentEntry documentEntry) throws BusinessException {
		Account owner = documentEntry.getEntryOwner();
		try {

			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) > 0) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this document. It still exists shares.");
			}
			
			AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomain().getIdentifier());
			removeDocSizeFromGlobalUsedQuota(documentEntry.getDocument().getSize(), domain);
			
			FileLogEntry logEntry  = new FileLogEntry(actor, LogAction.FILE_EXPIRE, "Expiration of a file",  documentEntry.getName(), documentEntry.getDocument().getSize(), documentEntry.getDocument().getType());
			logEntryService.create(LogEntryService.INFO, logEntry);
			documentEntryBusinessService.deleteDocumentEntry(documentEntry);
			
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete file " + documentEntry.getName() + " of user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public void deleteDocumentEntry(Account actor, DocumentEntry documentEntry) throws BusinessException {
		try {
			if (!isOwnerOrAdmin(actor, documentEntry.getEntryOwner())) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this document.");
			}
			
			if (documentEntryBusinessService.getRelatedEntriesCount(documentEntry) > 0) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to delete this document. It still exists shares.");
			}
			
			AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());
			removeDocSizeFromGlobalUsedQuota(documentEntry.getDocument().getSize(), domain);
			
			FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_DELETE, "Deletion of a file", documentEntry.getName(), documentEntry.getDocument().getSize(), documentEntry.getDocument().getType());
			logEntryService.create(LogEntryService.INFO, logEntry);
			documentEntryBusinessService.deleteDocumentEntry(documentEntry);
			
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete file " + documentEntry.getName()
					+ " of user " + actor.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT, "Could not delete document");
		}
	}

	@Override
	public long getUserMaxFileSize(Account account) throws BusinessException {
		
		//if user is not in one domain = BOUM
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(account.getDomain().getIdentifier());
		
		SizeUnitValueFunctionality userMaxFileSizeFunctionality = functionalityService.getUserMaxFileSizeFunctionality(domain);
		
		
		if(userMaxFileSizeFunctionality.getActivationPolicy().getStatus()) {
			
			long maxSize = userMaxFileSizeFunctionality.getPlainSize();					
			if (maxSize < 0) {
				maxSize = 0;
			}
			return maxSize;
		}
		return LinShareConstants.defaultMaxFileSize;
	}
	
	@Override
	public long getAvailableSize(Account account) throws BusinessException {
		
		//if user is not in one domain = BOUM
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(account.getDomain().getIdentifier());
		
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityService.getGlobalQuotaFunctionality(domain);
		SizeUnitValueFunctionality userQuotaFunctionality = functionalityService.getUserQuotaFunctionality(domain);
		
		
		if(globalQuotaFunctionality.getActivationPolicy().getStatus()) {
		
			long availableSize = globalQuotaFunctionality.getPlainSize() - domain.getUsedSpace().longValue();					
			if (availableSize < 0) {
				availableSize = 0;
			}
			return availableSize;
			
		} else if(userQuotaFunctionality.getActivationPolicy().getStatus()) {
				
			long userQuota = userQuotaFunctionality.getPlainSize();
			
			Set<Entry> entries = account.getEntries();
			for (Entry entry : entries) {
				if(entry.getEntryType().equals(EntryType.DOCUMENT)) {
					userQuota -= ((DocumentEntry)entry).getSize();
				}
			}
			
			return userQuota;
		}
		return LinShareConstants.defaultFreeSpace;
	}

	@Override
	public long getTotalSize(Account account) throws BusinessException {
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(account.getDomain().getIdentifier());
		
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityService.getGlobalQuotaFunctionality(domain);
		SizeUnitValueFunctionality userQuotaFunctionality = functionalityService.getUserQuotaFunctionality(domain);
		
		if(globalQuotaFunctionality.getActivationPolicy().getStatus()) {
			return globalQuotaFunctionality.getPlainSize();
		}
		
		long userQuota = userQuotaFunctionality.getPlainSize();

		return userQuota;

	}
	
	@Override
	public boolean documentHasThumbnail(Account owner, String docEntryUuid) {
		DocumentEntry documentEntry = documentEntryBusinessService.findById(docEntryUuid);
		if(documentEntry == null) {
			logger.error("Can't find document entry, are you sure it is not a share ? : " + docEntryUuid);
		} else if (documentEntry.getEntryOwner().equals(owner)) {
			String thmbUUID = documentEntry.getDocument().getThmbUuid();
			return (thmbUUID!=null && thmbUUID.length()>0);
		}
		return false;
	}

	@Override
	public InputStream getDocumentThumbnailStream(Account owner, String docEntryUuid) throws BusinessException {
		DocumentEntry documentEntry = documentEntryBusinessService.findById(docEntryUuid);
		if(documentEntry == null) {
			logger.error("Can't find document entry, are you sure it is not a share ? : " + docEntryUuid);
			return null;
		} else if (!documentEntry.getEntryOwner().equals(owner)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get thumbnail for this document.");
		} else {
			return documentEntryBusinessService.getDocumentThumbnailStream(documentEntry);
		}
	}
	
	@Override
	public InputStream getDocumentStream(Account owner, String docEntryUuid) throws BusinessException {
		DocumentEntry documentEntry = documentEntryBusinessService.findById(docEntryUuid);
		if(documentEntry == null) {
			logger.error("Can't find document entry, are you sure it is not a share ? : " + docEntryUuid);
			return null;
		} else if (!documentEntry.getEntryOwner().equals(owner)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get this document.");
		} else {
			return documentEntryBusinessService.getDocumentStream(documentEntry);
		}
	}

	@Override
	public boolean isSignatureActive(Account account) {
		return functionalityService.getSignatureFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isEnciphermentActive(Account account) {
		return functionalityService.getEnciphermentFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isGlobalQuotaActive(Account account) throws BusinessException {
		return functionalityService.getGlobalQuotaFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isUserQuotaActive(Account account) throws BusinessException {
		return functionalityService.getUserQuotaFunctionality(account.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public Long getGlobalQuota(Account account) throws BusinessException {
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityService.getGlobalQuotaFunctionality(account.getDomain());
		return globalQuotaFunctionality.getPlainSize();
	}
	
	@Override
	public DocumentEntry findById(Account actor, String currentDocEntryUuid) throws BusinessException {
		DocumentEntry entry = documentEntryBusinessService.findById(currentDocEntryUuid);
		if (entry == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,"Can not find document entry with uuid : " + currentDocEntryUuid);
					
		}
		if (!isOwnerOrAdmin(actor, entry.getEntryOwner())) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get this document. current actor is : " + actor.getAccountReprentation());
		}
		return entry;
	}

	@Override
	public List<DocumentEntry> findAllMyDocumentEntries(Account actor, User owner) throws BusinessException {
		if (!isOwnerOrAdmin(actor, owner)) {
			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get these documents.");
		}
		List<DocumentEntry> entry = documentEntryBusinessService.findAllMyDocumentEntries(owner);
		return entry;
	}

	@Override
	public void renameDocumentEntry(Account actor, String docEntryUuid, String newName) throws BusinessException {
		DocumentEntry entry = documentEntryBusinessService.findById(docEntryUuid);
		if (!actor.isSuperAdmin() && !actor.isTechnicalAccount()) {
			if (!entry.getEntryOwner().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to rename this document.");
			}
		}
		documentEntryBusinessService.renameDocumentEntry(entry, newName);
	}

	@Override
	public void updateFileProperties(Account actor, String docEntryUuid, String newName, String fileComment) throws BusinessException {
		DocumentEntry entry = documentEntryBusinessService.findById(docEntryUuid);
		if (!actor.isSuperAdmin() && !actor.isTechnicalAccount()) {
			if (!entry.getEntryOwner().equals(actor)) {
				throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to update this document.");
			}
		}
		documentEntryBusinessService.updateFileProperties(entry, newName, fileComment);
	}

	private void checkSpace(long size, String fileName, Account owner) throws BusinessException {
		// check the user quota
		if (getAvailableSize(owner) < size) {
			logger.info("The file  " + fileName + " is too large to fit in " + owner.getLsUuid() + " user's space");
            String[] extras = {fileName};
			throw new BusinessException(BusinessErrorCode.FILE_TOO_LARGE, "The file is too large to fit in user's space", extras);
		}
	}
	
	private Boolean checkVirus(String fileName, Account owner, File file) throws BusinessException {
		if (logger.isDebugEnabled()) {
			logger.debug("antivirus activation:" + !virusScannerService.isDisabled());
		}

		boolean checkStatus = false;
		try {
			checkStatus = virusScannerService.check(file);
		} catch (TechnicalException e) {
			LogEntry logEntry = new AntivirusLogEntry(owner, LogAction.ANTIVIRUS_SCAN_FAILED, e.getMessage());
			logger.error("File scan failed: antivirus enabled but not available ?");
			logEntryService.create(LogEntryService.ERROR,logEntry);
			throw new BusinessException(BusinessErrorCode.FILE_SCAN_FAILED, "File scan failed", e);
		}
		// check if the file contains virus
		if (!checkStatus) {
			LogEntry logEntry = new AntivirusLogEntry(owner, LogAction.FILE_WITH_VIRUS, fileName);
			logEntryService.create(LogEntryService.WARN,logEntry);
			logger.warn(owner.getLsUuid() + " tried to upload a file containing virus:" + fileName);
		    String[] extras = {fileName};
			throw new BusinessException(BusinessErrorCode.FILE_CONTAINS_VIRUS, "File contains virus", extras);
		}
		return checkStatus;
	}
	
	private void addDocSizeToGlobalUsedQuota(Document docEntity, AbstractDomain domain) throws BusinessException {
		long newUsedQuota = domain.getUsedSpace().longValue() + docEntity.getSize();
		domain.setUsedSpace(newUsedQuota);
		abstractDomainService.updateDomain(domain);
	}
	
	private void removeDocSizeFromGlobalUsedQuota(long docSize, AbstractDomain domain)  throws BusinessException {
		long newUsedQuota = domain.getUsedSpace().longValue() - docSize;
		domain.setUsedSpace(newUsedQuota);
		abstractDomainService.updateDomain(domain);
	}
	
	// FIXME : code duplication
	private boolean isOwnerOrAdmin(Account actor, Account user) {
		if (actor.equals(user)) {
			return true;
		} else if (actor.getRole().equals(Role.SUPERADMIN)) {
			return true;
		} else if (actor.getRole().equals(Role.SYSTEM)) {
			return true;
		} else if (actor.getRole().equals(Role.ADMIN)) {
			List<String> allMyDomain = abstractDomainService.getAllMyDomainIdentifiers(actor.getDomain().getIdentifier());
			for (String domain : allMyDomain) {
				if(domain.equals(user.getDomainId())) {
					return true;
				}
			}
		}
		if (user instanceof Guest) {
			// At this point the actor object could be an entity or a proxy. No idea why it happens. 
			// That is why we compare IDs.
			if (actor.getId() == ((Guest)user).getOwner().getId()) {
				return true;
			}
		}
		return false;
	}
	
}
