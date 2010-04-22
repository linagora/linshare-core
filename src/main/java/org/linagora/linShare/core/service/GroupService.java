/*
 *    This file is part of Linshare.
 *
 *   Linshare is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as
 *   published by the Free Software Foundation, either version 3 of
 *   the License, or (at your option) any later version.
 *
 *   Linshare is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public
 *   License along with Foobar.  If not, see
 *                                    <http://www.gnu.org/licenses/>.
 *
 *   (c) 2008 Groupe Linagora - http://linagora.org
 *
*/
package org.linagora.linShare.core.service;

import java.util.List;

import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMemberType;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;

public interface GroupService {
	public Group findByName(String name);
	public List<Group> findByUser(User user);
	public Group create(User owner, String name, String description, String functionalEmail) throws BusinessException;
	public void delete(Group group, User user) throws BusinessException;
	public void update(Group group, User user) throws BusinessException;
	public void addMember(Group group, User manager, User newMember, MailContainer mailContainer) throws BusinessException;
	public void addMember(Group group, User manager, User newMember, GroupMemberType memberType, MailContainer mailContainer) throws BusinessException;
	public void removeMember(Group group, User manager, User member) throws BusinessException;
	public void updateMember(Group group, User manager, User member, GroupMemberType type) throws BusinessException;
	public void acceptNewMember(Group group, User manager, User memberToAccept, MailContainer mailContainer) throws BusinessException;
	public void rejectNewMember(Group group, User manager, User memberToReject, MailContainer mailContainer) throws BusinessException;

}
