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
package org.linagora.linShare.core.service.impl;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linagora.linShare.core.domain.constants.GroupMemberType;
import org.linagora.linShare.core.domain.constants.GroupMembershipStatus;
import org.linagora.linShare.core.domain.entities.Group;
import org.linagora.linShare.core.domain.entities.GroupMember;
import org.linagora.linShare.core.domain.entities.GroupUser;
import org.linagora.linShare.core.domain.entities.MailContainer;
import org.linagora.linShare.core.domain.entities.MailContainerWithRecipient;
import org.linagora.linShare.core.domain.entities.Share;
import org.linagora.linShare.core.domain.entities.User;
import org.linagora.linShare.core.exception.BusinessException;
import org.linagora.linShare.core.exception.TechnicalErrorCode;
import org.linagora.linShare.core.exception.TechnicalException;
import org.linagora.linShare.core.repository.GroupRepository;
import org.linagora.linShare.core.repository.UserRepository;
import org.linagora.linShare.core.service.GroupService;
import org.linagora.linShare.core.service.MailContentBuildingService;
import org.linagora.linShare.core.service.NotifierService;
import org.linagora.linShare.core.service.ShareService;

public class GroupServiceImpl implements GroupService {

	private final GroupRepository groupRepository;
	private final UserRepository<GroupUser> userRepository;
    private final ShareService shareService;
    private final MailContentBuildingService mailElementsFactory;
	private final NotifierService notifierService;

	private static Log logger = LogFactory.getLog(GroupServiceImpl.class);

	public GroupServiceImpl(final GroupRepository groupRepository,
			final UserRepository<GroupUser> userRepository,
			final ShareService shareService,
			final MailContentBuildingService mailElementsFactory,
			final NotifierService notifierService) {
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.shareService = shareService;
		this.mailElementsFactory = mailElementsFactory;
		this.notifierService = notifierService;
	}
	
	@Override
	public Group findByName(String name) {
		return groupRepository.findByName(name);
	}
	
	@Override
	public List<Group> findByUser(User user) {
		return groupRepository.findByUser(user);
	}

	@Override
	public Group create(User owner, String name, String description, String functionalEmail)
			throws BusinessException {
		Group group = new Group();
		
		GroupUser groupUser = new GroupUser(name.toLowerCase()+"@linshare.groups", "", name, name.toLowerCase()+"@linshare.groups");
		groupUser = userRepository.create(groupUser);
		
		GroupMember member = new GroupMember();
		member.setType(GroupMemberType.OWNER);
		member.setUser(owner);
		member.setMembershipDate(GregorianCalendar.getInstance());
		group.addMember(member);
		group.setDescription(description);
		group.setFunctionalEmail(functionalEmail);
		group.setName(name);
		group.setGroupUser(groupUser);
		try {
			group = groupRepository.create(group);
		} catch (IllegalArgumentException e) {
			logger.error("Could not create group " + name + " with owner "
					+ owner.getLogin() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC,
					"Could not create group");

		}
		return group;
	}

	@Override
	public void delete(Group group, User user) throws BusinessException {
		Group groupPersistant = groupRepository.findByName(group.getName());
		try {
			// clearing received shares
			Set<Share> receivedShare = groupPersistant.getGroupUser().getReceivedShares();
			
			// delete group and groupUser
			groupRepository.delete(groupPersistant);		
			
			// refresh share attribute
			for (Share share : receivedShare) {
		    	shareService.refreshShareAttributeOfDoc(share.getDocument());
			}
			receivedShare.clear();
			
		} catch (IllegalArgumentException e) {
			logger.error("Could not delete group " + group.getName() + " by user "
					+ user.getLogin() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't delete the group " + group.getName());
		}
	}

	@Override
	public void update(Group group, User user) throws BusinessException {
		Group persistentGroup = groupRepository.load(group);
		persistentGroup.setDescription(group.getDescription());
		try {
			groupRepository.update(persistentGroup);
		} catch (IllegalArgumentException e) {
			logger.error("Could not update group " + group.getName() + " by user "
					+ user.getLogin() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't update the group " + group.getName());
		}
	}
	
	@Override
	public GroupMember retreiveMember(Group group,User member){
		
		Group groupPersistant = groupRepository.findByName(group.getName());
		Set<GroupMember> members = groupPersistant.getMembers();
		
		GroupMember memberRequesting = null;
		if (member != null) {
			for (GroupMember groupMember : members) {
				if (member.equals(groupMember.getUser())) {
					memberRequesting = groupMember;
				}
			}
		}
		return memberRequesting;
	}

	@Override
	public void addMember(Group group, User manager, User newMember, MailContainer mailContainer) throws BusinessException {
		addMember(group, manager, newMember, GroupMemberType.MEMBER, mailContainer);
	}
	
	@Override
	public void addMember(Group group, User manager, User newMember, GroupMemberType memberType, MailContainer mailContainer) throws BusinessException {
		Group groupPersistant = groupRepository.findByName(group.getName());
		GroupMember newGroupMember = new GroupMember();
		GroupMembershipStatus status = GroupMembershipStatus.WAITING_APPROVAL;
		
		if (memberType==null) {
			memberType=GroupMemberType.MEMBER;
		}
		
		GroupMember memberRequesting = retreiveMember(group,manager);
		
		if (memberRequesting != null && 
				(memberRequesting.getType().equals(GroupMemberType.MANAGER) 
						|| memberRequesting.getType().equals(GroupMemberType.OWNER))) {

			newGroupMember.setType(memberType);
			status = GroupMembershipStatus.ACCEPTED;
		}
		else {
			newGroupMember.setType(GroupMemberType.WAITING_APPROVAL);
		}
		
		newGroupMember.setUser(newMember);
		newGroupMember.setOwner(manager);
		newGroupMember.setMembershipDate(GregorianCalendar.getInstance());
		
		groupPersistant.addMember(newGroupMember);
		try {
			groupPersistant = groupRepository.update(groupPersistant);
		} catch (IllegalArgumentException e) {
			logger.error("Could not add member to group " + group.getName() + " by user "
					+ manager.getLogin() + ", reason : ", e);
			throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't add Member to the group " + group.getName());
		}
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		mailContainerWithRecipient.add(mailElementsFactory.buildMailGroupMembershipStatusWithRecipient(manager, mailContainer, newGroupMember, groupPersistant, status));
		
		mailContainerWithRecipient.add(mailElementsFactory.buildMailNewGroupMemberWithRecipient(manager, mailContainer, newGroupMember, groupPersistant));
		
		notifierService.sendAllNotifications( mailContainerWithRecipient);
	}

	@Override
	public void removeMember(Group group, User manager, User member) throws BusinessException {
		Group groupPersistant = groupRepository.findByName(group.getName());
		GroupMember memberToDelete = null;
		for (GroupMember groupMember : groupPersistant.getMembers()) {
			if (groupMember.getUser().getLogin().equals(member.getLogin())) {
				memberToDelete = groupMember;
				break;
			}
		}
		if (memberToDelete != null) {
			try {
				groupPersistant.removeMember(memberToDelete);
				groupRepository.update(groupPersistant);
			} catch (IllegalArgumentException e) {
				logger.error("Could not delete member of group " + group.getName() + " by user "
						+ manager.getLogin() + ", reason : ", e);
				throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't remove member for the group " + group.getName());
			}
		}
	}

	@Override
	public void updateMember(Group group, User manager, User member, GroupMemberType type) throws BusinessException {
		Group groupPersistant = groupRepository.findByName(group.getName());
		boolean toUpdate = false;
		for (GroupMember groupMember : groupPersistant.getMembers()) {
			if (groupMember.getUser().getLogin().equals(member.getLogin())) {
				groupMember.setType(type);
				toUpdate = true;
				break;
			}
		}
		if (toUpdate) {
			try {
				groupRepository.update(groupPersistant);
			} catch (IllegalArgumentException e) {
				logger.error("Could not update member of group " + group.getName() + " by user "
						+ manager.getLogin() + ", reason : ", e);
				throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't update member for the group " + group.getName());
			}
		}
	}
	
	@Override
	public void acceptNewMember(Group group, User manager, User memberToAccept, MailContainer mailContainer)
			throws BusinessException {
		Group groupPersistant = groupRepository.findByName(group.getName());
		GroupMember groupMemberToUpdate = null;

		boolean toUpdate = false;
		for (GroupMember groupMember : groupPersistant.getMembers()) {
			if (groupMember.getUser().equals(memberToAccept)) {
				groupMemberToUpdate = groupMember;
				groupMember.setType(GroupMemberType.MEMBER);
				toUpdate = true;
				break;
			}
		}
		if (toUpdate) {
			try {
				groupPersistant = groupRepository.update(groupPersistant);
			} catch (IllegalArgumentException e) {
				logger.error("Could not validate membership for group " + group.getName() + " by user "
						+ manager.getLogin() + ", reason : ", e);
				throw new TechnicalException(TechnicalErrorCode.GENERIC, "Couldn't update validate membership for the group " + group.getName());
			}
		}
		
		
		List<MailContainerWithRecipient> mailContainerWithRecipient = new ArrayList<MailContainerWithRecipient>();
		
		mailContainerWithRecipient.add(mailElementsFactory.buildMailGroupMembershipStatusWithRecipient(manager, mailContainer, groupMemberToUpdate, groupPersistant, GroupMembershipStatus.ACCEPTED));
		
		mailContainerWithRecipient.add(mailElementsFactory.buildMailNewGroupMemberWithRecipient(manager, mailContainer, groupMemberToUpdate, groupPersistant));
		
		notifierService.sendAllNotifications(mailContainerWithRecipient);
	}
	
	@Override
	public void rejectNewMember(Group group, User manager, User memberToReject, MailContainer mailContainer) throws BusinessException {

		Group groupPersistant = groupRepository.findByName(group.getName());
		GroupMember groupMemberToUpdate = null;
		String ownerMail = null;

		for (GroupMember groupMember : groupPersistant.getMembers()) {
			if (groupMember.getUser().equals(memberToReject)) {
				groupMemberToUpdate = groupMember;
				ownerMail = groupMember.getOwner().getMail();
				break;
			}
		}
		if (groupMemberToUpdate!=null) {
			removeMember(group, manager, memberToReject);
			if (ownerMail != null) {		
				
				notifierService.sendAllNotifications(mailElementsFactory.buildMailGroupMembershipStatusWithOneRecipient(manager, mailContainer, groupMemberToUpdate, groupPersistant, GroupMembershipStatus.REJECTED));
			}
		}
	}
	
	@Override
	public void deleteAllMembershipOfUser(User userToDelete)
			throws BusinessException {
		List<Group> groups = groupRepository.findByUser(userToDelete);
		for (Group group : groups) {
			for (GroupMember groupMember : group.getMembers()) {
				if (groupMember.getUser().equals(userToDelete)) {
					group.removeMember(groupMember);
					break;
				}
			}
			groupRepository.update(group);
		}
	}
}
