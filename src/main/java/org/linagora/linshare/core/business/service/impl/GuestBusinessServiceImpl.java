/*
 * Copyright (C) 2007-2023 - LINAGORA
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.linagora.linshare.core.business.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nonnull;

import org.linagora.linshare.core.business.service.EntryBusinessService;
import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.business.service.MailingListBusinessService;
import org.linagora.linshare.core.business.service.PasswordService;
import org.linagora.linshare.core.business.service.ShareEntryBusinessService;
import org.linagora.linshare.core.business.service.ShareEntryGroupBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceMemberBusinessService;
import org.linagora.linshare.core.business.service.SharedSpaceNodeBusinessService;
import org.linagora.linshare.core.business.service.UploadRequestGroupBusinessService;
import org.linagora.linshare.core.business.service.WorkGroupNodeBusinessService;
import org.linagora.linshare.core.domain.constants.Language;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.SystemAccount;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.RecipientFavouriteRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;


public class GuestBusinessServiceImpl implements GuestBusinessService {

	private static final Logger logger = LoggerFactory.getLogger(GuestBusinessServiceImpl.class);

	private final GuestRepository guestRepository;

	private final UserRepository<User> userRepository;

	private final AllowedContactRepository allowedContactRepository;

	private final PasswordService passwordService;
	private final MailingListBusinessService mailingListBusinessServiceImpl;

	private final RecipientFavouriteRepository recipientFavouriteRepository;
	private final UploadRequestGroupBusinessService uploadRequestGroupBusinessService;
	private final EntryBusinessService entryBusinessService;
	private final ShareEntryGroupBusinessService shareEntryGroupBusiness;
	private final ShareEntryBusinessService shareEntryBusinessService;
	private final SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService;
	private final WorkGroupNodeBusinessService workGroupNodeBusinessService;
	private final SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService;

	public GuestBusinessServiceImpl(final GuestRepository guestRepository,
			final UserRepository<User> userRepository,
			final AllowedContactRepository allowedContactRepository,
			final RecipientFavouriteRepository recipientFavouriteRepository,
			final PasswordService passwordService,
			final MailingListBusinessServiceImpl mailingListBusinessServiceImpl,
			final UploadRequestGroupBusinessServiceImpl uploadRequestGroupBusinessService,
			final EntryBusinessService entryBusinessService,
			final ShareEntryGroupBusinessService shareEntryGroupBusiness,
			final ShareEntryBusinessService shareEntryBusinessService,
			final SharedSpaceNodeBusinessService sharedSpaceNodeBusinessService,
			final WorkGroupNodeBusinessService workGroupNodeBusinessService,
			final SharedSpaceMemberBusinessService sharedSpaceMemberBusinessService) {
		this.guestRepository = guestRepository;
		this.userRepository = userRepository;
		this.allowedContactRepository = allowedContactRepository;
		this.recipientFavouriteRepository = recipientFavouriteRepository;
		this.passwordService = passwordService;
		this.mailingListBusinessServiceImpl = mailingListBusinessServiceImpl;
		this.uploadRequestGroupBusinessService = uploadRequestGroupBusinessService;
		this.entryBusinessService = entryBusinessService;
		this.shareEntryGroupBusiness = shareEntryGroupBusiness;
		this.shareEntryBusinessService = shareEntryBusinessService;
		this.sharedSpaceNodeBusinessService = sharedSpaceNodeBusinessService;
		this.workGroupNodeBusinessService = workGroupNodeBusinessService;
		this.sharedSpaceMemberBusinessService = sharedSpaceMemberBusinessService;
	}

	@Override
	public Guest findByLsUuid2(String lsUuid) throws BusinessException {
		return guestRepository.findByLsUuid(lsUuid);
	}

	@Override
	public Guest findByLsUuid(String lsUuid) throws BusinessException {
		Guest guest = guestRepository.findByLsUuid(lsUuid);
		if (guest != null) {
			if (guest.isRestricted()) {
				guest.addContacts(allowedContactRepository.findByOwner(guest));
			}
		}
		return guest;
	}

	@Override
	public Guest find(AbstractDomain domain, String mail)
			throws BusinessException {
		Guest guest = guestRepository.findByDomainAndMail(domain, mail);
		if (guest != null) {
			if (guest.isRestricted()) {
				guest.addContacts(allowedContactRepository.findByOwner(guest));
			}
		}
		return guest;
	}

	@Override
	public Guest findByMail(String mail) throws BusinessException {
		return guestRepository.findByMail(mail);
	}

	@Override
	public List<String> findOutdatedGuestIdentifiers() {
		return guestRepository.findOutdatedGuestIdentifiers();
	}

	@Override
	public List<String> findAllGuests() {
		return this.guestRepository.findAllGuests();
	}

	@Override
	public Guest create(Account actor, Guest guest,
			AbstractDomain domain, List<User> allowedContacts) throws BusinessException {
		String password = passwordService.generatePassword();
		String hashedPassword = passwordService.encode(password);
		guest.setMail(guest.getMail().toLowerCase());
		guest.setDomain(domain);
		guest.setCmisLocale(Language.ENGLISH.toString());
		if (guest.getMailLocale() == null) {
			guest.setMailLocale(domain.getExternalMailLocale());
		}
		if (guest.getExternalMailLocale() == null) {
			guest.setExternalMailLocale(domain.getExternalMailLocale());
		}
		guest.setPassword(hashedPassword);
		Guest create = guestRepository.create(guest);
		Set<AllowedContact> allowedContactsToAdd = Sets.newHashSet();
		if (create.isRestricted()) {
			if (allowedContacts == null || allowedContacts.isEmpty()) {
				throw new BusinessException(BusinessErrorCode.GUEST_INVALID_INPUT, "You can not create a restricted guest without a list of contacts.");
			} else {
				for (User contact : allowedContacts) {
					AllowedContact allowedContact = new AllowedContact(create,
							contact);
					allowedContactRepository.create(allowedContact);
					allowedContactsToAdd.add(allowedContact);
				}
			}
		}
		create.addContacts(allowedContactsToAdd);
		return create;
	}

	@Override
	public Guest update(Account actor, Guest entity, Guest guest,
			List<User> allowedContacts)
			throws BusinessException {
		boolean wasRestricted = entity.isRestricted();
		// fields that can not be null
		entity.setCanUpload(guest.isCanUpload());
		entity.setRestricted(guest.isRestricted());
		// fields that can be null.
		entity.setComment(guest.getComment());
		entity.setBusinessMailLocale(guest.getMailLocale());
		entity.setBusinessLastName(guest.getLastName());
		entity.setBusinessFirstName(guest.getFirstName());
		entity.setExpirationDate(guest.getExpirationDate());
		Guest update = guestRepository.update(entity);
		if (wasRestricted == guest.isRestricted()) {
			if (allowedContacts != null) {
				if ((allowedContacts.isEmpty() && entity.isRestricted())) {
					throw new BusinessException(BusinessErrorCode.GUEST_INVALID_INPUT, "You can not update a restricted guest without a list of contacts.");
				}
				// update
				allowedContactRepository.purge(update);
				Set<String> contacts = Sets.newHashSet();
				for (User contact : allowedContacts) {
					allowedContactRepository.create(new AllowedContact(update,
							contact));
					contacts.add(contact.getMail());
				}
				List<String> recipients = recipientFavouriteRepository.getElementsOrderByWeight(update);
				for (String recipient: recipients) {
					if (!contacts.contains(recipient)) {
						recipientFavouriteRepository.deleteOneFavoriteOfUser(update, recipient);
					}
				}
			}
		} else if (wasRestricted) {
			// it is not restricted anymore. purge
			allowedContactRepository.purge(update);
		} else {
			// it was not restricted,
			if (guest.isRestricted()) {
				// but it is now
				if (allowedContacts == null || allowedContacts.isEmpty()) {
					throw new BusinessException(BusinessErrorCode.GUEST_INVALID_INPUT, "You can not update a restricted guest without a list of contacts.");
				} else {
					Set<String> contacts = Sets.newHashSet();
					for (User contact : allowedContacts) {
						allowedContactRepository.create(new AllowedContact(update,
								contact));
						contacts.add(contact.getMail());
					}
					List<String> recipients = recipientFavouriteRepository.getElementsOrderByWeight(update);
					for (String recipient: recipients) {
						if (!contacts.contains(recipient)) {
							recipientFavouriteRepository.deleteOneFavoriteOfUser(update, recipient);
						}
					}
				}
			}
		}
		return update;
	}

	@Override
	public List<AllowedContact> loadAllowedContacts(User guest)
			throws BusinessException {
		return allowedContactRepository.findByOwner(guest);
	}

	@Override
	public void delete(Guest guest) throws BusinessException {
		if (guest.isRestricted()) {
			allowedContactRepository.purge(guest);
		}
		userRepository.delete(guest);
	}

	@Override
	public boolean exist(String domainId, String mail) {
		return guestRepository.findByMailAndDomain(domainId, mail) == null;
	}

	@Override
	public GuestWithMetadata resetPassword(Guest guest)
			throws BusinessException {
		String password = passwordService.generatePassword();
		String hashedPassword = passwordService.encode(password);
		guest.setPassword(hashedPassword);
		Guest update = guestRepository.update(guest);
		return new GuestWithMetadata(password, update);
	}

	@Override
	public void evict(Guest entity) {
		guestRepository.evict(entity);
	}

	@Override
	public SystemAccount getGuestSystemAccount() {
		// TODO create a dedicated guest account.
		return guestRepository.getBatchSystemAccount();
	}

	/**
	 * HELPERS
	 */

	public class GuestWithMetadata {

		private final String password;

		private final Guest guest;

		public GuestWithMetadata(String password, Guest guest) {
			super();
			this.password = password;
			this.guest = guest;
		}

		public String getPassword() {
			return password;
		}

		public Guest getGuest() {
			return guest;
		}
	}

	@Override
	public List<Guest> findAll(List<AbstractDomain> authorizedDomains,
			Optional<ModeratorRole> moderatorRole, Optional<User> moderatorAccount,
			Optional<String> pattern) {
		if (moderatorAccount.isEmpty()) {
			if (moderatorRole.isEmpty()) {
				return guestRepository.findAll(authorizedDomains, pattern);
			} else {
				// no possible
				throw new BusinessException(BusinessErrorCode.GUEST_INVALID_SEARCH_INPUT, "You can not search by role if you do not provide a related account.");
			}
		} else {
			// no need to filter by domains since we want only guests which moderatorAccount is moderator.
			return guestRepository.findAll(authorizedDomains, moderatorAccount.get(), moderatorRole, pattern);
		}
	}

	@Override
	public Account convertGuestToInternalUser(@Nonnull final Account internalAccount,
			@Nonnull final Guest guestAccount) {
		logger.info("Starting conversion of guest to internal user for user: {}", guestAccount.getMail());

		// Check if guest account is null
		if (guestAccount == null) {
			logger.error("No guest account found for user: {}", guestAccount.getMail());
			throw new BusinessException(BusinessErrorCode.GUEST_CONVERSION_FAILED,
					"No guest account found for the user.");
		}

		// Transfer the objects of guest account to internal
		this.transferContactListFromGuestToInternal(guestAccount, internalAccount);
		this.transferUploadRequestGroupsFromGuestToInternal(guestAccount, internalAccount);
		this.transferEntriesFromGuestToInternal(guestAccount, internalAccount);
		this.transferShareEntryFromGuestToInternal(guestAccount, internalAccount);
		this.transferShareEntryGroupFromGuestToInternal(guestAccount, internalAccount);
		this.transferWorkspaceFromGuestToInternal(guestAccount, internalAccount);
		this.transferWorkGroupFromGuestToInternal(guestAccount, internalAccount);
		this.transferSpaceMemberFromGuestToInternal(guestAccount, internalAccount);

		logger.debug("Guest conversion to internal user completed successfully for user: {}",
				internalAccount.getMail());
		return internalAccount;
	}

	private void transferSpaceMemberFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account internalAccount) {
		this.sharedSpaceMemberBusinessService.transferSharedSpaceMemberFromGuestToInternal(guestAccount,
				(User) internalAccount);
	}

	private void transferWorkGroupFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account internalAccount) {
		this.workGroupNodeBusinessService.transferWorkGroupFromGuestToInternal(guestAccount, (User) internalAccount);
	}

	private void transferWorkspaceFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account internalAccount) {
		this.sharedSpaceNodeBusinessService.transferWorkspaceFromGuestToInternal(guestAccount, (User) internalAccount);
	}

	private void transferShareEntryFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account internalAccount) {
		this.shareEntryBusinessService.transferShareEntryFromGuestToInternal(guestAccount, (User) internalAccount);
	}

	private void transferShareEntryGroupFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account internalAccount) {
		this.shareEntryGroupBusiness.transferShareEntryGroupFromGuestToInternal(guestAccount, internalAccount);
	}

	private void transferEntriesFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account internalAccount) {
		this.entryBusinessService.transferEntriesFromGuestToInternal(guestAccount, (User) internalAccount);
	}

	private void transferContactListFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account authUser) {
		logger.debug("Transferring mailing lists.");
		this.mailingListBusinessServiceImpl.transferContactListFromGuestToInternal(guestAccount, authUser);
	}

	private void transferUploadRequestGroupsFromGuestToInternal(@Nonnull final Guest guestAccount,
			@Nonnull final Account authUser) {
		logger.debug("Transferring upload request groups.");
		this.uploadRequestGroupBusinessService.transferUploadRequestGroupsFromGuestToInternal(guestAccount, authUser);
	}
}
