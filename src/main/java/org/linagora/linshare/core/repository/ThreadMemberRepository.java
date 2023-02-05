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

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;

public interface ThreadMemberRepository extends AbstractRepository<WorkgroupMember> {

	/**
	 * Find ThreadMember by id
	 * 
	 * @param id
	 * @return the ThreadMember
	 */
	public WorkgroupMember findById(long id);

	/**
	 * Find ThreadMember corresponding to a User
	 * 
	 * @param thread
	 * @param user
	 * @return the ThreadMember
	 */
	WorkgroupMember findUserThreadMember(Account thread, User user);

	/**
	 * Find all ThreadMember corresponding to a User
	 * 
	 * @param user
	 * @return the ThreadMember
	 */
	public List<WorkgroupMember> findAllUserMemberships(User user);
	
	/**
	 * Find all ThreadMember corresponding to a User where he's admin
	 * 
	 * @param user
	 * @return the ThreadMember
	 */
	public List<WorkgroupMember> findAllUserAdminMemberships(User user);

	/**
	 * Find if the User is admin of any Thread
	 * 
	 * @param user
	 * @return true if user is admin of any Thread
	 */
	boolean isUserAdminOfAny(User user);
	
	/**
	 * FInd if the user is admin of the thread
	 * 
	 * @param user
	 * @param workGroup
	 * @return true if user is admin of the thread
	 */
	boolean isUserAdmin(User user, WorkGroup workGroup);

	/**
	 * Count the amount of members in the thread
	 * 
	 * @param workGroup
	 * @return the amount of members
	 */
	public long count(WorkGroup workGroup);

	/**
	 * Find members with no deleted accounts
	 * @param workGroup
	 * @return the List of {@link WorkgroupMember}
	 */
	public List<WorkgroupMember> findAllThreadMembers(WorkGroup workGroup);

	public List<String> findAllAccountUuidForThreadMembers(WorkGroup workGroup);

	/**
	 * Find members with deleted accounts
	 * @param workGroup
	 * @return the List of {@link WorkgroupMember}
	 */
	public List<WorkgroupMember> findAllInconsistentThreadMembers(WorkGroup workGroup);
}
