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

import org.linagora.linshare.core.domain.constants.AccountType;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.AnonymousShareEntry;
import org.linagora.linshare.core.domain.entities.Contact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.Signature;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.MailContainer;
import org.linagora.linshare.core.domain.objects.MailContainerWithRecipient;
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
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.AbstractDomainService;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.GuestService;
import org.linagora.linshare.core.service.MailContentBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class ShareFacadeImpl extends GenericTapestryFacade implements ShareFacade {

	private static final Logger logger = LoggerFactory
			.getLogger(ShareFacadeImpl.class);

	private final ShareEntryTransformer shareEntryTransformer;

	private final UserRepository<User> userRepository;

	private final NotifierService notifierService;

	private final MailContentBuildingService mailElementsFactory;

	private final UserService userService;

	private final ShareEntryService shareEntryService;

	private final DocumentEntryTransformer documentEntryTransformer;

	private final DocumentEntryService documentEntryService;

	private final AbstractDomainService abstractDomainService;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final AnonymousShareEntryService anonymousShareEntryService;

	private final SignatureTransformer signatureTransformer;

	private final GuestService guestService;

	private final ShareService shareService;

	public ShareFacadeImpl(final ShareEntryTransformer shareEntryTransformer,
			final UserRepository<User> userRepository,
			final NotifierService notifierService,
			final MailContentBuildingService mailElementsFactory,
			final UserService userService, ShareEntryService shareEntryService,
			final DocumentEntryTransformer documentEntryTransformer,
			final DocumentEntryService documentEntryService,
			final AbstractDomainService abstractDomainService,
			final FunctionalityReadOnlyService functionalityService,
			final AnonymousShareEntryService anonymousShareEntryService,
			final SignatureTransformer signatureTransformer,
			final GuestService guestService,
			final ShareService shareService,
			final AccountService accountService) {
		super(accountService);
		this.shareEntryTransformer = shareEntryTransformer;
		this.userRepository = userRepository;
		this.notifierService = notifierService;
		this.mailElementsFactory = mailElementsFactory;
		this.userService = userService;
		this.shareEntryService = shareEntryService;
		this.documentEntryTransformer = documentEntryTransformer;
		this.documentEntryService = documentEntryService;
		this.abstractDomainService = abstractDomainService;
		this.functionalityReadOnlyService = functionalityService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.signatureTransformer = signatureTransformer;
		this.guestService = guestService;
		this.shareService = shareService;
	}

	private SuccessesAndFailsItems<ShareDocumentVo> createSharing(
			UserVo actorVo, List<DocumentVo> documents,
			List<UserVo> recipientsVo, Calendar expirationDate)
			throws BusinessException {
		logger.debug("createSharing:Begin");

		User actor = userService.findByLsUuid(actorVo.getLsUuid());

		List<User> recipients = new ArrayList<User>();

		for (UserVo userVo : recipientsVo) {
			try {
				recipients.add(userService.findOrCreateUserWithDomainPolicies(
						userVo.getMail(), actorVo.getDomainIdentifier()));
			} catch (BusinessException e) {
				logger.error("Could not find the recipient " + userVo.getMail()
						+ " in the database nor in the ldap");
				throw e;
			}
		}

		List<DocumentEntry> documentEntries = documentEntryTransformer
				.assembleList(documents);

		SuccessesAndFailsItems<ShareEntry> successAndFails = shareEntryService
				.createShare(documentEntries, actor, recipients, expirationDate);

		SuccessesAndFailsItems<ShareDocumentVo> results = disassembleShareResultList(successAndFails);

		logger.debug("createSharing:End");
		return results;
	}

	private SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMail(
			UserVo owner, List<DocumentVo> documents, List<UserVo> recipients,
			MailContainer mailContainer, Calendar expirationDate,
			boolean isOneDocEncrypted) throws BusinessException {
		logger.debug("createSharingWithMail:Begin");
		SuccessesAndFailsItems<ShareDocumentVo> result = createSharing(owner,
				documents, recipients, expirationDate);

		// Sending the mails
		List<UserVo> successfullRecipient = new ArrayList<UserVo>();
		for (ShareDocumentVo successfullSharing : result.getSuccessesItem()) {
			logger.debug("share:result:" + result);
			if (!successfullRecipient
					.contains(successfullSharing.getReceiver())) {
				successfullRecipient.add(successfullSharing.getReceiver());
			}
		}

		User owner_ = userRepository.findByLsUuid(owner.getLogin());
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();

		for (UserVo userVo : successfullRecipient) {
			logger.debug("Sending sharing notification to user "
					+ userVo.getLogin());
			User recipient = userRepository.findByLsUuid(userVo.getLogin());

			mailContainerWithRecipient.add(mailElementsFactory
					.buildMailNewSharingWithRecipient(owner_, mailContainer,
							recipient, result.getSuccessesItem(),
							isOneDocEncrypted));

		}

		notifierService.sendNotification(mailContainerWithRecipient);
		logger.debug("createSharingWithMail:End");
		return result;
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

	@Override
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmail(
			UserVo ownerVo, List<DocumentVo> documents,
			List<String> recipientsEmail, boolean secureSharing,
			MailContainer mailContainer) throws BusinessException {

		logger.debug("createSharingWithMailUsingRecipientsEmail");
		return createSharingWithMailUsingRecipientsEmailAndExpiryDate(ownerVo,
				documents, recipientsEmail, secureSharing, mailContainer, null);
	}

	@Override
	public SuccessesAndFailsItems<ShareDocumentVo> createSharingWithMailUsingRecipientsEmailAndExpiryDate(
			UserVo actorVo, List<DocumentVo> documents,
			List<String> recipientsEmailInput, boolean secureSharing,
			MailContainer mailContainer, Calendar expiryDateSelected)
			throws BusinessException {
		logger.debug("createSharingWithMailUsingRecipientsEmail");

		User sender = getActor(actorVo);
		SuccessesAndFailsItems<ShareDocumentVo> result = new SuccessesAndFailsItems<ShareDocumentVo>();

		List<UserVo> knownRecipients = new ArrayList<UserVo>();
		List<Contact> unKnownRecipientsEmail = new ArrayList<Contact>();

		logger.debug("The current user is : " + sender.getAccountReprentation());
		logger.debug("recipientsEmailInput size : "
				+ recipientsEmailInput.size());
		List<String> recipientsEmail = new ArrayList<String>();
		if (sender.getAccountType().equals(AccountType.GUEST)
				&& ((Guest) sender).isRestricted()) {
			Guest guest = guestService.findByLsUuid(sender, sender.getLsUuid());
			Set<AllowedContact> contacts = guest.getRestrictedContacts();
			List<String> guestAllowedContacts = Lists.newArrayList();
			for (AllowedContact contact : contacts) {
				guestAllowedContacts.add(contact.getContact().getMail());
			}
			logger.debug("guestAllowedContacts size : "
					+ guestAllowedContacts.size());
			for (String mailInput : recipientsEmailInput) {
				if (guestAllowedContacts.contains(mailInput)) {
					logger.debug("The current user is allowed to share with : "
							+ mailInput);
					recipientsEmail.add(mailInput);
				} else {
					logger.info("The current user is not allowed to share with : "
							+ mailInput);
					unKnownRecipientsEmail.add(new Contact(mailInput));
				}
			}
			logger.debug("Only " + recipientsEmail.size()
					+ " contacts are authorized for " + sender.getMail());
		} else {
			recipientsEmail.addAll(recipientsEmailInput);
		}
		logger.debug("recipientsEmail size : " + recipientsEmail.size());
		logger.debug("unKnownRecipientsEmail size : "
				+ unKnownRecipientsEmail.size());
		logger.debug("unKnownRecipientsEmail  : "
				+ unKnownRecipientsEmail.toString());

		boolean isOneDocEncrypted = oneDocIsEncrypted(documents);

		// find known and unknown recipients of the share
		User tempRecipient = null;
		for (String mail : recipientsEmail) {
			try {
				tempRecipient = userService.findOrCreateUserWithDomainPolicies(
						mail, sender.getDomainId());
				knownRecipients.add(new UserVo(tempRecipient));
			} catch (BusinessException e) {
				if (e.getErrorCode() == BusinessErrorCode.USER_NOT_FOUND) {
					logger.debug("unKnownRecipientsEmail  : adding a new contact : "
							+ mail.toString());
					unKnownRecipientsEmail.add(new Contact(mail));
				} else
					throw e;
			}
		}

		logger.debug("knownRecipients size : " + knownRecipients.size());
		logger.debug("knownRecipients  : " + knownRecipients.toString());
		logger.debug("unKnownRecipientsEmail size : "
				+ unKnownRecipientsEmail.size());
		logger.debug("unKnownRecipientsEmail  : "
				+ unKnownRecipientsEmail.toString());

		if (unKnownRecipientsEmail.size() > 0) { // secureUrl for these users
													// (no need to have an
													// account to activate
													// sharing)

			boolean hasRightsToShareWithExternals = abstractDomainService.hasRightsToShareWithExternals(sender);
			if (hasRightsToShareWithExternals) {
				List<DocumentEntry> documentEntries = documentEntryTransformer
						.assembleList(documents);

				for (Contact recipient : unKnownRecipientsEmail) {
					anonymousShareEntryService.createAnonymousShare(
							documentEntries, sender, recipient,
							expiryDateSelected, secureSharing, mailContainer);

				}
			} else {
				// Building all failed items for unkown recipients.
				for (DocumentVo doc : documents) {
					for (Contact oneContact : unKnownRecipientsEmail) {
						UserVo recipient = new UserVo(oneContact.getMail(), "",
								"", oneContact.getMail(), null);
						ShareDocumentVo failSharing = new ShareDocumentVo(doc,
								actorVo, recipient);
						result.addFailItem(failSharing);
					}
				}
			}
		}

		// keep old method to share with user referenced in db
		result.addAll(createSharingWithMail(actorVo, documents,
				knownRecipients, mailContainer, expiryDateSelected,
				isOneDocEncrypted));
		return result;
	}

	@Override
	public void sendDownloadNotification(ShareDocumentVo sharedDocument,
			UserVo actorVo) throws BusinessException {
		try {
			// send a notification by mail to the owner
			ShareEntry shareEntry = shareEntryService.find(getActor(actorVo),
					sharedDocument.getIdentifier());
			notifierService.sendNotification(mailElementsFactory
					.buildMailRegisteredDownloadWithOneRecipient(shareEntry));
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

	private boolean oneDocIsEncrypted(List<DocumentVo> docList) {
		for (DocumentVo doc : docList) {
			if (doc.getEncrypted()) {
				return true;
			}
		}
		return false;
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
	public List<ShareDocumentVo> share(UserVo actorVo,
			List<DocumentVo> documentVos, List<String> recipientsEmail,
			boolean secured, MailContainer mailContainer)
			throws BusinessException {
		User actor = getActor(actorVo);
		ShareContainer sc = new ShareContainer(mailContainer.getSubject(),
				mailContainer.getPersonalMessage(), secured);
		sc.addDocumentsVo(documentVos);
		sc.addRecipient(recipientsEmail);
		List<ShareEntry> list = shareService.create(actor, actor, sc);
		return shareEntryTransformer.disassembleList(list);
	}
}
