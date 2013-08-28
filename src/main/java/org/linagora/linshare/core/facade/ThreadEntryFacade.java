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
package org.linagora.linshare.core.facade;

import java.io.InputStream;
import java.util.List;

import org.linagora.linshare.core.domain.vo.DocumentVo;
import org.linagora.linshare.core.domain.vo.TagEnumVo;
import org.linagora.linshare.core.domain.vo.TagVo;
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadEntryFacade {
	
	public ThreadEntryVo insertFile(UserVo actorVo, ThreadVo threadVo, InputStream stream, Long size, String fileName) throws BusinessException ;

	public void copyDocinThread(UserVo actorVo, ThreadVo threadVo, DocumentVo documentVo) throws BusinessException;
	
	public void createThread(UserVo actorVo, String name) throws BusinessException;
	
	// public void deleteThread(UserVo actorVo, ThreadVo threadVo) throws BusinessException;
	
	public List<ThreadVo> getAllThread();
	
	public List<ThreadVo> getAllMyThread(UserVo actorVo);
	
	public List<ThreadVo> getAllMyThreadWhereCanUpload(UserVo actorVo);

	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo, ThreadVo threadVo) throws BusinessException;
	
	public TagEnumVo getTagEnumVo(UserVo actorVo, ThreadVo threadVo, String name) throws BusinessException;
	
	public void setTagsToThreadEntries(UserVo actorVo, ThreadVo threadVo, List<ThreadEntryVo> threadEntriesVo, List<TagVo> tags) throws BusinessException;

	public InputStream retrieveFileStream(UserVo actorVo, ThreadEntryVo entry) throws BusinessException;

	public InputStream retrieveFileStream(ThreadEntryVo entry, String lsUid) throws BusinessException;

	public boolean documentHasThumbnail(String lsUid, String docId);

	public InputStream getDocumentThumbnail(String actorUuid, String docEntryUuid);

	public void removeDocument(UserVo userVo, ThreadEntryVo threadEntryVo) throws BusinessException;

	public ThreadEntryVo findById(UserVo user, String threadEntryUuid) throws BusinessException;

	public List<ThreadEntryVo> getAllThreadEntriesTaggedWith(UserVo actorVo, ThreadVo threadVo, TagVo[] tags) throws BusinessException;
	
	public boolean userIsMember(UserVo userVo, ThreadVo threadVo) throws BusinessException;

	public List<ThreadMemberVo> getThreadMembers(UserVo actorVo, ThreadVo threadVo) throws BusinessException;

	public boolean userCanUpload(UserVo actorVo, ThreadVo threadVo) throws BusinessException;

	public boolean userIsAdmin(UserVo userVo, ThreadVo threadVo) throws BusinessException;

	public List<ThreadVo> getAllMyThreadWhereAdmin(UserVo actorVo) throws BusinessException;

	public void addMember(UserVo actorVo, ThreadVo threadVo, UserVo newMember, boolean readOnly);

	public void deleteMember(UserVo actorVo, ThreadVo threadVo, ThreadMemberVo memberVo);

	public boolean isUserAdminOfAnyThread(UserVo actorVo) throws BusinessException;

	public void updateMember(UserVo actorVo, ThreadMemberVo memberVo, ThreadVo threadVo);

	public void deleteThread(UserVo actorVo, ThreadVo threadVo) throws BusinessException;

	public void updateFileProperties(String lsUid, String threadEntryUuid, String fileComment);

	public ThreadEntryVo getThreadEntry(String login, String threadEntryUuid);

	public ThreadVo getThread(UserVo login, String threadUuid) throws BusinessException;

	public void renameThread(UserVo userVo, String threadUuid, String threadName) throws BusinessException;

	/**
	 * Provide completion for search User
	 * @param actorVo
	 * @param pattern
	 * @return
	 */
	public List<String> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException;
	
	/**
	 * Provide completion for search thread
	 * @param actor
	 * @param input
	 * @return
	 */
	public List<String> completionOnThreads(UserVo actor,String input);
	
	/**
	 * Provide completion for search thread member
	 * @param actorVo
	 * @param currentThread
	 * @param pattern
	 * @return
	 */
	public List<String> completionOnMembers(UserVo actorVo, ThreadVo currentThread, String pattern);
	
	/**
	 * Add User to thread
	 * @param currentUser
	 * @param threadVo
	 * @param domain
	 * @param mail
	 * @throws BusinessException
	 */
	public void addUserToThread(UserVo currentUser, ThreadVo threadVo,String domain, String mail) throws BusinessException;
	
	/**
	 * Remove member from thread
	 * @param currentUser
	 * @param threadVo
	 * @param domain
	 * @param mail
	 * @throws BusinessException
	 */
	public void removeMemberFromThread(UserVo currentUser, ThreadVo threadVo,String domain, String mail) throws BusinessException;
	
	
	/**
	 * Return list of users according to search input
	 * @param userVo
	 * @param input
	 * @return
	 * @throws BusinessException
	 */
	public List<UserVo> searchAmongUsers(UserVo userVo,String input) throws BusinessException;
	
	/**
	 * Return list of thread members according to search input
	 * @param userVo
	 * @param currentThread
	 * @param input
	 * @return
	 * @throws BusinessException
	 */
	public List<ThreadMemberVo> searchAmongMembers(UserVo userVo, ThreadVo currentThread,String input,String criteriaOnSearch) throws BusinessException;
	/**
	 *  return list of threads
	 * @param threads
	 * @param userVo
	 * @param criteriaOnSearch
	 * @param recipientsSearchUser
	 * @return 
	 */
	public List<ThreadVo> getListOfThreadFromSearchByUser(UserVo userVo,String criteriaOnSearch, String recipientsSearchUser) throws BusinessException;
	
	public List<ThreadVo> getListOfLastModifiedThreads(UserVo userVo);
	
	public boolean memberIsDeletable(UserVo actorVo, ThreadVo threadVo) throws BusinessException;
}
