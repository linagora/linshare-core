/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2013 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2013. Contribute to
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

import java.util.ArrayList;
import java.util.List;

import org.linagora.linshare.core.business.service.DocumentEntryBusinessService;
import org.linagora.linshare.core.domain.constants.LogAction;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagFilter;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadLogEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.ThreadView;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.TagRepository;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.ThreadViewRepository;
import org.linagora.linshare.core.service.LogEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadServiceImpl implements ThreadService {

	final private static Logger logger = LoggerFactory.getLogger(ThreadServiceImpl.class);
	
	private final ThreadRepository threadRepository;
	
	private final ThreadViewRepository threadViewRepository;
	
	private final ThreadMemberRepository threadMemberRepository;
	
	private final DocumentEntryBusinessService documentEntryBusinessService;

	private final TagRepository tagRepository;
	
	private final LogEntryService logEntryService;
    
	
	public ThreadServiceImpl(ThreadRepository threadRepository, ThreadViewRepository threadViewRepository,
			ThreadMemberRepository threadMemberRepository, TagRepository tagRepository,
			DocumentEntryBusinessService documentEntryBusinessService, LogEntryService logEntryService) {
		super();
		this.threadRepository = threadRepository;
		this.threadViewRepository = threadViewRepository;
		this.threadMemberRepository = threadMemberRepository;
		this.tagRepository = tagRepository;
		this.documentEntryBusinessService = documentEntryBusinessService;
		this.logEntryService = logEntryService;
	}
	
	@Override
	public Thread findByLsUuid(String uuid) {
		Thread thread = threadRepository.findByLsUuid(uuid);
		if (thread == null) {
			logger.error("Can't find thread  : " + uuid);
		}
		return thread;
	}

	@Override
	public List<Thread> findAll() {
		List<Thread> all = threadRepository.findAll();
		logger.debug("count : " + all.size());
		return all;
	}

	@Override
	public void create(Account actor, String name) throws BusinessException {
		Thread thread = null;
		ThreadView threadView = null;
		ThreadMember member = null;
		
		thread = new Thread(actor.getDomain(), actor, name);
		threadRepository.create(thread);
		logEntryService.create(new ThreadLogEntry(actor, thread,
				LogAction.THREAD_CREATE, "Creaetion of a new thread."));
		
		// creating default view
		threadView = new ThreadView(thread);
		threadViewRepository.create(threadView);
		thread.getThreadViews().add(threadView);
		threadRepository.update(thread);
		
		// setting default view
		thread.setCurrentThreadView(threadView);
		threadRepository.update(thread);
		
		// creator = first member = default admin
		member = new ThreadMember(true, true, (User)actor, thread);
		thread.getMyMembers().add(member);
		threadRepository.update(thread);
		logEntryService.create(new ThreadLogEntry(actor, member,
				LogAction.THREAD_ADD_MEMBER, "Creating the first member of the newly created thread."));
	}

	@Override
	public ThreadMember getThreadMemberById(String id) throws BusinessException {
		if (id == null) {
			logger.debug("id is null");
			return null;
		}
		return threadMemberRepository.findById(id);
	}

	@Override
	public ThreadMember getThreadMemberFromUser(Thread thread, User user) throws BusinessException {
		if (thread == null || user == null) {
			logger.debug("null parameter");
			return null;
		}
		return threadMemberRepository.findUserThreadMember(thread, user);
	}
	
	@Override
	public List<Thread> getThreadListIfAdmin(User user) throws BusinessException {
		List<ThreadMember> memberships = threadMemberRepository.findAllUserAdminMemberships(user);
		List<Thread> res = new ArrayList<Thread>();
		for (ThreadMember membership : memberships) {
			res.add(membership.getThread());
		}
		return res;
	}

	@Override
	public boolean hasAnyThreadWhereIsAdmin(User user) {
		List<ThreadMember> memberships = threadMemberRepository.findAllUserAdminMemberships(user);
		return memberships != null && !memberships.isEmpty();
	}

	@Override
	public void addMember(Account actor, Thread thread, User user, boolean readOnly) throws BusinessException {
		ThreadMember member = new ThreadMember(!readOnly, false, user, thread);
		thread.getMyMembers().add(member);
		try {
			threadRepository.update(thread);
			logEntryService.create(new ThreadLogEntry(actor, member,
					LogAction.THREAD_ADD_MEMBER, "Adding a new member in a thread."));
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public void updateMember(ThreadMember member, boolean admin, boolean canUpload) throws BusinessException {
		member.setAdmin(admin);
		member.setCanUpload(canUpload);
		try {
			threadMemberRepository.update(member);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void deleteMember(Account actor, Thread thread, ThreadMember member) throws BusinessException {
		thread.getMyMembers().remove(member);
		try {
			ThreadLogEntry log = new ThreadLogEntry(actor, member,
					LogAction.THREAD_REMOVE_MEMBER, "Deleting a member in a thread.");
			threadRepository.update(thread);
			threadMemberRepository.delete(member);
			logEntryService.create(log);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void deleteAllMembers(Account actor, Thread thread) throws BusinessException {
		Object[] myMembers = thread.getMyMembers().toArray();
		
		for (Object threadMember : myMembers) {
			thread.getMyMembers().remove(threadMember);
			try {
				threadRepository.update(thread);
				threadMemberRepository.delete((ThreadMember) threadMember);
			} catch (BusinessException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}
		logEntryService.create(new ThreadLogEntry(actor, thread,
				LogAction.THREAD_REMOVE_MEMBER, "Deleting all members in a thread."));
	}
	
	@Override
	public void deleteAllUserMemberships(User user) throws BusinessException {
		List <ThreadMember> memberships = threadMemberRepository.findAllUserMemberships(user);
		for (ThreadMember threadMember : memberships) {
			deleteMember(null, threadMember.getThread(), threadMember);
		}
	}
	
	@Override
	public void deleteThreadView(User user, Thread thread, ThreadView threadView) throws BusinessException {
		thread.getThreadViews().remove(threadView);
		try {
			threadRepository.update(thread);
			threadViewRepository.delete(threadView);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public void deleteAllThreadViews(User user, Thread thread) throws BusinessException {
		Object[] myThreadViews = thread.getThreadViews().toArray();
		
		for (Object threadView : myThreadViews) {
			thread.getThreadViews().remove(threadView);
			try {
				threadRepository.update(thread);
				threadViewRepository.delete((ThreadView) threadView);
			} catch (BusinessException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}
	}
	
	@Override
	public void deleteTagFilter(User user, Thread thread, TagFilter filter) throws BusinessException {
		thread.getThreadViews().remove(filter);
		try {
			threadRepository.update(thread);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void deleteTag(User user, Thread thread, Tag tag) throws BusinessException {
		thread.getTags().remove(tag);
		try {
			threadRepository.update(thread);
			tagRepository.delete(tag);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	@Override
	public void deleteAllTags(User user, Thread thread) throws BusinessException {
		Object[] myTags = thread.getTags().toArray();
		
		for (Object tag : myTags) {
			thread.getTags().remove(tag);
			try {
				threadRepository.update(thread);
				tagRepository.delete((Tag) tag);
			} catch (BusinessException e) {
				logger.error(e.getMessage());
				throw e;
			}
		}
	}
	
	@Override
	public void deleteThread(User actor, Thread thread) throws BusinessException {
		try {
			ThreadLogEntry log = new ThreadLogEntry(actor, thread,
					LogAction.THREAD_DELETE, "Deleting a thread.");
			// Delete all entries
			documentEntryBusinessService.deleteSetThreadEntry(thread.getEntries());
			thread.setEntries(null);
			threadRepository.update(thread);
			// Deleting members
			this.deleteAllMembers(actor, thread);
			// Deleting views
			thread.setCurrentThreadView(null);
			threadRepository.update(thread);
			this.deleteAllThreadViews(actor, thread);
			// Deleting tags
			this.deleteAllTags(actor, thread);
			// Deleting the thread
			threadRepository.delete(thread);
			logEntryService.create(log);
		} catch (BusinessException e) {
			logger.error(e.getMessage());
			throw e;
		}
	}

	@Override
	public void rename(Thread thread, String threadName) throws BusinessException {
		thread.setName(threadName);
		try {
			threadRepository.update(thread);
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		}
	}
}