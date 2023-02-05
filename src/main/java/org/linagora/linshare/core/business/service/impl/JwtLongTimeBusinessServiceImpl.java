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

import java.util.List;

import org.linagora.linshare.core.business.service.JwtLongTimeBusinessService;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.mongo.entities.PermanentToken;
import org.linagora.linshare.mongo.repository.JwtLongTimeMongoRepository;
import org.springframework.data.domain.Sort;

public class JwtLongTimeBusinessServiceImpl implements JwtLongTimeBusinessService{

	private final String CREATION_DATE = "creationDate";

	protected JwtLongTimeMongoRepository jwtLongTimeMongoRepository;

	public JwtLongTimeBusinessServiceImpl(JwtLongTimeMongoRepository jwtLongTimeMongoRepository) {
		super();
		this.jwtLongTimeMongoRepository = jwtLongTimeMongoRepository;
	}

	@Override
	public void create(PermanentToken entity) {
		jwtLongTimeMongoRepository.insert(entity);
	}

	@Override
	public List<PermanentToken> findAll(Account actor) {
		Sort sort = Sort.by(Sort.Direction.DESC, CREATION_DATE);
		List<PermanentToken> tokens = jwtLongTimeMongoRepository.findAllByActorUuid(actor.getLsUuid(), sort);
		return tokens;
	}

	@Override
	public void delete(PermanentToken entity) {
		jwtLongTimeMongoRepository.delete(entity);
	}

	@Override
	public PermanentToken find(String uuid) {
		return jwtLongTimeMongoRepository.findByUuid(uuid);
	}

	@Override
	public List<PermanentToken> findAllByDomain(String domainUuid) {
		return jwtLongTimeMongoRepository.findAllByDomainUuid(domainUuid);
	}

	@Override
	public List<PermanentToken> findAllByDomainRecursive(List<String> domains) {
		return jwtLongTimeMongoRepository.findAllByDomainRecursive(domains);
	}

	@Override
	public PermanentToken update(PermanentToken found) {
		return jwtLongTimeMongoRepository.save(found);
	}

}
