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

import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.User;

public interface ThreadRepository extends AccountRepository<WorkGroup> {

	/**
	 * Find all Thread where the actor is member
	 * 
	 * @param actor
	 * @return the list of Thread where actor is member
	 */
	List<WorkGroup> findAllWhereMember(User actor);
	
	/**
	 * Find all Thread where the actor is admin
	 * 
	 * @param actor
	 * @return the list of Thread where actor is admin
	 */
	List<WorkGroup> findAllWhereAdmin(User actor);

	/**
	 * Find all Thread where the actor can upload
	 * 
	 * @param actor
	 * @return the list of Thread where actor can upload
	 */
	List<WorkGroup> findAllWhereCanUpload(User actor);
	
	/**
	 * Find all threads modified by the actor on last 15 days
	 * @param actor
	 * @param limit
	 * @return List<Thread>
	 */
	List<WorkGroup> findLatestWhereMember(User actor, int limit);

	public List<WorkGroup> searchByName(User actor, String pattern);

	public List<WorkGroup> searchAmongMembers(User actor, String pattern);

	List<String> findAllThreadToUpgrade();

	WorkGroup setAsUpgraded(WorkGroup entity);

	List<String> findAllThreadUuid();

	List<String> findByDomainUuid(String domainUuid);

} 
