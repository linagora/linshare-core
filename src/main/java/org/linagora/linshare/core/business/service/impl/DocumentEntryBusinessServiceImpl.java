package org.linagora.linshare.core.business.service.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
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
import org.linagora.linshare.core.business.service.TagBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.User;
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
import org.linagora.linshare.core.utils.DocumentUtils;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentEntryBusinessServiceImpl implements DocumentEntryBusinessService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImpl.class);

	private final MimeTypeIdentifier mimeTypeIdentifier;
	private final FileSystemDao fileSystemDao;
	private final TimeStampingService timeStampingService;
	private final DocumentEntryRepository documentEntryRepository;
	private final ThreadEntryRepository threadEntryRepository;
	private final DocumentRepository documentRepository;
	private final AccountRepository<Account> accountRepository; 
	private final SignatureBusinessService signatureBusinessService;
	private final TagBusinessService tagBusinessService;
	
	
	public DocumentEntryBusinessServiceImpl(FileSystemDao fileSystemDao, TimeStampingService timeStampingService, DocumentEntryRepository documentEntryRepository, DocumentRepository documentRepository, 
			AccountRepository<Account> accountRepository, SignatureBusinessService signatureBusinessService, ThreadEntryRepository threadEntryRepository, TagBusinessService tagBusinessService) {
		super();
		this.mimeTypeIdentifier = new MagicMimeTypeIdentifier();
		this.fileSystemDao = fileSystemDao;
		this.timeStampingService = timeStampingService;
		this.documentEntryRepository = documentEntryRepository;
		this.documentRepository = documentRepository;
		this.accountRepository = accountRepository;
		this.signatureBusinessService = signatureBusinessService;
		this.threadEntryRepository = threadEntryRepository;
		this.tagBusinessService = tagBusinessService;
	}

	
	@Override
	public String getMimeType(BufferedInputStream theFileStream) throws BusinessException {
		byte[] bytes;
		theFileStream.mark(mimeTypeIdentifier.getMinArrayLength()+1);
		try {
			bytes = IOUtil.readBytes(theFileStream, mimeTypeIdentifier.getMinArrayLength());
			theFileStream.reset();
		} catch (IOException e) {
			logger.error("Could not read the uploaded file !", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND, "Could not read the uploaded file.");
		}

		// let the MimeTypeIdentifier determine the MIME type of this file
		String mimeType = mimeTypeIdentifier.identify(bytes, null, null);
		logger.debug("Mime type found : " + mimeType);
		if(mimeType == null) {
			mimeType = "data";
		}
		return mimeType;
	}
	
	
	@Override
	public DocumentEntry createDocumentEntry(Account owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException {
		
		// add an entry for the file in DB
		DocumentEntry entity = null;
		try {
			Document document = createDocument(owner, myFile, size, fileName, timeStampingUrl, mimeType);

			DocumentEntry docEntry = new DocumentEntry(owner, fileName, document);
			// We need to set an expiration date in case of file cleaner activation.
			docEntry.setExpirationDate(expirationDate);

			//aes encrypt ? check headers
			if(checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			entity = (DocumentEntry) documentEntryRepository.create(docEntry);
			
			owner.getEntries().add(entity);
			accountRepository.update(owner);
			

		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "couldn't register the file in the database");
		}
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
	public DocumentEntry findById(String docEntryUuid) {
		return documentEntryRepository.findById(docEntryUuid);
	}

	
	@Override
	public List<DocumentEntry> findAllMyDocumentEntries(User owner) {
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
	public void updateFileProperties(DocumentEntry entry, String newName, String fileComment) throws BusinessException {
			String uuid = entry.getDocument().getUuid();
			fileSystemDao.renameFile(uuid, newName);
			entry.setName(newName);
			entry.setComment(fileComment);
	        documentEntryRepository.update(entry);
	}

	
	@Override
	public DocumentEntry updateDocumentEntry(Account owner, DocumentEntry docEntry, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType, Calendar expirationDate) throws BusinessException {
		
		//create and insert the thumbnail into the JCR
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner.getLsUuid(), myFile);
		
		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUuid(), myFile);
		
		Document oldDocument = documentRepository.findByUuid(docEntry.getDocument().getUuid());
		
		// want a timestamp on doc ? if timeStamping url is null, time stamp will be null
		byte[] timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
		
		try {
			
			Document document = new Document(uuid, mimeType, size);
			document.setThmbUuid(uuidThmb);
			
			document.setTimeStamp(timestampToken);
			documentRepository.create(document);
			
			
			docEntry.setName(fileName);
			docEntry.setDocument(document);
			docEntry.setExpirationDate(expirationDate);
			
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
	public DocumentEntry duplicateDocumentEntry(DocumentEntry originalEntry, Account owner, String timeStampingUrl, Calendar expirationDate ) throws BusinessException {
		InputStream stream = getDocumentStream(originalEntry);
		BufferedInputStream bufStream = new BufferedInputStream(stream);
		
		DocumentUtils util = new DocumentUtils();
		File tempFile = util.getFileFromBufferedInputStream(bufStream, originalEntry.getName());
		
		DocumentEntry documentEntry = createDocumentEntry(owner, tempFile , originalEntry.getDocument().getSize(), originalEntry.getName(), 
				originalEntry.getCiphered(), timeStampingUrl, originalEntry.getDocument().getType(), expirationDate);
		tempFile.delete(); // remove the temporary file
		
		return documentEntry;
	}

	
	@Override
	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException {
		if(documentEntry.isShared()) {
			logger.error("Could not delete docEntry " + documentEntry.getName()+ " (" + documentEntry.getUuid() + " own by " + documentEntry.getEntryOwner().getLsUuid() + ", reason : it is still shared. ");
			throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_SHARED_DOCUMENT, "Can't delete a shared document. Delete all shares first.");
		}
		Account owner = documentEntry.getEntryOwner();
		owner.getEntries().remove(documentEntry);
		accountRepository.update(owner);

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
		try {
			Document document = createDocument(owner, myFile, size, fileName, timeStampingUrl, mimeType);
			ThreadEntry docEntry = new ThreadEntry(owner, fileName, document);

			//aes encrypt ? check headers
			if(checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			entity = (ThreadEntry) threadEntryRepository.create(docEntry);
			
			owner.getEntries().add(entity);
			accountRepository.update(owner);
			

		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUuid() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "couldn't register the file in the database");
		}
		return entity;
	}
	
	
	@Override
	public ThreadEntry findThreadEntryById(String docEntryUuid) {
		return (ThreadEntry)threadEntryRepository.findByUuid(docEntryUuid);
	}


	@Override
	public List<ThreadEntry> findAllThreadEntries(Thread owner) {
		return threadEntryRepository.findAllThreadEntries(owner);
	}

	@Override
	public List<ThreadEntry> findAllThreadEntriesTaggedWith(Thread owner, String[] names) {
		return threadEntryRepository.findAllThreadEntriesTaggedWith(owner, names);
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
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner.getLsUuid(), myFile);
			
		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUuid(), myFile);
			
		try {
			// want a timestamp on doc ?
			byte[] timestampToken = null;
			if(timeStampingUrl != null) {
				timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
			}
					
			// add an document for the file in DB
			Document document = new Document(uuid, mimeType, size);
			document.setThmbUuid(uuidThmb);
			document.setTimeStamp(timestampToken);
			documentRepository.create(document);
			return document;
		} catch (BusinessException e) {
			fileSystemDao.removeFileByUUID(uuid);
			fileSystemDao.removeFileByUUID(uuidThmb);
			throw e;
		}
	}



	
	private void deleteDocument(Document document) throws BusinessException {
		// delete old thumbnail in JCR
		String oldThumbUuid = document.getThmbUuid(); 
		if (oldThumbUuid != null && oldThumbUuid.length() > 0) {
			logger.debug("suppresion of Thumb, Uuid : " + oldThumbUuid);
			fileSystemDao.removeFileByUUID(oldThumbUuid);
		}
		
		// remove old document in JCR
		logger.debug("suppresion of doc, Uuid : " + document.getUuid());
		fileSystemDao.removeFileByUUID(document.getUuid());
		
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

	
	private String generateThumbnailIntoJCR(String fileName, String path, File tempFile) {
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
					
					uuidThmb = fileSystemDao.insertFile(path, fisThmb, tempThumbFile.length(),
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
		tagBusinessService.deleteAllTagAssociationsFromThreadEntry(threadEntry);
		threadEntry.setTagAssociations(null);
		threadEntryRepository.update(threadEntry);
		threadEntryRepository.delete(threadEntry);
		doc.setThreadEntry(null);
		documentRepository.update(doc);
		deleteDocument(doc);
	}	
}
