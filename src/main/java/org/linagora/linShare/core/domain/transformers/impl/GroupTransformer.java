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
package org.linagora.linShare.core.domain.transformers.impl;

import java.util.ArrayList;
import java.util.List;

import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.GroupMemberType;
import org.linagora.linShare.core.domain.transformers.Transformer;
import org.linagora.linShare.core.domain.vo.GroupMemberVo;
import org.linagora.linShare.core.domain.vo.GroupVo;
import org.linagora.linShare.core.repository.GroupRepository;

public class GroupTransformer implements Transformer<Group, GroupVo> {
	
	private final UserTransformer userTransformer;
	private final GroupRepository groupRepository;
	
	
	public GroupTransformer(final UserTransformer userTransformer,
			final GroupRepository groupRepository) {
		this.userTransformer = userTransformer;
		this.groupRepository = groupRepository;
	}

	public Group assemble(GroupVo valueObject) {
		Group group = groupRepository.findByName(valueObject.getName());

//		for (GroupMemberVo memberVo : valueObject.getMembers()) {
//			GroupMember member = new GroupMember();
//			String login = memberVo.getUserVo().getLogin();
//			member.setType(memberVo.getType());
//			member.setUser(userRepository.findByLogin(login));
//			group.addMember(member);
//		}

		return group;
	}


	public List<Group> assembleList(List<GroupVo> valueObjectList) {
		List<Group> groups = new ArrayList<Group>();
		for (GroupVo groupVo : valueObjectList) {
			groups.add(assemble(groupVo));
		}
		return groups;
	}


	public GroupVo disassemble(Group entityObject) {
		GroupVo groupVo = new GroupVo();
		String ownerLogin=null;
		groupVo.setDescription(entityObject.getDescription());
		groupVo.setName(entityObject.getName());
		for (GroupMember member : entityObject.getMembers()) {
			GroupMemberVo memberVo = new GroupMemberVo();
			String login = member.getUser().getLogin();
			if (member.getType().equals(GroupMemberType.OWNER)) {
				ownerLogin = login;
			}
			memberVo.setType(member.getType());
			memberVo.setMembershipDate(member.getMembershipDate());
			memberVo.setUserVo(userTransformer.disassemble(member.getUser()));
			groupVo.addMember(memberVo);
		}
		groupVo.setOwnerLogin(ownerLogin);
		return groupVo;
	}


	public List<GroupVo> disassembleList(List<Group> entityObjectList) {
		List<GroupVo> groups = new ArrayList<GroupVo>();
		for (Group group : entityObjectList) {
			groups.add(disassemble(group));
		}
		return groups;
	}

}
