package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListFacade {

	public MailingListVo retrieveList(String uuid);

	public MailingListVo createList(MailingListVo mailingListVo) throws BusinessException;

	public List<MailingListVo> findAllListByUser(UserVo actorVo) throws BusinessException;

	public void deleteList(UserVo actorVo, String uuid) throws BusinessException;

	public void updateList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException;

	public void deleteContact(MailingListVo listVo, String mail) throws BusinessException;

	public MailingListContactVo retrieveContact(MailingListVo list, String mail) throws BusinessException;

	public void updateContact(MailingListVo listVo, MailingListContactVo contactToUpdate) throws BusinessException;

	public List<MailingListVo> findAllMyList(UserVo user) throws BusinessException;

	/**
	 * Check if list identifier exists, if it does , purpose an alternative
	 * identifier
	 * 
	 * @param user
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	public String checkUniqueIdentifier(UserVo user, String value) throws BusinessException;

	/**
	 * provide completions to search a list for user
	 * 
	 * @param loginUser
	 * @param input
	 * @param criteriaOnSearch
	 * @return
	 * @throws BusinessException
	 */
	public List<String> completionsForUserSearchList(UserVo loginUser, String input, String criteriaOnSearch)
			throws BusinessException;

	/**
	 * Set list of results from search
	 * 
	 * @param loginUser
	 * @param targetLists
	 * @param criteriaOnSearch
	 * @return
	 * @throws BusinessException
	 */
	public List<MailingListVo> setListFromUserSearch(UserVo loginUser, String targetLists, String criteriaOnSearch)
			throws BusinessException;

	/**
	 * Check if user is in mailing list
	 * 
	 * @param contacts
	 * @param mail
	 * @return
	 */
	public boolean checkUserIsContact(List<MailingListContactVo> contacts, String mail);

	/**
	 * Provide completion for user search
	 * 
	 * @param actorVo
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	public List<String> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException;

	/**
	 * Search among user
	 * 
	 * @param userVo
	 * @param input
	 * @return
	 * @throws BusinessException
	 */
	public List<UserVo> searchAmongUsers(UserVo userVo, String input) throws BusinessException;

	/**
	 * Add user to mailing list
	 * 
	 * @param mailingListVo
	 * @param domain
	 * @param mail
	 * @throws BusinessException
	 */
	public void addUserToList(MailingListVo mailingListVo, String domain, String mail) throws BusinessException;

	public void addNewContactToList(MailingListVo mailingListVo, MailingListContactVo contactVo)
			throws BusinessException;

	public void refreshList(List<MailingListVo> list);

	public boolean getListIsDeletable(UserVo actorVo, MailingListVo listVo) throws BusinessException;

	public List<MailingListVo> getListsFromShare(String recipients);

	public List<String> completionsForShare(UserVo user, String input) throws BusinessException;
}
