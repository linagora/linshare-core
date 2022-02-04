/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2019-2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2022.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.linagora.linshare.core.business.service.ThumbnailGeneratorBusinessService;
import org.linagora.linshare.core.business.service.impl.DocumentEntryBusinessServiceImpl;
import org.linagora.linshare.core.dao.FileDataStore;
import org.linagora.linshare.core.domain.constants.FileMetaDataKind;
import org.linagora.linshare.core.domain.constants.ThumbnailType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.Thumbnail;
import org.linagora.linshare.core.domain.objects.FileMetaData;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.AbstractDocumentBusinessService;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.linagora.linshare.mongo.entities.WorkGroupDocument;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.mto.AccountMto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class AbstractDocumentBusinessServiceImpl implements AbstractDocumentBusinessService {

	protected static final Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImpl.class);

	protected final FileDataStore fileDataStore;

	protected final TimeStampingService timeStampingService;

	protected final DocumentRepository documentRepository;

	protected final ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService;

	protected final boolean deduplication;

	public AbstractDocumentBusinessServiceImpl(
			FileDataStore fileDataStore,
			TimeStampingService timeStampingService,
			DocumentRepository documentRepository,
			ThumbnailGeneratorBusinessService thumbnailGeneratorBusinessService,
			boolean deduplication) {
		super();
		this.fileDataStore = fileDataStore;
		this.timeStampingService = timeStampingService;
		this.documentRepository = documentRepository;
		this.thumbnailGeneratorBusinessService = thumbnailGeneratorBusinessService;
		this.deduplication = deduplication;
	}

	@Override
	public Document createDocument(Account owner, File myFile, Long size, String fileName, String timeStampingUrl,
			String mimeType) {
		String sha256sum = SHA256CheckSumFileStream(myFile);
		List<Document> documents = documentRepository.findBySha256Sum(sha256sum);
		Map<ThumbnailType, FileMetaData> fileMetadataThumbnail = Maps.newHashMap();
		if (documents.isEmpty() || !deduplication) {
			// Storing file
			FileMetaData metadata = new FileMetaData(FileMetaDataKind.DATA, mimeType, size, fileName);
			try {
				metadata = fileDataStore.add(Files.asByteSource(myFile), metadata);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
				throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "Can not store file when creating document entry.");
			}

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
			// we return the first one, the others will be removed by time (expiration,
			// users, ...)
			return documents.get(0);
		}
	}

	@Override
	public String SHA256CheckSumFileStream(File file) {
		try (InputStream fs = new FileInputStream(file)) {
			return SHA256CheckSumFileStream(fs);
		} catch (IOException e) {
			logger.error("Could not found the file : " + file.getName(), e);
			throw new BusinessException(BusinessErrorCode.FILE_UNREACHABLE,
					"Coul not found the file " + file.getName());
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
				hexString.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
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

	protected void removeMetadata(FileMetaData metadata) {
		try {
			fileDataStore.remove(metadata);
		} catch (IllegalArgumentException iae) {
			logger.debug("", iae);
		}
	}

	protected Map<ThumbnailType, Thumbnail> toFileThumbnail(Document document, Map<ThumbnailType, FileMetaData> fileMetadataThumbnail) {
		Map<ThumbnailType, Thumbnail> fileThumbnail = Maps.newHashMap();
		fileMetadataThumbnail.forEach((thumbnailKind, fileMetaData)-> {
			Thumbnail thumbnail = new Thumbnail(fileMetaData.getUuid(), thumbnailKind, document);
			fileThumbnail.put(thumbnailKind, thumbnail);
		});
		return fileThumbnail;
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

	protected boolean checkIfFileIsCiphered(String fileName, File tempFile) throws BusinessException {
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
}
