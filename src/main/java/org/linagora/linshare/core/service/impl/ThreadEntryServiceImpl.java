package org.linagora.linshare.core.service.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.utils.DocumentUtils;

public class ThreadEntryServiceImpl implements ThreadEntryService {

	private final DocumentEntryBusinessService documentEntryBusinessService;
	private final LogEntryService logEntryService;
	private final AbstractDomainService abstractDomainService;
	private final FunctionalityService functionalityService;
	private final MimeTypeService mimeTypeService;
	private final AccountService accountService;
	private final VirusScannerService virusScannerService;
	
	
	
	public ThreadEntryServiceImpl(DocumentEntryBusinessService documentEntryBusinessService, LogEntryService logEntryService, AbstractDomainService abstractDomainService,
			FunctionalityService functionalityService, MimeTypeService mimeTypeService, AccountService accountService, VirusScannerService virusScannerService) {
		super();
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
		this.mimeTypeService = mimeTypeService;
		this.accountService = accountService;
		this.virusScannerService = virusScannerService;
	}
	
	


	@Override
	public ThreadEntry createThreadEntry(Account actor, Thread thread, InputStream stream, Long size, String fileName) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getIdentifier());
		
		BufferedInputStream bufStream = new BufferedInputStream(stream);
		String mimeType = documentEntryBusinessService.getMimeType(bufStream);
		
		DocumentUtils util = new DocumentUtils();
		File tempFile =  util.getFileFromBufferedInputStream(bufStream, fileName);
		
		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityService.getMimeTypeFunctionality(domain);
		if(mimeFunctionality.getActivationPolicy().getStatus()) {
			mimeTypeService.checkFileMimeType(fileName, mimeType, actor);
		}
		
		Functionality antivirusFunctionality = functionalityService.getAntivirusFunctionality(domain);
		if(antivirusFunctionality.getActivationPolicy().getStatus()) {
//			checkVirus(fileName, actor, stream);
		}

		// want a timestamp on doc ?
		String timeStampingUrl = null;
		StringValueFunctionality timeStampingFunctionality = functionalityService.getTimeStampingFunctionality(domain);
		if(timeStampingFunctionality.getActivationPolicy().getStatus()) {
			 timeStampingUrl = timeStampingFunctionality.getValue();
		}
				
				
		Functionality enciphermentFunctionality = functionalityService.getEnciphermentFunctionality(domain);
		Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
		
		ThreadEntry threadEntry = documentEntryBusinessService.createThreadEntry(thread, tempFile, size, fileName, checkIfIsCiphered, timeStampingUrl, mimeType);
	
		FileLogEntry logEntry = new FileLogEntry(actor, LogAction.FILE_UPLOAD, "Creation of a file in a thread", threadEntry.getName(), threadEntry.getDocument().getSize(), threadEntry.getDocument().getType());
		logEntryService.create(logEntry);

		tempFile.delete(); // remove the temporary file
		return threadEntry;
	}

	
	@Override
	public ThreadEntry findById(Account actor, Thread thread, String currentDocEntryUuid) throws BusinessException {
		ThreadEntry entry = documentEntryBusinessService.findThreadEntryById(currentDocEntryUuid);
		// TODO : check permissions
//		if (!entry.getEntryOwner().equals(actor)) {
//			throw new BusinessException(BusinessErrorCode.NOT_AUTHORIZED, "You are not authorized to get this document.");
//		}
		return entry;
	}

	
	@Override
	public void deleteThreadEntry(Account actor, Thread thread, String docEntryUuid) throws BusinessException {
		// TODO Auto-generated method stub

	}

}
