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
package org.linagora.linshare.core.service.impl;

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
import org.linagora.linshare.core.dao.FileSystemDao;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.constants.Reason;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AntivirusLogEntry;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.FileLogEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.LogEntry;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.MimeTypeStatus;
import org.linagora.linshare.core.domain.entities.Share;
import org.linagora.linshare.core.domain.entities.ShareLogEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.StringValueFunctionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.SizeUnitValueFunctionality;
import org.linagora.linshare.core.domain.transformers.impl.ShareTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.DocumentRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.DocumentService;
import org.linagora.linshare.core.service.FunctionalityService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.MimeTypeService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.TimeStampingService;
import org.linagora.linshare.core.service.VirusScannerService;
import org.linagora.linshare.core.utils.AESCrypt;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DocumentServiceImpl implements DocumentService {

	@Override
	public String getMimeType(InputStream theFileStream, String theFilePath) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document insertFile(String path, InputStream file, long size, String fileName, String mimeType, User owner) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document updateFileContent(String currentFileUUID, InputStream file, long size, String fileName, String mimeType, boolean encrypted, User owner) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Document updateDocumentContent(String currentFileUUID, InputStream file, long size, String fileName, String mimeType, User owner) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getAvailableSize(User user) throws BusinessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getUserMaxFileSize(User user) throws BusinessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getTotalSize(User user) throws BusinessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteFile(String login, String uuid, Reason causeOfDeletion) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFileWithNotification(String login, String identifier, Reason causeOfDeletion, MailContainer mailContainer) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Document getDocument(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream downloadSharedDocument(ShareDocumentVo doc, UserVo actor) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream retrieveFileStream(DocumentVo doc, String actor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void insertSignatureFile(InputStream file, long size, String fileName, String mimeType, User owner, Document aDoc, X509Certificate signerCertificate) throws BusinessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Document duplicateDocument(Document document, User user) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void renameFile(String uuid, String newName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFileProperties(String uuid, String newName, String fileComment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public InputStream getDocumentThumbnail(String uuid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean documentHasThumbnail(String uuid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSignatureActive(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnciphermentActive(User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isGlobalQuotaActive(User user) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUserQuotaActive(User user) throws BusinessException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Long getGlobalQuota(User user) throws BusinessException {
		// TODO Auto-generated method stub
		return null;
	}
	

	
	



	
	
	
	
	

	

//				shareService.deleteAllSharesWithDocument(doc, actor, mailContainer);

	

//	
//
//	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor)
//			throws BusinessException {
//		if (doc instanceof ShareDocumentVo) {
//			// this a a file we received
//			User receiver = userRepository.findByMail(actor.getMail());
//			if (receiver == null) {
//				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
//						"The user couldn't be found");
//			}
//
//			// must find the share MAIS NON
////			Set<Share> listShare = receiver.getReceivedShares();
////			Share shareToRetrieve = null;
////
////			for (Share share : listShare) {
////				if (share.getDocument().getIdentifier().equals(
////						doc.getIdentifier())) {
////					shareToRetrieve = share;
////					break;
////				}
////			}
//			Share shareToRetrieve = shareTransformer.assemble((ShareDocumentVo) doc);
//
//			if (shareToRetrieve == null) {
//				throw new BusinessException(
//						BusinessErrorCode.SHARED_DOCUMENT_NOT_FOUND,
//						"The sharing couldn't be found");
//			}
//
//			return this.fileSystemDao.getFileContentByUUID(shareToRetrieve
//					.getDocument().getIdentifier());
//
//		} else {
//
//			boolean test = false;
//			User owner = userRepository.findByMail(actor.getMail());
//			for (Document currentDocument : owner.getDocuments()) {
//				if (currentDocument.getIdentifier().equals(doc.getIdentifier())) {
//					test = true;
//					break;
//				}
//			}
//
//			if (test) {
//				return this.fileSystemDao.getFileContentByUUID(doc
//						.getIdentifier());
//			} else {
//				throw new BusinessException(BusinessErrorCode.INVALID_UUID,
//						"Bad uuid for this user");
//			}
//		}
//	}
	
//	public InputStream downloadSharedDocument(ShareDocumentVo doc, UserVo actor)
//			throws BusinessException {
//		User receiver = userRepository.findByMail(actor.getMail());
//		if (receiver == null) {
//			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND,
//					"The user couldn't be found");
//		}
//		Share shareToRetrieve = shareTransformer.assemble(doc);
//
//		shareToRetrieve.setDownloaded(true);
//
//		ShareLogEntry logEntry;
//
//		logEntry = new ShareLogEntry(shareToRetrieve.getSender().getMail(),
//				shareToRetrieve.getSender().getFirstName(), shareToRetrieve
//						.getSender().getLastName(), shareToRetrieve.getSender().getDomainId(),
//				LogAction.SHARE_DOWNLOAD, "Download of a sharing", doc
//						.getFileName(), doc.getSize(), doc.getType(), actor
//						.getMail(), actor.getFirstName(), actor
//						.getLastName(), actor.getDomainIdentifier(), null);
//		logEntryService.create(logEntry);
//		
//		return this.fileSystemDao.getFileContentByUUID(shareToRetrieve
//				.getDocument().getIdentifier());
//	}

//	public InputStream retrieveFileStream(DocumentVo doc, String actor) {
//		if (doc == null)
//			throw new IllegalArgumentException("doc attribute is mandatory");
//		return this.fileSystemDao.getFileContentByUUID(doc.getIdentifier());
//	}

//	public Document duplicateDocument(Document document, User user)
//			throws BusinessException {
//		InputStream stream = fileSystemDao.getFileContentByUUID(document
//				.getIdentifier());
//		return insertFile(user.getLogin(), stream, document.getSize(), document
//				.getName(), document.getType(), user);
//	}
//
//	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc) {
//		return this.fileSystemDao.getFileContentByUUID(signaturedoc
//				.getIdentifier());
//	}


	
}
