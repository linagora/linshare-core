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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.OperationHistoryBusinessService;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.dao.MimeTypeMagicNumberDao;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.LogActionCause;
import org.linagora.linshare.core.domain.constants.OperationHistoryTypeEnum;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.UploadRequestStatus;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.OperationHistory;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.Thumbnail;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestGroup;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.CopyResource;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.notifications.context.EmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestDeleteFileByOwnerEmailContext;
import org.linagora.linshare.core.notifications.context.UploadRequestUnavailableSpaceEmailContext;
import org.linagora.linshare.core.notifications.service.MailBuildingService;
import org.linagora.linshare.core.rac.UploadRequestEntryRessourceAccessControl;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.QuotaService;
import org.linagora.linshare.core.service.UploadRequestEntryService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.utils.FileAndMetaData;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.DocumentEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.UploadRequestEntryAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.entities.mto.CopyMto;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

public class UploadRequestEntryServiceImpl extends GenericEntryServiceImpl<Account, UploadRequestEntry>
		implements UploadRequestEntryService {

	private final UploadRequestEntryBusinessService uploadRequestEntryBusinessService;

	private final UploadRequestBusinessService uploadRequestBusinessService;

	private final OperationHistoryBusinessService operationHistoryBusinessService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final MimeTypeService mimeTypeService;

	private final VirusScannerService virusScannerService;

	private final MimeTypeMagicNumberDao mimeTypeIdentifier;

	private final QuotaService quotaService;

	private final MailBuildingService mailBuildingService;

	private final NotifierService notifierService;

	protected final DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository;

	private DocumentEntryBusinessService documentEntryBusinessService;

	private LogEntryService logEntryService;

	private final DocumentRepository documentRepository;

	private final FileDataStore fileDataStore;

	private final static String ARCHIVE_MIME_TYPE = "application/zip";
	private final static String ARCHIVE_EXTENTION = ".zip";

	public UploadRequestEntryServiceImpl(
			UploadRequestEntryBusinessService uploadRequestEntryBusinessService,
			AbstractDomainService abstractDomainService,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			MimeTypeService mimeTypeService,
			VirusScannerService virusScannerService,
			MimeTypeMagicNumberDao mimeTypeIdentifier,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			UploadRequestEntryRessourceAccessControl rac,
			OperationHistoryBusinessService operationHistoryBusinessService,
			QuotaService quotaService,
			DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository,
			MailBuildingService mailBuildingService,
			NotifierService notifierService,
			DocumentEntryBusinessService documentEntryBusinessService,
			LogEntryService logEntryService,
			UploadRequestBusinessService uploadRequestBusinessService,
			DocumentRepository documentRepository,
			FileDataStore fileDataStore) {
		super(rac, sanitizerInputHtmlBusinessService);
		this.uploadRequestEntryBusinessService = uploadRequestEntryBusinessService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.mimeTypeService = mimeTypeService;
		this.virusScannerService = virusScannerService;
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.operationHistoryBusinessService = operationHistoryBusinessService;
		this.quotaService = quotaService;
		this.documentGarbageCollectorRepository = documentGarbageCollectorRepository;
		this.mailBuildingService = mailBuildingService;
		this.notifierService = notifierService;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
		this.uploadRequestBusinessService = uploadRequestBusinessService;
		this.documentRepository = documentRepository;
		this.fileDataStore = fileDataStore;
	}

	@Override
	public UploadRequestEntry create(Account authUser, Account actor, File tempFile, String fileName, String comment,
			boolean isFromCmis, String metadata, UploadRequestUrl uploadRequestUrl) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(fileName, "fileName is required.");
		UploadRequestEntry upReqEntry = null;
		try {
			fileName = sanitizeFileName(fileName);
			Long size = tempFile.length();
			try {
				checkSpace(actor, size);
			} catch (BusinessException e) {
				// if no space available notify the owner
				List<BusinessErrorCode> errCodes = Arrays.asList(BusinessErrorCode.QUOTA_FILE_FORBIDDEN_FILE_SIZE,
						BusinessErrorCode.QUOTA_ACCOUNT_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE,
						BusinessErrorCode.QUOTA_GLOBAL_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE,
						BusinessErrorCode.QUOTA_CONTAINER_FORBIDDEN_NO_MORE_SPACE_AVALAIBLE);
				if (errCodes.contains(e.getErrorCode())) {
					UploadRequestUnavailableSpaceEmailContext context = new UploadRequestUnavailableSpaceEmailContext(
							(User) uploadRequestUrl.getUploadRequest().getUploadRequestGroup().getOwner(),
							uploadRequestUrl.getUploadRequest(), uploadRequestUrl);
					notifierService.sendNotification(mailBuildingService.build(context));
				}
				throw e;
			}
			// detect file's mime type.
			String mimeType = mimeTypeIdentifier.getMimeType(tempFile);
			// check if the file MimeType is allowed
			if (mimeTypeFilteringStatus(actor)) {
				mimeTypeService.checkFileMimeType(actor, fileName, mimeType);
			}
			virusScannerService.checkVirus(fileName, actor, tempFile, size);
			// want a timestamp on doc ?
			String timeStampingUrl = null;
			StringValueFunctionality timeStampingFunctionality = functionalityReadOnlyService
					.getTimeStampingFunctionality(actor.getDomain());
			if (timeStampingFunctionality.getActivationPolicy().getStatus()) {
				timeStampingUrl = timeStampingFunctionality.getValue();
			}
			Functionality enciphermentFunctionality = functionalityReadOnlyService
					.getEnciphermentFunctionality(actor.getDomain());
			Boolean checkIfIsCiphered = enciphermentFunctionality.getActivationPolicy().getStatus();
			upReqEntry = uploadRequestEntryBusinessService.createUploadRequestEntryDocument(actor, tempFile, size,
					fileName, comment, checkIfIsCiphered, timeStampingUrl, mimeType, null, isFromCmis, metadata,
					uploadRequestUrl);
			createBusinessCheck(upReqEntry);
			addToQuota(actor, size);
		} finally {
			try {
				logger.debug("deleting temp file : " + tempFile.getName());
				if (tempFile.exists()) {
					tempFile.delete(); // remove the temporary file
				}
			} catch (Exception e) {
				logger.error("can not delete temp file : " + e.getMessage(), e);
			}
		}
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(authUser), new AccountMto(actor),
				LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY, upReqEntry.getUuid(), upReqEntry);
		logEntryService.insert(log);
		return upReqEntry;
	}

	private void createBusinessCheck(UploadRequestEntry entry) throws BusinessException {
		UploadRequest request = entry.getUploadRequestUrl().getUploadRequest();
		Integer numberOfUploadedFiles = uploadRequestBusinessService.countNbrUploadedFiles(request);
		Long totalUploadedEntriesSize = uploadRequestBusinessService.computeEntriesSize(request);
		UploadRequestUrl requestUrl = entry.getUploadRequestUrl();
		if (!request.getStatus().equals(UploadRequestStatus.ENABLED)) {
			throw new BusinessException(
					BusinessErrorCode.UPLOAD_REQUEST_READONLY_MODE,
					"The current upload request url is in read only mode : "
							+ requestUrl.getUuid());
		}
		if (request.getMaxFileSize() != null) {
			if (entry.getSize() > request.getMaxFileSize()) {
				throw new BusinessException(
						BusinessErrorCode.UPLOAD_REQUEST_FILE_TOO_LARGE,
						"You already have reached the uploaded file limit.");
			}
		}
		if (request.getMaxFileCount() != null) {
			// already reach the limit
			if (numberOfUploadedFiles > request.getMaxFileCount()) {
				String errMsg = String.format("You already have reached the uploaded file limit: %1$s",
						request.getMaxFileCount());
				throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_TOO_MANY_FILES, errMsg);
			}
		}
		if (request.getMaxDepositSize() != null) {
			if (totalUploadedEntriesSize > request.getMaxDepositSize()) {
				String errMsg = String.format("You already have reached the max deposit size limit : %1$s",
						request.getMaxDepositSize());
				throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_TOTAL_DEPOSIT_SIZE_TOO_LARGE, errMsg);
			}
		}
	}

	@Override
	public boolean mimeTypeFilteringStatus(Account actor) throws BusinessException {
		AbstractDomain domain = abstractDomainService.retrieveDomain(actor.getDomain().getUuid());
		Functionality mimeFunctionality = functionalityReadOnlyService.getMimeTypeFunctionality(domain);
		return mimeFunctionality.getActivationPolicy().getStatus();
	}

	@Override
	public DocumentEntry copy(Account authUser, Account owner, CopyResource cr) throws BusinessException {
		preChecks(authUser, owner);
		Validate.notEmpty(cr.getDocumentUuid(), "documentUuid is required.");
		Validate.notEmpty(cr.getName(), "fileName is required.");
		Validate.notNull(cr.getSize(), "size is required.");
		checkCreatePermission(authUser, owner, DocumentEntry.class,
				BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, null);
		UploadRequestEntry uploadRequestEntry = find(authUser, owner, cr.getResourceUuid());
		UploadRequest uploadRequest = uploadRequestEntry.getUploadRequestUrl().getUploadRequest();
		checkURStatusBeforeCopyAndDelete(uploadRequest, BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_BE_COPIED);
		checkSpace(owner, cr.getSize());
		if (uploadRequestEntry.getComment() == null) {
			cr.setComment("");
		}
		cr.setComment(sanitizeFileName(uploadRequestEntry.getComment()));
		cr.setName(sanitizeFileName(uploadRequestEntry.getName()));
		DocumentEntry documentEntry = documentEntryBusinessService.copy(owner, cr.getDocumentUuid(), cr.getName(),
				cr.getComment(), cr.getMetaData(), getDocumentExpirationDate(owner.getDomain()), cr.getCiphered());
		addToQuota(owner, cr.getSize());
		DocumentEntryAuditLogEntry log = new DocumentEntryAuditLogEntry(authUser, owner, documentEntry, LogAction.CREATE);
		log.setCause(LogActionCause.COPY);
		log.setFromResourceUuid(cr.getResourceUuid());
		log.setCopiedFrom(cr.getCopyFrom());
		logEntryService.insert(log);
		if (!uploadRequestEntry.getCopied()) {
			uploadRequestEntry.setCopied(true);
		}
		UploadRequestEntryAuditLogEntry logUREntry = new UploadRequestEntryAuditLogEntry(new AccountMto(authUser),
				new AccountMto(owner), LogAction.CREATE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY,
				uploadRequestEntry.getUuid(), uploadRequestEntry);
		logUREntry.setCause(LogActionCause.COPY);
		logUREntry.setCopiedTo(new CopyMto(documentEntry));
		logEntryService.insert(logUREntry);
		return documentEntry;
	}

	private void checkURStatusBeforeCopyAndDelete(UploadRequest uploadRequest, BusinessErrorCode error) {
		if (!Lists.newArrayList(UploadRequestStatus.ENABLED, UploadRequestStatus.CLOSED, UploadRequestStatus.ARCHIVED,
				UploadRequestStatus.PURGED).contains(uploadRequest.getStatus())) {
			throw new BusinessException(error,
					"You Cannot cannot perform the requested action if upload request's status is not enabled, closed or archived");
		}
	}

	protected void checkSpace(Account owner, long size) throws BusinessException {
		quotaService.checkIfUserCanAddFile(owner, size, ContainerQuotaType.USER);
	}

	protected void addToQuota(Account owner, Long size) {
		OperationHistory oh = new OperationHistory(owner, owner.getDomain(), size, OperationHistoryTypeEnum.CREATE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(oh);
	}

	protected Calendar getDocumentExpirationDate(AbstractDomain domain) {
		return functionalityReadOnlyService.getDefaultFileExpiryTime(domain);
	}

	@Override
	public UploadRequestEntry find(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		UploadRequestEntry entry = uploadRequestEntryBusinessService.findByUuid(uuid);
		if (entry == null) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_NOT_FOUND, "The upload request entry does not exists");
		}
		return entry;
	}

	@Override
	public ByteSource download(Account authUser, Account actor, String uuid) throws BusinessException {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "upload request entry uuid is required.");
		logger.debug("downloading for document : " + uuid);
		UploadRequestEntry entry = find(authUser, actor, uuid);
		checkDownloadPermission(authUser, actor, DocumentEntry.class, BusinessErrorCode.DOCUMENT_ENTRY_FORBIDDEN, entry);
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(actor), new AccountMto(actor),
				LogAction.DOWNLOAD, AuditLogEntryType.UPLOAD_REQUEST_ENTRY, entry.getUuid(), entry);
		logEntryService.insert(log);
		return uploadRequestEntryBusinessService.download(entry);
	}

	@Override
	public UploadRequestEntry deleteEntryByRecipients(UploadRequestUrl uploadRequestUrl, String entryUuid) throws BusinessException{
		if (!uploadRequestUrl.getUploadRequest().isCanDelete()) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_DELETED,
					"Upload request entry can not be deleted, please check your deletion right.");
		}
		UploadRequestEntry entry = uploadRequestEntryBusinessService.findByUuid(entryUuid);
		if (entry == null) {
			throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_NOT_FOUND,
					"File not found");
		}
		uploadRequestEntryBusinessService.delete(entry);
		if (!entry.getCopied()) {
			documentGarbageCollectorRepository.insert(new DocumentGarbageCollecteur(entry.getDocument().getUuid()));
		}
		Account actor = uploadRequestUrl.getUploadRequest().getUploadRequestGroup().getOwner();
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(actor),
				new AccountMto(actor), LogAction.DELETE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY,
				entry.getUuid(), entry);
		logEntryService.insert(log);
		delFromQuota(actor, entry.getSize());
		return entry;
	}

	@Override
	public UploadRequestEntry delete(Account authUser, Account actor, String uuid) {
		preChecks(authUser, actor);
		Validate.notEmpty(uuid, "entry uuid is required.");
		logger.debug(
				"Actor: " + actor.getAccountRepresentation() + " is trying to delete upload request entry: " + uuid);
		UploadRequestEntry uploadRequestEntry = find(authUser, actor, uuid);
		checkDeletePermission(authUser, actor, UploadRequestEntry.class,
				BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN, uploadRequestEntry);
		checkURStatusBeforeCopyAndDelete(uploadRequestEntry.getUploadRequestUrl().getUploadRequest(),
				BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FILE_CANNOT_DELETED);
		if (!uploadRequestEntry.getCopied()) {
			documentGarbageCollectorRepository.insert(new DocumentGarbageCollecteur(uploadRequestEntry.getDocument().getUuid()));
		}
		uploadRequestEntryBusinessService.delete(uploadRequestEntry);
		if (uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getEnableNotification()) {
			for (UploadRequestUrl urUrl : uploadRequestEntry.getUploadRequestUrl().getUploadRequest().getUploadRequestURLs()) {
				EmailContext context = new UploadRequestDeleteFileByOwnerEmailContext(
						(User) urUrl.getUploadRequest().getUploadRequestGroup().getOwner(),
						urUrl.getUploadRequest(),	urUrl, uploadRequestEntry);
				MailContainerWithRecipient mail = mailBuildingService.build(context);
				notifierService.sendNotification(mail); 
			}
		}
		AuditLogEntryUser log = new UploadRequestEntryAuditLogEntry(new AccountMto(authUser),
				new AccountMto(actor), LogAction.DELETE, AuditLogEntryType.UPLOAD_REQUEST_ENTRY,
				uploadRequestEntry.getUuid(), uploadRequestEntry);
		logEntryService.insert(log);
		delFromQuota(actor, uploadRequestEntry.getSize());
		return uploadRequestEntry;
	}

	@Override
	public void delFromQuota(Account owner, Long size) {
		OperationHistory oh = new OperationHistory(owner, owner.getDomain(), - size, OperationHistoryTypeEnum.DELETE,
				ContainerQuotaType.USER);
		operationHistoryBusinessService.create(oh);
	}

	@Override
	public List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl) {
		return uploadRequestEntryBusinessService.findAllExtEntries(uploadRequestUrl);
	}

	@Override
	public FileAndMetaData downloadEntries(Account authUser, Account actor, UploadRequestGroup uploadRequestGroup,
			List<UploadRequestEntry> entries) {
			FileAndMetaData fileAndMetaData = null;
			try {
				File zipFile = File.createTempFile("linshare-download-upload-request-entries-", ARCHIVE_EXTENTION);
				zipFile.deleteOnExit();
				try (FileOutputStream fos = new FileOutputStream(zipFile);
						ZipOutputStream zos = new ZipOutputStream(fos);) {
					for (UploadRequestEntry entry : entries) {
						String entryName = computeEntryName(entry);
						try (InputStream stream = download(authUser, actor, entry.getUuid()).openBufferedStream();) {
							addFileToZip(stream, zos, entryName, entry.getSize());
						} catch (IOException ioException) {
							logger.error("Download upload request entry with UUID {} was failed.", entry.getUuid(), ioException);
							throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_DOWNLOAD_INTERNAL_ERROR,
									"Can not generate the archive for this directory");
						}
					}
					zos.close();
					fileAndMetaData = new FileAndMetaData(Files.asByteSource(zipFile), zipFile.length(),
							uploadRequestGroup.getSubject().concat(ARCHIVE_EXTENTION), ARCHIVE_MIME_TYPE);
					fileAndMetaData.setFile(zipFile);
				} catch (IOException ioException) {
					logger.error("Download entries of the upload request group with UUID: {} failed.", uploadRequestGroup.getUuid(), ioException);
					throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_DOWNLOAD_INTERNAL_ERROR,
							"Can not generate the archive for this directory");
				}
			} catch (IOException ioException) {
				throw new BusinessException(BusinessErrorCode.UPLOAD_REQUEST_ENTRY_DOWNLOAD_INTERNAL_ERROR,
						"Can not generate a temp file");
			}
			return fileAndMetaData;
		}

	private void addFileToZip(InputStream stream, ZipOutputStream zos, String documentName, Long size)
			throws IOException {
		ZipEntry zipEntry = new ZipEntry(documentName);
		zos.putNextEntry(zipEntry);
		IOUtils.copy(stream, zos);
		zos.closeEntry();
	}

	private String computeEntryName(UploadRequestEntry entry) {
		String computedName = null;
		MimeTypes types = MimeTypes.getDefaultMimeTypes();
		MimeType entryType = null;
		String extension = FilenameUtils.getExtension(entry.getName());
		if (!mimeTypeIdentifier.isKnownExtension(extension)) {
			try {
				entryType = types.forName(entry.getDocument().getType());
				extension = entryType.getExtension();
			} catch (MimeTypeException e) {
				logger.debug("Error when trying to get the extension of the upload request entry with UUID: {}",
						entry.getUuid(), e.getMessage());
				throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND,
						"Unable to find the upload request entry's type");
			}
		}
		SimpleDateFormat formatter = new SimpleDateFormat("YYYYMMdd-HHmmss");
		computedName = entry.getName() + "-ure-"
				+ formatter.format(entry.getCreationDate().getTime()).concat(extension);
		return computedName;
	}

	@Override
	public List<UploadRequestEntry> findAllEntries(Account authUser, Account actor, UploadRequest uploadRequest) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, UploadRequestEntry.class, BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN,
				null);
		List<UploadRequestEntry> entries = uploadRequestEntryBusinessService.findAllEntries(uploadRequest);
		return entries;
	}

	@Override
	public Boolean exist(Account authUser, Account actor, String entryUuid, UploadRequest uploadRequest) {
		preChecks(authUser, actor);
		checkListPermission(authUser, actor, UploadRequestEntry.class, BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN,
				null);
		return uploadRequestEntryBusinessService.exist(uploadRequest, entryUuid);
	}

	@Override
	public FileAndMetaData thumbnail(Account authUser, Account actor, String uploadRequestEntryUuid,
			ThumbnailType thumbnailType) {
		preChecks(actor, actor);
		UploadRequestEntry uploadRequestEntry = find(authUser, actor, uploadRequestEntryUuid);
		checkThumbNailDownloadPermission(actor, actor, UploadRequestEntry.class,
				BusinessErrorCode.UPLOAD_REQUEST_ENTRY_FORBIDDEN, uploadRequestEntry);
		if (Objects.isNull(thumbnailType)) {
			thumbnailType = ThumbnailType.MEDIUM;
		}
		ByteSource byteSource = getThumbnailByteSource(uploadRequestEntry, thumbnailType);
		return new FileAndMetaData(byteSource, uploadRequestEntry.getName(), uploadRequestEntry.getType());
	}

	private ByteSource getThumbnailByteSource(UploadRequestEntry uploadRequestEntry, ThumbnailType thumbnailType) {
		Document doc = documentRepository.findByUuid(uploadRequestEntry.getDocument().getUuid());
		if (doc == null) {
			logger.error("can not find document entity with uuid : {}", uploadRequestEntry.getDocument().getUuid());
			throw new BusinessException(BusinessErrorCode.DOCUMENT_NOT_FOUND,
					"can not find document with uuid : " + uploadRequestEntry.getDocument().getUuid());
		}
		Map<ThumbnailType, Thumbnail> thumbnailMap = doc.getThumbnails();
		FileMetaDataKind fileMetaDataKind = ThumbnailType.toFileMetaDataKind(thumbnailType);
		if (thumbnailMap.containsKey(thumbnailType)) {
			FileMetaData metadata = new FileMetaData(fileMetaDataKind, doc);
			return fileDataStore.get(metadata);
		}
		throw new BusinessException(BusinessErrorCode.THUMBNAIL_NOT_FOUND,
				"Can not get thumbnail of the uploadRequestEntry with uuid: " + uploadRequestEntry.getUuid()
						+ ", please check the entered kind: " + thumbnailType);
	}
}
