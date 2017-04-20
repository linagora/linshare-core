/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2015. Contribute to
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
package org.linagora.linshare.core.repository;

import java.util.List;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;

public interface ThreadMemberRepository extends AbstractRepository<ThreadMember> {

	/**
	 * Find ThreadMember by id
	 * 
	 * @param id
	 * @return the ThreadMember
	 */
	public ThreadMember findById(long id);

	/**
	 * Find ThreadMember corresponding to a User
	 * 
	 * @param thread
	 * @param user
	 * @return the ThreadMember
	 */
	ThreadMember findUserThreadMember(Account thread, User user);

	/**
	 * Find all ThreadMember corresponding to a User
	 * 
	 * @param user
	 * @return the ThreadMember
	 */
	public List<ThreadMember> findAllUserMemberships(User user);
	
	/**
	 * Find all ThreadMember corresponding to a User where he's admin
	 * 
	 * @param user
	 * @return the ThreadMember
	 */
	public List<ThreadMember> findAllUserAdminMemberships(User user);

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
	 * @param thread
	 * @return true if user is admin of the thread
	 */
	boolean isUserAdmin(User user, Thread thread);

	/**
	 * Count the amount of members in the thread
	 * 
	 * @param thread
	 * @return the amount of members
	 */
	public long count(Thread thread);

	/**
	 * Find members with no deleted accounts
	 *
	 * @param thread
	 * @return the List of ThreadMember
	 */
	public List<ThreadMember> findAllThreadMembers(Thread thread);
	public List<String> findAllAccountUuidForThreadMembers(Thread thread);

	/**
	 * Find members with deleted accounts
	 *
	 * @param thread
	 * @return the List of ThreadMember
	 */
	public List<ThreadMember> findAllInconsistentThreadMembers(Thread thread);
}
