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

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AccountRepository;
import org.linagora.linshare.core.repository.ModeratorRepository;

public class ModeratorBusinessServiceImpl implements ModeratorBusinessService {

	private final ModeratorRepository  moderatorRepository;

	private final AccountRepository<Account> accountRepository;

	public ModeratorBusinessServiceImpl(
			ModeratorRepository moderatorRepository,
			AccountRepository<Account> accountRepository) {
		this.moderatorRepository = moderatorRepository;
		this.accountRepository = accountRepository;
	}

	@Override
	public Moderator create(Moderator moderator) {
		moderator.setCreationDate(new Date());
		moderator.setModificationDate(new Date());
		moderator.setUuid(UUID.randomUUID().toString());
		return moderatorRepository.create(moderator);
	}

	@Override
	public Moderator find(String uuid) {
		Moderator moderator = moderatorRepository.findByUuid(uuid);
		if (moderator == null) {
			throw new BusinessException(BusinessErrorCode.GUEST_MODERATOR_CANNOT_FIND,
					"Can not found moderator with uuid: " + uuid + ".");
		}
		return moderator;
	}

	@Override
	public Moderator update(Moderator moderator) {
		moderator.setModificationDate(new Date());
		return moderatorRepository.update(moderator);
	}

	@Override
	public Moderator delete(Moderator moderator) {
		moderatorRepository.delete(moderator);
		return moderator;
	}

	@Override
	public List<Moderator> findAllByGuest(Guest guest, ModeratorRole role, String pattern) {
		return moderatorRepository.findAllByGuest(guest, role, pattern);
	}

	@Override
	public Optional<Moderator> findByGuestAndAccount(Account actor, Guest guest) {
		return moderatorRepository.findByGuestAndAccount(actor, guest);
	}

	@Override
	public void deleteAllModerators(Guest guest) {
		moderatorRepository.deleteAllModerators(guest);
	}

	@Override
	public List<String> findAllModeratorUuidsByGuest(Account guest) {
		return accountRepository.findAllModeratorUuidsByGuest(guest);
	}

}
