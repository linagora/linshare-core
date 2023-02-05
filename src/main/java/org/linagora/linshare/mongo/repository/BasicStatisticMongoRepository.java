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

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.BasicStatisticType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.mongo.entities.BasicStatistic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BasicStatisticMongoRepository extends MongoRepository<BasicStatistic, String> {

	@Query("{ 'domainUuid' : ?0 ,'action' : {'$in' : ?1 }, 'creationDate' : { '$gt' : ?2 , '$lt' : ?3}, 'resourceType' : { '$in' : ?4 } , 'type' : ?5 }")
	Set<BasicStatistic> findBetweenTwoDates(String domainUuid, List<LogAction> actions, Date beginDate, Date endDate,
			List<AuditLogEntryType> resourceType, BasicStatisticType type);

	@Query(value = "{ 'domainUuid' : ?0 ,'action' : ?1 , 'creationDate' : { '$gt' : ?2 , '$lt' : ?3}, 'resourceType' : ?4  , 'type' : ?5 }}", count = true)
	Long countBasicStatistic(String domainUuid, LogAction action, Date beginDate, Date endDate,
			AuditLogEntryType resourceType, BasicStatisticType type);

	@Query(value = "{'creationDate' : { '$lt' : ?0}}", count = true)
	Long countBeforeDate(Date endDate);

	BasicStatistic findCreationDateByOrderByIdAsc();
}