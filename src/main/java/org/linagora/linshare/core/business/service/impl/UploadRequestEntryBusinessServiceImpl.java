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
import java.util.Map;

import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestEntryBusinessService;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Thumbnail;
import org.linagora.linshare.core.domain.entities.UploadRequest;
import org.linagora.linshare.core.domain.entities.UploadRequestEntry;
import org.linagora.linshare.core.domain.entities.UploadRequestUrl;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UploadRequestEntryRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class UploadRequestEntryBusinessServiceImpl implements
		UploadRequestEntryBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(UploadRequestEntryBusinessServiceImpl.class);

	private final UploadRequestEntryRepository uploadRequestEntryRepository;

	private final DocumentRepository documentRepository;

	private final FileDataStore fileDataStore;

	private final boolean deduplication;

	private final ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService;

	private final TimeStampingService timeStampingService;

	public UploadRequestEntryBusinessServiceImpl(
			final UploadRequestEntryRepository uploadRequestEntryRepository,
			final DocumentRepository documentRepository,
			final boolean deduplication,
			final FileDataStore fileDataStore,
			final ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService,
			final TimeStampingService timeStampingService) {
		super();
		this.uploadRequestEntryRepository = uploadRequestEntryRepository;
		this.documentRepository = documentRepository;
		this.deduplication = deduplication;
		this.fileDataStore = fileDataStore;
		this.thumbnailGeneratorBusinessService = thumbnailGeneratorBusinessService;
		this.timeStampingService = timeStampingService;
	}

	@Override
	public UploadRequestEntry findByUuid(String uuid) {
		return uploadRequestEntryRepository.findByUuid(uuid);
	}

	@Override
	public UploadRequestEntry create(UploadRequestEntry entry)
			throws BusinessException {
		return uploadRequestEntryRepository.create(entry);
	}

	@Override
	public UploadRequestEntry createUploadRequestEntryDocument(Account owner, File myFile, Long size, String fileName,
			String comment, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate,
			boolean isFromCmis, String metadata, UploadRequestUrl uploadRequestUrl) throws BusinessException {
		// add an entry for the file in DB
		UploadRequestEntry upReqEntry = null;
		try {
			Document document = createDocument(owner, myFile, size, fileName, timeStampingUrl, mimeType);
			if (comment == null) {
				comment = "";
			}
			UploadRequestEntry upReqdocEntry = new UploadRequestEntry(owner, fileName, comment, document,
					uploadRequestUrl);
			// We need to set an expiration date in case of file cleaner activation.
			upReqdocEntry.setExpirationDate(expirationDate);
			upReqdocEntry.setMetaData(metadata);

			// aes encrypt ? check headers
			if (checkIfIsCiphered) {
				upReqdocEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			upReqdocEntry.setCmisSync(isFromCmis);
			upReqEntry = uploadRequestEntryRepository.create(upReqdocEntry);
			owner.getEntries().add(upReqEntry);
		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT,
					"couldn't register the file in the database");
		}
		return upReqEntry;
	}

	@Override
	public UploadRequestEntry update(UploadRequestEntry entry)
			throws BusinessException {
		return uploadRequestEntryRepository.update(entry);
	}

	@Override
	public void delete(UploadRequestEntry entry) throws BusinessException {
		uploadRequestEntryRepository.delete(entry);
	}

	@Override
	public UploadRequestEntry findRelative(DocumentEntry entry) {
		return uploadRequestEntryRepository.findRelative(entry);
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
	public String SHA256CheckSumFileStream(InputStream fis) throws IOException {
		StringBuffer hexString = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] dataBytes = new byte[1024];

			int nread = 0;
			while ((nread = fis.read(dataBytes)) != -1) {
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
			logger.error(e.toString());
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_NOT_COMPUTED,"TimeStamp on file is not computed", new String[] {fileName});
		} catch (FileNotFoundException e) {
			logger.error(e.toString());
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_NOT_COMPUTED,"TimeStamp on file is not computed", new String[] {fileName});
		} catch (IOException e) {
			logger.error(e.toString());
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_NOT_COMPUTED,"TimeStamp on file is not computed", new String[] {fileName});
		} catch (URISyntaxException e) {
			logger.error(e.toString());
			throw new BusinessException(BusinessErrorCode.FILE_TIMESTAMP_WRONG_TSA_URL,"The Tsa Url is empty or invalid", new String[] {fileName});
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

	private Document createDocument(Account owner, File myFile, Long size, String fileName, String timeStampingUrl, String mimeType) throws BusinessException {
		String sha256sum = SHA256CheckSumFileStream(myFile);
		List<Document> documents = documentRepository.findBySha256Sum(sha256sum);
		Map<ThumbnailType, FileMetaData> fileMetadataThumbnail = Maps.newHashMap();
		if (documents.isEmpty() || !deduplication) {
			// Storing file
			FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, mimeType, size, fileName);
			metadata = fileDataStore.add(myFile, metadata);

			// Computing and storing thumbnail
			if (thumbnailGeneratorBusinessService.isSupportedMimetype(mimeType)) {
				fileMetadataThumbnail = thumbnailGeneratorBusinessService.getThumbnails(owner, myFile, metadata, mimeType);
			}
			try {
				// want a timestamp on doc ?
				byte[] timestampToken = null;
				if (timeStampingUrl != null) {
					timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
				}
				// add an document for the file in DB
				Document document = new Document(metadata);
				document.setSha256sum(sha256sum);
				document.setTimeStamp(timestampToken);
				document.setHasThumbnail(false);
				if (!fileMetadataThumbnail.isEmpty()) {
					Map<ThumbnailType, Thumbnail> fileThumbnails = toFileThumbnail(document, fileMetadataThumbnail);
					document.setHasThumbnail(true);
					document.setThumbnails(fileThumbnails);
				}
				return documentRepository.create(document);
			} catch (Exception e) {
				if (metadata != null)
					fileDataStore.remove(metadata);
				if (!fileMetadataThumbnail.isEmpty()) {
					for(Map.Entry<ThumbnailType, FileMetaData> entry : fileMetadataThumbnail.entrySet() ){
						if (entry.getValue() != null) {
							removeMetadata(entry.getValue());
						}
					}
				}
				throw e;
			}
		} else {
			// we return the first one, the others will be removed by time (expiration, users, ...)
			return documents.get(0);
		}
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

	private void removeMetadata(FileMetaData metadata) {
		try {
			fileDataStore.remove(metadata);
		} catch (IllegalArgumentException iae) {
			logger.debug("", iae);
		}
	}

	private Map<ThumbnailType, Thumbnail> toFileThumbnail(Document document, Map<ThumbnailType, FileMetaData> fileMetadataThumbnail) {
		Map<ThumbnailType, Thumbnail> fileThumbnail = Maps.newHashMap();
		fileMetadataThumbnail.forEach((thumbnailKind, fileMetaData)-> {
			Thumbnail thumbnail = new Thumbnail(fileMetaData.getUuid(), thumbnailKind, document);
			fileThumbnail.put(thumbnailKind, thumbnail);
		});
		return fileThumbnail;
	}

	@Override
	public InputStream download(UploadRequestEntry entry) {
		String UUID = entry.getDocument().getUuid();
		if (UUID != null && UUID.length() > 0) {
			logger.debug("retrieve from fileDataStore : " + UUID);
			InputStream stream = null;
			try {
				FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, entry.getDocument());
				stream = fileDataStore.get(metadata);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE, "no stream available.");
			}
			return stream;
		}
		return null;
	}

	@Override
	public List<UploadRequestEntry> findAllExtEntries(UploadRequestUrl uploadRequestUrl) {
		return uploadRequestEntryRepository.findAllExtEntries(uploadRequestUrl);
	}

	@Override
	public Long computeEntriesSize(UploadRequest request) {
		Long totalSize = 0L;
		for (UploadRequestUrl url : request.getUploadRequestURLs()) {
			totalSize += uploadRequestEntryRepository.computeEntriesSize(url);
		}
		return totalSize;
	}
}
