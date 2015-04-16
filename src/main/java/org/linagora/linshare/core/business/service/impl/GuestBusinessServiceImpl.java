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
