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
package org.linagora.linshare.mongo.repository;

import java.util.List;

import org.linagora.linshare.mongo.entities.PermanentToken;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface JwtLongTimeMongoRepository extends MongoRepository<PermanentToken, String> {
	@Query("{'actor.uuid': ?0}")
	List<PermanentToken> findAllByActorUuid(String actorUuid, Sort sort);

	PermanentToken findByUuid(String uuid);
	
	@Query("{'domain.uuid' : ?0}")
	List<PermanentToken> findAllByDomainUuid(String domainUuid);

	@Query("{'domain.uuid' : {'$in' : ?0 }}")
	List<PermanentToken> findAllByDomainRecursive(List<String> domains);

}
