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
package org.linagora.linShare.core.Facade;

import java.util.List;

import org.linagora.linShare.core.domain.entities.GroupMemberType;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.vo.GroupMemberVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;

public interface GroupFacade {
	public GroupVo findByName(String groupName);
	public List<GroupVo> findByUser(String userLogin);
	public GroupVo create(UserVo owner, String name, String description, String functionalEmail) throws BusinessException;
	public void delete(GroupVo group, UserVo user) throws BusinessException;
	public void update(GroupVo group, UserVo user) throws BusinessException;
	public void addMember(GroupVo group, UserVo manager, UserVo newMember, MailContainer mailContainer) throws BusinessException;
	public void addMember(GroupVo group, UserVo manager, UserVo newMember, GroupMemberType memberType, MailContainer mailContainer) throws BusinessException;
	public void removeMember(GroupVo group, UserVo manager, UserVo member) throws BusinessException;
	public void updateMember(GroupVo group, UserVo manager, UserVo member, GroupMemberType type) throws BusinessException;
	public boolean nameAlreadyExists(String groupName);
	public void acceptNewMember(GroupVo groupVo, GroupMemberVo managerVo, GroupMemberVo memberToAcceptVo, MailContainer mailContainer) throws BusinessException;
	public void rejectNewMember(GroupVo groupVo, GroupMemberVo managerVo, GroupMemberVo memberToRejectVo, MailContainer mailContainer) throws BusinessException;
}
