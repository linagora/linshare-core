/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.BooleanValueFunctionality;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.IntegerValueFunctionality;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.domain.objects.TimeUnitValueFunctionality;
import org.linagora.linshare.core.domain.transformers.impl.DocumentEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.ShareEntryTransformer;
import org.linagora.linshare.core.domain.transformers.impl.SignatureTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.ShareDocumentVo;
import org.linagora.linshare.core.domain.vo.SignatureVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ShareFacade;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * TODO
 * - Signature methods (just burn everything)
 * - getSharingsByUserAndFile
 * - getAnonymousSharingsByUserAndFile
 */
public class ShareFacadeImpl extends GenericTapestryFacade implements ShareFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(ShareFacadeImpl.class);

	private final ShareEntryTransformer shareEntryTransformer;

	private final ShareEntryService shareEntryService;

	private final DocumentEntryTransformer documentEntryTransformer;

	private final DocumentEntryService documentEntryService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final SignatureTransformer signatureTransformer;

	private final ShareService shareService;

	private final AbstractDomainService abstractDomainService;

	public ShareFacadeImpl(AccountService accountService,
			final ShareEntryTransformer shareEntryTransformer,
			final ShareEntryService shareEntryService,
			final DocumentEntryTransformer documentEntryTransformer,
			final DocumentEntryService documentEntryService,
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final SignatureTransformer signatureTransformer, ShareService shareService,
			final AbstractDomainService abstractDomainService) {
		super(accountService);
		this.shareEntryTransformer = shareEntryTransformer;
		this.shareEntryService = shareEntryService;
		this.documentEntryTransformer = documentEntryTransformer;
		this.documentEntryService = documentEntryService;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.signatureTransformer = signatureTransformer;
		this.shareService = shareService;
		this.abstractDomainService = abstractDomainService;
	}

	@Override
	public List<ShareDocumentVo> getAllSharingReceivedByUser(UserVo recipientVo) throws BusinessException {
		User actor = getActor(recipientVo);
		List<ShareEntry> shares = shareEntryService.findAllMyRecievedShareEntries(actor, actor);
		return shareEntryTransformer.disassembleList(shares);
	}

	// TODO - Refactoring
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

	// TODO - Refactoring
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
		User actor = getActor(actorVo);
		shareEntryService.delete(actor, actor, share.getIdentifier());
	}

	@Override
	public DocumentVo createLocalCopy(ShareDocumentVo shareDocumentVo,
			UserVo actorVo) throws BusinessException {
		User actor = getActor(actorVo);
		DocumentEntry documentEntry = shareEntryService.copy(
				actor, actor, shareDocumentVo.getIdentifier());
		return documentEntryTransformer.disassemble(documentEntry);
	}

	@Override
	public boolean isVisibleSecuredAnonymousUrlCheckBox(String domainIdentifier) {
		try {
			BooleanValueFunctionality anonymousUrl = functionalityReadOnlyService.getAnonymousUrl(domainIdentifier);
			if (anonymousUrl.getActivationPolicy().getStatus()) {
				return anonymousUrl.getDelegationPolicy().getStatus();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public boolean isVisibleShareExpiration(String domainId) {
		AbstractDomain domain = abstractDomainService.retrieveDomain(domainId);
		try {
			TimeUnitValueFunctionality shareExpiration = functionalityReadOnlyService
					.getDefaultShareExpiryTimeFunctionality(domain);
			if (shareExpiration.getActivationPolicy().getStatus()) {
				return shareExpiration.getDelegationPolicy().getStatus();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public Date getDefaultShareExpirationValue(UserVo actorVo) {
		User actor = getActor(actorVo);
		return shareService.getFinalShareExpiryDate(actor, null);
	}

	@Override
	public boolean getDefaultSecuredAnonymousUrlCheckBoxValue(
			String domainIdentifier) {
		try {
			BooleanValueFunctionality anonymousUrl = functionalityReadOnlyService.getAnonymousUrl(domainIdentifier);
			if (anonymousUrl.getActivationPolicy().getStatus()) {
				return anonymousUrl.getValue();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public ShareDocumentVo getShareDocumentVoByUuid(UserVo actorVo, String uuid)
			throws BusinessException {
		User actor = getActor(actorVo);
		return shareEntryTransformer.disassemble(shareEntryService.find(
				actor, actor, uuid));
	}

	@Override
	public void updateShareComment(UserVo actorVo, String uuid, String comment)
			throws IllegalArgumentException, BusinessException {
		logger.debug("updateShareComment:" + uuid);
		logger.debug("comment : " + comment);
		ShareEntry share = new ShareEntry();
		share.setUuid(uuid);
		share.setComment(comment);
		User actor = getActor(actorVo);
		shareEntryService.update(actor, actor, share);
	}

	@Override
	public InputStream getShareThumbnailStream(UserVo actorVo,
			String shareEntryUuid) throws BusinessException {
		logger.debug("downloading thumbnail for share : " + shareEntryUuid);
		try {
			User actor = getActor(actorVo);
			return shareEntryService.getThumbnailStream(actor,
					actor, shareEntryUuid);
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
		User actor = getActor(actorVo);
		return shareEntryService.getStream(actor, actor, shareEntryUuid);
	}

	@Override
	public boolean isSignedShare(UserVo actorVo, ShareDocumentVo shareVo) {
		boolean res = false;
		User actor = getActor(actorVo);
		try {
			ShareEntry share = shareEntryService.find(actor,
					actor, shareVo.getIdentifier());
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
		User actor = getActor(actorVo);
		try {
			ShareEntry share = shareEntryService.find(actor,
					actor, shareVoIdentifier);
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
		User actor = getActor(actorVo);
		try {
			ShareEntry share = shareEntryService.find(actor,
					actor, documentVo.getIdentifier());

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
		User actor = getActor(actorVo);
		try {
			ShareEntry share = shareEntryService.find(actor,
					actor, documentVo.getIdentifier());
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
	public void share(UserVo actorVo, List<DocumentVo> documentVos,
			List<String> recipientsEmail, boolean secured,
			MailContainer mailContainer, boolean creationAcknowledgement,
			Date shareExpiryDate, boolean enableUSDA, Date notificationDateForUSDA) throws BusinessException {
		User actor = getActor(actorVo);
		ShareContainer sc = new ShareContainer(mailContainer.getSubject(),
				mailContainer.getPersonalMessage(), secured,
				creationAcknowledgement);

		sc.setExpiryDate(shareExpiryDate);
		sc.addDocumentVos(documentVos);
		sc.addMail(recipientsEmail);
		sc.setNotificationDateForUSDA(notificationDateForUSDA);
		sc.setEnableUSDA(enableUSDA);
		shareService.create(actor, actor, sc);
	}

	@Override
	public boolean isVisibleAcknowledgementCheckBox(String domainIdentifier)
			throws BusinessException {
		try {
			BooleanValueFunctionality acknowledgement = functionalityReadOnlyService.getAcknowledgement(domainIdentifier);
			if (acknowledgement.getActivationPolicy().getStatus()) {
				return acknowledgement.getDelegationPolicy().getStatus();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public boolean getDefaultAcknowledgementCheckBox(String domainIdentifier)
			throws BusinessException {
		try {
			BooleanValueFunctionality acknowledgement = functionalityReadOnlyService.getAcknowledgement(domainIdentifier);
			if (acknowledgement.getActivationPolicy().getStatus()) {
				return acknowledgement.getValue();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public boolean isVisibleUndownloadedSharedDocumentsAlert(UserVo actorVo) {
		User actor = getActor(actorVo);
		try {
			BooleanValueFunctionality undownloadedSharedFunc = functionalityReadOnlyService
					.getUndownloadedSharedDocumentsAlert(actor.getDomain());
			if (undownloadedSharedFunc.getActivationPolicy().getStatus()) {
				return undownloadedSharedFunc.getDelegationPolicy().getStatus();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public boolean isVisibleUndownloadedSharedDocumentsNotificationDatePicker(
			UserVo actorVo) {
		User actor = getActor(actorVo);
		try {
			IntegerValueFunctionality notificationDatePickerFunc = functionalityReadOnlyService
					.getUndownloadedSharedDocumentsAlertDuration(actor
							.getDomain());
			if (notificationDatePickerFunc.getActivationPolicy().getStatus()) {
				return notificationDatePickerFunc.getDelegationPolicy()
						.getStatus();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public boolean getDefaultUndownloadedSharedDocumentsAlert(UserVo actorVo) {
		User actor = getActor(actorVo);
		try {
			BooleanValueFunctionality alert = functionalityReadOnlyService.getUndownloadedSharedDocumentsAlert(actor.getDomain());
			if (alert.getActivationPolicy().getStatus()) {
				return alert.getValue();
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			logger.debug(e.toString());
		}
		return false;
	}

	@Override
	public Date getUndownloadedSharedDocumentsAlertDefaultValue(
			UserVo actorVo) {
		User actor = getActor(actorVo);
		Date duration = shareService.getUndownloadedSharedDocumentsAlertDuration(actor);
		return duration;
	}
}
