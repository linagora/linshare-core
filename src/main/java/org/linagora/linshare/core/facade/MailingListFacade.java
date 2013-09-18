package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListFacade {

	MailingListVo searchList(String uuid) throws BusinessException;

	MailingListVo createList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException;

	void deleteList(UserVo actorVo, String uuid) throws BusinessException;

	void updateList(UserVo actorVo, MailingListVo mailingListVo) throws BusinessException;

	MailingListContactVo retrieveContact(MailingListVo list, String mail) throws BusinessException;

	List<MailingListVo> getAllMyList(UserVo user) throws BusinessException;

	/**
	 * Check if list identifier exists, if it does , purpose an alternative
	 * identifier
	 * 
	 * @param user
	 * @param value
	 * @return
	 * @throws BusinessException
	 */
	String checkUniqueIdentifier(UserVo user, String value) throws BusinessException;

	/**
	 * provide completions to search a list for user
	 * 
	 * @param loginUser
	 * @param input
	 * @param criteriaOnSearch
	 * @return
	 * @throws BusinessException
	 */
	public List<MailingListVo> performSearchForUser(UserVo loginUser,
			String input, String criteriaOnSearch) throws BusinessException;

	/**
	 * Set list of results from search
	 * 
	 * @param loginUser
	 * @param targetLists
	 * @param criteriaOnSearch
	 * @return
	 * @throws BusinessException
	 */
	List<MailingListVo> setListFromUserSearch(UserVo loginUser, String targetLists, String criteriaOnSearch)
			throws BusinessException;

	/**
	 * Check if user is in mailing list
	 * 
	 * @param contacts
	 * @param mail
	 * @return
	 */
	boolean checkUserIsContact(List<MailingListContactVo> contacts, String mail);

	/**
	 * Provide completion for user search
	 * 
	 * @param actorVo
	 * @param pattern
	 * @return
	 * @throws BusinessException
	 */
	List<String> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException;

	/**
	 * Search among user
	 * 
	 * @param userVo
	 * @param input
	 * @return
	 * @throws BusinessException
	 */
	List<UserVo> searchAmongUsers(UserVo userVo, String input) throws BusinessException;

	/**
	 * Add user to mailing list
	 * 
	 * @param mailingListVo
	 * @param domain
	 * @param mail
	 * @throws BusinessException
	 */
	void addUserToList(UserVo actorVo, MailingListVo mailingListVo, String domain, String mail)
			throws BusinessException;

	/**
	 * Add contact to mailing list
	 * 
	 * @param actorVo
	 * @param mailingListVo
	 * @param contactVo
	 * @throws BusinessException
	 */
	void addNewContactToList(UserVo actorVo, MailingListVo mailingListVo, MailingListContactVo contactVo)
			throws BusinessException;

	void refreshList(List<MailingListVo> list);

	boolean getListIsDeletable(UserVo actorVo, MailingListVo listVo) throws BusinessException;

	/**
	 * Get list from share
	 * 
	 * @param recipients
	 * @return
	 */
	List<MailingListVo> getListsFromShare(String recipients);

	List<String> completionsForShare(UserVo user, String input) throws BusinessException;

	void deleteContact(UserVo actorVo, MailingListVo listVo, String mail) throws BusinessException;

	void updateContact(UserVo actorVo, MailingListVo listVo, MailingListContactVo contactToUpdate)
			throws BusinessException;

	public List<MailingListVo> completionForUploadForm(UserVo userVo, String input) throws BusinessException;
}
