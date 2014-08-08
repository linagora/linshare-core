/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.SuccessesAndFailsItems;
import org.linagora.linshare.core.domain.transformers.impl.DocumentEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.ShareEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.SignatureTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShareFacadeImpl extends GenericTapestryFacade implements ShareFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(ShareFacadeImpl.class);

	private final ShareEntryTransformer shareEntryTransformer;

	private final NotifierService notifierService;

	private final UserService userService;

	private final ShareEntryService shareEntryService;

	private final DocumentEntryTransformer documentEntryTransformer;

	private final DocumentEntryService documentEntryService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final SignatureTransformer signatureTransformer;

	private final ShareService shareService;

	public ShareFacadeImpl(AccountService accountService,
			final ShareEntryTransformer shareEntryTransformer,
			final NotifierService notifierService,
			final UserService userService, ShareEntryService shareEntryService,
			final DocumentEntryTransformer documentEntryTransformer,
			final DocumentEntryService documentEntryService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final SignatureTransformer signatureTransformer, ShareService shareService) {
		super(accountService);
		this.shareEntryTransformer = shareEntryTransformer;
		this.notifierService = notifierService;
		this.userService = userService;
		this.shareEntryService = shareEntryService;
		this.documentEntryTransformer = documentEntryTransformer;
		this.documentEntryService = documentEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.signatureTransformer = signatureTransformer;
		this.shareService = shareService;
	}

	@Override
	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipientVo) throws BusinessException {
		User actor = getActor(recipientVo);
		ArrayList<ShareEntry> arrayList = new ArrayList<ShareEntry>(
				actor.getShareEntries());
		logger.debug("AllSharingReceived size : " + arrayList.size());
		return shareEntryTransformer.disassembleList(arrayList);
	}

	@Override
	public List<ShareDocumentVo> getSharingsByUserAndFile(UserVo actorVo,
			DocumentVo documentVo) {
		DocumentEntry documentEntry;
		try {
			User actor = getActor(actorVo);
			logger.debug("looking for document : " + documentVo.getIdentifier());
			documentEntry = documentEntryService.find(actor, actor,
					documentVo.getIdentifier());
			return shareEntryTransformer
					.disassembleList(new ArrayList<ShareEntry>(documentEntry
							.getShareEntries()));
		} catch (BusinessException e) {
			logger.error("Document " + documentVo.getIdentifier()
					+ " was not found ! " + e.getMessage());
		}
		return new ArrayList<ShareDocumentVo>();
	}

	@Override
	public Map<String, Calendar> getAnonymousSharingsByUserAndFile(
			UserVo actorVo, DocumentVo documentVo) {

		logger.debug("looking for document : " + documentVo.getIdentifier());
		Map<String, Calendar> res = new HashMap<String, Calendar>();
		DocumentEntry documentEntry;
		try {
			User actor = getActor(actorVo);
			documentEntry = documentEntryService.find(actor, actor,
					documentVo.getIdentifier());

			for (AnonymousShareEntry entry : documentEntry
					.getAnonymousShareEntries()) {
				res.put(entry.getAnonymousUrl().getContact().getMail(),
						entry.getExpirationDate());
			}
		} catch (BusinessException e) {
			logger.error("Document " + documentVo.getIdentifier()
					+ " was not found ! " + e.getMessage());
		}
		return res;
	}

	@Override
	public void deleteSharing(ShareDocumentVo share, UserVo actorVo)
			throws BusinessException {
		shareEntryService.delete(getActor(actorVo), share.getIdentifier());
	}

	@Override
	public DocumentVo createLocalCopy(ShareDocumentVo shareDocumentVo,
			UserVo actorVo) throws BusinessException {
		DocumentEntry documentEntry = shareEntryService.copyDocumentFromShare(
				shareDocumentVo.getIdentifier(), getActor(actorVo));
		return documentEntryTransformer.disassemble(documentEntry);
	}

	@Deprecated
	@Override
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmail(
			UserVo ownerVo, List<DocumentVo> documents,
			List<String> recipientsEmail, boolean secureSharing,
			MailContainer mailContainer) throws BusinessException {

		logger.debug("createSharingWithMailUsingRecipientsEmail");
		return null;
	}

	@Override
	public void sendDownloadNotification(ShareDocumentVo sharedDocument,
			UserVo actorVo) throws BusinessException {
		try {
			// send a notification by mail to the owner
			ShareEntry shareEntry = shareEntryService.find(getActor(actorVo),
					sharedDocument.getIdentifier());
//			notifierService.sendNotification(mailElementsFactory
//					.buildMailRegisteredDownloadWithOneRecipient(shareEntry));
		} catch (BusinessException e) {
			// TODO : FIXME : send the notification to the domain administration
			// address. => a new functionality need to be add.
			if (e.getErrorCode()
					.equals(BusinessErrorCode.RELAY_HOST_NOT_ENABLE)) {
				logger.error("Can't send share downloaded notification ("
						+ sharedDocument.getIdentifier()
						+ ") to owner because : " + e.getMessage());
			}
		}
	}

	private SuccessesAndFailsItems<ShareDocumentVo> disassembleShareResultList(
			SuccessesAndFailsItems<ShareEntry> successAndFails) {
		SuccessesAndFailsItems<ShareDocumentVo> results = new SuccessesAndFailsItems<ShareDocumentVo>();
		results.setFailsItem(shareEntryTransformer
				.disassembleList(successAndFails.getFailsItem()));
		results.setSuccessesItem(shareEntryTransformer
				.disassembleList(successAndFails.getSuccessesItem()));
		return results;
	}

	@Override
	public boolean isVisibleSecuredAnonymousUrlCheckBox(String domainIdentifier) {
		try {
			return functionalityReadOnlyService.isSauAllowed(domainIdentifier);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
			return false;
		}
	}

	@Override
	public boolean getDefaultSecuredAnonymousUrlCheckBoxValue(
			String domainIdentifier) {
		try {
			return functionalityReadOnlyService
					.getDefaultSauValue(domainIdentifier);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
			return false;
		}
	}

	@Override
	public ShareDocumentVo getShareDocumentVoByUuid(UserVo actorVo, String uuid)
			throws BusinessException {
		return shareEntryTransformer.disassemble(shareEntryService.find(
				getActor(actorVo), uuid));
	}

	@Override
	public void updateShareComment(UserVo actorVo, String uuid, String comment)
			throws IllegalArgumentException, BusinessException {
		logger.debug("updateShareComment:" + uuid);
		logger.debug("comment : " + comment);
		ShareEntry share = new ShareEntry();
		share.setUuid(uuid);
		share.setComment(comment);
		shareEntryService.update(getActor(actorVo), share);
	}

	@Override
	public boolean shareHasThumbnail(UserVo actorVo, String shareEntryUuid) {
		try {
			User actor = getActor(actorVo);
			return shareEntryService.hasThumbnail(actor, shareEntryUuid);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
			return false;
		}
	}

	@Override
	public InputStream getShareThumbnailStream(UserVo actorVo,
			String shareEntryUuid) throws BusinessException {
		logger.debug("downloading thumbnail for share : " + shareEntryUuid);
		try {
			User actor = getActor(actorVo);
			return shareEntryService.getThumbnailStream(actor,
					shareEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + shareEntryUuid
					+ " : " + e.getMessage());
		}
		return null;
	}

	@Override
	public InputStream getShareStream(UserVo actorVo, String shareEntryUuid)
			throws BusinessException {
		logger.debug("downloading share : " + shareEntryUuid);
		String lsUid = actorVo.getLsUuid();
		if (lsUid == null) {
			logger.error("Can't find user with null parametter.");
			return null;
		}

		User actor = userService.findByLsUuid(lsUid);
		if (actor == null) {
			logger.error("Can't find logged user.");
			return null;
		}

		try {
			return shareEntryService.getStream(actor, shareEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + shareEntryUuid
					+ " : " + e.getMessage());
			throw e;
		}
	}

	@Override
	public boolean isSignedShare(UserVo actorVo, ShareDocumentVo shareVo) {
		boolean res = false;
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		try {
			ShareEntry share = shareEntryService.find(actor,
					shareVo.getIdentifier());
			Set<Signature> signatures = share.getDocumentEntry().getDocument()
					.getSignatures();
			if (signatures != null && signatures.size() > 0)
				res = true;
		} catch (BusinessException e) {
			logger.error("Can't find document : " + shareVo.getIdentifier()
					+ ": " + e.getMessage());
		}
		return res;
	}

	@Override
	public boolean isSignedShare(UserVo actorVo, String shareVoIdentifier) {
		boolean res = false;
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		try {
			ShareEntry share = shareEntryService.find(actor,
					shareVoIdentifier);
			Set<Signature> signatures = share.getDocumentEntry().getDocument()
					.getSignatures();
			if (signatures != null && signatures.size() > 0)
				res = true;
		} catch (BusinessException e) {
			logger.error("Can't find document : " + shareVoIdentifier + ": "
					+ e.getMessage());
		}
		return res;
	}

	@Override
	public SignatureVo getSignature(UserVo actorVo, ShareDocumentVo documentVo) {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		try {
			ShareEntry share = shareEntryService.find(actor,
					documentVo.getIdentifier());

			SignatureVo res = null;
			for (Signature signature : share.getDocumentEntry().getDocument()
					.getSignatures()) {
				if (signature.getSigner().equals(actor)) {
					res = signatureTransformer.disassemble(signature);
					break;
				}
			}
			return res;
		} catch (BusinessException e) {
			logger.error("Can't find document : " + documentVo.getIdentifier()
					+ ": " + e.getMessage());
		}
		return null;
	}

	@Override
	public List<SignatureVo> getAllSignatures(UserVo actorVo,
			ShareDocumentVo documentVo) {
		User actor = userService.findByLsUuid(actorVo.getLsUuid());
		try {
			ShareEntry share = shareEntryService.find(actor,
					documentVo.getIdentifier());
			return signatureTransformer
					.disassembleList(new ArrayList<Signature>(share
							.getDocumentEntry().getDocument().getSignatures()));
		} catch (BusinessException e) {
			logger.error("Can't find document : " + documentVo.getIdentifier()
					+ ": " + e.getMessage());
		}
		return null;
	}

	@Override
	public void share(UserVo actorVo,
			List<DocumentVo> documentVos, List<String> recipientsEmail,
			boolean secured, MailContainer mailContainer)
			throws BusinessException {
		User actor = getActor(actorVo);
		ShareContainer sc = new ShareContainer(mailContainer.getSubject(),
				mailContainer.getPersonalMessage(), secured);
		sc.addDocumentVos(documentVos);
		sc.addMail(recipientsEmail);
		shareService.create(actor, actor, sc);
	}
}
