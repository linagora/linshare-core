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
    
    public List<MailingListVo> findAllMailingListByIdentifier(String identifier, UserVo actorVo) throws BusinessException ;
    
    public void deleteMailingListContact(MailingListVo listVo,long persistenceId) throws BusinessException;
    
    public MailingListContactVo retrieveMailingListContact(String mail , MailingListVo list);
    
    public void updateMailingListContact(MailingListContactVo contactToUpdate) throws BusinessException;
    
    public String checkUniqueId(String value,UserVo user)  throws BusinessException ;
    
    public List<MailingListVo> findAllMailingListByOwner(UserVo user) throws BusinessException ;
    
    public List<MailingListVo> copyList(List<MailingListVo> list);
    
    public List<MailingListVo> getMailingListFromQuickShare(final String mailingLists,UserVo user);
    
    public MailingListVo retrieveMailingListByOwnerAndIdentifier(String identifier, String ownerFullName);
    
    public List<String> onProvideCompletionsForSearchList(String input,String criteriaOnSearch,UserVo loginUser) throws BusinessException;
    
    public List<MailingListVo> setListFromSearch(String targetLists, String criteriaOnSearch, UserVo loginUser) throws BusinessException;

}
