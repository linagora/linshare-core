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
package org.linagora.linshare.core.repository;

import java.util.List;
import java.util.Optional;

import org.linagora.linshare.core.domain.entities.AbstractDomain;
import org.linagora.linshare.core.domain.entities.OIDCUserProvider;

public interface OIDCUserProviderRepository extends
		AbstractRepository<OIDCUserProvider> {

	Optional<OIDCUserProvider> findByDomainDiscriminator(String discriminator);

	List<OIDCUserProvider> findAllByDomainDiscriminator(List<String> discriminators);

	boolean isDomainDiscriminatorAlreadyInUse(String discriminator, AbstractDomain domain);
}
