package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.utils.HashUtils;

public class GuestBusinessServiceImpl implements GuestBusinessService {

	private final GuestRepository guestRepository;

	private final UserRepository<User> userRepository;

	private final AllowedContactRepository allowedContactRepository;

	private final PasswordService passwordService;

	public GuestBusinessServiceImpl(final GuestRepository guestRepository,
			final UserRepository<User> userRepository,
			final AllowedContactRepository allowedContactRepository,
			final PasswordService passwordService) {
		this.guestRepository = guestRepository;
		this.userRepository = userRepository;
		this.allowedContactRepository = allowedContactRepository;
		this.passwordService = passwordService;
	}

	@Override
	public Guest findByLsUuid(String lsUuid) throws BusinessException {
		Guest guest = guestRepository.findByLsUuid(lsUuid);
		if (guest.isRestricted()) {
			guest.addContacts(allowedContactRepository.findByOwner(guest));
		}
		return guest;
	}

	@Override
	public List<Guest> findAll() {
		List<Guest> all = guestRepository.findAll();
		for (Guest guest : all) {
			if (guest.isRestricted()) {
				guest.addContacts(allowedContactRepository.findByOwner(guest));
			}
		}
		return all;
	}

	@Override
	public List<Guest> findOutdatedGuests() {
		List<Guest> outdatedGuests = guestRepository.findOutdatedGuests();
		for (Guest guest : outdatedGuests) {
			if (guest.isRestricted()) {
				guest.addContacts(allowedContactRepository.findByOwner(guest));
			}
		}
		return outdatedGuests;
	}

	@Override
	public GuestWithMetadata create(Guest guest, User owner,
			GuestDomain domain, Date expiryDate) throws BusinessException {
		String password = passwordService.generatePassword();
		String hashedPassword = HashUtils.hashSha1withBase64(password
				.getBytes());

		guest.setOwner(owner);
		guest.setDomain(domain);
		guest.setLocale(domain.getDefaultTapestryLocale());
		guest.setExternalMailLocale(domain.getDefaultTapestryLocale());
		guest.setPassword(hashedPassword);
		guest.setExpirationDate(expiryDate);
		Guest create = guestRepository.create(guest);
		if (create.isRestricted()) {
			for (AllowedContact c : guest.getRestrictedContacts()) {
				allowedContactRepository.create(c);
			}
		}
		return new GuestWithMetadata(password, create);
	}

	@Override
	public Guest update(Guest guest, Account owner, AbstractDomain domain)
			throws BusinessException {
		try {
			guest.setOwner(owner);
			guest.setDomain(domain);
			Guest update = guestRepository.update(guest);
			if (update.isRestricted()) {
				try {
					allowedContactRepository.purge(update);
				} catch (IllegalArgumentException e) {
					throw new TechnicalException(
							TechnicalErrorCode.USER_INCOHERENCE, "Guest "
									+ guest.getLsUuid()
									+ " contacts cannot be purge");
				}
				for (AllowedContact c : guest.getRestrictedContacts()) {
					allowedContactRepository.create(c);
				}
			}
			return update;
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					"Cannot update guest " + guest);
		}
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
		String hashedPassword = HashUtils.hashSha1withBase64(password
				.getBytes());
		guest.setPassword(hashedPassword);
		Guest update = guestRepository.update(guest);
		return new GuestWithMetadata(password, update);
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
}
