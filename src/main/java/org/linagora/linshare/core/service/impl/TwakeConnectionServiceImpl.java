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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.linagora.linshare.core.business.service.SanitizerInputHtmlBusinessService;
import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.TwakeConnection;
import org.linagora.linshare.core.repository.AbstractDomainRepository;
import org.linagora.linshare.core.repository.hibernate.TwakeConnectionRepositoryImpl;

public class TwakeConnectionServiceImpl extends RemoteServerServiceImpl<TwakeConnection> {

	public TwakeConnectionServiceImpl(
			TwakeConnectionRepositoryImpl twakeConnectionRepositoryImpl,
			SanitizerInputHtmlBusinessService sanitizerInputHtmlBusinessService,
			AbstractDomainRepository abstractDomainRepository) {
		super(twakeConnectionRepositoryImpl, sanitizerInputHtmlBusinessService, abstractDomainRepository);
	}

	@Override
	protected void updateFields(TwakeConnection remoteServer, TwakeConnection updateRemoteServer) {
		updateRemoteServer.setClientId(remoteServer.getClientId());
		updateRemoteServer.setClientSecret(remoteServer.getClientSecret());
	}

	@Override
	public List<AbstractDomain> findAllDomainsByRemoteServer(TwakeConnection twakeConnection) {
		Validate.notNull(twakeConnection, "Twake connection must be set.");
		return abstractDomainRepository.findAllDomainsByTwakeConnection(twakeConnection);
	}
}
