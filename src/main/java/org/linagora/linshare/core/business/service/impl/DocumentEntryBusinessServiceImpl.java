package org.linagora.linshare.core.business.service.impl;

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

import javax.imageio.ImageIO;

import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampResponse;
import org.linagora.LinThumbnail.FileResource;
import org.linagora.LinThumbnail.FileResourceFactory;
import org.linagora.LinThumbnail.utils.Constants;
import org.linagora.LinThumbnail.utils.ImageUtils;
import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.DocumentEntryRepository;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentEntryBusinessServiceImpl implements DocumentEntryBusinessService {
	
	
	private static final Logger logger = LoggerFactory.getLogger(DocumentEntryBusinessServiceImpl.class);

	private final MimeTypeIdentifier mimeTypeIdentifier;
	private final FileSystemDao fileSystemDao;
	private final TimeStampingService timeStampingService;
	private final DocumentEntryRepository documentEntryRepository;
	private final DocumentRepository documentRepository;
	private final AccountRepository<Account> accountRepository; 
	
	
	public DocumentEntryBusinessServiceImpl(MimeTypeIdentifier mimeTypeIdentifier, FileSystemDao fileSystemDao, TimeStampingService timeStampingService, DocumentEntryRepository documentEntryRepository, DocumentRepository documentRepository, AccountRepository<Account> accountRepository) {
		super();
		this.mimeTypeIdentifier = mimeTypeIdentifier;
		this.fileSystemDao = fileSystemDao;
		this.timeStampingService = timeStampingService;
		this.documentEntryRepository = documentEntryRepository;
		this.documentRepository = documentRepository;
		this.accountRepository = accountRepository;
	}

	
	@Override
	public String getMimeType(InputStream theFileStream)
			throws BusinessException {
		byte[] bytes;
		try {
			bytes = IOUtil.readBytes(theFileStream, mimeTypeIdentifier
					.getMinArrayLength());
		} catch (IOException e) {
			logger.error("Could not read the uploaded file !", e);
			throw new BusinessException(BusinessErrorCode.MIME_NOT_FOUND, "Could not read the uploaded file.");
		}

		// let the MimeTypeIdentifier determine the MIME type of this file
		return mimeTypeIdentifier.identify(bytes, null, null);
	}
	

	@Override
	public DocumentEntry createDocumentEntry(Account owner, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException {
		
		//create and insert the thumbnail into the JCR
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner.getLsUid(), myFile);
		
		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUid(), myFile);
		
		// want a timestamp on doc ?
		byte[] timestampToken = null;
		if(timeStampingUrl != null) {
			timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
		}
				
				
		// add an entry for the file in DB
		DocumentEntry entity = null;
		try {
			Document document = new Document(uuid, mimeType, size);
			document.setThmbUuid(uuidThmb);
			document.setTimeStamp(timestampToken);
			documentRepository.create(document);

			DocumentEntry docEntry = new DocumentEntry(owner, fileName, document);

			//aes encrypt ? check headers
			if(checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			entity = (DocumentEntry) documentEntryRepository.create(docEntry);
			
			owner.getEntries().add(entity);
			accountRepository.update(owner);
			

		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUid() + ", reason : ", e);
			fileSystemDao.removeFileByUUID(uuid);
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
	public File getFileFromStream(InputStream stream, String fileName) {
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
				logger.debug("createTempFile:" + tempFile);
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
		return tempFile;
	}
	
	
	@Override
	public InputStream getDocumentThumbnail(String uuid) {
		Document doc = documentRepository.findById(uuid);
		String thmbUUID = doc.getThmbUuid();

		if (thmbUUID!=null && thmbUUID.length()>0) {
			InputStream stream = fileSystemDao.getFileContentByUUID(thmbUUID);
			return stream;
		}
		return null;
	}
	
	
	@Override
	public InputStream getDocument(String uuid) {
		Document doc = documentRepository.findById(uuid);
		String UUID = doc.getIdentifier();

		if (UUID!=null && UUID.length()>0) {
			InputStream stream = fileSystemDao.getFileContentByUUID(UUID);
			return stream;
		}
		return null;
	}


	@Override
	public DocumentEntry findById(Long id) {
		return (DocumentEntry)documentEntryRepository.findById(id);
	}

	
	@Override
	public void renameDocumentEntry(DocumentEntry entry, String newName) throws BusinessException {
		String uuid = entry.getDocument().getIdentifier();
		fileSystemDao.renameFile(uuid, newName);
		entry.setName(newName);
        documentEntryRepository.update(entry);
	}
	
	
	@Override
	public void updateFileProperties(DocumentEntry entry, String newName, String fileComment) throws BusinessException {
			String uuid = entry.getDocument().getIdentifier();
			fileSystemDao.renameFile(uuid, newName);
			entry.setName(newName);
			entry.setComment(fileComment);
	        documentEntryRepository.update(entry);
	        
	}

	
	@Override
	public DocumentEntry updateDocumentEntry(Account owner, DocumentEntry docEntry, File myFile, Long size, String fileName, Boolean checkIfIsCiphered, String timeStampingUrl, String mimeType) throws BusinessException {
		
		//create and insert the thumbnail into the JCR
		String uuidThmb = generateThumbnailIntoJCR(fileName, owner.getLsUid(), myFile);
		
		String uuid = insertIntoJCR(size, fileName, mimeType, owner.getLsUid(), myFile);
		
		Document oldDocument = documentRepository.findById(docEntry.getDocument().getIdentifier());
		
		// want a timestamp on doc ? if timeStamping url is null, time stamp will be null
		byte[] timestampToken = getTimeStamp(fileName, myFile, timeStampingUrl);
		
		try {
			
			Document document = new Document(uuid, mimeType, size);
			document.setThmbUuid(uuidThmb);
			
			document.setTimeStamp(timestampToken);
			documentRepository.create(document);
			
			
			docEntry.setName(fileName);
			docEntry.setDocument(document);
			
			//aes encrypt ? check headers
			if(checkIfIsCiphered) {
				docEntry.setCiphered(checkIfFileIsCiphered(fileName, myFile));
			}
			documentEntryRepository.update(docEntry);
			deleteDocument(oldDocument);
			
			return docEntry;
		} catch (BusinessException e) {
			logger.error("Could not add  " + fileName + " to user " + owner.getLsUid() + ", reason : ", e);
			fileSystemDao.removeFileByUUID(uuid);
			throw new TechnicalException(TechnicalErrorCode.COULD_NOT_INSERT_DOCUMENT, "couldn't register the file in the database");
		}
	}
		
	
	@Override
	public DocumentEntry duplicateDocumentEntry(DocumentEntry originalEntry, Account owner, String timeStampingUrl ) throws BusinessException {
		InputStream stream = getDocument(originalEntry.getDocument().getIdentifier());
		File tempFile = getFileFromStream(stream, originalEntry.getName());
		
		DocumentEntry documentEntry = createDocumentEntry(owner, tempFile , originalEntry.getDocument().getSize(), 
				originalEntry.getName(), originalEntry.getCiphered(), timeStampingUrl, originalEntry.getDocument().getType());
		tempFile.delete(); // remove the temporary file
		
		return documentEntry;
	}

	
	@Override
	public void deleteDocumentEntry(DocumentEntry documentEntry) throws BusinessException {
		if(documentEntry.isShared()) {
			logger.error("Could not delete docEntry " + documentEntry.getName()+ " (" + documentEntry.getId() + " own by " + documentEntry.getEntryOwner().getLsUid() + ", reason : it is still shared. ");
			throw new BusinessException(BusinessErrorCode.CANNOT_DELETE_SHARED_DOCUMENT, "Can't delete a shared document. Delete all shares first.");
		}
		Account owner = documentEntry.getEntryOwner();
		owner.getEntries().remove(documentEntry);
		accountRepository.update(owner);

		deleteDocument(documentEntry.getDocument());
		documentEntryRepository.delete(documentEntry);
//		actor
//		
	}

	
	private void deleteDocument(Document document) throws BusinessException {
		// delete old thumbnail in JCR
		String oldThumbUuid = document.getThmbUuid(); 
		if (oldThumbUuid != null && oldThumbUuid.length() > 0) {
			fileSystemDao.removeFileByUUID(oldThumbUuid);
		}
		
		// remove old document in JCR
		fileSystemDao.removeFileByUUID(document.getIdentifier());
		
		// remove old document from database
		documentRepository.delete(document);
	}

	
	private String generateThumbnailIntoJCR(String fileName, String path,
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
				logger.debug("insert of the document in jack rabbit:" + fileName);
			}

			uuid = fileSystemDao.insertFile(path, fis, size, fileName, mimeType);
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
	
//
//	public void insertSignatureFile(InputStream file, long size,
//			String fileName, String mimeType, User owner, Document aDoc,
//			X509Certificate signerCertificate) throws BusinessException {
//
//		// insert signature in jack rabbit
//		String uuid = fileSystemDao.insertFile(owner.getLogin(), null, file,
//				size, mimeType);
//		// create signature in db
//		Signature sign = new Signature(uuid, fileName, new GregorianCalendar(),
//				owner, size, signerCertificate);
//		// link it to document
//		List<Signature> signatures = aDoc.getSignatures();
//		signatures.add(sign);
//		aDoc.setSignatures(signatures);
//
//		documentRepository.update(aDoc);
//
//		FileLogEntry logEntry = new FileLogEntry(owner.getMail(), owner
//				.getFirstName(), owner.getLastName(), owner.getDomainId(), LogAction.FILE_SIGN,
//				"signature of a file", aDoc.getName(), aDoc.getSize(), aDoc
//						.getType());
//
//		logEntryService.create(logEntry);
//	}
}
