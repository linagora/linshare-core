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
package org.linagora.linshare.core.facade.webservice.user.impl;

import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.MimeType;
import org.linagora.linshare.core.facade.webservice.common.dto.MimeTypeDto;
import org.linagora.linshare.core.facade.webservice.user.MimeTypeFacade;
import org.linagora.linshare.core.service.AccountService;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class MimeTypeFacadeImpl extends GenericFacadeImpl implements MimeTypeFacade {

	public MimeTypeFacadeImpl(final AccountService accountService) {
		super(accountService);
	}

	@Override
	public List<MimeTypeDto> find(String actorUuid, Boolean disabled) {
		Account authUser = checkAuthentication();
		Account actor = getActor(authUser, actorUuid);
		Set<MimeType> mimeTypes = actor.getDomain().getMimePolicy().getMimeTypes();
		List<MimeTypeDto> res = Lists.transform(Lists.newArrayList(mimeTypes), MimeTypeDto.toDto()
				);
		return ImmutableList.copyOf(Iterables.filter(res
					, MimeTypeDto.isMimeTypeDisabled(disabled)));
	}
}