package org.linagora.linshare.core.facade;

import java.util.List;

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
}
