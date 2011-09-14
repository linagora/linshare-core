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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.linagora.linShare.core.domain.LogAction;
import org.linagora.linShare.core.domain.entities.Contact;
import org.linagora.linShare.core.domain.entities.Document;
import org.linagora.linShare.core.domain.entities.SecuredUrl;
import org.linagora.linShare.core.domain.entities.ShareLogEntry;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.vo.DocumentVo;
import org.linagora.linShare.core.exception.BusinessErrorCode;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.LinShareNotSuchElementException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.LogEntryRepository;
import org.linagora.linShare.core.repository.SecuredUrlRepository;
import org.linagora.linShare.core.service.LogEntryService;
import org.linagora.linShare.core.service.SecuredUrlService;
import org.linagora.linShare.core.utils.HashUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SecuredUrlServiceImpl implements SecuredUrlService {

	private final SecuredUrlRepository securedUrlRepository;
	private final ShareExpiryDateServiceImpl shareExpiryDateService;
	private final LogEntryService logEntryService;

	private final String baseSecuredUrl;

	private static final Logger logger = LoggerFactory
	.getLogger(SecuredUrlServiceImpl.class);

	public SecuredUrlServiceImpl(final SecuredUrlRepository securedUrlRepository,
			final ShareExpiryDateServiceImpl shareExpiryDateService, String pageName,
			final LogEntryService logEntryService) {
		this.securedUrlRepository = securedUrlRepository;
		this.shareExpiryDateService = shareExpiryDateService;

		this.baseSecuredUrl = pageName;
		this.logEntryService = logEntryService;
	}

	protected String getBaseSecuredUrl() {
		return baseSecuredUrl;
	}

	protected SecuredUrl getSecuredUrl(String shareId, String url)
	throws LinShareNotSuchElementException {
		return securedUrlRepository.find(shareId, url);
	}

	protected String generateAlea() {

		SecureRandom sr = null;
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			throw new TechnicalException("Algorithm \"SHA1PRNG\" not supported");
		}

		return Long.toString(sr.nextLong() & Long.MAX_VALUE, 36);
	}

	public SecuredUrl create(List<Document> documents, User sender, String password,List<Contact> recipients, Calendar expiryDate) {
		return create(documents, sender, password, null,recipients, expiryDate);
	}

	public SecuredUrl create(List<Document> documents, User sender, String password,
			String urlPath,List<Contact> recipients, Calendar expiryDate) {
		// Generate an alea
		String alea = generateAlea();

		// Get the defaultUrl if urlPath is null
		String url = urlPath == null ? getBaseSecuredUrl() : urlPath;

		// If the user have not selected an expiration date, compute default date
		
		// create the sercured url
		SecuredUrl securedUrl = new SecuredUrl(url, alea,
				expiryDate!=null? expiryDate :
				shareExpiryDateService.computeMinShareExpiryDateOfList(documents, sender)
				, sender, recipients);
		securedUrl.addDocuments(documents);

		// Hash the password
		if (password != null) {
			String hashedPassword = HashUtils.hashSha1withBase64(password
					.getBytes());
			securedUrl.setPassword(hashedPassword);
		}
		// save the securedurl
		try {
			securedUrl = securedUrlRepository.create(securedUrl);
		} catch (BusinessException e) {
			logger.error("Impossible to create secure url : " + e.toString());
		}

		return securedUrl;
	}

	public void delete(String alea, String urlPath) {
		try {
			SecuredUrl securedUrl = getSecuredUrl(alea, urlPath);
			securedUrlRepository.delete(securedUrl);
		} catch (BusinessException e) {
			logger.warn("Impossible to delete securedUrl :" + e.toString());
		}

	}

	public Document getDocument(String alea, String url, Integer documentId)
	throws BusinessException {
		return getDocument(alea, url, null, documentId);
	}

	public Document getDocument(String alea, String urlPath, String password,
			Integer documentId) throws BusinessException {
		List<Document> documents = getDocuments(alea, urlPath, password);
		if (documents.size() -1 < documentId.intValue()) {
			throw new BusinessException(BusinessErrorCode.SECURED_URL_WRONG_DOCUMENT_ID,"wrong document id");
		}
		return documents.get(documentId.intValue());
	}

	public List<Document> getDocuments(String alea, String urlPath)
	throws BusinessException {
		return getDocuments(alea, urlPath, null);
	}

	public List<Document> getDocuments(String alea, String urlPath,
			String password) throws BusinessException {
		SecuredUrl securedUrl = getSecuredUrl(alea, urlPath);
		checkIfValid(securedUrl, password);

		if (securedUrl.getDocuments() == null) {
			throw new BusinessException(BusinessErrorCode.SECURED_URL_WRONG_DOCUMENT_ID,"No documents in securedUrl");			
		}
		return securedUrl.getDocuments();
	}

	protected boolean isValidPassword(SecuredUrl securedUrl, String password) {
		if (securedUrl == null)
			throw new IllegalArgumentException("secured url cannot be null");

		// Check password validity
		if (password != null) {
			String hashedPassword = HashUtils.hashSha1withBase64(password
					.getBytes());
			return hashedPassword.equals(securedUrl.getPassword());
		}
		return true;
	}

	protected boolean isExpired(SecuredUrl securedUrl) {
		if (securedUrl == null)
			throw new IllegalArgumentException("secured url cannot be null");

		return securedUrl.getExpirationTime().before(Calendar.getInstance());
	}

	protected void checkIfValid(SecuredUrl securedUrl, String password)
	throws BusinessException {
		if (securedUrl == null)
			throw new IllegalArgumentException("secured url cannot be null");
		if (isExpired(securedUrl)) {
			throw new BusinessException(BusinessErrorCode.SECURED_URL_IS_EXPIRED, "the secured url is expired");
		}
		if (!isValidPassword(securedUrl, password)) {
			throw new BusinessException(BusinessErrorCode.SECURED_URL_BAD_PASSWORD, "Bad password for secured url");
		}

	}

	public boolean isValid(String alea, String urlPath) {
		return isValid(alea, urlPath, null);
	}

	public boolean isValid(String alea, String urlPath, String password) {
		SecuredUrl securedUrl = null;
		try {
			securedUrl = getSecuredUrl(alea, urlPath);
		} catch (LinShareNotSuchElementException e) {
			return false;
		}
		return (isValidPassword(securedUrl, password) && !isExpired(securedUrl));
	}

	public boolean isPasswordProtected(String alea, String urlPath)
	throws LinShareNotSuchElementException {
		SecuredUrl securedUrl = getSecuredUrl(alea, urlPath);
		return !StringUtils.isEmpty(securedUrl.getPassword());
	}

	public void removeOutdatedSecuredUrl() {
		List<SecuredUrl> securedUrlList = securedUrlRepository
		.getOutdatedSecuredUrl();
		logger.info(securedUrlList.size()
				+ " expired secured url(s) found to be delete.");

		for (SecuredUrl securedUrl : securedUrlList) {
			delete(securedUrl.getAlea(), securedUrl.getUrlPath());
		}

	}

	public boolean exists(String alea, String urlPath) {
		try {
			getSecuredUrl(alea, urlPath);
			return true;
		} catch (LinShareNotSuchElementException e) {
			return false;
		}
	}

	public void logDownloadedDocument(String alea, String urlPath, String password,
			Integer documentId, String email) {
		SecuredUrl securedUrl;
		try {
			securedUrl = getSecuredUrl(alea, urlPath);

			checkIfValid(securedUrl, password);
		} catch (LinShareNotSuchElementException e) {
			throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not find the secured url");
		} catch (BusinessException e) {
			throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not find the secured url");
		}
		if (securedUrl.getDocuments() == null) {
			throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "No documents associated");
		}
		User owner = securedUrl.getSender();

		// if it's one document, we log it
		if (documentId != null) {
			Document docEntity = securedUrl.getDocuments().get(documentId);

			ShareLogEntry logEntry;
			
			
			logEntry = new ShareLogEntry(owner.getMail(), owner
				.getFirstName(), owner.getLastName(), owner.getDomainId(),
				LogAction.ANONYMOUS_SHARE_DOWNLOAD, "Anonymous download of a file", docEntity
				.getName(), docEntity.getSize(), docEntity
				.getType(), email!=null?email:"" , "", "" , "",null);
			
			try {
				logEntryService.create(logEntry);
			} catch (IllegalArgumentException e) {
				throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not log the action" + e);
			} catch (BusinessException e) {
				throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not log the action" + e);
			}
		} else {
			//it's all the file at once
			for (Document docEntity : securedUrl.getDocuments()) {
				ShareLogEntry logEntry = new ShareLogEntry(owner.getMail(), owner
						.getFirstName(), owner.getLastName(), owner.getDomainId(),
						LogAction.ANONYMOUS_SHARE_DOWNLOAD, "Anonymous download of a file", docEntity
						.getName(), docEntity.getSize(), docEntity
						.getType(), email, "", "" , "",null);

				try {
					logEntryService.create(logEntry);
				} catch (IllegalArgumentException e) {
					throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not log the action " + e);
				} catch (BusinessException e) {
					throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "Could not log the action " + e);
				}
			}
		}
	}

	public User getSecuredUrlOwner(String alea, String urlPath) {
		
		try {
			SecuredUrl url = getSecuredUrl(alea, urlPath);
			return url.getSender();
		} catch (LinShareNotSuchElementException e) {
			throw new TechnicalException(TechnicalErrorCode.DATA_INCOHERENCE, "The secured URL cannot be found");
		}
	}
	
	public List<SecuredUrl> getUrlsByMailAndFile(User sender, DocumentVo document) {
		List<SecuredUrl> allUrl = securedUrlRepository.findBySender(sender);

		List<SecuredUrl> byDocUrl = new ArrayList<SecuredUrl>();
		for (SecuredUrl securedUrl : allUrl) {
			for (Document doc : securedUrl.getDocuments()) {
				if (document.getIdentifier().equalsIgnoreCase(doc.getIdentifier())) {
					byDocUrl.add(securedUrl);
					break;
				}
			}
		}
		return byDocUrl;
	}
}
