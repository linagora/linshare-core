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
package org.linagora.linShare.core.Facade.impl;

import java.util.List;

import org.linagora.linShare.core.Facade.GroupFacade;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMemberType;
import org.linagora.linShare.core.domain.entities.GroupUser;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.domain.transformers.impl.GroupTransformer;
import org.linagora.linShare.core.domain.vo.GroupMemberVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.domain.vo.UserVo;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.GroupService;
import org.linagora.linShare.core.service.UserService;

public class GroupFacadeImpl implements GroupFacade {
	private final UserRepository<User> userRepository;
	private final UserService userService;
	private final GroupService groupService;
	private final GroupTransformer groupTransformer;
	
	public GroupFacadeImpl(final UserRepository<User> userRepository,
			final UserService userService,
			final GroupService groupService, final GroupTransformer groupTransformer) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.groupService = groupService;
		this.groupTransformer = groupTransformer;
	}

	public GroupVo findByName(String name) {
		Group group = groupService.findByName(name);
		return groupTransformer.disassemble(group);
	}
	
	public List<GroupVo> findByUser(String userLogin) {
		User user = userRepository.findByLogin(userLogin);
		List<Group> groups = groupService.findByUser(user);
		return groupTransformer.disassembleList(groups);
	}
	
	public GroupVo create(UserVo ownerVo, String name, String description) throws BusinessException {
		User owner = userRepository.findByLogin(ownerVo.getLogin());
		Group group = groupService.create(owner, name, description);
		return groupTransformer.disassemble(group);
	}

	public void delete(GroupVo groupVo, UserVo userVo) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(userVo.getLogin());
		groupService.delete(group, manager);
	}

	public void update(GroupVo groupVo, UserVo userVo) throws BusinessException {
		User manager = userRepository.findByLogin(userVo.getLogin());
		Group group = groupTransformer.assemble(groupVo);
		group.setDescription(groupVo.getDescription());
		groupService.update(group, manager);
	}

	public void addMember(GroupVo groupVo, UserVo managerVo, UserVo newMemberVo) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(managerVo.getLogin());
		User newMember = userService.findAndCreateUser(newMemberVo.getLogin());
		groupService.addMember(group, manager, newMember);
	}
	
	public void addMember(GroupVo groupVo, UserVo managerVo, UserVo newMemberVo,
			GroupMemberType memberType) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(managerVo.getLogin());
		User newMember = userService.findAndCreateUser(newMemberVo.getLogin());
		groupService.addMember(group, manager, newMember, memberType);
	}

	public void removeMember(GroupVo groupVo, UserVo managerVo, UserVo memberVo) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(managerVo.getLogin());
		User member = userRepository.findByLogin(memberVo.getLogin());
		groupService.removeMember(group, manager, member);
	}

	public void updateMember(GroupVo groupVo, UserVo managerVo, UserVo memberVo, GroupMemberType type) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(managerVo.getLogin());
		User member = userRepository.findByLogin(memberVo.getLogin());
		groupService.updateMember(group, manager, member, type);
	}

	public boolean nameAlreadyExists(String groupName) {
		Group groupExistant = groupService.findByName(groupName);
		return (groupExistant!=null);
	}
	public void acceptNewMember(GroupVo groupVo, GroupMemberVo managerVo, GroupMemberVo memberToAcceptVo) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(managerVo.getUserVo().getLogin());
		User memberToAccept = userRepository.findByLogin(memberToAcceptVo.getUserVo().getLogin());
		groupService.acceptNewMember(group, manager, memberToAccept);
	}
	
	public void rejectNewMember(GroupVo groupVo, GroupMemberVo managerVo,
			GroupMemberVo memberToRejectVo) throws BusinessException {
		Group group = groupTransformer.assemble(groupVo);
		User manager = userRepository.findByLogin(managerVo.getUserVo().getLogin());
		User member = userRepository.findByLogin(memberToRejectVo.getUserVo().getLogin());
		groupService.rejectNewMember(group, manager, member);
	}
}
