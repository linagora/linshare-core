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
package org.linagora.linShare.core.service.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.GregorianCalendar;
import java.util.List;

import javax.imageio.ImageIO;

import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.linagora.LinThumbnail.FileResource;
import org.linagora.LinThumbnail.FileResourceFactory;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.LinThumbnail.utils.ImageUtils;
import org.linagora.linShare.core.dao.FileSystemDao;
import org.linagora.linShare.core.domain.constants.LinShareConstants;
import org.linagora.linShare.core.domain.constants.LogAction;
import org.linagora.linShare.core.domain.constants.Reason;
import org.linagora.linShare.core.domain.entities.AbstractDomain;
import org.linagora.linShare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.FileLogEntry;
import org.linagora.linShare.core.domain.entities.Functionality;
import org.linagora.linShare.core.domain.entities.LogEntry;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MimeTypeStatus;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.Signature;
import org.linagora.linShare.core.domain.entities.StringValueFunctionality;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linShare.core.domain.transformers.impl.ShareTransformer;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.domain.vo.ShareDocumentVo;
import org.linagora.linShare.core.domain.vo.SignatureVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.DocumentRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.AbstractDomainService;
import org.linagora.linShare.core.service.DocumentService;
import org.linagora.linShare.core.service.FunctionalityService;
import org.linagora.linShare.core.service.LogEntryService;
import org.linagora.linShare.core.service.MimeTypeService;
import org.linagora.linShare.core.service.ShareService;
import org.linagora.linShare.core.service.TimeStampingService;
import org.linagora.linShare.core.service.VirusScannerService;
import org.linagora.linShare.core.utils.AESCrypt;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentServiceImpl implements DocumentService {
	

	private final DocumentRepository documentRepository;

	private final UserRepository<User> userRepository;

	private final FileSystemDao fileSystemDao;
	private final MimeTypeIdentifier identifier;

	private final MimeTypeService mimeTypeService;

	private final VirusScannerService virusScannerService;
	private final TimeStampingService timeStampingService;

	private final ShareService shareService;

	private final ShareTransformer shareTransformer;

	private final LogEntryService logEntryService;

	private final AbstractDomainService abstractDomainService;
	private final FunctionalityService functionalityService;
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);

	public DocumentServiceImpl(final DocumentRepository documentRepository,
			final FileSystemDao fileSystemDao,
			final UserRepository<User> userRepository,
			final MimeTypeService mimeTypeService,
			final ShareService shareService,
			final LogEntryService logEntryService,
			final VirusScannerService virusScannerService,
			final TimeStampingService timeStampingService,
			final ShareTransformer shareTransformer,
			final AbstractDomainService abstractDomainService,
			final FunctionalityService functionalityService) {
		this.documentRepository = documentRepository;
		this.fileSystemDao = fileSystemDao;
		this.userRepository = userRepository;
		this.identifier = new MagicMimeTypeIdentifier();
		this.mimeTypeService = mimeTypeService;
		this.shareService = shareService;
		this.logEntryService = logEntryService;
		this.virusScannerService = virusScannerService;
		this.timeStampingService = timeStampingService;
		this.shareTransformer = shareTransformer;
		this.abstractDomainService = abstractDomainService;
		this.functionalityService = functionalityService;
	}

	/**
	 * return the mime type of the file, using Aperture
	 */
	public String getMimeType(InputStream theFileStream, String theFilePath)
			throws BusinessException {
		byte[] bytes;
		try {
			bytes = IOUtil.readBytes(theFileStream, identifier
					.getMinArrayLength());
		} catch (IOException e) {
			logger.error("Could not read the uploaded file " + theFilePath
					+ " to fetch its mime : ", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND,
					"Could not read the uploaded file to fetch its mime");
		}

		// let the MimeTypeIdentifier determine the MIME type of this file
		return identifier.identify(bytes, theFilePath, null);

	}

	/**
	 * Insert the file into JCR, and add the file in DB Oddly enough, we can
	 * fail using the DB, but not JCR TODO : SET THE EXPIRATION DATE OF THE DOC
	 * @throws BusinessException
	 */
	public Document insertFile(String path, InputStream stream, long size,
			String fileName, String mimeType, User owner)
			throws BusinessException {
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(owner.getDomain().getIdentifier());
		
		boolean putwarning =  false;
		

		if (logger.isDebugEnabled()) {
			logger.debug("*****begin process insertFile");
			logger.debug("1)check the user quota:" + getAvailableSize(owner) + ">"
					+ size);
		}

		// check the user quota
		if (getAvailableSize(owner) < size) {
			logger.info("The file  " + fileName + " is too large to fit in "
					+ owner.getLogin() + " user's space");
            String[] extras = {fileName};
			throw new BusinessException(BusinessErrorCode.FILE_TOO_LARGE,
					"The file is too large to fit in user's space", extras);
		}

		// check if the file MimeType is allowed
		Functionality mimeFunctionality = functionalityService.getMimeTypeFunctionality(domain);
		if(mimeFunctionality.getActivationPolicy().getStatus()) {
			// use mimetype filtering
			if (logger.isDebugEnabled()) {
				logger.debug("2)check the type mime:" + mimeType);
			}

				// if we refuse some type of mime type
				if (mimeType != null) {
					MimeTypeStatus status = mimeTypeService.giveStatus(mimeType);

					if (status==MimeTypeStatus.DENIED) {
						if (logger.isDebugEnabled())
							logger.debug("mimetype not allowed: " + mimeType);
                        String[] extras = {fileName};
						throw new BusinessException(
								BusinessErrorCode.FILE_MIME_NOT_ALLOWED,
								"This kind of file is not allowed: " + mimeType, extras);
					} else if(status==MimeTypeStatus.WARN){
						if (logger.isInfoEnabled())
							logger.info("mimetype warning: " + mimeType + "for user: "+owner.getMail());
						putwarning = true;
					}
				} else {
					//type mime is null ?
                    String[] extras = {fileName};
					throw new BusinessException(BusinessErrorCode.FILE_MIME_NOT_ALLOWED,
                        "type mime is empty for this file" + mimeType, extras);
				}
		}
		
		
		// Copy the input stream to a temporary file for safe use
		File tempFile = null;
		BufferedOutputStream bof = null;
		int splitIdx = fileName.lastIndexOf('.');
		String extension = "";
		if(splitIdx>-1){
			extension = fileName.substring(splitIdx, fileName.length());
		}
		
		logger.debug("Found extension :"+extension);

		try {
			tempFile = File.createTempFile("linshare", extension); //we need to keep the extension for the thumbnail generator
			tempFile.deleteOnExit();

			if (logger.isDebugEnabled()) {
				logger.debug("3)createTempFile:" + tempFile);
			}

			bof = new BufferedOutputStream(new FileOutputStream(tempFile));

			// Transfer bytes from in to out
			byte[] buf = new byte[20480];
			int len;
			while ((len = stream.read(buf)) > 0) {
				bof.write(buf, 0, len);
			}

			bof.flush();

		} catch (IOException e) {
			if (tempFile != null && tempFile.exists())
				tempFile.delete();
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"couldn't create a temporary file");
		} finally {

			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}

			if (bof != null) {
				try {
					bof.close();
				} catch (IOException e) {
				}
			}
		}
		
		
		boolean aesencrypted = false;
		//aes encrypt ? check headers
		Functionality enciphermentFunctionality = functionalityService.getEnciphermentFunctionality(domain);
		if(enciphermentFunctionality.getActivationPolicy().getStatus()) {
			
			if(fileName.endsWith(".aes")){
				
				boolean testheaders = false;
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
				else 
					aesencrypted = true;
			}
		}

		
		Functionality antivirusFunctionality = functionalityService.getAntivirusFunctionality(domain);
		if(antivirusFunctionality.getActivationPolicy().getStatus()) {
			
			if (logger.isDebugEnabled()) {
				logger.debug("4)antivirus activation:" + !virusScannerService.isDisabled());
			}

			boolean checkStatus = false;
			try {
				checkStatus = virusScannerService.check(tempFile);
			} catch (TechnicalException e) {
				LogEntry logEntry = new AntivirusLogEntry(owner.getMail(), owner.getFirstName(), 
						owner.getLastName(), owner.getDomainId(), 
						LogAction.ANTIVIRUS_SCAN_FAILED, e.getMessage());
				logEntryService.create(LogEntryService.ERROR,logEntry);
				logger.error("File scan failed: antivirus enabled but not available ?");
				throw new BusinessException(BusinessErrorCode.FILE_SCAN_FAILED,
					"File scan failed", e);
			}
			// check if the file contains virus
			if (!checkStatus) {
				LogEntry logEntry = new AntivirusLogEntry(owner.getMail(), owner.getFirstName(), 
						owner.getLastName(), owner.getDomainId(), LogAction.FILE_WITH_VIRUS, fileName);
				logEntryService.create(LogEntryService.WARN,logEntry);
				logger.warn(owner.getMail()
						+ " tried to upload a file containing virus:" + fileName);
				tempFile.delete(); // SOS ! do not keep the file on the system...
	            String[] extras = {fileName};
				throw new BusinessException(BusinessErrorCode.FILE_CONTAINS_VIRUS,
						"File contains virus", extras);
			}
		}

		
		byte[] timestampToken = null;
		// want a timestamp on doc ?
		StringValueFunctionality timeStampingFunctionality = functionalityService.getTimeStampingFunctionality(domain);
		if(timeStampingFunctionality.getActivationPolicy().getStatus()) {

			FileInputStream fis = null;
			
			try{
			
				fis = new FileInputStream(tempFile);
				TimeStampResponse resp =  timeStampingService.getTimeStamp(timeStampingFunctionality.getValue(), fis);
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
				} catch (IOException e) {}
			}
			
		}
		

		//create and insert the thumbnail into the JCR
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner, tempFile);
		
		// insert the file into JCR
		FileInputStream fis = null;
		String uuid;
		try {
			fis = new FileInputStream(tempFile);

			if (logger.isDebugEnabled()) {
				logger.debug("5.2)start insert of the document in jack rabbit:" + fileName);
			}

			uuid = fileSystemDao.insertFile(owner.getLogin(), fis, size,
					fileName, mimeType);
		} catch (FileNotFoundException e1) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"couldn't open inputStream on the temporary file");
		} finally {

			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				// Do nothing Happy java :)
			}

			tempFile.delete(); // remove the temporary file
		}

		// add an entry for the file in DB
		Document docEntity = null;
		try {
			Document aDoc = new Document(uuid, fileName, mimeType,
					new GregorianCalendar(), new GregorianCalendar(), owner,
					aesencrypted, false, false, size);
			aDoc.setThmbUUID(uuidThmb);
			if(timestampToken!=null) aDoc.setTimeStamp(timestampToken);

			if (logger.isDebugEnabled()) {
				logger.debug("6)start insert in database file uuid:" + uuid);
			}

			docEntity = documentRepository.create(aDoc);
			owner.addDocument(docEntity);

			userRepository.update(owner);

			FileLogEntry logEntry = new FileLogEntry(owner.getMail(), owner
					.getFirstName(), owner.getLastName(), owner.getDomainId(),
					LogAction.FILE_UPLOAD, "Creation of a file", docEntity
							.getName(), docEntity.getSize(), docEntity
							.getType());

			logEntryService.create(logEntry);
			
			addDocSizeToGlobalUsedQuota(docEntity, domain);

		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user "
					+ owner.getLogin() + ", reason : ", e);
			fileSystemDao.removeFileByUUID(uuid);
			throw new TechnicalException(
					TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT,
					"couldn't register the file in the database");

		}

		if (logger.isDebugEnabled())	logger.debug("*****end process insertFile");
		

		if(putwarning){
            String[] extras = {fileName};
			throw new BusinessException(BusinessErrorCode.FILE_MIME_WARNING,mimeType, extras);
		}
		
		return docEntity;
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
	

	/**
	 * Insert the tumbnail of the file in the JCR and return the thumbnail uuid.
	 * 
	 * @param fileName the name of the original file
	 * @param owner the owner of the original file
	 * @param tempFile the copy of the original file
	 * @return the thumbnail uuid
	 */
	private String generateThumbnailIntoJCR(String fileName, User owner,
			File tempFile) {
		FileResourceFactory fileResourceFactory = FileResourceFactory.getInstance();
		FileResource fileResource = fileResourceFactory.getFileResource(tempFile);
		InputStream fisThmb = null;
		BufferedImage bufferedImage=null;
		File tempThumbFile = null;
		String uuidThmb = null;
		if (fileResource != null) {
			try {
				bufferedImage = fileResource.generateThumbnailImage();
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
					
					uuidThmb = fileSystemDao.insertFile(owner.getLogin(), fisThmb, tempThumbFile.length(),
							tempThumbFile.getName(), mimeTypeThb);
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
				}
				if(tempThumbFile!=null){
					tempThumbFile.delete();
				}
			}
		}
		return uuidThmb;
	}

	/**
	 * usefull method to replace file content (use it for example with encrypted
	 * content)
	 * 
	 * @param currentFileUUID
	 * @param inputStream
	 * @param size
	 * @param fileName
	 * @param mimeType
	 * @param owner
	 * @return
	 * @throws BusinessException
	 */
	public Document updateFileContent(String currentFileUUID,
			InputStream inputStream, long size, String fileName, String mimeType, boolean encrypted,
			User owner) {

		String newUuid = null;
		Document aDoc = null;
		String uuidThmb = null;
		FileInputStream finputStream = null;

		try {
			
			// get the document to update
			aDoc = documentRepository.findById(currentFileUUID);
			
			if(!encrypted){
			
				// jack rabbit new thumbnail
				File tempFile = File.createTempFile("linshareUpdateThmb", fileName);
				tempFile.deleteOnExit();
	
				BufferedOutputStream bof = new BufferedOutputStream(new FileOutputStream(tempFile));
	
				// Transfer bytes from in to out
				byte[] buf = new byte[20480];
				int len;
				while ((len = inputStream.read(buf)) > 0) {
					bof.write(buf, 0, len);
				}
				bof.flush();
				
				uuidThmb = generateThumbnailIntoJCR(fileName, owner, tempFile);
				
				finputStream = new FileInputStream(tempFile);
				// jack rabbit new file
				newUuid = fileSystemDao.insertFile(owner.getLogin(), finputStream, size,
						fileName, mimeType);
				
			} else {
			
				// jack rabbit new file
				newUuid = fileSystemDao.insertFile(owner.getLogin(), inputStream, size,
						fileName, mimeType);
			}

			// delete old thumbnail in JCR
			String oldThumbUuid = aDoc.getThmbUUID();
			if (oldThumbUuid != null && oldThumbUuid.length() > 0) {
				fileSystemDao.removeFileByUUID(oldThumbUuid);
			}
			
			
			// update the document
			aDoc.setIdentifier(newUuid);
			aDoc.setName(fileName);
			aDoc.setType(mimeType);
			aDoc.setSize(size);
			aDoc.setThmbUUID(uuidThmb);
			aDoc.setEncrypted(encrypted);
			aDoc = documentRepository.update(aDoc);
			
			// remove old document in JCR
			fileSystemDao.removeFileByUUID(currentFileUUID);

			// do we need a log entry for update content ? (check
			// encrypt/decrypt)

			return aDoc;
		} catch (BusinessException e) {
			logger.error("Could not update file identifier" + currentFileUUID
					+ " to user " + owner.getLogin() + ", reason : ", e);
			if (newUuid != null)
				fileSystemDao.removeFileByUUID(newUuid);
			throw new TechnicalException(
					TechnicalErrorCode.COULD_NOT_UPDATE_DOCUMENT,
					"couldn't update the file content in the database");
		} catch (IOException e) {
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					e.getMessage());
		} finally {
			
			if(finputStream!=null)
				try {
					finputStream.close();
				} catch (IOException e) {}
		}

	}

	public void insertSignatureFile(InputStream file, long size,
			String fileName, String mimeType, User owner, Document aDoc,
			X509Certificate signerCertificate) throws BusinessException {

		// insert signature in jack rabbit
		String uuid = fileSystemDao.insertFile(owner.getLogin(), file, size,
				fileName, mimeType);
		// create signature in db
		Signature sign = new Signature(uuid, fileName, new GregorianCalendar(),
				owner, size, signerCertificate);
		// link it to document
		List<Signature> signatures = aDoc.getSignatures();
		signatures.add(sign);
		aDoc.setSignatures(signatures);

		documentRepository.update(aDoc);

		FileLogEntry logEntry = new FileLogEntry(owner.getMail(), owner
				.getFirstName(), owner.getLastName(), owner.getDomainId(), LogAction.FILE_SIGN,
				"signature of a file", aDoc.getName(), aDoc.getSize(), aDoc
						.getType());

		logEntryService.create(logEntry);
	}

	public long getAvailableSize(User user) throws BusinessException {
		
		//if user is not in one domain = BOUM
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(user.getDomain().getIdentifier());
		
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityService.getGlobalQuotaFunctionality(domain);
		SizeUnitValueFunctionality userQuotaFunctionality = functionalityService.getUserQuotaFunctionality(domain);
		
		
		if(globalQuotaFunctionality.getActivationPolicy().getStatus()) {
		
			long availableSize = globalQuotaFunctionality.getPlainSize() - domain.getUsedSpace().longValue();					
			if (availableSize < 0) {
				availableSize = 0;
			}
			return availableSize;
			
		} else if(userQuotaFunctionality.getActivationPolicy().getStatus()) {
				
			// use config parameter
			long userQuota = userQuotaFunctionality.getPlainSize();
	
			if ((user.getDocuments() == null) || (user.getDocuments().size() == 0)) {
				return userQuota;
			}
	
			for (Document userDoc : user.getDocuments()) {
				userQuota -= userDoc.getSize();
			}
			return userQuota;
		}
		return LinShareConstants.defaultFreeSpace;
	}
	
	public long getUserMaxFileSize(User user) throws BusinessException {
		
		//if user is not in one domain = BOUM
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(user.getDomain().getIdentifier());
		
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
	
	

	public long getTotalSize(User user) throws BusinessException {
		
		AbstractDomain domain = abstractDomainService.retrieveDomain(user.getDomain().getIdentifier());
		
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityService.getGlobalQuotaFunctionality(domain);
		SizeUnitValueFunctionality userQuotaFunctionality = functionalityService.getUserQuotaFunctionality(domain);
		
		if(globalQuotaFunctionality.getActivationPolicy().getStatus()) {
			return globalQuotaFunctionality.getPlainSize();
		}
		
		long userQuota = userQuotaFunctionality.getPlainSize();

		return userQuota;

	}

	public void deleteFile(String login, String identifier,
			Reason causeOfDeletion)
			throws BusinessException {
		deleteFileWithNotification(login, identifier, causeOfDeletion, null);
	}
	
	public void deleteFileWithNotification(String login, String uuid, Reason causeOfDeletion, MailContainer mailContainer)
			throws BusinessException {
		Document doc = documentRepository.findById(uuid);
		User owner = doc.getOwner();
		User actor = userRepository.findByLogin(login);
		if (null != doc) {
			try {

				shareService.deleteAllSharesWithDocument(doc, actor, mailContainer);
				
				String fileUUID = uuid;
				String thumbnailUUID = doc.getThmbUUID();
				long docSize = doc.getSize();

				owner.deleteDocument(doc);

				userRepository.update(owner);
				
				removeDocSizeFromGlobalUsedQuota(docSize, owner.getDomain());

				if (!Reason.INCONSISTENCY.equals(causeOfDeletion)) {
					// If the reason of the delete is inconsistency, the
					// document doesn't exist in file system.
					if (thumbnailUUID != null && thumbnailUUID.length()>0) {
						fileSystemDao.removeFileByUUID(thumbnailUUID);
					}
					fileSystemDao.removeFileByUUID(fileUUID);
				}

				FileLogEntry logEntry;
				int level = LogEntryService.INFO;

				if (Reason.EXPIRY.equals(causeOfDeletion)) {
					logEntry = new FileLogEntry(actor.getMail(), actor
							.getFirstName(), actor.getLastName(), actor.getDomainId(),
							LogAction.FILE_EXPIRE, "Expiration of a file", doc
									.getName(), doc.getSize(), doc.getType());
				} else if (Reason.INCONSISTENCY.equals(causeOfDeletion)) {
					logEntry = new FileLogEntry(actor.getMail(), actor
							.getFirstName(), actor.getLastName(), actor.getDomainId(),
							LogAction.FILE_INCONSISTENCY,
							"File removed because of inconsistence. "
									+ "Please contact your administrator.", doc
									.getName(), doc.getSize(), doc.getType());
					level = LogEntryService.WARN;
				} else {
					logEntry = new FileLogEntry(actor.getMail(), actor
							.getFirstName(), actor.getLastName(), actor.getDomainId(),
							LogAction.FILE_DELETE, "Deletion of a file", doc
									.getName(), doc.getSize(), doc.getType());
				}
				logEntryService.create(level, logEntry);

			} catch (IllegalArgumentException e) {
				logger.error("Could not delete file " + doc.getName()
						+ " of user " + owner.getLogin() + ", reason : ", e);
				throw new TechnicalException(
						TechnicalErrorCode.COULD_NOT_DELETE_DOCUMENT,
						"Could not delete document");
			}
		}
	}

	public Document getDocument(String uuid) {
		return documentRepository.findById(uuid);
	}

	public InputStream getDocumentThumbnail(String uuid) {
		Document doc = documentRepository.findById(uuid);
		String thmbUUID = doc.getThmbUUID();

		if (thmbUUID!=null && thmbUUID.length()>0) {
			InputStream stream = fileSystemDao.getFileContentByUUID(thmbUUID);
			return stream;
		}
		return null;
	}
	
	public boolean documentHasThumbnail(String uuid) {
		Document doc = documentRepository.findById(uuid);
		String thmbUUID = doc.getThmbUUID();

		return (thmbUUID!=null && thmbUUID.length()>0);
	}

	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor)
			throws BusinessException {
		if (doc instanceof ShareDocumentVo) {
			// this a a file we received
			User receiver = userRepository.findByMail(actor.getMail());
			if (receiver == null) {
				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
						"The user couldn't be found");
			}

			// must find the share MAIS NON
//			Set<Share> listShare = receiver.getReceivedShares();
//			Share shareToRetrieve = null;
//
//			for (Share share : listShare) {
//				if (share.getDocument().getIdentifier().equals(
//						doc.getIdentifier())) {
//					shareToRetrieve = share;
//					break;
//				}
//			}
			Share shareToRetrieve = shareTransformer.assemble((ShareDocumentVo) doc);

			if (shareToRetrieve == null) {
				throw new BusinessException(
						BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND,
						"The sharing couldn't be found");
			}

			return this.fileSystemDao.getFileContentByUUID(shareToRetrieve
					.getDocument().getIdentifier());

		} else {

			boolean test = false;
			User owner = userRepository.findByMail(actor.getMail());
			for (Document currentDocument : owner.getDocuments()) {
				if (currentDocument.getIdentifier().equals(doc.getIdentifier())) {
					test = true;
					break;
				}
			}

			if (test) {
				return this.fileSystemDao.getFileContentByUUID(doc
						.getIdentifier());
			} else {
				throw new BusinessException(BusinessErrorCode.INVALID_UUID,
						"Bad uuid for this user");
			}
		}
	}
	
	public InputStream downloadSharedDocument(ShareDocumentVo doc, UserVo actor)
			throws BusinessException {
		User receiver = userRepository.findByMail(actor.getMail());
		if (receiver == null) {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
					"The user couldn't be found");
		}
		Share shareToRetrieve = shareTransformer.assemble(doc);

		shareToRetrieve.setDownloaded(true);

		ShareLogEntry logEntry;

		logEntry = new ShareLogEntry(shareToRetrieve.getSender().getMail(),
				shareToRetrieve.getSender().getFirstName(), shareToRetrieve
						.getSender().getLastName(), shareToRetrieve.getSender().getDomainId(),
				LogAction.SHARE_DOWNLOAD, "Download of a sharing", doc
						.getFileName(), doc.getSize(), doc.getType(), actor
						.getMail(), actor.getFirstName(), actor
						.getLastName(), actor.getDomainIdentifier(), null);
		logEntryService.create(logEntry);
		
		return this.fileSystemDao.getFileContentByUUID(shareToRetrieve
				.getDocument().getIdentifier());
	}

	public InputStream retrieveFileStream(DocumentVo doc, String actor) {
		if (doc == null)
			throw new IllegalArgumentException("doc attribute is mandatory");
		return this.fileSystemDao.getFileContentByUUID(doc.getIdentifier());
	}

	public Document duplicateDocument(Document document, User user)
			throws BusinessException {
		InputStream stream = fileSystemDao.getFileContentByUUID(document
				.getIdentifier());
		return insertFile(user.getLogin(), stream, document.getSize(), document
				.getName(), document.getType(), user);
	}

	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc) {
		return this.fileSystemDao.getFileContentByUUID(signaturedoc
				.getIdentifier());
	}

	public Document updateDocumentContent(String currentFileUUID,
			InputStream file, long size, String fileName, String mimeType,
			User owner) throws BusinessException {
		
		
		Document currentDoc = documentRepository.findById(currentFileUUID);
		
		String logText = currentDoc.getName(); //old name of the doc
		
		Document doc = updateFileContent(currentFileUUID, file, size, fileName, mimeType, false, owner);
		

		//clean all signatures ...
		List<Signature> signatures = doc.getSignatures();
		signatures.clear();
		documentRepository.update(doc);
		
		//put new file name in log
		//if the file is updated/replaced with a new file (new file name)
		//put new file name in log
		if (!logText.equalsIgnoreCase(doc.getName())) {
			logText = doc.getName() + " [" +logText+"]";
		}	
		
		FileLogEntry logEntry = new FileLogEntry(owner.getMail(), owner.getFirstName(), owner.getLastName(),
				owner.getDomainId(), LogAction.FILE_UPDATE, "Update of a file", logText, doc.getSize(), doc.getType());
		
		logEntryService.create(logEntry);

		return doc;
		
	}

    public void renameFile(String uuid, String newName) {
        fileSystemDao.renameFile(uuid, newName);
        Document document = documentRepository.findById(uuid);
        document.setName(newName);
        try {
			documentRepository.update(document);
		} catch (IllegalArgumentException e) {
			logger.error("can't rename a document");
			e.printStackTrace();
		} catch (BusinessException e) {
			logger.error("can't rename a document");
			e.printStackTrace();
		}
    }
    
    public void updateFileProperties(String uuid, String newName, String fileComment){
    	
    	Document document = documentRepository.findById(uuid);
    	
    	if (newName!=null){
            fileSystemDao.renameFile(uuid, newName);
            document.setName(newName);
    	}
        
    	document.setFileComment(fileComment);
    }

	@Override
	public boolean isSignatureActive(User user) {
		return functionalityService.getSignatureFunctionality(user.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isEnciphermentActive(User user) {
		return functionalityService.getEnciphermentFunctionality(user.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isGlobalQuotaActive(User user) throws BusinessException {
		return functionalityService.getGlobalQuotaFunctionality(user.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public boolean isUserQuotaActive(User user) throws BusinessException {
		return functionalityService.getUserQuotaFunctionality(user.getDomain()).getActivationPolicy().getStatus();
	}

	@Override
	public Long getGlobalQuota(User user) throws BusinessException {
		SizeUnitValueFunctionality globalQuotaFunctionality = functionalityService.getGlobalQuotaFunctionality(user.getDomain());
		return globalQuotaFunctionality.getPlainSize();
	}
}
