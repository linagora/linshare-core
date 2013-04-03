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
package org.linagora.linshare.core.facade.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.linagora.linshare.core.domain.constants.TagType;
import org.linagora.linshare.core.domain.entities.Account;
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
		
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		ThreadEntry threadEntry = threadEntryService.createThreadEntry(actor, thread, stream, size, fileName);
		return new ThreadEntryVo(threadEntry);
	}
	
	@Override
	public void copyDocinThread(UserVo actorVo, ThreadVo threadVo, DocumentVo documentVo) throws BusinessException {
		Account owner = accountService.findByLsUuid(documentVo.getOwnerLogin());
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
		List<ThreadVo> res = new ArrayList<ThreadVo>(); 
		User actor = (User) accountService.findByLsUuid(actorVo.getLsUuid());
	
		if (actor == null) {
			logger.error("Can't find logged in user.");
			return res;
		}
		logger.debug("actor : " + actor.getAccountReprentation());
		for (Thread thread : threadService.findAllWhereMember(actor)) {
			res.add(new ThreadVo(thread));
		}
		return res;
	}

	@Override
	public List<ThreadVo> getAllMyThreadWhereCanUpload(UserVo actorVo) {
		List<ThreadVo> res = new ArrayList<ThreadVo>(); 
		User actor = (User) accountService.findByLsUuid(actorVo.getLsUuid());
	
		if (actor == null) {
			logger.error("Can't find logged in user.");
			return res;
		}
		logger.debug("actor : " + actor.getAccountReprentation());
		for (Thread thread : threadService.findAllWhereCanUpload(actor)) {
			res.add(new ThreadVo(thread));
		}
		return res;
	}

	@Override
	public List<ThreadVo> getAllMyThreadWhereAdmin(UserVo actorVo) throws BusinessException {
		List<ThreadVo> res = new ArrayList<ThreadVo>();
		User actor = (User) accountService.findByLsUuid(actorVo.getLsUuid());
	
		if (actor == null) {
			logger.error("Can't find logged in user.");
			return res;
		}
		logger.debug("actor : " + actor.getAccountReprentation());
		for (Thread thread : threadService.findAllWhereAdmin(actor)) {
			res.add(new ThreadVo(thread));
		}
		return res;
	}

	@Override
	public boolean isUserAdminOfAnyThread(UserVo actorVo) throws BusinessException {
		User actor = (User) accountService.findByLsUuid(actorVo.getLsUuid());
	
		if (actor == null) {
			logger.error("Can't find logged user.");
			return false;
		}
		return threadService.hasAnyWhereAdmin(actor);
	}
	
	@Override
	public boolean userIsAdmin(UserVo userVo, ThreadVo threadVo) throws BusinessException {
		User user = (User) accountService.findByLsUuid(userVo.getLsUuid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		return threadService.isUserAdmin(user, thread);
	}

	@Override
	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo, ThreadVo threadVo) throws BusinessException {
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
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
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
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
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		
		Tag tag = tagService.findByOwnerAndName(actor, thread, name);
		
		if(tag != null && tag.getTagType().equals(TagType.ENUM)) {
			return new TagEnumVo((TagEnum)tag);
		}
		return null;
	}


	@Override
	public void setTagsToThreadEntries(UserVo actorVo, ThreadVo threadVo, List<ThreadEntryVo> threadEntriesVo, List<TagVo> tags) throws BusinessException {
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
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
		Account actor = accountService.findByLsUuid(lsUid);
		return threadEntryService.getDocumentStream(actor, entry.getIdentifier());
	}

	@Override
	public InputStream retrieveFileStream(ThreadEntryVo entry, UserVo actorVo) throws BusinessException {
		return retrieveFileStream(entry, actorVo.getLsUuid());
	}


	@Override
	public boolean documentHasThumbnail(String lsUid, String docId) {
		if(lsUid == null) {
			logger.error("Can't find user with null parameter.");
			return false;
		}		
		Account actor = accountService.findByLsUuid(lsUid);
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
		Account actor = accountService.findByLsUuid(actorUuid);
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
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
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
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
		ThreadEntry threadEntry = threadEntryService.findById(actor, threadEntryUuid);
		if (threadEntry != null)
			return new ThreadEntryVo(threadEntry);
		return null;
	}

	@Override
	public boolean isMember(ThreadVo threadVo, UserVo userVo) throws BusinessException {
		User user = (User) accountService.findByLsUuid(userVo.getLsUuid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
		return member != null;
	}
	
	@Override
	public boolean userCanUpload(UserVo userVo, ThreadVo threadVo) throws BusinessException {
		User user = (User) accountService.findByLsUuid(userVo.getLsUuid());
		Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
		ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
		if (member != null)
			return member.getCanUpload() || member.getAdmin();
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
				User user = (User) accountService.findByLsUuid(newMember.getLsUuid());
				Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
				threadService.addMember(actor, thread, user, readOnly);
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void deleteMember(ThreadVo threadVo, UserVo actorVo, ThreadMemberVo memberVo) {
		try {
			if (userIsAdmin(actorVo, threadVo)) {
					Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
					Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
					User user = (User) accountService.findByLsUuid(memberVo.getUser().getLsUuid());
					ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
					threadService.deleteMember(actor, thread, member);
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
	}
	
	@Override
	public void updateMember(UserVo actorVo, ThreadMemberVo memberVo, ThreadVo threadVo) {
		try {
			if (userIsAdmin(actorVo, threadVo)) {
				Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
				Thread thread = threadService.findByLsUuid(threadVo.getLsUuid());
				User user = (User) accountService.findByLsUuid(memberVo.getLsUuid());
				ThreadMember member = threadService.getThreadMemberFromUser(thread, user);
				if (member == null) {
					logger.error("Member not found. User: " + user.getAccountReprentation()
							+ "; Thread: " + thread.getAccountReprentation());
					return;
				}
				threadService.updateMember(actor, member, memberVo.isAdmin(), memberVo.isCanUpload());
			}
		} catch (BusinessException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public void createThread(UserVo actorVo, String name) throws BusinessException {
		if (actorVo == null || name == null)
			return;
		if (actorVo.isGuest()) {
			logger.error("guests are not authorised to create a thread");
			return;
		}
		Account actor = accountService.findByLsUuid(actorVo.getLsUuid());
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
		threadService.deleteThread((User)accountService.findByLsUuid(actorVo.getLsUuid()), thread);
	}

	@Override
	public void updateFileProperties(String lsUid, String threadEntryUuid, String fileComment) {
		Account actor = accountService.findByLsUuid(lsUid);
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
		Account actor = accountService.findByLsUuid(login);
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

	@Override
	public ThreadVo getThread(UserVo userVo, String threadUuid) throws BusinessException {
		Thread thread = threadService.findByLsUuid(threadUuid);
		ThreadVo threadVo = new ThreadVo(thread);
		if (!this.isMember(threadVo, userVo)) {
			logger.error("Not authorised to get the thread " + threadUuid);
			throw new BusinessException("Not authorised to get the thread " + threadUuid);
		}
		return threadVo;
	}


	@Override
	public void renameThread(UserVo userVo, String threadUuid, String threadName) throws BusinessException {
		Thread thread = threadService.findByLsUuid(threadUuid);
		ThreadVo threadVo = new ThreadVo(thread);
		User actor = (User) accountService.findByLsUuid(userVo.getLsUuid());
		if (!this.userIsAdmin(userVo, threadVo)) {
			logger.error("Not authorised to get the thread " + threadUuid);
			throw new BusinessException("Not authorised to get the thread " + threadUuid);
		}
		threadService.rename(actor, thread, threadName);
	}

}
