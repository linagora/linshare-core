package org.linagora.linshare.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;

import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagFilter;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.ThreadView;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.repository.AbstractRepository;
import org.linagora.linshare.core.repository.TagRepository;
import org.linagora.linshare.core.repository.ThreadMemberRepository;
import org.linagora.linshare.core.repository.ThreadRepository;
import org.linagora.linshare.core.repository.ThreadViewRepository;
import org.linagora.linshare.core.repository.hibernate.AbstractRepositoryImpl;
import org.linagora.linshare.core.service.ThreadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadServiceImpl implements ThreadService {

	final private static Logger logger = LoggerFactory.getLogger(ThreadServiceImpl.class);
	
	private final ThreadRepository threadRepository;
	
	private final ThreadViewRepository threadViewRepository;
	
	private ThreadMemberRepository threadMemberRepository;

	private TagRepository tagRepository;
    
	
	public ThreadServiceImpl(ThreadRepository threadRepository, ThreadViewRepository threadViewRepository, ThreadMemberRepository threadMemberRepository, TagRepository tagRepository) {
		super();
		this.threadRepository = threadRepository;
		this.threadViewRepository = threadViewRepository;
		this.threadMemberRepository = threadMemberRepository;
		this.tagRepository = tagRepository;
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
	public void addMember(Thread thread, User user, boolean readOnly) {
		ThreadMember member = new ThreadMember(!readOnly, false, user, thread);
		thread.getMyMembers().add(member);
		// XXX : error handling
		try {
			threadRepository.update(thread);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateMember(ThreadMember member, boolean admin, boolean canUpload) {
		member.setAdmin(admin);
		member.setCanUpload(canUpload);
		// XXX : error handling
		try {
			threadMemberRepository.update(member);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteMember(Thread thread, ThreadMember member) {
		thread.getMyMembers().remove(member);
		// XXX : error handling
		try {
			threadRepository.update(thread);
			threadMemberRepository.delete(member);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteAllUserMemberships(User user) {
		// XXX : error handling
		List <ThreadMember> memberships = threadMemberRepository.findAllUserMemberships(user);
		for (ThreadMember threadMember : memberships) {
			deleteMember(threadMember.getThread(), threadMember);
		}
	}
	
	@Override
	public void deleteThreadView(User user, Thread thread, ThreadView threadView) {
		thread.getThreadViews().remove(threadView);
		// XXX : error handling
		try {
			threadRepository.update(thread);
			threadViewRepository.delete(threadView);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void deleteTagFilter(User user, Thread thread, TagFilter filter) {
		thread.getThreadViews().remove(filter);
		// XXX : error handling
		try {
			threadRepository.update(thread);
			// TagRepository.delete(Filer);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteTag(User user, Thread thread, Tag tag) {
		thread.getThreadViews().remove(tag);
		// XXX : error handling
		try {
			threadRepository.update(thread);
			tagRepository.delete(tag);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void deleteThread(User user, Thread thread) {
		// XXX : error handling
		try {
			// Deleting members
			for (ThreadMember member : thread.getMyMembers()) {
				this.deleteMember(thread, member);
			}
			// Deleting views
			thread.setCurrentThreadView(null);
			threadRepository.update(thread);
			for (ThreadView threadView : thread.getThreadViews()) {
				this.deleteThreadView(user, thread, threadView);
			}
			// Deleting tags
			for (Tag tag : thread.getTags()) {
				this.deleteTag(user, thread, tag);
			}
			// Deleting the thread
			threadRepository.delete(thread);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}