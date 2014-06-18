package org.linagora.linshare.core.business.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

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

public class GuestBusinessServiceImpl implements GuestBusinessService {

	private final GuestRepository guestRepository;

	private final UserRepository<User> userRepository;

	private final AllowedContactRepository allowedContactRepository;

	public GuestBusinessServiceImpl(final GuestRepository guestRepository,
			final UserRepository<User> userRepository,
			final AllowedContactRepository allowedContactRepository) {
		this.guestRepository = guestRepository;
		this.userRepository = userRepository;
		this.allowedContactRepository = allowedContactRepository;
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
	public Guest create(Guest guest, User owner, GuestDomain domain)
			throws BusinessException {
		guest.setOwner(owner);
		guest.setDomain(domain);
		guest.setLsUuid(UUID.randomUUID().toString());
		guest.setCreationDate(new Date());
		guest.setModificationDate(new Date());
		return guestRepository.create(guest);
	}

	@Override
	public Guest update(Guest guest, Account owner, AbstractDomain domain)
			throws BusinessException {
		try {
			guest.setOwner(owner);
			guest.setDomain(domain);
			guest.setModificationDate(new Date());
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
	public boolean isValid(String domainId, String mail) {
		return guestRepository.findByMailAndDomain(domainId, mail) != null;
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
	public List<AllowedContact> getRestrictedContacts(Guest guest) {
		return allowedContactRepository.findByOwner(guest);
	}
}
