/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2016 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.linagora.LinThumbnail.FileResource;
import org.linagora.LinThumbnail.FileResourceFactory;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.LinThumbnail.utils.ImageUtils;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.business.service.SignatureBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Entry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.objects.FileInfo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.ThreadEntryRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocumentEntryBusinessServiceImpl implements DocumentEntryBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImpl.class);

	private final FileSystemDao fileSystemDao;
	private final TimeStampingService timeStampingService;
	private final DocumentEntryRepository documentEntryRepository;
	private final ThreadEntryRepository threadEntryRepository;
	private final DocumentRepository documentRepository;
	private final AccountRepository<Account> accountRepository; 
	private final SignatureBusinessService signatureBusinessService;
	private final UploadRequestEntryBusinessService uploadRequestEntryBusinessService;
	private final boolean thumbEnabled;
	private final boolean pdfThumbEnabled;

	public DocumentEntryBusinessServiceImpl(
			final FileSystemDao fileSystemDao,
			final TimeStampingService timeStampingService,
			final DocumentEntryRepository documentEntryRepository,
			final DocumentRepository documentRepository,
			final AccountRepository<Account> accountRepository,
			final SignatureBusinessService signatureBusinessService,
			final ThreadEntryRepository threadEntryRepository,
			final UploadRequestEntryBusinessService uploadRequestEntryBusinessService,
			final boolean thumbEnabled,
			final boolean pdfThumbEnabled) {
		super();
		this.fileSystemDao = fileSystemDao;
		this.timeStampingService = timeStampingService;
		this.documentEntryRepository = documentEntryRepository;
		this.documentRepository = documentRepository;
		this.accountRepository = accountRepository;
		this.signatureBusinessService = signatureBusinessService;
		this.threadEntryRepository = threadEntryRepository;
		this.uploadRequestEntryBusinessService = uploadRequestEntryBusinessService;
		this.thumbEnabled = thumbEnabled;
		this.pdfThumbEnabled = pdfThumbEnabled;
	}

	@Override
	public DocumentEntry createDocumentEntry(Account owner, File myFile,
			Long size, String fileName, String comment,
			Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			Calendar expirationDate, boolean isFromCmis, String metadata) throws BusinessException {
		// add an entry for the file in DB
		DocumentEntry entity = null;
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
		return entity;
	}

	@Override
	public byte[] getTimeStamp(String fileName, File tempFile, String timeStampingUrl) throws BusinessException {
		if(timeStampingUrl == null) {
			return null;
		}

		byte[] timestampToken = null;
		FileInputStream fis = null;

		try{
			fis = new FileInputStream(tempFile);
			TimeStampResponse resp =  timeStampingService.getTimeStamp(timeStampingUrl, fis);
			timestampToken  = resp.getEncoded();
		} catch (TSPException e) {
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_NOT_COMPUTED, String.format("Could not compute timestamp on file '%s' using TSA URL: '%s'.", fileName, timeStampingUrl), e);
		} catch (FileNotFoundException e) {
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_NOT_COMPUTED, String.format("Could not compute timestamp on file '%s' using TSA URL: '%s'.", fileName, timeStampingUrl), e);
		} catch (IOException e) {
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_NOT_COMPUTED, String.format("Could not compute timestamp on file '%s' using TSA URL: '%s'.", fileName, timeStampingUrl), e);
		} catch (URISyntaxException e) {
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_WRONG_TSA_URL, String.format("Could not compute timestamp on file '%s'. TSA URL is invalid: '%s'.", fileName, timeStampingUrl), e);
		} finally {
			try {
				if (fis != null)
					fis.close();
				fis = null;
			} catch (IOException e) {
				logger.error(e.toString());
			}
		}
		return timestampToken ;
	}

	@Override
	public InputStream getDocumentThumbnailStream(DocumentEntry entry) {
		Document doc = documentRepository.findByUuid(entry.getDocument().getUuid());
		String thmbUuid = doc.getThmbUuid();

		if (thmbUuid != null && thmbUuid.length()>0) {
			InputStream stream = fileSystemDao.getFileContentByUUID(thmbUuid);
			return stream;
		}
		return null;
	}

	@Override
	public InputStream getThreadEntryThumbnailStream(ThreadEntry entry) {
		Document doc = documentRepository.findByUuid(entry.getDocument().getUuid());
		String thmbUuid = doc.getThmbUuid();

		if (thmbUuid != null && thmbUuid.length()>0) {
			InputStream stream = fileSystemDao.getFileContentByUUID(thmbUuid);
			return stream;
		}
		return null;
	}

	@Override
	public InputStream getDocumentStream(DocumentEntry entry) {
		String UUID = entry.getDocument().getUuid();
		if (UUID!=null && UUID.length()>0) {
			logger.debug("retrieve from jackrabbity : " + UUID);
			InputStream stream = fileSystemDao.getFileContentByUUID(UUID);
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
	public void renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException {
		String uuid = entry.getDocument().getUuid();
		fileSystemDao.renameFile(uuid, newName);
		entry.setName(newName);
		documentEntryRepository.update(entry);
	}

	@Override
	public DocumentEntry updateFileProperties(DocumentEntry entry, String newName, String fileComment, String meta) throws BusinessException {
		String uuid = entry.getDocument().getUuid();
		fileSystemDao.renameFile(uuid, newName);
		entry.setBusinessName(newName);
		entry.setBusinessComment(fileComment);
		entry.setBusinessMetaData(meta);
		return documentEntryRepository.update(entry);
	}

	@Override
	public ThreadEntry updateFileProperties(ThreadEntry entry, String fileComment, String metaData, String newName) throws BusinessException {
		entry.setBusinessComment(fileComment);
		entry.setBusinessMetaData(metaData);
		entry.setBusinessName(newName);
		return threadEntryRepository.update(entry);
	}

	@Override
	public DocumentEntry updateDocumentEntry(Account owner,
			DocumentEntry docEntry, File myFile, Long size, String fileName,
			Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType,
			Calendar expirationDate) throws BusinessException {

		//create and insert the thumbnail into the JCR
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner.getLsUuid(), myFile, mimeType);

		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUuid(), myFile);

		Document oldDocument = documentRepository.findByUuid(docEntry.getDocument().getUuid());

		// want a timestamp on doc ? if timeStamping url is null, time stamp will be null
		byte[] timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);

		try {

			Document document = new Document(uuid, mimeType, size);
			document.setThmbUuid(uuidThmb);
			document.setTimeStamp(timestampToken);
			document.setSha256sum(SHA256CheckSumFileStream(myFile));
			documentRepository.create(document);

			docEntry.setName(fileName);
			docEntry.setDocument(document);
			docEntry.setExpirationDate(expirationDate);
			docEntry.setSize(size);
			docEntry.setType(mimeType);
			docEntry.setSha256sum(document.getSha256sum());

			//aes encrypt ? check headers
			if(checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			documentEntryRepository.update(docEntry);
			deleteDocument(oldDocument);

			return docEntry;
		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			fileSystemDao.removeFileByUUID(uuid);
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

		Document doc = documentEntry.getDocument(); 
		documentEntryRepository.delete(documentEntry);
		doc.setDocumentEntry(null);
		documentRepository.update(doc);
		deleteDocument(doc);
	}

	@Override
	public ThreadEntry createThreadEntry(Thread owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException {
		// add an entry for the file in DB
		ThreadEntry entity = null;
		Document document = createDocument(owner, myFile, size, fileName, timeStampingUrl, mimeType);
		ThreadEntry docEntry = new ThreadEntry(owner, fileName, document);

		//aes encrypt ? check headers
		if (checkIfIsCiphered) {
			docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
		}
		entity = threadEntryRepository.create(docEntry);
		owner.getEntries().add(entity);
		return entity;
	}

	@Override
	public ThreadEntry copyFromDocumentEntry(Thread thread, DocumentEntry documentEntry,
			InputStream stream)
			throws BusinessException {
		String uuid = null;
		String uuidThmb = null;
		InputStream streamThmb = null;
		Long size = documentEntry.getSize();
		String name = documentEntry.getName();
		String type = documentEntry.getType();
		String path = thread.getLsUuid();
		try {
			uuid = fileSystemDao.insertFile(path, stream, size, name, type);
			String thmbUuidOld = documentEntry.getDocument().getThmbUuid();
			if (thmbUuidOld != null) {
				streamThmb = fileSystemDao.getFileContentByUUID(thmbUuidOld);
				if (streamThmb != null) {
					FileInfo info = fileSystemDao.getFileInfoByUUID(thmbUuidOld);
					uuidThmb = fileSystemDao.insertFile(path, streamThmb, 1, info.getName(), info.getMimeType());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (uuid != null) {
				fileSystemDao.removeFileByUUID(uuid);
			}
			if (uuidThmb != null) {
				fileSystemDao.removeFileByUUID(uuidThmb);
			}
			throw e;
		} finally {
			if (streamThmb != null) {
				try {
					streamThmb.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		Document document = new Document(uuid, type, size);
		document.setSha256sum(documentEntry.getSha256sum());
		document.setThmbUuid(uuidThmb);
		document.setTimeStamp(documentEntry.getDocument().getTimeStamp());
		document = documentRepository.create(document);
		ThreadEntry threadEntry = new ThreadEntry(thread, name, document);
		threadEntry.setCiphered(documentEntry.getCiphered());
		threadEntry = threadEntryRepository.create(threadEntry);
		thread.getEntries().add(threadEntry);
		return threadEntry;
	}

	@Override
	public DocumentEntry copyFromThreadEntry(Account owner,
			ThreadEntry entry, InputStream stream, Calendar expirationDate)
			throws BusinessException {
		String uuid = null;
		String uuidThmb = null;
		InputStream streamThmb = null;
		Long size = entry.getSize();
		String name = entry.getName();
		String type = entry.getType();
		String path = owner.getLsUuid();
		try {
			uuid = fileSystemDao.insertFile(path, stream, size, name, type);
			String thmbUuidOld = entry.getDocument().getThmbUuid();
			if (thmbUuidOld != null) {
				streamThmb = fileSystemDao.getFileContentByUUID(thmbUuidOld);
				if (streamThmb != null) {
					FileInfo info = fileSystemDao.getFileInfoByUUID(thmbUuidOld);
					uuidThmb = fileSystemDao.insertFile(path, streamThmb, 1, info.getName(), info.getMimeType());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (uuid != null) {
				fileSystemDao.removeFileByUUID(uuid);
			}
			if (uuidThmb != null) {
				fileSystemDao.removeFileByUUID(uuidThmb);
			}
			throw e;
		} finally {
			if (streamThmb != null) {
				try {
					streamThmb.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		Document document = new Document(uuid, type, size);
		document.setSha256sum(entry.getSha256sum());
		document.setThmbUuid(uuidThmb);
		document.setTimeStamp(entry.getDocument().getTimeStamp());
		document = documentRepository.create(document);

		DocumentEntry docEntry = new DocumentEntry(owner, name, entry.getComment(), document);
		// We need to set an expiration date in case of file cleaner activation.
		docEntry.setExpirationDate(expirationDate);
		docEntry.setMetaData(entry.getMetaData());
		docEntry.setCiphered(entry.getCiphered());
		docEntry = documentEntryRepository.create(docEntry);
		owner.getEntries().add(docEntry);
		return docEntry;
	}

	@Override
	public DocumentEntry copyFromShareEntry(Account owner,
			ShareEntry entry, InputStream stream, Calendar expirationDate) throws BusinessException {
		String uuid = null;
		String uuidThmb = null;
		InputStream streamThmb = null;
		Long size = entry.getSize();
		String name = entry.getName();
		String type = entry.getType();
		String path = owner.getLsUuid();
		try {
			uuid = fileSystemDao.insertFile(path, stream, size, name, type);
			String thmbUuidOld = entry.getDocumentEntry().getDocument().getThmbUuid();
			if (thmbUuidOld != null) {
				streamThmb = fileSystemDao.getFileContentByUUID(thmbUuidOld);
				if (streamThmb != null) {
					FileInfo info = fileSystemDao.getFileInfoByUUID(thmbUuidOld);
					uuidThmb = fileSystemDao.insertFile(path, streamThmb, 1, info.getName(), info.getMimeType());
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			if (uuid != null) {
				fileSystemDao.removeFileByUUID(uuid);
			}
			if (uuidThmb != null) {
				fileSystemDao.removeFileByUUID(uuidThmb);
			}
			throw e;
		} finally {
			if (streamThmb != null) {
				try {
					streamThmb.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		Document document = new Document(uuid, type, size);
		document.setSha256sum(entry.getDocumentEntry().getSha256sum());
		document.setThmbUuid(uuidThmb);
		document.setTimeStamp(entry.getDocumentEntry().getDocument().getTimeStamp());
		document = documentRepository.create(document);
		DocumentEntry docEntry = new DocumentEntry(owner, name, entry.getComment(), document);
		// We need to set an expiration date in case of file cleaner activation.
		docEntry.setExpirationDate(expirationDate);
		docEntry.setMetaData(entry.getMetaData());
		docEntry.setCiphered(entry.getDocumentEntry().getCiphered());
		docEntry = documentEntryRepository.create(docEntry);
		owner.getEntries().add(docEntry);
		return docEntry;
	}

	@Override
	public ThreadEntry findThreadEntryById(String docEntryUuid) {
		return threadEntryRepository.findByUuid(docEntryUuid);
	}

	@Override
	public List<ThreadEntry> findAllThreadEntries(Thread owner) {
		return threadEntryRepository.findAllThreadEntries(owner);
	}

	@Override
	public long countThreadEntries(Thread thread) {
		return threadEntryRepository.count(thread);
	}

	@Override
	public InputStream getDocumentStream(ThreadEntry entry) {
		String UUID = entry.getDocument().getUuid();
		if (UUID!=null && UUID.length()>0) {
			logger.debug("retrieve from jackrabbit : " + UUID);
			InputStream stream = fileSystemDao.getFileContentByUUID(UUID);
			return stream;
		}
		return null;
	}

	private Document createDocument(Account owner, File myFile, Long size, String fileName, String timeStampingUrl, String mimeType) throws BusinessException {
		//create and insert the thumbnail into the JCR
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner.getLsUuid(), myFile, mimeType);
		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUuid(), myFile);
		try {
			// want a timestamp on doc ?
			byte[] timestampToken = null;
			if(timeStampingUrl != null) {
				timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
			}

			// add an document for the file in DB
			Document document = new Document(uuid, mimeType, size);
			document.setSha256sum(SHA256CheckSumFileStream(myFile));
			document.setSha1sum(SHA1CheckSumFileStream(myFile));
			document.setThmbUuid(uuidThmb);
			document.setTimeStamp(timestampToken);
			return documentRepository.create(document);
		} catch (Exception e) {
			fileSystemDao.removeFileByUUID(uuid);
			if (uuidThmb != null) {
				fileSystemDao.removeFileByUUID(uuidThmb);
			}
			throw e;
		}
	}

	@Override
	public void deleteDocument(Document document) throws BusinessException {
		// delete old thumbnail in JCR
		String oldThumbUuid = document.getThmbUuid(); 
		if (oldThumbUuid != null && oldThumbUuid.length() > 0) {
			logger.debug("suppresion of Thumb, Uuid : " + oldThumbUuid);

			try {
				fileSystemDao.removeFileByUUID(oldThumbUuid);
			} catch (org.springframework.dao.DataRetrievalFailureException e) {
				logger.error("Can not suppress document {}", document.getUuid());
				logger.debug(e.toString());
			}
		}

		// remove old document in JCR
		logger.debug("suppresion of doc, Uuid : " + document.getUuid());
		try {
			fileSystemDao.removeFileByUUID(document.getUuid());
		} catch (org.springframework.dao.DataRetrievalFailureException e) {
			logger.error("Can not suppress document {}", document.getUuid());
			logger.debug(e.toString());
		}

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

	private String generateThumbnailIntoJCR(String fileName, String path, File tempFile, String mimeType) {
		if (!thumbEnabled || (!pdfThumbEnabled && mimeType.contains("pdf"))) {
			logger.warn("Thumbnail generation is disabled.");
			return null;
		}

		FileResourceFactory fileResourceFactory = FileResourceFactory.getInstance();
		FileResource fileResource = fileResourceFactory.getFileResource(tempFile);
		InputStream fisThmb = null;
		BufferedImage bufferedImage=null;
		File tempThumbFile = null;
		String uuidThmb = null;
		if (fileResource != null) {
			try {
				try {
					bufferedImage = fileResource.generateThumbnailImage();
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage());
					logger.debug(e.toString());
				}
				if (bufferedImage != null) {
					fisThmb = ImageUtils.getInputStreamFromImage(bufferedImage, "png");
					tempThumbFile = File.createTempFile("linthumbnail", fileName+"_thumb.png");
					tempThumbFile.createNewFile();

					if (bufferedImage!=null)
						ImageIO.write(bufferedImage, Constants.THMB_DEFAULT_FORMAT, tempThumbFile);

					if (logger.isDebugEnabled()) {
						logger.debug("5.1)start insert of thumbnail in jack rabbit:" + tempThumbFile.getName());
					}
					String mimeTypeThb = "image/png";//getMimeType(fisThmb, file.getAbsolutePath());

					uuidThmb = fileSystemDao.insertFile(path, fisThmb, tempThumbFile.length(), tempThumbFile.getName(), mimeTypeThb);
				}
			} catch (FileNotFoundException e) {
				logger.error(e.toString());
				// if the thumbnail generation fails, it's not big deal, it has not to block
				// the entire process, we just don't have a thumbnail for this document
			} catch (IOException e) {
				logger.error(e.toString());
			} finally {
				try {
					if (fisThmb != null)
						fisThmb.close();
				} catch (IOException e) {
					// Do nothing Happy java :)
					logger.error(e.toString());
				}
				if(tempThumbFile!=null){
					tempThumbFile.delete();
				}
			}
		}
		return uuidThmb;
	}

	private String insertIntoJCR(long size, String fileName, String mimeType, String path, File tempFile) {
		// insert the file into JCR
		FileInputStream fis = null;
		String uuid;
		try {
			fis = new FileInputStream(tempFile);

			if (logger.isDebugEnabled()) {
				logger.debug("insert of the document in jack rabbit:" + fileName + ", size:"+ size + ", path:" + path + " , type: " + mimeType);
			}
			uuid = fileSystemDao.insertFile(path, fis, size, fileName, mimeType);
		} catch (FileNotFoundException e1) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"couldn't open inputStream on the temporary file");
		} finally {
			try {
				logger.debug("closing FileInputStream ");
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				// Do nothing Happy java :)
				logger.error("IO exception : should not happen ! ");
				logger.error(e.toString());
			}
		}
		return uuid;
	}

	private boolean checkIfFileIsCiphered(String fileName, File tempFile) throws BusinessException {
		boolean testheaders = false;
		if(fileName.endsWith(".aes")){
			try {
				AESCrypt aestest = new AESCrypt();
				testheaders = aestest.ckeckFileHeader(tempFile.getAbsolutePath());
			} catch (IOException e) {
				throw new BusinessException(BusinessErrorCode.FILE_ENCRYPTION_UNDEFINED,"undefined encryption format");
			} catch (GeneralSecurityException e) {
				throw new BusinessException(BusinessErrorCode.FILE_ENCRYPTION_UNDEFINED,"undefined encryption format");
			}

			if(!testheaders)
				throw new BusinessException(BusinessErrorCode.FILE_ENCRYPTION_UNDEFINED,"undefined encryption format");
		}
		return testheaders;
	}

	@Override
	public long getRelatedEntriesCount(DocumentEntry documentEntry) {
		return documentEntryRepository.getRelatedEntriesCount(documentEntry);
	}

	@Override
	public void deleteThreadEntry(ThreadEntry threadEntry) throws BusinessException {
		Account owner = threadEntry.getEntryOwner();
		owner.getEntries().remove(threadEntry);
		accountRepository.update(owner);
		Document doc = threadEntry.getDocument();
		threadEntryRepository.update(threadEntry);
		threadEntryRepository.delete(threadEntry);
		doc.setThreadEntry(null);
		documentRepository.update(doc);
		deleteDocument(doc);
	}

	@Override
	public void deleteSetThreadEntry(Set<Entry> setThreadEntry) throws BusinessException {
		for (Object threadEntry : setThreadEntry.toArray()) {
			this.deleteThreadEntry((ThreadEntry) threadEntry);
		}
	}

	@Override
	public long getUsedSpace(Account owner) throws BusinessException {
		return documentEntryRepository.getUsedSpace(owner);
	}

	@Override
	public String SHA256CheckSumFileStream(File file) {
		try(InputStream fs = new FileInputStream(file)) {
			return SHA256CheckSumFileStream(fs);
		} catch (IOException e) {
			logger.error("Could not found the file : " + file.getName(), e);
			throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE, "Coul not found the file " + file.getName());
		}
	}

	@Override
	public String SHA1CheckSumFileStream(File file) {
		try(InputStream fs = new FileInputStream(file)) {
			return SHA1CheckSumFileStream(fs);
		} catch (IOException e) {
			logger.error("Could not found the file : " + file.getName(), e);
			throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE, "Coul not found the file " + file.getName());
		}
	}

	/**
	 *
	 * @param fileStream
	 * @return String SHA256SUM of fileStream
	 * @throws IOException 
	 */
	@Override
	public String SHA256CheckSumFileStream(InputStream fs) throws IOException {
		try {
			StringBuffer hexString = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fs.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			// convert the byte to hex format
			for (int i = 0; i < mdbytes.length; i++) {
				hexString.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(e.getMessage());
		}
	}

	/**
	 *
	 * @param fileStream
	 * @return String SHA1SUM of fileStream
	 * @throws IOException 
	 */
	@Override
	public String SHA1CheckSumFileStream(InputStream fs) throws IOException {
		try {
			StringBuffer hexString = new StringBuffer();
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fs.read(dataBytes)) != -1) {
				md.update(dataBytes, 0, nread);
			}
			byte[] mdbytes = md.digest();
			// convert the byte to hex format
			for (int i = 0; i < mdbytes.length; i++) {
				hexString.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			logger.error(e.getMessage(), e);
			throw new TechnicalException(e.getMessage());
		}
	}

	@Override
	public void update(DocumentEntry docEntry) throws BusinessException {
		documentEntryRepository.update(docEntry);
	}

	@Override
	public List<ThreadEntry> findMoreRecentByName(Thread thread)
			throws BusinessException {
		return threadEntryRepository.findAllDistinctEntries(thread);
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
	public List<String> findAllEntriesWithoutExpirationDate() {
		return documentEntryRepository.findAllEntriesWithoutExpirationDate();
	}
}
