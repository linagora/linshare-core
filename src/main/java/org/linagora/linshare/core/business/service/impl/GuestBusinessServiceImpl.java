package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;

import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AllowedContact;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.GuestDomain;
import org.linagora.linshare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.exception.TechnicalErrorCode;
import org.linagora.linshare.core.exception.TechnicalException;
import org.linagora.linshare.core.repository.AllowedContactRepository;
import org.linagora.linshare.core.repository.GuestRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.MailBuildingService;
import org.linagora.linshare.core.service.NotifierService;
import org.linagora.linshare.core.service.PasswordService;
import org.linagora.linshare.core.utils.HashUtils;
import org.springframework.util.Assert;

public class GuestBusinessServiceImpl implements GuestBusinessService {

	private final GuestRepository guestRepository;

	private final UserRepository<User> userRepository;

	private final AllowedContactRepository allowedContactRepository;

	private final PasswordService passwordService;

	private final NotifierService notifierService;

	private final MailBuildingService mailBuildingService;

	public GuestBusinessServiceImpl(final GuestRepository guestRepository,
			final UserRepository<User> userRepository,
			final AllowedContactRepository allowedContactRepository,
			final PasswordService passwordService,
			final NotifierService notifierService,
			final MailBuildingService mailBuildingService) {
		this.guestRepository = guestRepository;
		this.userRepository = userRepository;
		this.allowedContactRepository = allowedContactRepository;
		this.passwordService = passwordService;
		this.notifierService = notifierService;
		this.mailBuildingService = mailBuildingService;
	}

	@Override
	public Guest findByLsUuid(String lsUuid) throws BusinessException {
		return guestRepository.findByLsUuid(lsUuid);
	}

	@Override
	public List<Guest> findAll() {
		return guestRepository.findAll();
	}

	@Override
	public List<Guest> findOutdatedGuests() {
		return guestRepository.findOutdatedGuests();
	}

	@Override
	public Guest create(Guest guest, User owner, GuestDomain domain, Date expiryDate)
			throws BusinessException {
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
        
		MailContainerWithRecipient mail = mailBuildingService.buildNewGuest(
				owner, guest, password);
		notifierService.sendAllNotification(mail);
		return create;
	}

	@Override
	public Guest update(Guest guest, Account owner, AbstractDomain domain)
			throws BusinessException {
		try {
			guest.setOwner(owner);
			guest.setDomain(domain);
			return guestRepository.update(guest);
		} catch (IllegalArgumentException iae) {
			throw new BusinessException(BusinessErrorCode.GUEST_NOT_FOUND,
					"Cannot update guest " + guest);
		}
	}

	@Override
	public void delete(Guest guest) throws BusinessException {
		userRepository.delete(guest);
	}

	@Override
	public boolean exist(String domainId, String mail) {
		return guestRepository.findByMailAndDomain(domainId, mail) == null;
	}

	@Override
	public void purgeRestriction(Guest guest) throws BusinessException {
		try {
			allowedContactRepository.purge(guest);
		} catch (IllegalArgumentException e) {
			throw new TechnicalException(TechnicalErrorCode.USER_INCOHERENCE,
					"Guest " + guest.getLsUuid() + " contacts cannot be purge");
		}
	}

	@Override
	public void addRestrictedContact(Guest guest, User contact)
			throws BusinessException {
		AllowedContact allowedContact = new AllowedContact(guest, contact);
		allowedContactRepository.create(allowedContact);
	}

	@Override
	public List<AllowedContact> getRestrictedContacts(Guest guest) {
		return allowedContactRepository.findByOwner(guest);
	}

	@Override
	public Guest enableContactRestriction(Guest guest)
			throws BusinessException {
		Assert.notNull(guest);

		guest.setRestricted(true);
		return update(guest, guest.getOwner(), guest.getDomain());
	}
	
	@Override
	public Guest removeContactRestriction(Guest guest)
			throws BusinessException {
		Assert.notNull(guest);

		purgeRestriction(guest);
		guest.setRestricted(false);
		return update(guest, guest.getOwner(), guest.getDomain());
	}

	@Override
	public void resetPassword(Guest guest) throws BusinessException {
		String password = passwordService.generatePassword();
		String hashedPassword = HashUtils.hashSha1withBase64(password
				.getBytes());
		MailContainerWithRecipient mail = mailBuildingService
				.buildResetPassword(guest, password);
		notifierService.sendAllNotification(mail);
		guest.setPassword(hashedPassword);
		guestRepository.update(guest);
	}

	/**
	 * HELPERS
	 */


}
