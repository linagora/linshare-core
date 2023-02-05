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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linagora.linshare.core.domain.constants.SupportedLanguage;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WelcomeMessages;
import org.linagora.linshare.core.domain.entities.WelcomeMessagesEntry;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.webservice.user.WelcomeMessagesFacade;
import org.linagora.linshare.core.service.AccountService;

import com.google.common.collect.Lists;

public class WelcomeMessagesFacadeImpl extends UserGenericFacadeImp implements WelcomeMessagesFacade {

	public WelcomeMessagesFacadeImpl(AccountService accountService) {
		super(accountService);
	}

	@Override
	public List<Map<SupportedLanguage, String>> findAll(String actorUuid) throws BusinessException {
		User authUser = checkAuthentication();
		User actor = getActor(authUser, actorUuid);
		WelcomeMessages currentWelcomeMessage = actor.getDomain().getCurrentWelcomeMessage();
		List<Map<SupportedLanguage, String>> resList = Lists.newArrayList();
		Map<SupportedLanguage, String> res = new HashMap<SupportedLanguage, String>();
		for (WelcomeMessagesEntry entry : currentWelcomeMessage
				.getWelcomeMessagesEntries().values()) {
			res.put(entry.getLang(), entry.getValue());
		}
		resList.add(res);
		return resList;
	}

}
