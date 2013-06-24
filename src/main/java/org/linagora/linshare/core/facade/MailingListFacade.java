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
    public List<MailingListVo> findAllMailingListByIdentifier(String identifier) throws BusinessException ;
    public boolean mailingListIdentifierUnicity(MailingListVo toCreate,UserVo actorVo)throws BusinessException;
    public List<MailingListContactVo> getListOfMailAdd(MailingListVo list) throws BusinessException ;
    public List<MailingListContactVo> getListOfMailRemove(MailingListVo list) throws BusinessException ;
    
    public void deleteMailingListContact(long persistenceId) throws BusinessException;
    public MailingListContactVo retrieveMailingListContact(long persistenceId);
    
    public void checkUniqueId(MailingListVo listVo,UserVo user)  throws BusinessException ;
    public List<MailingListVo> findAllMailingListByOwner(UserVo user) throws BusinessException ;
}
