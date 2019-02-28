/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.business.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.Validate;
import org.apache.cxf.helpers.IOUtils;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.constants.WorkGroupNodeType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.Thumbnail;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.ThumbnailRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.service.impl.AbstractDocumentBusinessServiceImpl;
import org.linagora.linshare.mongo.entities.DocumentGarbageCollecteur;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupDocumentRevision;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.linagora.linshare.mongo.repository.DocumentGarbageCollectorMongoRepository;
import org.linagora.linshare.mongo.repository.WorkGroupNodeMongoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class DocumentEntryBusinessServiceImpl extends AbstractDocumentBusinessServiceImpl implements DocumentEntryBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImpl.class);

	private final DocumentEntryRepository documentEntryRepository;
	private final SignatureBusinessService signatureBusinessService;
	private final UploadRequestEntryBusinessService uploadRequestEntryBusinessService;
	protected final WorkGroupNodeMongoRepository repository;
	protected final DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository;
	protected final ThumbnailRepository thumbnailRepository;

	public DocumentEntryBusinessServiceImpl(
			FileDataStore fileDataStore,
			TimeStampingService timeStampingService,
			DocumentRepository documentRepository,
			SignatureBusinessService signatureBusinessService,
			ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService,
			boolean deduplication,
			DocumentEntryRepository documentEntryRepository,
			UploadRequestEntryBusinessService uploadRequestEntryBusinessService,
			WorkGroupNodeMongoRepository repository,
			DocumentGarbageCollectorMongoRepository documentGarbageCollectorRepository,
			ThumbnailRepository thumbnailRepository) {
		super(fileDataStore, timeStampingService, documentRepository,
				thumbnailGeneratorBusinessService, deduplication);
		this.documentEntryRepository = documentEntryRepository;
		this.signatureBusinessService = signatureBusinessService;
		this.uploadRequestEntryBusinessService = uploadRequestEntryBusinessService;
		this.repository = repository;
		this.documentGarbageCollectorRepository = documentGarbageCollectorRepository;
		this.thumbnailRepository = thumbnailRepository;
	}

	@Override
	public DocumentEntry createDocumentEntry(Account owner, File myFile,
			Long size, String fileName, String comment,
			Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			Calendar expirationDate, boolean isFromCmis, String metadata) throws BusinessException {
		// add an entry for the file in DB
		DocumentEntry entity = null;
		try {
			Document document = createDocument(owner, myFile, size, fileName, timeStampingUrl, mimeType);
			if (comment == null)
				comment = "";
			DocumentEntry docEntry = new DocumentEntry(owner, fileName, comment, document);
			// We need to set an expiration date in case of file cleaner activation.
			docEntry.setExpirationDate(expirationDate);
			docEntry.setMetaData(metadata);

			//aes encrypt ? check headers
			if(checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			docEntry.setCmisSync(isFromCmis);
			entity = documentEntryRepository.create(docEntry);
			owner.getEntries().add(entity);
		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "couldn't register the file in the database");
		}
		return entity;
	}

	@Override
	public Document findDocument(String documentUuid) {
		return documentRepository.findByUuid(documentUuid);
	}

	@Override
	public InputStream getDocumentThumbnailStream(DocumentEntry entry, ThumbnailType kind) {
		Document doc = entry.getDocument();
		FileMetaDataKind fileMetaDataKind = ThumbnailType.toFileMetaDataKind(kind);
		return getDocumentThumbnailStream(doc, kind, fileMetaDataKind);
	}

	protected InputStream getDocumentThumbnailStream(Document doc, ThumbnailType kind,
			FileMetaDataKind fileMetaDataKind) {
		Map<ThumbnailType, Thumbnail> thumbnailMap = doc.getThumbnails();
		if (thumbnailMap.containsKey(kind)) {
			FileMetaData metadata = new FileMetaData(fileMetaDataKind, doc);
			try {
				InputStream stream = fileDataStore.get(metadata);
				return stream;
			} catch (TechnicalException ex) {
				logger.debug(ex.getMessage(), ex);
			}
		}
		return null;
	}

	@Override
	public InputStream getThreadEntryThumbnailStream(WorkGroupDocument entry, ThumbnailType kind) {
		Document doc = documentRepository.findByUuid(entry.getDocumentUuid());
		Map<ThumbnailType, Thumbnail> thumbnailMap = doc.getThumbnails();
		FileMetaDataKind fileMetaDataKind = ThumbnailType.toFileMetaDataKind(kind);
		if (thumbnailMap.containsKey(kind)) {
			FileMetaData metadata = new FileMetaData(fileMetaDataKind, doc);
			try {
				InputStream stream = fileDataStore.get(metadata);
				return stream;
			} catch (TechnicalException ex) {
				logger.debug(ex.getMessage(), ex);
			}
		}
		return null;
	}

	@Override
	public InputStream getDocumentStream(DocumentEntry entry) {
		String UUID = entry.getDocument().getUuid();
		if (UUID!=null && UUID.length()>0) {
			logger.debug("retrieve from fileDataStore : " + UUID);
			FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, entry.getDocument());
			InputStream stream = fileDataStore.get(metadata);
			return stream;
		}
		return null;
	}

	@Override
	public DocumentEntry find(String uuid) {
		return documentEntryRepository.findById(uuid);
	}

	@Override
	public List<DocumentEntry> findAllMyDocumentEntries(Account owner) {
		return documentEntryRepository.findAllMyDocumentEntries(owner);
	}

	@Override
	public DocumentEntry renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException {
		entry.setName(newName);
		entry.setModificationDate(new GregorianCalendar());
		return documentEntryRepository.update(entry);
	}

	@Override
	public DocumentEntry updateFileProperties(DocumentEntry entry, String newName, String fileComment, String meta) throws BusinessException {
		entry.setBusinessName(newName);
		entry.setBusinessComment(fileComment);
		entry.setBusinessMetaData(meta);
		entry.setModificationDate(new GregorianCalendar());
		return documentEntryRepository.update(entry);
	}

	@Override
	public DocumentEntry updateDocumentEntry(Account owner,
			DocumentEntry docEntry, File myFile, Long size, String fileName,
			Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			Calendar expirationDate) throws BusinessException {

		String sha256sum = SHA256CheckSumFileStream(myFile);
		FileMetaData metadata = null;
		// want a timestamp on doc ? if timeStamping url is null, time stamp
		// will be null
		Map<ThumbnailType, FileMetaData> fileMetadataThumbnail = Maps.newHashMap();
		try {
			Document document = null;
			List<Document> documents = documentRepository.findBySha256Sum(sha256sum);
			if (documents.isEmpty()) {
				// Storing file
				metadata = new FileMetaData(FileMetaDataKind.DATA, mimeType, size, fileName);
				metadata.setFileName(fileName);
				metadata = fileDataStore.add(myFile, metadata);
				// Computing and storing thumbnail
				if (thumbnailGeneratorBusinessService.isSupportedMimetype(mimeType)) {
					fileMetadataThumbnail = thumbnailGeneratorBusinessService.getThumbnails(owner, myFile, metadata, mimeType);
				}
				byte[] timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
				document = new Document(metadata);
				document.setTimeStamp(timestampToken);
				document.setSha256sum(sha256sum);
				document.setHasThumbnail(false);
				if (!fileMetadataThumbnail.isEmpty()) {
					Map<ThumbnailType, Thumbnail> fileThumbnails = toFileThumbnail(document, fileMetadataThumbnail);
					document.setHasThumbnail(true);
					document.setThumbnails(fileThumbnails);
				}
				document = documentRepository.create(document);
			} else {
				document = documents.get(0);
			}
			docEntry.setName(fileName);
			docEntry.setDocument(document);
			docEntry.setExpirationDate(expirationDate);
			docEntry.setSize(size);
			docEntry.setType(mimeType);
			docEntry.setSha256sum(sha256sum);
			// aes encrypt ? check headers
			if (checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			docEntry.setModificationDate(new GregorianCalendar());
			documentEntryRepository.update(docEntry);
			return docEntry;
		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getAccountRepresentation() + ", reason : ",
					e);
			if (metadata != null)
				fileDataStore.remove(metadata);
			if (!fileMetadataThumbnail.isEmpty()) {
				for(Map.Entry<ThumbnailType, FileMetaData> entry : fileMetadataThumbnail.entrySet() ){
					if (entry.getValue() != null) {
						removeMetadata(entry.getValue());
					}
				}
			}
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "couldn't register the file in the database");
		}
	}

	@Override
	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException {
		UploadRequestEntry uploadRequestEntry = uploadRequestEntryBusinessService.findRelative(documentEntry);
		if (uploadRequestEntry != null) {
			uploadRequestEntry.setDocumentEntry(null);
			uploadRequestEntryBusinessService.update(uploadRequestEntry);
		}
		logger.debug("Deleting document entry: " + documentEntry.getUuid());
		Account owner = documentEntry.getEntryOwner();
		owner.getEntries().remove(documentEntry);
		documentGarbageCollectorRepository.insert(new DocumentGarbageCollecteur(documentEntry.getDocument().getUuid()));
		documentEntryRepository.delete(documentEntry);
	}

	protected void setDocumentProperties(Account actor, WorkGroupDocument documentNode, String fileName, WorkGroupNode parentNode, File myFile, Boolean checkIfIsCiphered) {
		Validate.notNull(documentNode);
		Validate.notNull(myFile);
		// aes encrypt ? check headers
		if (checkIfIsCiphered) {
			documentNode.setCiphered(checkIfFileIsCiphered(fileName, myFile));
		}
		documentNode.setCreationDate(new Date());
		documentNode.setModificationDate(new Date());
		documentNode.setUploadDate(new Date());
		documentNode.setPathFromParent(parentNode);
		documentNode.setLastAuthor(new AccountMto(actor, true));
	}

	@Override
	public WorkGroupDocument copy(Account actor, WorkGroup toWorkGroup, WorkGroupNode nodeParent, String documentUuid,
			String name, boolean ciphered) throws BusinessException {
		if (exists(toWorkGroup, name, nodeParent) && !WorkGroupNodeType.DOCUMENT.equals(nodeParent.getNodeType())) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_ALREADY_EXISTS,
					"Can not create a new document, it already exists.");
		}
		Document document = documentRepository.findByUuid(documentUuid);
		if (document == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Can not create a new document, missing underlying document.");
		}
		Document createDocument = createDocument(toWorkGroup, document, name);
		WorkGroupDocument node = new WorkGroupDocumentRevision(actor, name, createDocument, toWorkGroup, nodeParent);
		node.setCreationDate(new Date());
		node.setModificationDate(new Date());
		node.setUploadDate(new Date());
		node.setPathFromParent(nodeParent);
		node.setCiphered(ciphered);
		node.setLastAuthor(new AccountMto(actor, true));
		try {
			node = repository.insert(node);
		} catch (org.springframework.dao.DuplicateKeyException e) {
			throw new BusinessException(BusinessErrorCode.WORK_GROUP_DOCUMENT_ALREADY_EXISTS,
					"Can not create a new document, it already exists.");
		}
		return node;
	}

	@Override
	public DocumentEntry copy(Account owner, String documentUuid, String fileName, String comment, String metadata,
			Calendar expirationDate, boolean ciphered) throws BusinessException {
		Document document = documentRepository.findByUuid(documentUuid);
		if (document == null) {
			throw new BusinessException(BusinessErrorCode.NO_SUCH_ELEMENT,
					"Can not create a new document, missing underlying document.");
		}
		Document createDocument = createDocument(owner, document, fileName);
		DocumentEntry docEntry = new DocumentEntry(owner, fileName, comment, createDocument);
		// We need to set an expiration date in case of file cleaner activation.
		docEntry.setExpirationDate(expirationDate);
		if (comment == null) {
			docEntry.setComment("");
		}
		docEntry.setMetaData(metadata);
		docEntry.setCiphered(ciphered);
		docEntry.setCmisSync(false);
		docEntry = documentEntryRepository.create(docEntry);
		owner.getEntries().add(docEntry);
		return docEntry;
	}

	@Override
	public InputStream getDocumentStream(WorkGroupDocument entry) {
		String UUID = entry.getDocumentUuid();
		if (UUID!=null && UUID.length()>0) {
			Document document = documentRepository.findByUuid(UUID);
			logger.debug("retrieve from fileDataStore : " + UUID);
			FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, document);
			InputStream stream = fileDataStore.get(metadata);
			return stream;
		}
		return null;
	}


	protected Document createDocument(Account owner, Document srcDocument, String fileName) throws BusinessException {
		boolean createIt = false;
		Document document = null;
		List<Document> documents = documentRepository.findBySha256Sum(srcDocument.getSha256sum());
		if (documents.isEmpty() || !deduplication) {
			createIt = true;
		} else {
			// we return the first one, the others will be removed by time
			// (expiration, users, ...)
			document = documents.get(0);
			if (!fileDataStore.exists(new FileMetaData(FileMetaDataKind.DATA, document))) {
				createIt = true;
			}
		}

		if (createIt) {
			FileMetaData metadataSrc = new FileMetaData(FileMetaDataKind.DATA, srcDocument);
			// copy document
			FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, srcDocument.getType(),
					srcDocument.getSize(), fileName);
			try (InputStream docStream = fileDataStore.get(metadataSrc)) {
				metadata = fileDataStore.add(docStream, metadata);
			} catch (IOException e1) {
				logger.error(e1.getMessage(), e1);
				throw new BusinessException("Can not create a copy of existing document.");
			}
			// dirty copy thumbnail
			Map<ThumbnailType, FileMetaData> fileMetaDataThumbnail = copyThumbnail(srcDocument,
					owner, metadata);
			try {
				document = new Document(metadata);
				document.setSha256sum(srcDocument.getSha256sum());
				document.setSha1sum(srcDocument.getSha1sum());
				document.setHasThumbnail(false);
				Map<ThumbnailType, Thumbnail> fileThumbnails = toFileThumbnail(document, fileMetaDataThumbnail);
				if (!fileThumbnails.isEmpty()) {
					document.setHasThumbnail(true);
					document.setThumbnails(fileThumbnails);
				}
				document = documentRepository.create(document);
			} catch (BusinessException e) {
				if (metadata != null)
					fileDataStore.remove(metadata);
				if (!fileMetaDataThumbnail.isEmpty()) {
					for(Map.Entry<ThumbnailType, FileMetaData> entry : fileMetaDataThumbnail.entrySet() ){
						if (entry.getValue() != null) {
							removeMetadata(entry.getValue());
						}
					}
				}
			}
		}
		return document;
	}

	private Map<ThumbnailType, FileMetaData> copyThumbnail(Document srcDocument, Account owner, FileMetaData metadata) {
		Map<ThumbnailType, FileMetaData> thumbnailMetaData = Maps.newHashMap();
		File tempThumbFile = null;
		for (ThumbnailType kind : ThumbnailType.values()) {
			try {
				tempThumbFile = getFileCopyThumbnail(srcDocument, owner, kind);
				if (tempThumbFile != null) {
					thumbnailMetaData.put(kind, storeThumbnail(tempThumbFile, metadata));
				}
			} catch (Exception e) {
				logger.error("Copy thumbnail failed");
				logger.error(e.getMessage(), e);
			} finally {
				if (tempThumbFile != null) {
					tempThumbFile.delete();
				}
			}
		}
		return thumbnailMetaData;
	}

	private File getFileCopyThumbnail(Document srcDocument, Account owner, ThumbnailType kind) {
		FileMetaDataKind fileMetaDataKind = ThumbnailType.toFileMetaDataKind(kind);
		try (InputStream inputStream = getDocumentThumbnailStream(srcDocument, kind, fileMetaDataKind)) {
			if (inputStream != null) {
				File tempThumbFile = null;
				tempThumbFile = File.createTempFile("linthumbnail", owner + "_thumb.png");
				tempThumbFile.createNewFile();
				try (FileOutputStream fos = new FileOutputStream(tempThumbFile)) {
					IOUtils.copyAndCloseInput(inputStream, fos);
					return tempThumbFile;
				}
			}
		} catch (IOException e1) {
			logger.error("Can not create a copy thumbnail of existing document.");
			logger.error(e1.getMessage(), e1);
		}
		return null;
	}

	private FileMetaData storeThumbnail(File thumbFile, FileMetaData metadata) {
		FileMetaData metadataThumb = null;
		if (thumbFile != null) {
			metadataThumb = new FileMetaData(FileMetaDataKind.THUMBNAIL_SMALL, "image/png", thumbFile.length(),
					metadata.getFileName());
			metadataThumb = fileDataStore.add(thumbFile, metadataThumb);
		}
		return metadataThumb;
	}

	@Override
	public void deleteDocument(Document document) throws BusinessException {
		// delete old thumbnail in JCR (Small, Medium and Large)
		if (document.getHasThumbnail()) {
			deleteThumbnail(document);
		}
		// remove old document in JCR
		logger.debug("suppression of doc, Uuid : " + document.getUuid());
		FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, document);
		fileDataStore.remove(metadata);

		//clean all signatures ...
		Set<Signature> signatures = document.getSignatures();
		for (Signature signature : signatures) {
			signatureBusinessService.deleteSignature(signature);
		}
		signatures.clear();
		documentRepository.update(document);

		// remove old document from database
		documentRepository.delete(document);
	}

	private void deleteThumbnail(Document document) {
		for (ThumbnailType kind : ThumbnailType.values()) {
			FileMetaDataKind fileMetaDataKind = ThumbnailType.toFileMetaDataKind(kind);
			Thumbnail thumbnail = document.getThumbnails().get(kind);
			if (thumbnail != null) {
				logger.info("suppression of " + fileMetaDataKind + " Thumb, Uuid : " + thumbnail.getThumbnailUuid());
				FileMetaData metadata = new FileMetaData(fileMetaDataKind, document, "image/png");
				fileDataStore.remove(metadata);
			}
		}
	}

	@Override
	public long getRelatedEntriesCount(DocumentEntry documentEntry) {
		return documentEntryRepository.getRelatedEntriesCount(documentEntry);
	}

	@Deprecated
	@Override
	public long getUsedSpace(Account owner) throws BusinessException {
		return documentEntryRepository.getUsedSpace(owner);
	}

	@Override
	public void update(DocumentEntry docEntry) throws BusinessException {
		docEntry.setModificationDate(new GregorianCalendar());
		documentEntryRepository.update(docEntry);
	}

	@Override
	public DocumentEntry findMoreRecentByName(Account owner, String fileName)
			throws BusinessException {
		return documentEntryRepository.findMoreRecentByName(owner, fileName);
	}

	@Override
	public void syncUniqueDocument(Account owner, String fileName)
			throws BusinessException {
		documentEntryRepository.syncUniqueDocument(owner, fileName);
	}

	@Override
	public List<DocumentEntry> findAllMySyncEntries(Account owner)
			throws BusinessException {
		return documentEntryRepository.findAllMySyncEntries(owner);
	}

	@Override
	public List<String> findAllExpiredEntries() {
		return documentEntryRepository.findAllExpiredEntries();
	}

	@Override
	public void updateThumbnail(Document document, Account account) {
		Map<ThumbnailType, FileMetaData> fileMetadataThumbnail = Maps.newHashMap();
		Set<DocumentEntry> documentEntries = document.getDocumentEntries();
		List<WorkGroupDocument> wgDocuments = repository.findByDocumentUuid(document.getUuid());
		if (thumbnailGeneratorBusinessService.isSupportedMimetype(document.getType())) {
			FileMetaData fileMetaData = new FileMetaData(FileMetaDataKind.DATA, document);
			if (fileDataStore.exists(fileMetaData)) {
				File myFile = null;
				try (InputStream stream = fileDataStore.get(fileMetaData);) {
					String oldThumbUuid = document.getThmbUuid();
					if (oldThumbUuid != null && oldThumbUuid.length() > 0) {
						FileMetaData thmbMetadata = new FileMetaData(FileMetaDataKind.THUMBNAIL, document);
						if (fileDataStore.exists(thmbMetadata)) {
							fileDataStore.remove(thmbMetadata);
							document.setThmbUuid(null);
						}
					}
					myFile = File.createTempFile("temp", "file");
					FileUtils.copyInputStreamToFile(stream, myFile);
					fileMetadataThumbnail = thumbnailGeneratorBusinessService.getThumbnails(account, myFile, fileMetaData,
							document.getType());
					Map<ThumbnailType, Thumbnail> fileThumbnails = toFileThumbnail(document, fileMetadataThumbnail);
					if (!fileThumbnails.isEmpty()) {
						document.setHasThumbnail(true);
						document.setThumbnails(fileThumbnails);
						documentEntries.stream().forEach(docEntry -> updateHasThumbnailDocumentEntry(docEntry, true));
						wgDocuments.stream().forEach(wgDocument -> updateHasThumbnailWorkGroupDocument(wgDocument, true));
						repository.saveAll(wgDocuments);
					}
					document.setComputeThumbnail(false);
					documentRepository.update(document);
					logger.info("Update the document to generate Thumbnail succes " + document.getRepresentation());
				} catch (IOException io) {
					throw new BusinessException("failed to get the document");
				} finally {
					if (myFile != null) {
						myFile.delete();
					}
				}
			}
		} else {
			document.setHasThumbnail(false);
			document.setComputeThumbnail(false);
			documentRepository.update(document);
			documentEntries.stream().forEach(docEntry -> updateHasThumbnailDocumentEntry(docEntry, false));
			wgDocuments.stream().forEach(wgDocument -> updateHasThumbnailWorkGroupDocument(wgDocument, false));
			repository.saveAll(wgDocuments);
		}
	}

	private void updateHasThumbnailDocumentEntry(DocumentEntry docEntry, boolean hasThumbnail) {
		docEntry.setHasThumbnail(hasThumbnail);
		documentEntryRepository.update(docEntry);
	}

	private void updateHasThumbnailWorkGroupDocument(WorkGroupDocument wgDocument, boolean hasThumbnail) {
		wgDocument.setHasThumbnail(hasThumbnail);
	}

	protected boolean exists(WorkGroup workGroup, String fileName, WorkGroupNode nodeParent) {
		List<WorkGroupNode> nodes = repository.findByWorkGroupAndParentAndName(workGroup.getLsUuid(),
				nodeParent.getUuid(), fileName);
		return (nodes != null && !nodes.isEmpty()) ? true : false;
	}

	@Override
	public DocumentEntry copy(Account owner, UploadRequestEntry uploadRequestEntry) {
		DocumentEntry entity = null;
		DocumentEntry docEntry = new DocumentEntry(owner, uploadRequestEntry.getName(), uploadRequestEntry.getComment(),
				uploadRequestEntry.getDocument());
		// We need to set an expiration date in case of file cleaner activation.
		if (uploadRequestEntry.getComment() == null) {
			docEntry.setComment("");
		}
		docEntry.setSize(uploadRequestEntry.getSize());
		docEntry.setSha256sum(uploadRequestEntry.getSha256sum());
		docEntry.setExpirationDate(uploadRequestEntry.getExpirationDate());
		docEntry.setMetaData(uploadRequestEntry.getMetaData());
		docEntry.setCmisSync(uploadRequestEntry.isCmisSync());
		docEntry.setHasThumbnail(uploadRequestEntry.isHasThumbnail());
		entity = documentEntryRepository.create(docEntry);
		if (uploadRequestEntry.getCopied() == false) {
			uploadRequestEntry.setCopied(true);
		}
		return entity;
	}

}
