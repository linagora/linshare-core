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
package org.linagora.linshare.core.repository.hibernate;

import javax.annotation.Nonnull;
import org.linagora.linshare.core.domain.constants.LinShareConstants;
import org.linagora.linshare.core.domain.entities.Root;
import org.linagora.linshare.core.repository.RootUserRepository;
import org.springframework.orm.hibernate5.HibernateTemplate;

public class RootUserRepositoryImpl  extends GenericUserRepositoryImpl<Root> implements RootUserRepository {

	public RootUserRepositoryImpl(HibernateTemplate hibernateTemplate) {
		super(hibernateTemplate);
	}

	@Override
	public Root findByLogin(String login) {
		return super.findByDomainAndMail(LinShareConstants.rootDomainIdentifier, login);
	}

	@Override
	public Root findByDomainAndMail(@Nonnull final String domainUuid, @Nonnull final String mail) {
		return super.findByDomainAndMail(LinShareConstants.rootDomainIdentifier, mail);
	}
}
