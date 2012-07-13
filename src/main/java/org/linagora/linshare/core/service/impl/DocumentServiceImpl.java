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

import java.io.InputStream;
import java.security.cert.X509Certificate;

import org.linagora.linshare.core.domain.constants.Reason;
import org.linagora.linshare.core.domain.entities.Document;
import org.linagora.linshare.core.domain.entities.MailContainer;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.service.DocumentService;


public class DocumentServiceImpl implements DocumentService {


	@Override
	public Document updateFileContent(String currentFileUUID, InputStream file, long size, String fileName, String mimeType, boolean encrypted, User owner) throws BusinessException {
		
		return null;
	}

	@Override
	public Document getDocument(String uuid) {
		
		return null;
	}

	@Override
	public InputStream retrieveFileStream(DocumentVo doc, UserVo actor) throws BusinessException {
		
		return null;
	}

	@Override
	public InputStream retrieveSignatureFileStream(SignatureVo signaturedoc) {
		
		return null;
	}

	@Override
	public InputStream retrieveFileStream(DocumentVo doc, String actor) {
		
		return null;
	}

	@Override
	public void insertSignatureFile(InputStream file, long size, String fileName, String mimeType, User owner, Document aDoc, X509Certificate signerCertificate) throws BusinessException {
		
		
	}

	@Override
	public Document duplicateDocument(Document document, User user) throws BusinessException {
		
		return null;
	}

	@Override
	public void renameFile(String uuid, String newName) {
		
		
	}

	@Override
	public void updateFileProperties(String uuid, String newName, String fileComment) {
		
		
	}

	@Override
	public InputStream getDocumentThumbnail(String uuid) {
		
		return null;
	}

	@Override
	public boolean documentHasThumbnail(String uuid) {
		
		return false;
	}

	@Override
	public boolean isSignatureActive(User user) {
		
		return false;
	}

	@Override
	public boolean isEnciphermentActive(User user) {
		
		return false;
	}

	@Override
	public boolean isGlobalQuotaActive(User user) throws BusinessException {
		
		return false;
	}

	@Override
	public boolean isUserQuotaActive(User user) throws BusinessException {
		
		return false;
	}

	@Override
	public Long getGlobalQuota(User user) throws BusinessException {
		
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
