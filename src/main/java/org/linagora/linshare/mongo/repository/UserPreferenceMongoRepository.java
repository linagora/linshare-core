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

import org.linagora.linshare.mongo.entities.UserPreference;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserPreferenceMongoRepository extends MongoRepository<UserPreference, String> {

	/**
	 * Find one preference by its uuid.
	 * 
	 * @param uuid : pref uuid
	 * @return UserPreference
	 */
	UserPreference findByUuid(String uuid);

	/**
	 * Find all preferences related to one key.
	 * 
	 * @param key : pref key
	 * @return List of UserPreference
	 */
	List<UserPreference> findByKey(String key);

	/**
	 * Find a specific key for an account. It is unique.
	 * 
	 * @param accountUuid : owner pref uuid
	 * @param key : pref key
	 * @return UserPreference
	 */
	UserPreference findByAccountUuidAndKey(String accountUuid, String key);

	/**
	 * Find all preferences for one account
	 * 
	 * @param accountUuid : owner pref uuid
	 * @return List of UserPreference
	 */
	List<UserPreference> findByAccountUuid(String accountUuid);

	/**
	 * Find all account's preferences from one domain.
	 * 
	 * @param domainUuid  : domain uuid
	 * @return List of UserPreference
	 */
	List<UserPreference> findByDomainUuid(String domainUuid);

	/**
	 * It is to check if the UserPreference uuid is really owner by the current
	 * account.
	 * 
	 * @param accountUuid : owner pref uuid
	 * @param uuid : pref uuid
	 * @return UserPreference
	 */
	UserPreference findByAccountUuidAndUuid(String accountUuid, String uuid);

	void deleteByDomainUuid(String domainUuid);

	void deleteByAccountUuid(String accountUuid);

}
