/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2022 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2022. Contribute to
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
import java.util.Optional;
import java.util.UUID;

import org.linagora.linshare.core.business.service.ModeratorBusinessService;
import org.linagora.linshare.core.domain.constants.ModeratorRole;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Guest;
import org.linagora.linshare.core.domain.entities.Moderator;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.ModeratorRepository;

public class ModeratorBusinessServiceImpl implements ModeratorBusinessService {

	private final ModeratorRepository  moderatorRepository;

	public ModeratorBusinessServiceImpl(ModeratorRepository moderatorRepository) {
		this.moderatorRepository = moderatorRepository;
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

}
