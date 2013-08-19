package org.linagora.linshare.core.facade;

import java.util.List;

import org.linagora.linshare.core.domain.vo.MailingListContactVo;
import org.linagora.linshare.core.domain.vo.MailingListVo;
import org.linagora.linshare.core.domain.vo.UserVo;
import org.linagora.linshare.core.exception.BusinessException;

public interface MailingListFacade {

    public MailingListVo retrieveMailingList(long persistenceId);
    
    public MailingListVo createMailingList(MailingListVo mailingListVo) throws BusinessException;
    
    public List<MailingListVo> findAllMailingList();
    
    public List<MailingListVo> findAllMailingListByUser(UserVo actorVo) throws BusinessException;
    
    public void deleteMailingList(long persistenceId) throws BusinessException;
    
    public void updateMailingList(MailingListVo mailingListVo) throws BusinessException;
    
    public List<MailingListVo> findAllMailingListByIdentifier(UserVo actorVo, String identifier) throws BusinessException ;
    
    public void deleteMailingListContact(MailingListVo listVo,long persistenceId) throws BusinessException;
    
    public MailingListContactVo retrieveMailingListContact(MailingListVo list , String mail);
    
    public void updateMailingListContact(MailingListContactVo contactToUpdate) throws BusinessException;
    
    public List<MailingListVo> findAllMailingListByOwner(UserVo user) throws BusinessException ;
    
    public List<MailingListVo> getMailingListFromQuickShare(UserVo user,final String mailingLists);
    
    public MailingListVo retrieveMailingListByOwnerAndIdentifier(String identifier, String ownerFullName);
    
    /**
     * Check if list identifier exists, if it does , purpose an alternative identifier
     * @param user
     * @param value
     * @return
     * @throws BusinessException
     */
    public String checkUniqueId(UserVo user,String value)  throws BusinessException ;
    
    
    /**
     * provide completions to search a list
     * @param loginUser
     * @param input
     * @param criteriaOnSearch
     * @return
     * @throws BusinessException
     */
    public List<String> completionsForSearchList(UserVo loginUser,String input,String criteriaOnSearch) throws BusinessException;
    
    /**
     * Set list of results from search
     * @param loginUser
     * @param targetLists
     * @param criteriaOnSearch
     * @return
     * @throws BusinessException
     */
    public List<MailingListVo> setListFromSearch(UserVo loginUser, String targetLists, String criteriaOnSearch) throws BusinessException;

    /**
     * Check if user is in mailing list
     * @param contacts
     * @param mail
     * @return
     */
    public boolean checkUserIsContact(List<MailingListContactVo> contacts , String mail);
    
    
    /**
     * Provide completion for user search
     * @param actorVo
     * @param pattern
     * @return
     * @throws BusinessException
     */
    public List<String> completionOnUsers(UserVo actorVo, String pattern) throws BusinessException ;
    
    /**
     * Search among user
     * @param userVo
     * @param input
     * @return
     * @throws BusinessException
     */
    public List<UserVo> searchAmongUsers(UserVo userVo, String input) throws BusinessException;
    
    /**
     * Add user to mailing list
     * @param mailingListVo
     * @param domain
     * @param mail
     * @throws BusinessException
     */
    public void addUserToMailingListContact(MailingListVo mailingListVo, String domain, String mail) throws BusinessException;
    
    /**
     * remove contact from mailing list
     * @param mailingListVo
     * @param domain
     * @param mail
     * @return
     * @throws BusinessException
     */
    public long removeContactFromMailingList(MailingListVo mailingListVo, String domain, String mail) throws BusinessException;
    
    /**
     * Set new owner to a mailing list 
     * @param mailingListVo
     * @param input
     * @throws BusinessException
     */
	public void setNewOwner(MailingListVo mailingListVo, String input) throws BusinessException ;
	
	public void refreshListOfMailingList(List<MailingListVo> list);
}
