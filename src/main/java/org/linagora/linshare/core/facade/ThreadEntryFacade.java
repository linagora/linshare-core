/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 * 
 * Copyright (C) 2014 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display of the “LinShare™” trademark/logo at the top
 * of the interface window, the display of the “You are using the Open Source
 * and free version of LinShare™, powered by Linagora © 2009–2014. Contribute to
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
import org.linagora.linshare.core.domain.vo.ThreadEntryVo;
import org.linagora.linshare.core.domain.vo.ThreadMemberVo;
import org.linagora.linshare.core.domain.vo.ThreadVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface ThreadEntryFacade {

	public ThreadEntryVo insertFile(UserVo actorVo, ThreadVo threadVo,
			InputStream stream, Long size, String fileName)
			throws BusinessException;

	public void copyDocinThread(UserVo actorVo, ThreadVo threadVo,
			DocumentVo documentVo) throws BusinessException;

	public void createThread(UserVo actorVo, String name)
			throws BusinessException;

	public ThreadVo getThread(UserVo actorVo, String uuid) throws BusinessException;

	public List<ThreadVo> getAllMyThread(UserVo actorVo) throws BusinessException;

	public List<ThreadVo> getAllMyThreadWhereCanUpload(UserVo actorVo) throws BusinessException;

	public List<ThreadEntryVo> getAllThreadEntryVo(UserVo actorVo,
			ThreadVo threadVo) throws BusinessException;

	public InputStream retrieveFileStream(UserVo actorVo, ThreadEntryVo entry)
			throws BusinessException;

	public boolean documentHasThumbnail(UserVo actorVo, String documentEntryId);

	public InputStream getDocumentThumbnail(UserVo actorVo,
			String docEntryUuid);

	public void removeDocument(UserVo userVo, ThreadEntryVo entryVo)
			throws BusinessException;

	public int countEntries(ThreadVo threadVo)
			throws BusinessException;

	public ThreadEntryVo findById(UserVo user, String entryUuid)
			throws BusinessException;

	public boolean userIsMember(UserVo userVo, ThreadVo threadVo)
			throws BusinessException;

	public List<ThreadMemberVo> getThreadMembers(UserVo actorVo,
			ThreadVo threadVo) throws BusinessException;

	public boolean userCanUpload(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException;

	public boolean userIsAdmin(UserVo userVo, ThreadVo threadVo)
			throws BusinessException;

	public List<ThreadVo> getAllMyThreadWhereAdmin(UserVo actorVo) throws BusinessException;

	public void addMember(UserVo actorVo, ThreadVo threadVo, UserVo newMember,
			boolean readOnly) throws BusinessException;

	public void deleteMember(UserVo actorVo, ThreadVo threadVo,
			ThreadMemberVo memberVo) throws BusinessException;

	public boolean isUserAdminOfAnyThread(UserVo actorVo)
			throws BusinessException;

	public void updateMember(UserVo actorVo, ThreadMemberVo memberVo,
			ThreadVo threadVo) throws BusinessException;

	public void deleteThread(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException;

	public void updateFileProperties(String lsUid, String threadEntryUuid,
			String fileComment) throws BusinessException;

	public ThreadEntryVo getThreadEntry(UserVo actorVo, String threadEntryUuid) throws BusinessException;

	public void renameThread(UserVo actorVo, ThreadVo threadVo, String threadName)
			throws BusinessException;

	/**
	 * Add User to thread
	 * 
	 * @param actorVo
	 * @param threadVo
	 * @param domain
	 * @param mail
	 * @throws BusinessException
	 */
	public void addMember(UserVo actorVo, ThreadVo threadVo,
			String domain, String mail) throws BusinessException;

	/**
	 * Remove member from thread
	 * 
	 * @param currentUser
	 * @param threadVo
	 * @param userVo
	 * @throws BusinessException
	 */
	public void removeMember(UserVo currentUser, ThreadVo threadVo,
			UserVo userVo) throws BusinessException;

	/**
	 * Get list of the latest threads orderer by modification date
	 * 
	 * @param actorVo
	 * @param limit amount of threads wanted
	 * @return list of latest threads ordered by modification date
	 * @throws BusinessException
	 */
	public List<ThreadVo> getLatestThreads(UserVo actorVo, int limit)
			throws BusinessException;

	public int countMembers(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException;

	public boolean memberIsDeletable(UserVo actorVo, ThreadVo threadVo)
			throws BusinessException;

	/**
	 * Search a thread by name
	 * @param userVo
	 * @param pattern
	 * @return list of matching threads
	 */
	public List<ThreadVo> searchThread(UserVo userVo, String pattern)
			throws BusinessException;


    /*
     * Deprecated methods
     */

	/**
	 * Return list of users according to search input
	 * 
	 * @param userVo
	 * @param input
	 * @return
	 * @throws BusinessException
	 */
    @Deprecated
	public List<UserVo> searchAmongUsers(UserVo userVo, String input)
			throws BusinessException;

	/**
	 * Return list of thread members according to search input
	 * 
	 * @param userVo
	 * @param currentThread
	 * @param input
	 * @return
	 * @throws BusinessException
	 */
    @Deprecated
	public List<ThreadMemberVo> searchAmongMembers(UserVo userVo,
			ThreadVo currentThread, String input, String criteriaOnSearch)
			throws BusinessException;

	/**
	 * return list of threads
	 * 
	 * @param threads
	 * @param userVo
	 * @param criteriaOnSearch
	 * @param recipientsSearchUser
	 * @return
	 */
    @Deprecated
	public List<ThreadVo> getListOfThreadFromSearchByUser(UserVo userVo,
			String criteriaOnSearch, String recipientsSearchUser)
			throws BusinessException;

	/**
	 * Provide completion for search thread
	 * 
	 * @param actor
	 * @param input
	 * @return
	 */
    @Deprecated
	public List<String> completionOnThreads(UserVo actor, String input);

	/**
	 * Provide completion for search User
	 * 
	 * @param actorVo
	 * @param pattern
	 * @return
	 */
    @Deprecated
	public List<String> completionOnUsers(UserVo actorVo, String pattern)
			throws BusinessException;

	/**
	 * Provide completion for search thread member
	 * 
	 * @param actorVo
	 * @param currentThread
	 * @param pattern
	 * @return
	 */
    @Deprecated
	public List<String> completionOnMembers(UserVo actorVo,
			ThreadVo currentThread, String pattern);
}
