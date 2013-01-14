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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.TagType;
import org.linagora.linshare.core.domain.entities.Account;
import org.linagora.linshare.core.domain.entities.DocumentEntry;
import org.linagora.linshare.core.domain.entities.Tag;
import org.linagora.linshare.core.domain.entities.TagEnum;
import org.linagora.linshare.core.domain.entities.Thread;
import org.linagora.linshare.core.domain.entities.ThreadEntry;
import org.linagora.linshare.core.domain.entities.ThreadMember;
import org.linagora.linshare.core.domain.entities.User;
import org.linagora.linshare.core.domain.transformers.impl.ThreadEntryTransformer;
import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.TagEnumVo;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessErrorCode;
import org.linagora.linshare.core.exception.BusinessException;
import org.linagora.linshare.core.facade.ThreadEntryFacade;
import org.linagora.linshare.core.service.AccountService;
import org.linagora.linshare.core.service.DocumentEntryService;
import org.linagora.linshare.core.service.TagService;
import org.linagora.linshare.core.service.ThreadEntryService;
import org.linagora.linshare.core.service.ThreadService;
import org.linagora.linshare.core.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadEntryFacadeImpl implements ThreadEntryFacade {

	private static final Logger logger = LoggerFactory.getLogger(ThreadEntryFacadeImpl.class);

	private final AccountService accountService;
	
	private final ThreadService threadService;
	
	private final ThreadEntryService threadEntryService;

	private final ThreadEntryTransformer threadEntryTransformer;
	
	private final TagService tagService;
	
	private final DocumentEntryService documentEntryService;
	
	public ThreadEntryFacadeImpl(AccountService accountService, ThreadService threadService, ThreadEntryService threadEntryService, ThreadEntryTransformer threadEntryTransformer,
			TagService tagService, DocumentEntryService documentEntryService) {
		super();
		this.accountService = accountService;
		this.threadService = threadService;
		this.threadEntryService = threadEntryService;
		this.threadEntryTransformer = threadEntryTransformer;
		this.tagService = tagService;
		this.documentEntryService = documentEntryService;
	}


	@Override
	public ThreadEntryVo insertFile(UserVo actorVo, ThreadVo threadVo, InputStream stream, Long size, String fileName) throws BusinessException {
		logger.debug("insert file for thread entries");
		
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor, thread, stream, size, fileName);

		return new ThreadEntryVo(threadEntry);
	}
	
	@Override
	public void copyDocinThread(UserVo actorVo, ThreadVo threadVo, DocumentVo documentVo) throws BusinessException {
		Account owner = accountService.findByLsUid(documentVo.getOwnerLogin());
		InputStream stream = documentEntryService.getDocumentStream(owner , documentVo.getIdentifier());
		insertFile(actorVo, threadVo, stream, documentVo.getSize(), documentVo.getFileName());
	}

	@Override
	public List<ThreadVo> getAllThread() {
		List<Thread> all = threadService.findAll();
		List<ThreadVo> res = new ArrayList<ThreadVo>(); 
		for (Thread thread : all) {
			res.add(new ThreadVo(thread));
		}
		return res;
	}

	@Override
	public List<ThreadVo> getAllMyThread(UserVo actorVo) {
		//TODO: To be optimized (find my threads using UserVo as research entry point than all threads).
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		logger.debug("actor : " + actor.getAccountReprentation());
		List<Thread> all = threadService.findAll();
		List<ThreadVo> res = new ArrayList<ThreadVo>(); 
		for (Thread thread : all) {
			List<User> userMembers = new ArrayList<User>();
			for (ThreadMember threadMember : thread.getMyMembers()) {
				userMembers.add(threadMember.getUser());
			}
			if (userMembers.contains(actor)) {
				logger.debug("thread name " + thread.getName());
				res.add(new ThreadVo(thread));
			}
		}
		return res;
	}

	@Override
	public List<ThreadVo> getAllMyThreadWhereCanUpload(UserVo actorVo) {
		//TODO: To be optimized (find my threads using UserVo as research entry point than all threads).
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		logger.debug("actor : " + actor.getAccountReprentation());
		List<Thread> all = threadService.findAll();
		List<ThreadVo> res = new ArrayList<ThreadVo>(); 
		for (Thread thread : all) {
			List<User> userMembers = new ArrayList<User>();
			for (ThreadMember threadMember : thread.getMyMembers()) {
				if (threadMember.getCanUpload()) {
					userMembers.add(threadMember.getUser());
				}
			}
			if (userMembers.contains(actor)) {
				logger.debug("thread name " + thread.getName());
				res.add(new ThreadVo(thread));
			}
		}
		return res;
	}

	@Override
	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo, ThreadVo threadVo) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		List<ThreadEntry> threadEntries = threadEntryService.findAllThreadEntries(actor, thread);
		
		List<ThreadEntryVo> res = new ArrayList<ThreadEntryVo>(); 
		for (ThreadEntry threadEntry : threadEntries) {
			res.add(new ThreadEntryVo(threadEntry));
		}
		return res;
	}

	@Override
	public List<ThreadEntryVo> getAllThreadEntriesTaggedWith(UserVo actorVo, ThreadVo threadVo, TagVo[] tags) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		String[] names = new String[tags.length];
		for (int i = 0; i < names.length; ++i) {
			names[i] = tags[i].getName();
		}
		List<ThreadEntry> threadEntries = threadEntryService.findAllThreadEntriesTaggedWith(actor, thread, names);
		List<ThreadEntryVo> res = new ArrayList<ThreadEntryVo>(); 
		for (ThreadEntry threadEntry : threadEntries) {
			res.add(new ThreadEntryVo(threadEntry));
		}
		return res;
	}

	@Override
	public TagEnumVo getTagEnumVo(UserVo actorVo, ThreadVo threadVo, String name) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		
		Tag tag = tagService.findByOwnerAndName(actor, thread, name);
		
		if(tag != null && tag.getTagType().equals(TagType.ENUM)) {
			return new TagEnumVo((TagEnum)tag);
		}
		return null;
	}


	@Override
	public void setTagsToThreadEntries(UserVo actorVo, ThreadVo threadVo, List<ThreadEntryVo> threadEntriesVo, List<TagVo> tags) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		
		if (userCanUpload(actorVo, threadVo) || userIsAdmin(actorVo, threadVo)) {
			List<ThreadEntry> threadEntries = new ArrayList<ThreadEntry>();
			for (ThreadEntryVo threadEntryVo : threadEntriesVo) {
				threadEntries.add(threadEntryService.findById(actor, threadEntryVo.getIdentifier()));
			}

			for (TagVo tagVo : tags) {
				tagService.setTagToThreadEntries(actor, thread, threadEntries, tagVo.getName(), tagVo.getTagEnumValue());
			}
		}
	}

	@Override
	public InputStream retrieveFileStream(ThreadEntryVo entry, String lsUid) throws BusinessException {
		Account actor = accountService.findByLsUid(lsUid);
		return threadEntryService.getDocumentStream(actor, entry.getIdentifier());
	}

	@Override
	public InputStream retrieveFileStream(ThreadEntryVo entry, UserVo actorVo) throws BusinessException {
		return retrieveFileStream(entry, actorVo.getLsUid());
	}


	@Override
	public boolean documentHasThumbnail(String lsUid, String docId) {
		if(lsUid == null) {
			logger.error("Can't find user with null parameter.");
			return false;
		}		
		Account actor = accountService.findByLsUid(lsUid);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return false;
		}
		return threadEntryService.documentHasThumbnail(actor, docId);
	}


	@Override
	public InputStream getDocumentThumbnail(String actorUuid, String docEntryUuid) {
		if(actorUuid == null) {
			logger.error("Can't find user with null parameter.");
			return null;
		}
		Account actor = accountService.findByLsUid(actorUuid);
		if(actor == null) {
			logger.error("Can't find logged user.");
			return null;
		}
		try {
			return threadEntryService.getDocumentThumbnailStream(actor, docEntryUuid);
		} catch (BusinessException e) {
			logger.error("Can't get document thumbnail : " + docEntryUuid + " : " + e.getMessage());
		}
    	return null;
	}


	@Override
	public void removeDocument(UserVo actorVo, ThreadEntryVo threadEntryVo) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		ThreadEntry threadEntry = threadEntryService.findById(actor, threadEntryVo.getIdentifier());
		if (actor != null) {
			try {
				threadEntryService.deleteThreadEntry(actor, threadEntry);
			} catch (BusinessException e) {
				logger.error("Cannot delete Thread Entry : " + threadEntryVo.getIdentifier());
				throw e;
			}
		} else {
			throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND, "The user couldn't be found");
		}
	}


	@Override
	public ThreadEntryVo findById(UserVo actorVo, String threadEntryUuid) throws BusinessException {
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		ThreadEntry threadEntry = threadEntryService.findById(actor, threadEntryUuid);
		if (threadEntry != null)
			return new ThreadEntryVo(threadEntry);
		return null;
	}

	@Override
	public boolean isMember(ThreadVo threadVo, UserVo userVo) throws BusinessException {
		User user = (User) accountService.findByLsUid(userVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
		return member != null;
	}
	
	@Override
	public boolean userCanUpload(UserVo userVo, ThreadVo threadVo) throws BusinessException {
		User user = (User) accountService.findByLsUid(userVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
		if (member != null)
			return member.getCanUpload() || member.getAdmin();
		return false;
	}

	@Override
	public boolean userIsAdmin(UserVo userVo, ThreadVo threadVo) throws BusinessException {
		User user = (User) accountService.findByLsUid(userVo.getLsUid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
		if (member != null)
			return member.getAdmin();
		return false;
	}

	@Override
	public List<ThreadMemberVo> getThreadMembers(ThreadVo threadVo) throws BusinessException {
		Set<ThreadMember> threadMembers = threadService.findByLsUuid(threadVo.getLsUuid()).getMyMembers();
		List<ThreadMemberVo> members = new ArrayList<ThreadMemberVo>();
		for (ThreadMember threadMember : threadMembers) {
			members.add(new ThreadMemberVo(threadMember));
		}
		Collections.sort(members);
		return members;
	}
	
	@Override
	public void addMember(ThreadVo threadVo, UserVo actorVo, UserVo newMember, boolean readOnly) {
		try {
			if (userIsAdmin(actorVo, threadVo)) {
				Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
				User user = (User) accountService.findByLsUid(newMember.getLsUid());
				List<ThreadMemberVo> members = new ArrayList<ThreadMemberVo>();
				for (ThreadMemberVo member : members) {
					if (member.getUser().getLsUid().equals(user.getLsUuid())) {
						// TODO error duplicate member
						return;
					}
				}
				threadService.addMember(thread, user, readOnly);
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateMember(UserVo actorVo, ThreadMemberVo memberVo, ThreadVo threadVo) {
		try {
			if (userIsAdmin(actorVo, threadVo)) {
				Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
				User user = (User) accountService.findByLsUid(memberVo.getLsUuid());
				ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
				if (member == null) {
					// member doesn't exist
					return;
				}
				threadService.updateMember(member, memberVo.isAdmin(), memberVo.isCanUpload());
			}
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<ThreadVo> getAllMyAdminThread(UserVo actorVo) throws BusinessException {
		List<ThreadVo> res = new ArrayList<ThreadVo>();
		User actor = (User) accountService.findByLsUid(actorVo.getLsUid());
		if (actor == null) {
			logger.error("Can't find logged user.");
		}
		else {
			List<Thread> tmp = threadService.getThreadListIfAdmin(actor);
			for (Thread thread : tmp) {
				res.add(new ThreadVo(thread));
			}
		}
		return res;
	}

	@Override
	public boolean isUserAdminOfAnyThread(UserVo actorVo) throws BusinessException {
		User actor = (User) accountService.findByLsUid(actorVo.getLsUid());
		if (actor == null) {
			logger.error("Can't find logged user.");
			return false;
		}
		return threadService.hasAnyThreadWhereIsAdmin(actor);
	}

	@Override
	public void createThread(UserVo actorVo, String name) throws BusinessException {
		if (actorVo == null || name == null)
			return;
		if (actorVo.isGuest()) {
			logger.error("guests are not authorised to create a thread");
			return;
		}
		Account actor = accountService.findByLsUid(actorVo.getLsUid());
		if (actor != null) {
			threadService.create(actor, name);
		}
	}

	@Override
	public void deleteThread(UserVo actorVo, ThreadVo threadVo) throws BusinessException {
		if (actorVo == null || threadVo == null)
			return;
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		if (!userIsAdmin(actorVo, threadVo)) {
			logger.error("not authorised");
			return;
		}
		threadService.deleteThread((User)accountService.findByLsUid(actorVo.getLsUid()), thread);
	}

	@Override
	public void updateFileProperties(String lsUid, String threadEntryUuid, String fileComment) {
		Account actor = accountService.findByLsUid(lsUid);
		if(fileComment == null) {
			fileComment = "";
		}
        try {
			threadEntryService.updateFileProperties(actor, threadEntryUuid, fileComment);
		} catch (BusinessException e) {
			logger.error("Can't update file properties document : " + threadEntryUuid + " : " + e.getMessage());
		}
	}

	@Override
	public ThreadEntryVo getThreadEntry(String login, String threadEntryUuid) {
		Account actor = accountService.findByLsUid(login);
		if(actor != null) {
			ThreadEntry entry;
			try {
				entry = threadEntryService.findById(actor, threadEntryUuid);
				return new ThreadEntryVo(entry);
			} catch (BusinessException e) {
				logger.error("can't get document : " + e.getMessage());
			}
		}
		return null;
	}
}
