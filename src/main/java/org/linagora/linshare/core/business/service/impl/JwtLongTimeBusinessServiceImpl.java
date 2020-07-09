/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2018-2020 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2020. Contribute to
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
