package org.linagora.linshare.core.service.impl;

import java.util.Set;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.domain.constants.EntryType;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.ShareEntry;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.objects.Recipient;
import org.linagora.linshare.core.domain.objects.ShareContainer;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ShareEntryResourceAccessControl;
import org.linagora.linshare.core.service.AnonymousShareEntryService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.ShareEntryService;
import org.linagora.linshare.core.service.ShareService;
import org.linagora.linshare.core.service.UserService;

public class ShareServiceImpl extends GenericServiceImpl<ShareEntry> implements
		ShareService {

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final DocumentEntryService documentEntryService;

	private final UserService userService;

	private final AnonymousShareEntryService anonymousShareEntryService;

	private final ShareEntryService shareEntryService;

	private final NotifierService notifierService;

	public ShareServiceImpl(
			final FunctionalityReadOnlyService functionalityReadOnlyService,
			final DocumentEntryService documentEntryService,
			final UserService userService,
			final AnonymousShareEntryService anonymousShareEntryService,
			final ShareEntryService shareEntryService,
			final NotifierService notifierService,
			final ShareEntryResourceAccessControl rac) {
		super(rac);
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.documentEntryService = documentEntryService;
		this.userService = userService;
		this.anonymousShareEntryService = anonymousShareEntryService;
		this.shareEntryService = shareEntryService;
		this.notifierService = notifierService;
	}

	@Override
	public void create(Account actor, User owner,
			ShareContainer shareContainer) throws BusinessException {
		preChecks(actor, owner);
		Validate.notNull(shareContainer);
		checkCreatePermission(actor, owner, EntryType.SHARE,
				BusinessErrorCode.FORBIDDEN);

		// Check functionalities

		// Check recipients
		transformRecipients(actor, owner, shareContainer);
		if (shareContainer.needAnonymousShares()) {
			if (!hasRightsToShareWithExternals(owner)) {
				throw new BusinessException(
						BusinessErrorCode.ANONYMOUS_SHARE_ENTRY_FORBIDDEN,
						"You are not authorized to create anonymous share entries.");
			}
		}

		if (!shareContainer.canShare()) {
			throw new BusinessException(
					BusinessErrorCode.SHARE_MISSING_RECIPIENTS,
					"Can not share documents, missing recipients.");
		}

		// Check documents
		transformDocuments(actor, owner, shareContainer);
		shareContainer.updateEncryptedStatus();

		// Creation
		anonymousShareEntryService.create(actor, owner, shareContainer);
		shareEntryService.create(actor, owner, shareContainer);

		// Notification
		notifierService.sendNotification(shareContainer.getMailContainers());

	}

	private boolean hasRightsToShareWithExternals(User sender) {
		AbstractDomain domain = sender.getDomain();
		if (domain != null) {
			Functionality func = functionalityReadOnlyService
					.getAnonymousUrlFunctionality(domain);
			return func.getActivationPolicy().getStatus();
		}
		return false;
	}

	private void transformRecipients(Account actor, User owner,
			ShareContainer shareContainer) throws BusinessException {

		// Initialize the shareContainer for guest if needed.
		if (owner.isGuest() && owner.isRestricted()) {
			Set<AllowedContact> allowedContacts = ((Guest) owner)
					.getRestrictedContacts();
			shareContainer.addAllowedRecipients(allowedContacts);
		}

		for (Recipient recipient : shareContainer.getRecipients()) {
			// step 1
			if (addUserByUuid(shareContainer, recipient)) {
				// no need to look further.
				continue;
			}
			// step 2
			if (addUserByDomainAndMail(shareContainer, recipient, owner)) {
				// no need to look further.
				continue;
			}
			// step 2
			if (addUserByMail(shareContainer, recipient, owner)) {
				// no need to look further.
				continue;
			}
			// step 4
			// It did not find a account related to the recipient object.
			recipient.setLocale(owner.getExternalMailLocale());
			shareContainer.addAnonymousShareRecipient(recipient);
		}
	}

	private boolean addUserByUuid(ShareContainer shareContainer,
			Recipient recipient) throws BusinessException {
		String uuid = recipient.getUuid();
		if (uuid != null) {
			logger.debug("step1:looking into the database using : " + uuid);
			User user = userService.findByLsUuid(uuid);
			if (user != null) {
				logger.debug("step1:user found : "
						+ user.getAccountReprentation());
				shareContainer.addShareRecipient(user);
				return true;
			}
		}
		return false;
	}

	private boolean addUserByDomainAndMail(ShareContainer shareContainer,
			Recipient recipient, Account owner) throws BusinessException {
		String mail = recipient.getUuid();
		String domain = recipient.getUuid();
		if (mail != null && domain != null) {
			logger.debug("step2:looking into the database and the ldap using domain and mail : "
					+ domain + " : " + mail);
			try {
				User user = userService.findOrCreateUserWithDomainPolicies(
						domain, mail, owner.getDomainId());
				logger.debug("step2:user found : "
						+ user.getAccountReprentation());
				shareContainer.addShareRecipient(user);
				return true;
			} catch (BusinessException e) {
				if (!e.getErrorCode().equals(BusinessErrorCode.USER_NOT_FOUND)) {
					throw e;
				}
			}
		}
		return false;
	}

	private boolean addUserByMail(ShareContainer shareContainer,
			Recipient recipient, Account owner) throws BusinessException {
		String mail = recipient.getMail();
		if (mail != null) {
			// step 3
			logger.debug("step3:looking into the database and the ldap using only mail : "
					+ mail);
			try {
				User user = userService.findOrCreateUserWithDomainPolicies(
						mail, owner.getDomainId());
				logger.debug("step3:user found : "
						+ user.getAccountReprentation());
				shareContainer.addShareRecipient(user);
				return true;
			} catch (BusinessException e) {
				if (!e.getErrorCode().equals(BusinessErrorCode.USER_NOT_FOUND)) {
					throw e;
				}
			}
		}
		return false;
	}

	protected void transformDocuments(Account actor, User owner,
			ShareContainer shareContainer) throws BusinessException {
		for (String uuid : shareContainer.getDocumentUuids()) {
			DocumentEntry doc = documentEntryService.find(actor, owner,
					uuid);
			shareContainer.addDocumentEntry(doc);
		}
	}
}
