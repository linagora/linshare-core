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

import java.util.List;

import org.linagora.linshare.core.business.service.GuestBusinessService;
import org.linagora.linshare.core.domain.constants.SupportedLanguage;
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
	public List<Guest> findAllMyGuests(Account owner) {
		return guestRepository.findAllMyGuests(owner);
	}

	@Override
	public List<Guest> findAllOthersGuests(List<AbstractDomain> authorizedDomains, Account owner) {
		return guestRepository.findAllOthersGuests(authorizedDomains, owner);
	}

	@Override
	public List<Guest> findAll(List<AbstractDomain> authorizedDomains) {
		return guestRepository.findAll(authorizedDomains);
	}

	@Override
	public List<String> findOutdatedGuestIdentifiers() {
		return guestRepository.findOutdatedGuestIdentifiers();
	}

	@Override
	public Guest create(Account owner, Guest guest,
			AbstractDomain domain, List<User> allowedContacts) throws BusinessException {
		String password = passwordService.generatePassword();
		String hashedPassword = HashUtils.hashSha1withBase64(password
				.getBytes());
		guest.setMail(guest.getMail().toLowerCase());
		guest.setOwner(owner);
		guest.setDomain(domain);
		guest.setLocale(domain.getDefaultTapestryLocale());
		guest.setCmisLocale(SupportedLanguage.toLanguage(domain.getDefaultTapestryLocale()).getTapestryLocale());
		guest.setExternalMailLocale(SupportedLanguage.toLanguage(domain
				.getDefaultTapestryLocale()));
		guest.setPassword(hashedPassword);
		Guest create = guestRepository.create(guest);
		if (create.isRestricted()) {
			if (allowedContacts == null || allowedContacts.isEmpty()) {
				throw new BusinessException(BusinessErrorCode.GUEST_INVALID_INPUT, "You can not create a restricted guest without a list of contacts.");
			} else {
				for (User contact : allowedContacts) {
					allowedContactRepository.create(new AllowedContact(create,
							contact));
				}
			}
		}
		return create;
	}

	@Override
	public Guest update(Account owner, Guest entity, Guest guest,
			AbstractDomain domain, List<User> allowedContacts)
			throws BusinessException {
		boolean wasRestricted = entity.isRestricted();
		entity.setOwner(owner);
		entity.setDomain(domain);
		// fields that can not be null
		entity.setCanUpload(guest.getCanUpload());
		entity.setRestricted(guest.isRestricted());
		// fields that can be null.
		entity.setComment(guest.getComment());
		entity.setBusinessLocale(guest.getLocale());
		entity.setBusinessExternalMailLocale(guest.getExternalMailLocale());
		entity.setBusinessLastName(guest.getLastName());
		entity.setBusinessFirstName(guest.getFirstName());
		entity.setBusinessMail(guest.getMail());
		entity.setExpirationDate(guest.getExpirationDate());
		Guest update = guestRepository.update(entity);
		if (wasRestricted == guest.isRestricted()) {
			if (allowedContacts != null) {
				if ((allowedContacts.isEmpty() && entity.isRestricted())) {
					throw new BusinessException(BusinessErrorCode.GUEST_INVALID_INPUT, "You can not update a restricted guest without a list of contacts.");
				}
				// update
				allowedContactRepository.purge(update);
				for (User contact : allowedContacts) {
					allowedContactRepository.create(new AllowedContact(update,
							contact));
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
					for (User contact : allowedContacts) {
						allowedContactRepository.create(new AllowedContact(update,
								contact));
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
		String hashedPassword = HashUtils.hashSha1withBase64(password
				.getBytes());
		guest.setPassword(hashedPassword);
		Guest update = guestRepository.update(guest);
		return new GuestWithMetadata(password, update);
	}

	@Override
	public Guest resetPassword(Guest guest, String password) throws BusinessException {
		String hashedPassword = HashUtils.hashSha1withBase64(password.getBytes());
		guest.setPassword(hashedPassword);
		Guest update = guestRepository.update(guest);
		return update;
	}

	@Override
	public void evict(Guest entity) {
		guestRepository.evict(entity);
	}

	@Override
	public List<Guest> search(List<AbstractDomain> authorizedDomains, String firstName, String lastName, String mail, Account owner)
			throws BusinessException {
		return guestRepository.search(authorizedDomains, mail, firstName, lastName, owner);
	}

	@Override
	public List<Guest> search(List<AbstractDomain> authorizedDomains, String pattern) throws BusinessException {
		return guestRepository.search(authorizedDomains, pattern);
	}

	@Override
	public List<Guest> searchMyGuests(List<AbstractDomain> authorizedDomains, String pattern, Account owner)
			throws BusinessException {
		return guestRepository.searchMyGuests(authorizedDomains, pattern, owner);
	}

	@Override
	public List<Guest> searchExceptGuests(List<AbstractDomain> authorizedDomains, String pattern, Account owner)
			throws BusinessException {
		return guestRepository.searchExceptGuests(authorizedDomains, pattern, owner);
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
}
