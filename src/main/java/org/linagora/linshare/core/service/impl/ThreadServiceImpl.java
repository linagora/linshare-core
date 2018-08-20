/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2015-2018 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2018. Contribute to
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
package org.linagora.linshare.core.service.impl;

import java.util.List;

import org.apache.commons.lang.Validate;
import org.linagora.linshare.core.business.service.AccountQuotaBusinessService;
import org.linagora.linshare.core.business.service.ContainerQuotaBusinessService;
import org.linagora.linshare.core.domain.constants.AuditLogEntryType;
import org.linagora.linshare.core.domain.constants.ContainerQuotaType;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.AccountQuota;
import org.linagora.linshare.core.domain.entities.ContainerQuota;
import org.linagora.linshare.core.domain.entities.Functionality;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.entities.WorkGroup;
import org.linagora.linshare.core.domain.entities.WorkgroupMember;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.rac.ThreadResourceAccessControl;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.UserRepository;
import org.linagora.linshare.core.service.FunctionalityReadOnlyService;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.WorkGroupNodeService;
import org.linagora.linshare.mongo.entities.WorkGroupNode;
import org.linagora.linshare.mongo.entities.logs.AuditLogEntryUser;
import org.linagora.linshare.mongo.entities.logs.ThreadAuditLogEntry;
import org.linagora.linshare.mongo.entities.logs.ThreadMemberAuditLogEntry;
import org.linagora.linshare.mongo.entities.mto.ThreadMemberMto;
import org.linagora.linshare.mongo.entities.mto.ThreadMto;

import com.google.common.collect.Lists;

public class ThreadServiceImpl extends GenericServiceImpl<Account, WorkGroup> implements ThreadService {

	private final ThreadRepository threadRepository;

	private final ThreadMemberRepository threadMemberRepository;

	private final LogEntryService logEntryService;

	private final UserRepository<User> userRepository;

	private final FunctionalityReadOnlyService functionalityReadOnlyService;

	private final AccountQuotaBusinessService accountQuotaBusinessService;

	private final ContainerQuotaBusinessService containerQuotaBusinessService;

	private final WorkGroupNodeService workGroupNodeService;

	public ThreadServiceImpl(
			ThreadRepository threadRepository,
			ThreadMemberRepository threadMemberRepository,
			LogEntryService logEntryService,
			ThreadResourceAccessControl rac,
			UserRepository<User> userRepository,
			FunctionalityReadOnlyService functionalityReadOnlyService,
			AccountQuotaBusinessService accountQuotaBusinessService,
			ContainerQuotaBusinessService containerQuotaBusinessService,
			WorkGroupNodeService workGroupNodeService) {
		super(rac);
		this.threadRepository = threadRepository;
		this.threadMemberRepository = threadMemberRepository;
		this.logEntryService = logEntryService;
		this.userRepository = userRepository;
		this.functionalityReadOnlyService = functionalityReadOnlyService;
		this.accountQuotaBusinessService = accountQuotaBusinessService;
		this.containerQuotaBusinessService = containerQuotaBusinessService;
		this.workGroupNodeService = workGroupNodeService;
	}

	@Override
	public WorkGroup find(Account actor, Account owner, String uuid) {
		preChecks(actor, owner);
		Validate.notEmpty(uuid, "Missing thread uuid");
		WorkGroup workGroup = threadRepository.findByLsUuid(uuid);

		if (workGroup == null) {
			logger.error("Can't find thread  : " + uuid);
			logger.error("Current actor " + actor.getAccountRepresentation()
					+ " is looking for a misssing thread (" + uuid
					+ ") owned by : " + owner.getAccountRepresentation());
			String message = "Can not find thread with uuid : " + uuid;
			throw new BusinessException(
					BusinessErrorCode.THREAD_NOT_FOUND, message);
		}
		return workGroup;
	}

	@Override
	public WorkGroup findByLsUuidUnprotected(String uuid) {
		WorkGroup workGroup = threadRepository.findByLsUuid(uuid);
		if (workGroup == null) {
			logger.error("Can't find thread  : " + uuid);
		}
		return workGroup;
	}

	@Override
	public List<WorkGroup> findAll(Account actor, Account owner) {
		return threadRepository.findAll();
	}

	@Override
	public WorkGroup create(Account actor, Account owner, String name) throws BusinessException {
		Functionality threadFunc = functionalityReadOnlyService.getWorkGroupFunctionality(owner.getDomain());
		Functionality threadCreation = functionalityReadOnlyService.getWorkGroupCreationRight(owner.getDomain());
		if (!threadFunc.getActivationPolicy().getStatus()
				|| !threadCreation.getActivationPolicy().getStatus()) {
			throw new BusinessException(BusinessErrorCode.THREAD_FORBIDDEN, "Functionality forbideen.");
		}
		WorkGroup workGroup = null;
		WorkgroupMember member = null;
		logger.debug("User " + owner.getAccountRepresentation() + " trying to create new thread named " + name);
		workGroup = new WorkGroup(owner.getDomain(), owner, name);
		threadRepository.create(workGroup);
		createQuotaThread(workGroup);
		member = new WorkgroupMember(true, true, (User) owner, workGroup);
		workGroup.getMyMembers().add(member);
		workGroup = threadRepository.update(workGroup);
		// workgroup creation
		ThreadAuditLogEntry log = new ThreadAuditLogEntry(actor, owner, LogAction.CREATE, AuditLogEntryType.WORKGROUP,
				new ThreadMto(workGroup, false));
		logEntryService.insert(log);
		// first workgroup member
		ThreadMemberAuditLogEntry log2 = new ThreadMemberAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_MEMBER, member);
		logEntryService.insert(log2);
		return workGroup;
	}

	@Override
	public WorkgroupMember getThreadMemberById(long id) throws BusinessException {
		return threadMemberRepository.findById(id);
	}

	@Override
	public WorkgroupMember getMemberFromUser(WorkGroup workGroup, User user) throws BusinessException {
		return threadMemberRepository.findUserThreadMember(workGroup, user);
	}

	@Override
	public List<WorkgroupMember> findAllThreadMembers(Account actor, User owner,
			WorkGroup workGroup) throws BusinessException {
		return threadMemberRepository.findAllThreadMembers(workGroup);
	}

	@Override
	public List<WorkgroupMember> findAllInconsistentMembers(Account actor, User owner,
			WorkGroup workGroup) throws BusinessException {
		return threadMemberRepository.findAllInconsistentThreadMembers(workGroup);
	}

	@Override
	public List<WorkGroup> findAllWhereMember(User user) {
		return threadRepository.findAllWhereMember(user);
	}

	@Override
	public List<WorkGroup> findAllWhereAdmin(User user) {
		return threadRepository.findAllWhereAdmin(user);
	}

	@Override
	public List<WorkGroup> findAllWhereCanUpload(User user) {
		return threadRepository.findAllWhereCanUpload(user);
	}

	@Override
	public boolean hasAnyWhereAdmin(User user) {
		return threadMemberRepository.isUserAdminOfAny(user);
	}

//	@Override
//	public boolean isUserAdmin(User user, WorkGroup workGroup) {
//		return threadMemberRepository.isUserAdmin(user, workGroup);
//	}

	@Override
	public long countMembers(WorkGroup workGroup) {
		return threadMemberRepository.count(workGroup);
	}

	@Override
	public WorkgroupMember addMember(Account actor, Account owner, WorkGroup workGroup,
			User user, boolean admin, boolean canUpload)
			throws BusinessException {
		// TODO : Remove me !
		WorkgroupMember member = new WorkgroupMember(canUpload, admin, user, workGroup);
		if (getMemberFromUser(workGroup, user) != null) {
			logger.warn("The current " + user.getAccountRepresentation()
					+ " user is already member of the workgroup : "
					+ workGroup.getAccountRepresentation());
			throw new BusinessException(BusinessErrorCode.THREAD_MEMBER_ALREADY_EXISTS,
					"You are not authorized to add member to this workgroup. Already exists.");
		}
		workGroup.getMyMembers().add(member);
		threadRepository.update(workGroup);
		ThreadMemberAuditLogEntry log = new ThreadMemberAuditLogEntry(actor, owner, LogAction.CREATE,
				AuditLogEntryType.WORKGROUP_MEMBER, member);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
//		WorkGroupWarnNewMemberEmailContext context = new WorkGroupWarnNewMemberEmailContext(member, owner);
//		MailContainerWithRecipient mail = mailBuildingService.build(context);
//		notifierService.sendNotification(mail, true);
		return member;
	}

	@Override
	public WorkgroupMember updateMember(Account actor, Account owner, String workGroupUuid, String userUuid,
			boolean admin, boolean canUpload)
			throws BusinessException {
		WorkGroup workGroup = find(actor, owner, workGroupUuid);
		User user = getUserMember(userUuid);
		WorkgroupMember member = getMemberFromUser(workGroup, user);
		ThreadMemberAuditLogEntry log = new ThreadMemberAuditLogEntry(actor, owner, LogAction.UPDATE,
				AuditLogEntryType.WORKGROUP_MEMBER, member);
		addMembersToLog(workGroup, log);
		member.setAdmin(admin);
		member.setCanUpload(canUpload);
		WorkgroupMember res = threadMemberRepository.update(member);
		log.setResourceUpdated(new ThreadMemberMto(res));
		logEntryService.insert(log);
//		WorkGroupWarnUpdatedMemberEmailContext context = new WorkGroupWarnUpdatedMemberEmailContext(member, owner);
//		MailContainerWithRecipient mail = mailBuildingService.build(context);
//		notifierService.sendNotification(mail, true);
		return res;
	}

	@Override
	public WorkgroupMember deleteMember(Account actor, Account owner, String threadUuid,
			String userUuid) throws BusinessException {
		preChecks(actor, owner);
		Validate.notEmpty(userUuid);
		Validate.notEmpty(threadUuid);
		WorkGroup workGroup = find(actor, owner, threadUuid);
		User user = getUserMember(userUuid);
		WorkgroupMember member = getMemberFromUser(workGroup,	user);
		workGroup.getMyMembers().remove(member);
		threadRepository.update(workGroup);
		threadMemberRepository.delete(member);
		ThreadMemberAuditLogEntry log = new ThreadMemberAuditLogEntry(actor, owner, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP_MEMBER, member);
		addMembersToLog(workGroup, log);
		logEntryService.insert(log);
//		WorkGroupWarnDeletedMemberEmailContext context = new WorkGroupWarnDeletedMemberEmailContext(member, owner);
//		MailContainerWithRecipient mail = mailBuildingService.build(context);
//		notifierService.sendNotification(mail, true);
		return member;
	}

	private User getUserMember(String userUuid) {
		User user = userRepository.findByLsUuid(userUuid);
		if (user == null) {
			user = userRepository.findDeleted(userUuid);
			if (user == null) {
				throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "Can not find user with uuid : " + userUuid);
			} else {
				logger.info("The member with uuid " + userUuid
						+ " you are trying to delete is already deleted");
			}
		}
		return user;
	}

	@Override
	public void deleteAllMembers(Account actor, WorkGroup workGroup) throws BusinessException {
		// permission check
//		checkUserIsAdmin(actor, workGroup);
		Object[] myMembers = workGroup.getMyMembers().toArray();
		List<AuditLogEntryUser> audits = Lists.newArrayList();
		for (Object threadMember : myMembers) {
			workGroup.getMyMembers().remove(threadMember);
			threadRepository.update(workGroup);
			threadMemberRepository.delete((WorkgroupMember) threadMember);
			ThreadMemberAuditLogEntry log = new ThreadMemberAuditLogEntry(actor, actor, LogAction.DELETE,
					AuditLogEntryType.WORKGROUP_MEMBER, (WorkgroupMember) threadMember);
			addMembersToLog(workGroup, log);
			audits.add(log);
		}
		logEntryService.insert(audits);
	}

	@Override
	public void deleteAllUserMemberships(Account actor, User user)
			throws BusinessException {
		List<WorkgroupMember> memberships = threadMemberRepository
				.findAllUserMemberships(user);
		for (WorkgroupMember threadMember : memberships) {
			deleteMember(actor, actor, threadMember.getThread().getLsUuid(),
					threadMember.getUser().getLsUuid());
		}
	}

	@Override
	public void deleteThread(User actor, Account owner, WorkGroup workGroup)
			throws BusinessException {
		User owner2 = (User) owner;
		ThreadAuditLogEntry threadAuditLog = new ThreadAuditLogEntry(actor, owner, LogAction.DELETE,
				AuditLogEntryType.WORKGROUP, new ThreadMto(workGroup, true));
		addMembersToLog(workGroup, threadAuditLog);
		WorkGroupNode rootFolder = workGroupNodeService.getRootFolder(actor, owner2, workGroup);
		workGroupNodeService.delete(actor, owner2, workGroup, rootFolder.getUuid());
		workGroup.setEntries(null);
		threadRepository.update(workGroup);
		// Deleting members
		this.deleteAllMembers(actor, workGroup);
		// Deleting the thread
		threadRepository.delete(workGroup);
		logEntryService.insert(threadAuditLog);
	}

	@Override
	public WorkGroup update(User actor, Account owner, String threadUuid,
			String threadName) throws BusinessException {
		WorkGroup workGroup = find(actor, owner, threadUuid);
		ThreadAuditLogEntry log = new ThreadAuditLogEntry(actor, owner, LogAction.UPDATE, AuditLogEntryType.WORKGROUP,
				new ThreadMto(workGroup, true));
		workGroup.setName(threadName);
		addMembersToLog(workGroup, log);
		WorkGroup update = threadRepository.update(workGroup);
		User owner2 = (User) owner;
		WorkGroupNode rootFolder = workGroupNodeService.getRootFolder(actor, owner2, workGroup);
		rootFolder.setName(threadName);
		workGroupNodeService.update(actor, owner2, workGroup, rootFolder);
		log.setResourceUpdated(new ThreadMto(update, true));
		logEntryService.insert(log);
		return update;
	}

	@Override
	public List<WorkGroup> findLatestWhereMember(User actor, int limit) {
		return threadRepository.findLatestWhereMember(actor, limit);
	}

	@Override
	public List<WorkGroup> searchByName(User actor, String pattern) {
		return threadRepository.searchByName(actor, pattern);
	}

	@Override
	public List<WorkGroup> searchByMembers(User actor, String pattern) {
		return threadRepository.searchAmongMembers(actor, pattern);
	}

	private void createQuotaThread(WorkGroup workGroup) throws BusinessException {
		Validate.notNull(workGroup, "Thread must be set.");
		ContainerQuota containerQuota = containerQuotaBusinessService.find(workGroup.getDomain(), ContainerQuotaType.WORK_GROUP);
		if (containerQuota == null) {
			throw new BusinessException("Missing container quota entity for current work_group");
		}
		AccountQuota threadQuota = new AccountQuota(
				workGroup.getDomain(),
				workGroup.getDomain().getParentDomain(),
				workGroup, containerQuota);
		threadQuota.setDomainShared(containerQuota.getDomainQuota().getDomainShared());
		threadQuota.setDomainSharedOverride(containerQuota.getDomainQuota().getDomainSharedOverride());
		accountQuotaBusinessService.create(threadQuota);
	}

    /* ***********************************************************
     *                   Helpers
     ************************************************************ */

	protected void addMembersToLog(WorkGroup workGroup, AuditLogEntryUser log) {
		List<String> members = threadMemberRepository.findAllAccountUuidForThreadMembers(workGroup);
		log.addRelatedAccounts(members);
	}

	@Override
	public boolean isUserAdmin(User user, WorkGroup workGroup) {
		return false;
	}

	/**
	 * Check if actor is admin of the thread and so has the right to perform any action.
	 * Throw a BusinessException if the actor isn't authorized to modify the thread.
	 */
//	private void checkUserIsAdmin(Account actor, WorkGroup workGroup) throws BusinessException {
//		if (actor.getRole().equals(Role.SUPERADMIN) || actor.getRole().equals(Role.SYSTEM)) {
//			return; // superadmin or system accounts have all rights
//		}
//		if (!isUserAdmin((User) actor, workGroup)) {
//			logger.error("Actor: " + actor.getAccountRepresentation() + " isn't admin of the Thread: "
//					+ workGroup.getAccountRepresentation());
//			throw new BusinessException(BusinessErrorCode.FORBIDDEN,
//					"you are not authorized to perform this action on this thread.");
//		}
//	}
}
